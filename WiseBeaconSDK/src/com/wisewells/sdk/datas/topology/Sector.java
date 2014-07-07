package com.wisewells.sdk.datas.topology;

import java.util.ArrayList;
import java.util.List;

import com.wisewells.sdk.datas.RssiVector;

public class Sector {
	private String name;
	private ArrayList<RssiVector> sectorSamples;
	
	public Sector() {
		init();
	}
	
	public Sector(String name) {
		init();
		this.name = name;
	}
	
	private void init() {
		sectorSamples = new ArrayList<RssiVector>();
	}
	
	public void addSectorSample(RssiVector vector) {
		sectorSamples.add(vector);
	}

	public void addSectorSamples(List<RssiVector> vectors) {		
		sectorSamples.addAll(vectors);
	}
	
	public void clearSectorSamples() {
		sectorSamples.clear();
	}
	
	public String getName() {
		return this.name;
	}

}
