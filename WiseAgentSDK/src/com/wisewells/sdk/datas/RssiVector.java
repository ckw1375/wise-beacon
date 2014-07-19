package com.wisewells.sdk.datas;

import android.os.Parcel;
import android.os.Parcelable;

public class RssiVector implements Parcelable {
	private double[] rssies;
	
	public static Parcelable.Creator<RssiVector> CREATOR = new Creator<RssiVector>() {
		
		@Override
		public RssiVector[] newArray(int size) {
			return new RssiVector[size];
		}
		
		@Override
		public RssiVector createFromParcel(Parcel source) {
			return new RssiVector(source);
		}
	};
	
	public RssiVector(int size) {
		rssies = new double[size];
	}
	
	private RssiVector(Parcel p) {
		p.readDoubleArray(rssies);
	}
	
	public RssiVector(double... values) {
		rssies = new double[values.length];
		
		for(int i=0; i<values.length; i++) 
			rssies[i] = values[i];
	}
	
	public double[] getRssies() {
		return rssies;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDoubleArray(rssies);
	}
}
