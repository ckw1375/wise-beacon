package com.wisewells.wisebeacon.topology;

import com.wisewells.sdk.datas.Beacon;

public class ProximityTopologyListData {
	private Beacon beacon;
	private double range;
	
	public ProximityTopologyListData(Beacon beacon) {
		this.beacon = beacon;
	}
	
	public void setRange(double range) {
		this.range = range;
	}
	
	public double getRange() {
		return this.range;
	}
	
	public Beacon getBeacon() {
		return this.beacon;
	}
}
