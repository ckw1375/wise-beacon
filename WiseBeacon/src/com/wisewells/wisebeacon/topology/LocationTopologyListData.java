package com.wisewells.wisebeacon.topology;

import android.graphics.Point;

import com.wisewells.sdk.datas.Beacon;

public class LocationTopologyListData {
	private String beaconCode;
	private String beaconName;
	private Point point;
	
	public LocationTopologyListData(Beacon beacon) {
		this.beaconCode = beacon.getCode();
		this.beaconName = beacon.getName();
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}
	
	public String getBeaconCode() {
		return this.beaconCode;
	}
	
	public Point getPoint() {
		return this.point;
	}
	
	public String getBeaconName() {
		return this.beaconName;
	}
}
