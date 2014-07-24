package com.wisewells.sdk.datas;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class MinorGroup extends BeaconGroup implements Parcelable{
	
	private int minor;

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
	
	public MinorGroup(String name) {
		super(name);
	}

	private MinorGroup(Parcel p) {
		super(p);
		minor = p.readInt();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(minor);
	}
	
	public void addBeacon(Beacon b) {
		childCodes.add(b.getCode());
		b.setBeaconGroupCode(this.code);
	}

	
	public ArrayList<Beacon> getBeacons() {
		throw new RuntimeException("getBeacons is not completed method");
		
//		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
//		
//		/*for(String beaconCode : childCodes) {
//			Beacon b = WiseObjects.getInstance().getBeacon(beaconCode);
//			beacons.add(b);
//		}*/
//		
//		return beacons;
	}
	
	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}
}
