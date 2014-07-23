package com.wisewells.wisebeacon.topology;

import android.graphics.Point;

public class LocationTopologyListData {
	private String beaconCode;
	private Point point;
	
	public LocationTopologyListData(String beaconCode) {
		this.beaconCode = beaconCode;		
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
}
