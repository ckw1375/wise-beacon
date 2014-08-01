package com.wisewells.wisebeacon.topology;

import android.graphics.Point;

import com.wisewells.sdk.datas.Beacon;

public class LocationTopologyListData {
	private Beacon beacon;
	private Point point;
	
	public LocationTopologyListData(Beacon beacon) {
		this.beacon = beacon;
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}
	
	public Point getPoint() {
		return this.point;
	}
	
	public Beacon getBeacon() {
		return this.beacon;
	}
}
