package com.wisewells.sdk.datas;

import java.util.HashMap;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.utils.IpcUtils;

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
	
	public LocationTopology(String name) {
		super(name, Topology.TYPE_LOCATION);
		init();
	}

	public LocationTopology(Parcel p) {
		super(p);
		beaconLocations = (HashMap<String, Point>) IpcUtils.readMapFromParcel(p, Point.class.getClassLoader());
	}
	
	private void init() {
		beaconLocations = new HashMap<String, Point>();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		IpcUtils.writeMapToParcel(dest, beaconLocations);
	}
	
	public Point getCurrentPoint(RssiVector vector) {
		/*
		 * �̱���
		 */
		return null;
	}
	
	public void setBeaconPoint(String beaconCode, Point point) {
		beaconLocations.put(beaconCode, point);
	}

	@Override
	public String getTypeName() {
		return "Location";
	}
}
