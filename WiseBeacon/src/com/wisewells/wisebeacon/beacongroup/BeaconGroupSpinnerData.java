package com.wisewells.wisebeacon.beacongroup;

import com.wisewells.sdk.datas.UuidGroup;

/**
 * @file	BeaconGroupSpinnerData.java
 * @author 	Mingook
 * @date	2014. 7. 24.
 * @description
 * UuidGroup�� toString�� ���� Ŭ����
 */

public class BeaconGroupSpinnerData {
	private UuidGroup uuidGroup;
	
	public BeaconGroupSpinnerData(UuidGroup group) {
		this.uuidGroup  = group;
	}
	
	@Override
	public String toString() {
		return this.uuidGroup.getName();
	}
	
	public UuidGroup getUuidGroup() {
		return this.uuidGroup;
	}
}
