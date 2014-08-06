package com.wisewells.sdk.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.BeaconTracker;
import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.utils.L;

public abstract class Topology implements Parcelable {

	public static final int TYPE_PROXIMITY = 1;
	public static final int TYPE_SECTOR = 2;
	public static final int TYPE_LOCATION = 3;
	
	protected String mCode;
	protected String mBeaconGroupCode;
	protected String mServiceCode;
	protected int mType;
	
	protected BeaconVector mBeaconVector;
	protected BeaconTracker mTracker;
	
	protected Topology(int type, BeaconVector beaconVector, BeaconTracker tracker) {
		
		mType = type;
		mBeaconVector = beaconVector;
		mTracker = tracker;
	}

	protected Topology(Parcel p) {
		mCode = p.readString();
		mBeaconGroupCode = p.readString();
		mServiceCode = p.readString();
		mType = p.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		L.w(getClass().getName());
		dest.writeString(mCode);
		dest.writeString(mBeaconGroupCode);
		dest.writeString(mServiceCode);
		dest.writeInt(mType);
	}

	public void attachTo(BeaconGroup bg) {
		mBeaconGroupCode = bg.getCode();
		bg.addTopologyCode(mCode);
	}
	
	public void attachTo(Service service) {
		mServiceCode = service.getCode();
		service.setTopologyCode(mCode);
	}

	public String getCode() {
		return mCode;
	}

	public void setCode(String code) {
		this.mCode = code;
	}

	public String getBeaconGroupCode() {
		return mBeaconGroupCode;
	}

	/**
	 * 직접 호출 되지 않고 BeaconGroup의 attachTo함수를 통해 수행된다.
	 * @param beaconGroupCode
	 */
	public void setBeaconGroupCode(String beaconGroupCode) {
		this.mBeaconGroupCode = beaconGroupCode;
	}

	public String getServiceCode() {
		return mServiceCode;
	}

	/**
	 * 직접 호출 되지 않고 Service의 attachTo 함수를 통해 수행된다.
	 * @param serviceCode
	 */
	public void setServiceCode(String serviceCode) {
		this.mServiceCode = serviceCode;
	}
	
	public void setBeaconVector(BeaconVector beaconVector) {
		this.mBeaconVector = beaconVector;
	}
	
	public int getType() {
		return this.mType;
	}
	
	public abstract String getTypeName();
	public abstract Object getResult();
}
