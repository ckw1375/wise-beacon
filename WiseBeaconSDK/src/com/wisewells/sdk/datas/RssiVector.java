package com.wisewells.sdk.datas;

public class RssiVector {
	private double[] rssies;
	
	public RssiVector(int size) {
		rssies = new double[size];
	}
	
	public RssiVector(double... values) {
		rssies = new double[values.length];
		
		for(int i=0; i<values.length; i++) 
			rssies[i] = values[i];
	}
	
	public double[] getRssies() {
		return rssies;
	}
}
