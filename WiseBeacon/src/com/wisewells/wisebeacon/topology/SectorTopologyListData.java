package com.wisewells.wisebeacon.topology;

import com.wisewells.sdk.beacon.Beacon;

public class SectorTopologyListData {
	private Beacon beacon;
	private double range;
	
	public SectorTopologyListData(Beacon beacon) {
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
