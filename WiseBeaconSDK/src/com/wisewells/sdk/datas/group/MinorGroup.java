package com.wisewells.sdk.datas.group;

import java.util.ArrayList;

import com.wisewells.sdk.datas.Beacon;

import android.os.Parcel;
import android.os.Parcelable;

public class MinorGroup extends BeaconGroup implements Parcelable{
	
	private int minor;
	private ArrayList<String> beacons;

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
		p.readStringList(beacons);
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(minor);
		dest.writeStringList(beacons);
	}
	
	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public ArrayList<String> getBeacons() {
		return beacons;
	}

	public void setBeacons(ArrayList<String> beacons) {
		this.beacons = beacons;
	}
}
