package com.wisewells.sdk.service;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.BeaconTracker;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.beacon.DistanceVector;
import com.wisewells.sdk.beacon.Region;

public class ProximityTopology extends Topology implements Parcelable {
	
	private Double[] mRange; //Ranges in meters
	private int mSize;
	
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
	
	public ProximityTopology(BeaconVector beaconVector, BeaconTracker tracker) {
		super(TYPE_PROXIMITY, beaconVector, tracker);	
		mSize = mBeaconVector.getSize();
		mRange = new Double[mSize];
		for(int ind = 0; ind < mSize; ind++)
			mRange[ind] = Double.valueOf(Double.POSITIVE_INFINITY);
	}
	
	public ProximityTopology(BeaconVector beaconVector, Double[] ranges, BeaconTracker tracker) {
		super(TYPE_PROXIMITY, beaconVector, tracker);
		mRange = ranges;
	}
	
	private ProximityTopology(Parcel in) {
		super(in);
		mRange = (Double[]) in.readSerializable();
		mSize = in.readInt();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeSerializable(mRange);
		dest.writeInt(mSize);
	}
	
	public Double getRange(Region beacon)
	{
		int ind = mBeaconVector.indexOf(beacon);
		return (ind >= 0) ? mRange[ind] : null;
	}
	
	public boolean setRange(Region beacon, Double nRange)
	{
		int ind = mBeaconVector.indexOf(beacon);
		if(ind == -1) return false;
		mRange[ind] = nRange;
		return true;
	}
	
	public Region getResult() {
		Double minDist = Double.valueOf(Double.POSITIVE_INFINITY); 
		Region result = null;
		DistanceVector dv = mTracker.getAvgDist(mBeaconVector);
		ArrayList<Boolean> nb = mTracker.isNearby(mBeaconVector);
				
		for(int ind = 0; ind < mSize; ind++) {
			if(nb.get(ind)) {
				Double dist = dv.get(ind);
				if(dist.compareTo(mRange[ind]) <= 0 && dist.compareTo(minDist) <= 0) {
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
