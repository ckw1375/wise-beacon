package com.wisewells.wisebeacon.beacongroup;

import java.util.List;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.utils.L;

public class BeaconGroupListData {
	private BeaconGroup mBeaconGroup;
	private List<Beacon> mBeacons;
	private boolean mIsNearby;
	
	public BeaconGroupListData(BeaconGroup group) {
		mBeaconGroup = group;
	}
	
	public BeaconGroupListData(WiseManager manager, BeaconGroup group) {
		mBeaconGroup = group;
		mBeacons = manager.getBeaconsInGroup(group.getCode());
		mIsNearby = false;
		
		int i = 0;
		BeaconVector bv = new BeaconVector(mBeacons.size());
		for(Beacon beacon : mBeacons) {
			bv.set(i++, beacon.getRegion());
		}
		
		L.d("Beacon Group List Data");
		boolean[] allBeaconsNearby = manager.isNearbyBeacon(bv);
		if(allBeaconsNearby == null)
			return;
		
		for(int k=0; k<allBeaconsNearby.length; k++) {
			if(allBeaconsNearby[k]) {
				mIsNearby = true;
				break;
			}
		}
	}
	
	public BeaconGroup getBeaconGroup() {
		return mBeaconGroup;
	}
	
	public List<Beacon> getBeacons() {
		return mBeacons;
	}
	
	public boolean getIsNearby() {
		return mIsNearby;
	}
}
