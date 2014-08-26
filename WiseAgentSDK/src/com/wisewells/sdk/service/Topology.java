package com.wisewells.sdk.service;

import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.BeaconTracker;
import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.utils.L;

public abstract class Topology implements Parcelable {

	public static final int TYPE_PROXIMITY = 1;
	public static final int TYPE_LOCATION = 2;
	public static final int TYPE_SECTOR = 3;

	// store DB
	protected int mId;
	protected int mType;
	protected String mBeaconGroupCode;
	protected String mServiceCode;
	protected String mUpdateDate;
	protected String mUpdateTime;
	
	/**
	 * BeaconVector, BeaconTracker는 Agent에서 생성된다.
	 */
	protected BeaconVector mBeaconVector;
	protected BeaconTracker mTracker;
	
//	protected Topology(int type, BeaconVector beaconVector) {
//		
//		mType = type;
//		mBeaconVector = beaconVector;
//	}
	
	protected Topology(int id, int type, String groupCode, String serviceCode, String updateDate, String updateTime) {
		mId = id;
		mType = type;
		mBeaconGroupCode = groupCode;
		mServiceCode = serviceCode;
		mUpdateDate = updateDate;
		mUpdateTime = updateTime;
	}

	protected Topology(Parcel in) {
		mId= in.readInt();
		mType = in.readInt();
		mBeaconGroupCode = in.readString();
		mServiceCode = in.readString();
		mUpdateDate = in.readString();
		mUpdateTime = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		L.w(getClass().getName());
		dest.writeInt(mId);
		dest.writeInt(mType);
		dest.writeString(mBeaconGroupCode);
		dest.writeString(mServiceCode);
		dest.writeString(mUpdateDate);
		dest.writeString(mUpdateTime);
	}

	public int getId() {
		return mId;
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
	
	public void setBeaconTracker(BeaconTracker tracker) {
		mTracker = tracker;
	}
	
	public int getType() {
		return this.mType;
	}
	
	public abstract String getTypeName();
	public abstract Object getResult();
}
