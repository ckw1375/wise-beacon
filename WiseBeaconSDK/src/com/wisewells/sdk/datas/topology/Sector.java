package com.wisewells.sdk.datas.topology;

import java.util.ArrayList;

import com.wisewells.sdk.datas.RssiVector;

public class Sector {
	private String name;
	private ArrayList<RssiVector> sectorSamples;
	
	public Sector() {
		init();
	}	
	
	private void init() {
		sectorSamples = new ArrayList<RssiVector>();
	}
	
	public void addSectorSample(RssiVector vector) {
		sectorSamples.add(vector);
	}
}
