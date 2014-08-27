package com.wisewells.wisebeacon.beacongroup;

import java.util.List;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;

public class BeaconGroupListData {
	private BeaconGroup mBeaconGroup;
	private List<Beacon> mBeacons;
	
	public BeaconGroupListData(WiseManager manager, BeaconGroup group) {
		mBeaconGroup = group;
		mBeacons = manager.getBeaconsInGroup(group.getCode());
	}
	
	public BeaconGroup getBeaconGroup() {
		return mBeaconGroup;
	}
	
	public List<Beacon> getBeacons() {
		return mBeacons;
	}
}
