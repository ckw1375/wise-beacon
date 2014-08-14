package com.wisewells.sdk.beacon;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class RssiVector implements Parcelable {
	private Double[] rssi;
	
	public static final Creator<RssiVector> CREATOR = new Creator<RssiVector>() {
		@Override
		public RssiVector[] newArray(int size) {
			return new RssiVector[size];
		}
		@Override
		public RssiVector createFromParcel(Parcel source) {
			return new RssiVector(source);
		}
	};

	public RssiVector(int size) {
		rssi = new Double[size];
	}
	
	private RssiVector(Parcel in) {
		rssi = (Double[]) in.readSerializable();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(rssi);
	}

	public int getSize() {
		return rssi.length;
	}

	public boolean setAll(ArrayList<Double> nRssi) {
		if (nRssi.size() != rssi.length)
			return false;
		for (int ind = 0; ind < rssi.length; ind++)
			rssi[ind] = nRssi.get(ind);
		return true;
	}

	public boolean set(int ind, Double nRssi) {
		if (ind < 0 || ind > rssi.length)
			return false;
		rssi[ind] = nRssi;
		return true;
	}

	public Double get(int ind) {
		if (ind < 0 || ind > rssi.length)
			return null;
		return rssi[ind];
	}

	// Calculate a squared distance to another rssi vector
	public Double sqDist(RssiVector x) {
		if (this.getSize() != x.getSize())
			return null;
		Double result = 0D;
		for (int i = 0; i < rssi.length; i++) {
			Double a = this.get(i);
			Double b = x.get(i);
			if (a != null && b != null) {
				result += Math.pow(a - b, 2);
			} else if (a == null && b == null) {
				continue;
			} else {
				return Double.valueOf(Double.POSITIVE_INFINITY);
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for(int i=0; i<rssi.length; i++) {
			sb.append(rssi[i] +",");
		}
		sb.append("]");
			
		return sb.toString();
	}
}
