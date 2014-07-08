package com.wisewells.sdk.datas.topology;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.RssiVector;

public class ProximityTopology extends Topology implements Parcelable {
	private Bundle searchRanges;

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

	public ProximityTopology() {
		init();
	}
	
	private ProximityTopology(Parcel p) {
		searchRanges = p.readBundle();
	}

	private void init() {
		searchRanges = new Bundle();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeBundle(searchRanges);
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
