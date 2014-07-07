package com.wisewells.sdk.datas.topology;

import com.wisewells.sdk.datas.Beacon;

import android.os.Parcel;
import android.os.Parcelable;

public class Proximity implements Parcelable {

	private double searchRange;
	private Beacon beacon;
	
	public static Parcelable.Creator<Proximity> CREATOR = new Creator<Proximity>() {
		
		@Override
		public Proximity[] newArray(int size) {
			return new Proximity[size];
		}
		
		@Override
		public Proximity createFromParcel(Parcel source) {
			return new Proximity(source);
		}
	};
	
	public Proximity() {
		
	}
	
	private Proximity(Parcel p) {
		searchRange = p.readDouble();
		beacon = p.readParcelable(Beacon.class.getClassLoader());
	}
	
	public Proximity(Beacon beacon, double searchRange) {
		this.beacon = beacon;
		this.searchRange = searchRange;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(searchRange);
		dest.writeParcelable(beacon, 0);
	}

	/*
	 * Getter, Setter
	 */
	public double getSearchRange() {
		return searchRange;
	}

	public void setSearchRange(double searchRange) {
		this.searchRange = searchRange;
	}

	public Beacon getBeacon() {
		return beacon;
	}

	public void setBeacon(Beacon beacon) {
		this.beacon = beacon;
	}
}
