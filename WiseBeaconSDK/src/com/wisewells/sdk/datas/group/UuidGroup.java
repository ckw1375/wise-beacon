package com.wisewells.sdk.datas.group;

import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;


public class UuidGroup extends BeaconGroup implements Parcelable {
	private UUID uuid;
	
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

	public UuidGroup(String code, String name, String uuid) {
		super(code, name);
		this.uuid = UUID.fromString(uuid);
	}

	public UuidGroup(Parcel p) {
		super(p);
		uuid = (UUID) p.readSerializable();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeSerializable(uuid);
	}

	/*
	 * Getter, Setter
	 */

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
}
