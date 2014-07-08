package com.wisewells.sdk.datas.group;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.WiseObjects;
import com.wisewells.sdk.datas.Beacon;

public class MinorGroup extends BeaconGroup implements Parcelable{
	
	private int minor;
	private ArrayList<String> beaconCodes;

	public static Parcelable.Creator<MinorGroup> CREATOR = new Creator<MinorGroup>() {
		
		@Override
		public MinorGroup[] newArray(int size) {
			return new MinorGroup[size];
		}
		
		@Override
		public MinorGroup createFromParcel(Parcel source) {
			return new MinorGroup(source);
		}
	};
	
	public MinorGroup() {
		
	}
	
	private MinorGroup(Parcel p) {
		super(p);
		minor = p.readInt();
		p.readStringList(beaconCodes);
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(minor);
		dest.writeStringList(beaconCodes);
	}

	public ArrayList<Beacon> getBeacons() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		
		for(String beaconCode : beaconCodes) {
			Beacon b = WiseObjects.getInstance().getBeacon(beaconCode);
			beacons.add(b);
		}
		
		return beacons;
	}
	
	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public ArrayList<String> getBeaconCodes() {
		return beaconCodes;
	}

	public void setBeacons(ArrayList<String> beaconCodes) {
		this.beaconCodes = beaconCodes;
	}
}
