package com.wisewells.sdk.beacon;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DistanceVector implements Parcelable {
	private Double[] dist;
	private int size;

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
	
	public DistanceVector(int nSize) {
		size = nSize;
		dist = new Double[size];
	}

	public int getSize() {
		return size;
	}

	public boolean setAll(ArrayList<Double> nDist) {
		if (nDist.size() != size)
			return false;
		for (int ind = 0; ind < size; ind++)
			dist[ind] = nDist.get(ind);
		return true;
	}

	public boolean set(int ind, Double nDist) {
		if (ind < 0 || ind > size)
			return false;
		dist[ind] = nDist;
		return true;
	}

	public Double get(int ind) {
		if (ind < 0 || ind > size)
			return null;
		return dist[ind];
	}

	// Calculate a squared distance to another distance vector
	public Double sqDist(DistanceVector x) {
		if (this.getSize() != x.getSize())
			return null;
		Double result = 0D;
		for (int i = 0; i < size; i++) {
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
		dist = (Double[]) in.readArray(Double.class.getClassLoader());
		size = in.readInt();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeArray(dist);
		dest.writeInt(size);
	}
}
