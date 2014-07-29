package com.wisewells.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Messenger;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.ibeacon.Region;
import com.wisewells.sdk.utils.BeaconUtils;
import com.wisewells.sdk.utils.L;

class RangingRegion {
	private static final Comparator<Beacon> BEACON_ACCURACY_COMPARATOR = new Comparator<Beacon>() {
		public int compare(Beacon lhs, Beacon rhs) {
			return Double.compare(BeaconUtils.computeAccuracy(lhs), BeaconUtils.computeAccuracy(rhs));
		}
	};

	private final ConcurrentHashMap<Beacon, Long> beacons;
	final Region region;
	final Messenger replyTo;

	RangingRegion(Region region, Messenger replyTo) {
		this.region = region;
		this.replyTo = replyTo;
		this.beacons = new ConcurrentHashMap<Beacon, Long>();
	}

	public final Collection<Beacon> getSortedBeacons() {
		ArrayList<Beacon> sortedBeacons = new ArrayList<Beacon>(this.beacons.keySet());
		Collections.sort(sortedBeacons, BEACON_ACCURACY_COMPARATOR);
		return sortedBeacons;
	}

	/**
	 * @param beaconsFoundInScanCycle
	 * Scan Sycle ���� ã�� Beacon�� �� Region�� ���ϴ� Beacon���� �����ؼ� ����. 
	 */
	public final void processFoundBeacons(Map<Beacon, Long> beaconsFoundInScanCycle) {
		for (Map.Entry<Beacon, Long> entry : beaconsFoundInScanCycle.entrySet())
			if (BeaconUtils.isBeaconInRegion((Beacon)entry.getKey(), this.region)) {
				this.beacons.remove(entry.getKey());
				this.beacons.put((Beacon)entry.getKey(), (Long)entry.getValue());
			}
	}

	public final void removeNotSeenBeacons(long currentTimeMillis) {
		Iterator iterator = this.beacons.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			if (currentTimeMillis - ((Long)entry.getValue()).longValue() > WiseAgent.EXPIRATION_MILLIS) {
				L.v("Not seen lately: " + entry.getKey());
				iterator.remove();
			}
		}
	}
}
