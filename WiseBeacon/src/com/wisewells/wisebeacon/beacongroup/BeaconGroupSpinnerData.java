package com.wisewells.wisebeacon.beacongroup;

import com.wisewells.sdk.beacon.BeaconGroup;

public class BeaconGroupSpinnerData {
	private BeaconGroup rootGroup;
	
	public BeaconGroupSpinnerData(BeaconGroup group) {
		this.rootGroup  = group;
	}
	
	@Override
	public String toString() {
		return this.rootGroup.getName();
	}
	
	public BeaconGroup getRootGroup() {
		return this.rootGroup;
	}
}
