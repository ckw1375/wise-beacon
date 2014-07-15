package com.wisewells.sdk.datas.group;

import android.os.Parcel;
import android.os.Parcelable;

public class MajorGroup extends BeaconGroup implements Parcelable{
	
	private int major;

	public static Parcelable.Creator<MajorGroup> CREATOR = new Creator<MajorGroup>() {
		
		@Override
		public MajorGroup[] newArray(int size) {
			return new MajorGroup[size];
		}
		
		@Override
		public MajorGroup createFromParcel(Parcel source) {
			return new MajorGroup(source);
		}
	};
	
	public MajorGroup(String name) {
		super(name);
	}
	
	private MajorGroup(Parcel p) {
		super(p);
		major = p.readInt();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(major);
	}

	/*
	 * Getter, Setter
	 */
	
	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}
}
