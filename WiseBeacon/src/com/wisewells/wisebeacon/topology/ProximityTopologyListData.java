package com.wisewells.wisebeacon.topology;

import com.wisewells.sdk.beacon.Beacon;

public class ProximityTopologyListData {
	private Beacon beacon;
	private Double range;
	
	public ProximityTopologyListData(Beacon beacon) {
		this.beacon = beacon;	
		this.range = Double.POSITIVE_INFINITY;
	}
	
	public void setRange(Double range) {
		this.range = range;
	}
	
	public Double getRange() {
		return this.range;
	}
	
	public Beacon getBeacon() {
		return this.beacon;
	}
}
