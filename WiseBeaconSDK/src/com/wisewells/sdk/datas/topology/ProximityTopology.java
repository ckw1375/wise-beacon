package com.wisewells.sdk.datas.topology;

import java.util.ArrayList;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.RssiVector;

public class ProximityTopology extends Topology {
	private ArrayList<Proximity> proximities;	
	
	public ProximityTopology() {
		init();
	}

	private void init() {
		proximities = new ArrayList<Proximity>();
	}
	
	public void setSearchRange(Beacon beacon, double searchRange) {
		proximities.add(new Proximity(beacon, searchRange));
	}
	
	public Beacon getNearestBeacon(RssiVector vector) {
		/*
		 * ¹Ì±¸Çö
		 */
		
		return null;
	}
}
