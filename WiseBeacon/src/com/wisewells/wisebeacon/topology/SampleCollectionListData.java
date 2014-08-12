package com.wisewells.wisebeacon.topology;

public class SampleCollectionListData {
	private String name;
	private String number;
	private int sampleStateImage;
	private int sampleResetImage;
	
	public SampleCollectionListData(String name, String number, int sampleStateImage, int sampleResetImage) {
		this.name = name;
		this.number = number;
		this.sampleStateImage = sampleStateImage;
		this.sampleResetImage = sampleResetImage;
	}

	public void setSampleStateImage(int sampleStateImage) {
		this.sampleStateImage = sampleStateImage;
	}
	 
	public void setSampleResetImage(int sampleResetImage) {
		this.sampleResetImage = sampleResetImage;
	}
}
