package com.wisewells.wisebeacon.topology;

import com.wisewells.sdk.service.Sector;

public class SampleCollectionListData {
	private Sector sector;
	private String name;
	private int number;
	private int sampleStateImage;
	private int sampleResetImage;
	
	public SampleCollectionListData(Sector sector, int sampleStateImage, int sampleResetImage) {
		this.sector = sector;
		this.name = sector.getName();
		this.number = sector.getSampleNumber();
		this.sampleStateImage = sampleStateImage;
		this.sampleResetImage = sampleResetImage;
	}

	public void setSampleStateImage(int sampleStateImage) {
		this.sampleStateImage = sampleStateImage;
	}
	 
	public void setSampleResetImage(int sampleResetImage) {
		this.sampleResetImage = sampleResetImage;
	}

	public Sector getSector() {
		return this.sector;
	}

	public String getName() {
		return this.name;
	}

	public int getNumber() {
		return this.number;
	}

	public int getSampleStateImage() {
		return this.sampleStateImage;
	}

	public int getSampleResetImage() {
		return this.sampleResetImage;
	}
	
	public void updateNumber() {
		this.number = this.sector.getSampleNumber();
	}
}
