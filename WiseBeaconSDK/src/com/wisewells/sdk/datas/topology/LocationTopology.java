package com.wisewells.sdk.datas.topology;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.RssiVector;

public class LocationTopology extends Topology implements Parcelable {
	private Bundle beaconLocations;
	
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
	
	public LocationTopology(Parcel p) {
		beaconLocations = p.readBundle();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeBundle(beaconLocations);
	}
	
	public Point getCurrentPoint(RssiVector vector) {
		/*
		 * ¹Ì±¸Çö
		 */
		return null;
	}
	
	public void setBeaconPoint(String beaconCode, Point point) {
		beaconLocations.putParcelable(beaconCode, point);
	}
}
