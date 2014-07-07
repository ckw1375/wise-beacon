package com.wisewells.sdk.datas.group;

import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.wisewells.sdk.utils.L;


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
	
	public UuidGroup() {
		L.i("UuidGroup »ý¼º");
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
