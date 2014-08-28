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
	private boolean mIsNearbyGroup;
	
	public BeaconGroupListData(BeaconGroup group) {
		mBeaconGroup = group;
	}
	
	public BeaconGroupListData(BeaconGroup group, List<Beacon> beacons, boolean isNearby) {
		mBeaconGroup = group;
		mBeacons = beacons;
		mIsNearbyGroup = isNearby;
	}
	
	public BeaconGroup getBeaconGroup() {
		return mBeaconGroup;
	}
	
	public List<Beacon> getBeacons() {
		return mBeacons;
	}
	
	public boolean getIsNearbyGroup() {
		return mIsNearbyGroup;
	}
	
	public void setIsNearbyGroup(boolean b) {
		mIsNearbyGroup = b;
	}
}
