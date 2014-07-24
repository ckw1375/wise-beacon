package com.wisewells.wisebeacon.beacongroup;

import com.wisewells.sdk.datas.UuidGroup;

public class BeaconGroupSpinnerData {
	private String code;
	private String name;
	
	public BeaconGroupSpinnerData(UuidGroup group) {
		this.code = group.getCode();
		this.name = group.getName();
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String getCode() {
		return this.code;
	}
}
