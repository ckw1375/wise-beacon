package com.wisewells.sdk.datas.topology;

import java.util.HashMap;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.RssiVector;
import com.wisewells.sdk.utils.Utils;

public class LocationTopology extends Topology implements Parcelable {
	private HashMap<String, Point> beaconLocations;
	
	public static Parcelable.Creator<LocationTopology> CREATOR = new Creator<LocationTopology>() {
		
		@Override
		public LocationTopology[] newArray(int size) {
			return new LocationTopology[size];
		}
		
		@Override
		public LocationTopology createFromParcel(Parcel source) {
			return new LocationTopology(source);
		}
	};
	
	public LocationTopology(String code, String name) {
		super(code, name);
		beaconLocations = new HashMap<String, Point>();
	}

	public LocationTopology(Parcel p) {
		super(p);
		beaconLocations = (HashMap<String, Point>) Utils.readMapFromParcel(p, Point.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		Utils.writeMapToParcel(dest, beaconLocations);
	}
	
	public Point getCurrentPoint(RssiVector vector) {
		/*
		 * ¹Ì±¸Çö
		 */
		return null;
	}
	
	public void setBeaconPoint(String beaconCode, Point point) {
		beaconLocations.put(beaconCode, point);
	}
}
