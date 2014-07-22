package com.wisewells.sdk.ipc;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableString implements Parcelable {

	private String str;
	
	public static final Parcelable.Creator<ParcelableString> CREATOR = new Creator<ParcelableString>() {
		
		@Override
		public ParcelableString[] newArray(int size) {
			return new ParcelableString[size];
		}
		
		@Override
		public ParcelableString createFromParcel(Parcel source) {
			return new ParcelableString(source);
		}
	};
	
	public ParcelableString(String str) {
		this.str = str;
	}
	
	private ParcelableString(Parcel in) {
		str = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(str);
	}

	@Override
	public String toString() {
		return str;
	}
}
