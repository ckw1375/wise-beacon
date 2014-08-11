package com.wisewells.sdk.beacon;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DistanceVector implements Parcelable {
	private Double[] dist;

	public static final Creator<DistanceVector> CREATOR = new Creator<DistanceVector>() {
		@Override
		public DistanceVector[] newArray(int size) {
			return new DistanceVector[size];
		}
		@Override
		public DistanceVector createFromParcel(Parcel source) {
			return new DistanceVector(source);
		}
	};
	
	public DistanceVector(int size) {
		dist = new Double[size];
	}

	public int getSize() {
		return dist.length;
	}

	public boolean setAll(ArrayList<Double> nDist) {
		if (nDist.size() != dist.length)
			return false;
		for (int ind = 0; ind < dist.length; ind++)
			dist[ind] = nDist.get(ind);
		return true;
	}

	public boolean set(int ind, Double nDist) {
		if (ind < 0 || ind > dist.length)
			return false;
		dist[ind] = nDist;
		return true;
	}

	public Double get(int ind) {
		if (ind < 0 || ind > dist.length)
			return null;
		return dist[ind];
	}

	// Calculate a squared distance to another distance vector
	public Double sqDist(DistanceVector x) {
		if (this.getSize() != x.getSize())
			return null;
		Double result = 0D;
		for (int i = 0; i < dist.length; i++) {
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

	private DistanceVector(Parcel in) {
		dist = (Double[]) in.readSerializable();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(dist);
	}
}
