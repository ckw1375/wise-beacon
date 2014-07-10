package com.wisewells.sdk.datas.topology;

import java.util.HashMap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.RssiVector;
import com.wisewells.sdk.utils.Utils;

public class ProximityTopology extends Topology implements Parcelable {
	private HashMap<String, Double> searchRanges;

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
	
	public ProximityTopology(String code, String name) {
		super(code, name);
		searchRanges = new HashMap<String, Double>();
	}
	
	private ProximityTopology(Parcel p) {
		super(p);
		searchRanges = 
	}

	private void init() {
		searchRanges = new HashMap<String, Double>();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		Utils.writeMapToParcel(dest, map);
	}
	
	public void setSearchRange(String beaconCode, double searchRange) {
		searchRanges.putDouble(beaconCode, searchRange);
	}
	
	public Beacon getNearestBeacon(RssiVector vector) {
		/*
		 * ¹Ì±¸Çö
		 */
		
		return null;
	}
}
