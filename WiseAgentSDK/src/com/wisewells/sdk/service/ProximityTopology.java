package com.wisewells.sdk.service;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.BeaconTracker;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.beacon.DistanceVector;
import com.wisewells.sdk.beacon.Region;

public class ProximityTopology extends Topology implements Parcelable {
	
	private BeaconVector mBeaconVector;
	private Double[] mRanges; //Ranges in meters
	
	public static final Creator<ProximityTopology> CREATOR = new Creator<ProximityTopology>() {
		@Override
		public ProximityTopology[] newArray(int size) {
			return new ProximityTopology[size];
		}
		@Override
		public ProximityTopology createFromParcel(Parcel source) {
			return new ProximityTopology(source);
		}
	};
	
	public ProximityTopology(int id, int type, String groupCode, 
			String serviceCode, String updateDate, String updateTime, 
			BeaconVector beaconVector, ArrayList<Double> ranges) {
		
		super(id, type, groupCode, serviceCode, updateDate, updateTime);
		mBeaconVector = beaconVector;
		
		int i = 0;
		for(double range : ranges) {
			mRanges[i] = range;
			i++;
		}
	}
	
	private ProximityTopology(Parcel in) {
		super(in);
		mRanges = (Double[]) in.readSerializable();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeSerializable(mRanges);
	}
	
	public Double getRange(Region beacon)
	{
		int ind = mBeaconVector.indexOf(beacon);
		return (ind >= 0) ? mRanges[ind] : null;
	}
	
	public boolean setRange(Region beacon, Double nRange)
	{
		int ind = mBeaconVector.indexOf(beacon);
		if(ind == -1) return false;
		mRanges[ind] = nRange;
		return true;
	}
	
	@Override
	public Region getResult() {
		Double minDist = Double.valueOf(Double.POSITIVE_INFINITY); 
		Region result = null;
		DistanceVector dv = mTracker.getAvgDist(mBeaconVector);
		ArrayList<Boolean> nb = mTracker.isNearby(mBeaconVector);
				
		for(int ind = 0; ind < mBeaconVector.getSize(); ind++) {
			if(nb.get(ind)) {
				Double dist = dv.get(ind);
				if(dist.compareTo(mRanges[ind]) <= 0 && dist.compareTo(minDist) <= 0) {
					result = mBeaconVector.get(ind);					
					minDist = dist;
				}				
			}
		}
		return result;
	}
	
	@Override
	public String getTypeName() {
		return "Proximity";
	}
}
