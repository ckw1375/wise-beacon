package com.wisewells.sdk.beacon;

import android.os.Parcel;
import android.os.Parcelable;


public class UuidGroup extends BeaconGroup implements Parcelable {
	private String uuid;
	
	public static Parcelable.Creator<UuidGroup> CREATOR = new Creator<UuidGroup>() {

		@Override
		public UuidGroup[] newArray(int size) {
			return new UuidGroup[size];
		}

		@Override
		public UuidGroup createFromParcel(Parcel source) {
			return new UuidGroup(source);
		}
	};
	
	public UuidGroup(String name) {
		super(name);
	}

	private UuidGroup(Parcel p) {
		super(p);
		uuid = p.readString();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(uuid);
	}

	/*
	 * Getter, Setter
	 */

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
