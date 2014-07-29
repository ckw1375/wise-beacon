package com.wisewells.sdk.datas;

import java.util.HashMap;
import java.util.Set;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.utils.IpcUtils;

public class ProximityTopology extends Topology implements Parcelable {
	private HashMap<String, Range> searchRanges;

	public static Parcelable.Creator<ProximityTopology> CREATOR = new Creator<ProximityTopology>() {
		
		@Override
		public ProximityTopology[] newArray(int size) {
			return new ProximityTopology[size];
		}
		
		@Override
		public ProximityTopology createFromParcel(Parcel source) {
			return new ProximityTopology(source);
		}
	};
	
	public ProximityTopology(String name) {
		super(name, Topology.TYPE_PROXIMITY);
		init();
	}
	
	private ProximityTopology(Parcel p) {
		super(p);
		searchRanges = (HashMap<String, Range>) IpcUtils.readMapFromParcel(p, Range.class.getClassLoader());
	}

	private void init() {
		searchRanges = new HashMap<String, Range>();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		IpcUtils.writeMapToParcel(dest, searchRanges);
	}
	
	public void setSearchRange(String beaconCode, double searchRange) {
		searchRanges.put(beaconCode, new Range(searchRange));
	}
	
	public Beacon getNearestBeacon(RssiVector vector) {
		/*
		 * �̱���
		 */
		
		return null;
	}
	
	
}

class Range implements Parcelable {

	double range;
	
	public static Parcelable.Creator<Range> CREATOR = new Creator<Range>() {
		@Override
		public Range[] newArray(int size) {
			return new Range[size];
		}
		
		@Override
		public Range createFromParcel(Parcel source) {
			return new Range(source);
		}
	};
	
	public Range(double range) {
		this.range = range;
	}
	
	private Range(Parcel in) {
		range = in.readDouble();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(range);
	}
	
	public double getRange() {
		return this.range;
	}
}
