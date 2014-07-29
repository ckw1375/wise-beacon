package com.wisewells.wisebeacon.beacongroup;

import com.wisewells.sdk.datas.BeaconGroup;

public class BeaconGroupSpinnerData {
	private BeaconGroup uuidGroup;
	
	public BeaconGroupSpinnerData(BeaconGroup uuidGroup) {
		this.uuidGroup  = uuidGroup;
	}
	
	@Override
	public String toString() {
		return this.uuidGroup.getName();
	}
	
	public BeaconGroup getUuidGroup() {
		return this.uuidGroup;
	}
}
