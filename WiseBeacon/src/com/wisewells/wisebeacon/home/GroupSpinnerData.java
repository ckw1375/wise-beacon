package com.wisewells.wisebeacon.home;

import com.wisewells.sdk.datas.UuidGroup;

public class GroupSpinnerData {
	private String code;
	private String name;
	
	public GroupSpinnerData(UuidGroup group) {
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
