package com.wisewells.sdk.beacon;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class BeaconVector implements Parcelable {
	private Region[] beacons;
	
	public static final Creator<BeaconVector> CREATOR = new Creator<BeaconVector>() {
		@Override
		public BeaconVector[] newArray(int size) {
			return new BeaconVector[size];
		}
		@Override
		public BeaconVector createFromParcel(Parcel source) {
			return new BeaconVector(source);
		}
	};

	public BeaconVector(int size) {
		beacons = new Region[size];
	}

	private boolean checkValidity(Region r) {
		if (r == null || r.getProximityUUID() == null || r.getMajor() == null || r.getMinor() == null)
			return false;
		return true;
	}

	public int getSize() {
		return beacons.length;
	}

	public boolean setAll(ArrayList<Region> nBeacons) {
		if (nBeacons.size() != beacons.length)
			return false;
		for (Region it : nBeacons)
			if (checkValidity(it) == false)
				return false;
		for (int ind = 0; ind < beacons.length; ind++)
			beacons[ind] = nBeacons.get(ind);
		return true;
	}

	public boolean set(int ind, Region nBeacon) {
		if (ind < 0 || ind > beacons.length)
			return false;
		if (checkValidity(nBeacon) == false)
			return false;
		beacons[ind] = nBeacon;
		return true;
	}

	public Region get(int ind) {
		if (ind < 0 || ind > beacons.length)
			return null;
		return beacons[ind];
	}

	public int indexOf(Region r) {
		for (int ind = 0; ind < beacons.length; ind++)
			if (beacons[ind].equals(r))
				return ind;
		return -1;
	}
	
	private BeaconVector(Parcel in) {
		in.readTypedArray(beacons, Region.CREATOR);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedArray(beacons, 0);
	}
}
