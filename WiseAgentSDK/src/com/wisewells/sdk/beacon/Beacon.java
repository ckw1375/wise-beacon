package com.wisewells.sdk.beacon;

import android.os.Parcel;
import android.os.Parcelable;

import com.estimote.sdk.internal.Objects;
import com.wisewells.sdk.utils.BeaconUtils;
import com.wisewells.sdk.utils.L;


public class Beacon implements Parcelable {

	// static values(stored data)
	private String mCode;
	private String mName;
	private String mBeaconGroupCode;
	private String mMacAddress;
	private String mProximityUUID;
	private int mMajor;
	private int mMinor;
	private double mBattery;
	private double mTxPower;
	private double mMeasuredPower;
	private double mInterval;
	private String mMaker;
	private String mImage;
	private String mUpdateDate;
	private String mUpdateTime;

	// dynamic values(not sotred data)
	private double mRssi;
	private double mDistance;

	public static final Parcelable.Creator<Beacon> CREATOR = new Parcelable.Creator<Beacon>() {
		public Beacon createFromParcel(Parcel source) {
			return new Beacon(source);
		}
		public Beacon[] newArray(int size) {
			return new Beacon[size];
		}
	};

	public Beacon(String proximityUUID, String name, String macAddress, 
			int major, int minor, double measuredPower, double rssi, double distance) {
		mProximityUUID = BeaconUtils.normalizeProximityUUID(proximityUUID);
		mName = name;
		mMacAddress = macAddress;
		mMajor = major;
		mMinor = minor;
		mMeasuredPower = measuredPower;
		rssi = rssi;
		distance = distance;
	}

	public Beacon(String macAddress, String uuid, int major, int minor, double measuredPower, double rssi) {
		super();
		mMacAddress = macAddress;
		mProximityUUID = uuid;
		mMajor = major;
		mMinor = minor;
		mMeasuredPower = measuredPower;
		rssi = rssi;
	}
	
	public Beacon(String code, String name, String groupCode, String macAddress, 
			String uuid, int major, int minor, double battery, double txPower,
			double measuredPower, double interval, String maker, String image,
			String updateDate, String updateTime) {
		
		mCode = code;
		mName = name;
		mBeaconGroupCode = groupCode;
		mMacAddress = macAddress;
		mProximityUUID = uuid;
		mMajor = major;
		mMinor = minor;
		mBattery = battery;
		mTxPower = txPower;
		mMeasuredPower = measuredPower;
		mInterval = interval;
		mMaker = maker;
		mImage = image;
		mUpdateDate = updateDate;
		mUpdateTime = updateTime;
	}

	private Beacon(Parcel in) {
		mCode = in.readString();
		mName = in.readString();
		mBeaconGroupCode = in.readString();
		mMacAddress = in.readString();
		mProximityUUID = in.readString();	   
		mMajor = in.readInt();
		mMinor = in.readInt();
		mBattery = in.readDouble();
		mTxPower = in.readDouble();
		mMeasuredPower = in.readDouble();
		mInterval = in.readDouble();
		mMaker = in.readString();
		mImage = in.readString();
		mRssi = in.readDouble();
		mDistance = in.readDouble();
		mUpdateDate = in.readString();
		mUpdateTime = in.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mCode);
		dest.writeString(mName);
		dest.writeString(mBeaconGroupCode);
		dest.writeString(mMacAddress);
		dest.writeString(mProximityUUID);		
		dest.writeInt(mMajor);
		dest.writeInt(mMinor);
		dest.writeDouble(mBattery);
		dest.writeDouble(mTxPower);
		dest.writeDouble(mMeasuredPower);
		dest.writeDouble(mInterval);
		dest.writeString(mMaker);
		dest.writeString(mImage);
		dest.writeDouble(mRssi);
		dest.writeDouble(mDistance);
		dest.writeString(mUpdateDate);
		dest.writeString(mUpdateTime);
	}

	public String getProximityUUID() {
		return mProximityUUID;
	}

	public String getName() {
		return mName;
	}

	public String getMacAddress() {
		return mMacAddress;
	}

	public String getCode() {
		return mCode;
	}

	public int getMajor() {
		return mMajor;
	}

	public int getMinor() {
		return mMinor;
	}

	public double getMeasuredPower() {
		return mMeasuredPower;
	}

	public double getRssi() {
		return mRssi;
	}

	public double getDistance() {
		return mDistance;
	}
	
	public void setDistance(double distance) {
		distance = distance;
	}

	public Region getRegion() {
		return new Region(mProximityUUID, mMajor, mMinor);
	}

	public void setCode(String code) {
		mCode = code;
	}

	public void setName(String name) {
		mName = name;
	}

	/**
	 * 이 함수는 직접 사용되지 않는다
	 * MinorGroup에 addBeacon을 통해 beacon을 추가하면  추가된다.
	 * @param code
	 */
	 void setBeaconGroupCode(String code) {
		 mBeaconGroupCode = code;
	 }

	 public String toString() {
		 return Objects.toStringHelper(this).add("macAddress", mMacAddress).add("proximityUUID", mProximityUUID).add("major", mMajor).add("minor", mMinor).add("measuredPower", mMeasuredPower).add("rssi", mRssi).toString();
	 }

	 public boolean equals(Object o) {
		 if (this == o) return true;
		 if ((o == null) || (getClass() != o.getClass())) return false;

		 Beacon beacon = (Beacon)o;

		 if (mMajor != beacon.mMajor) return false;
		 if (mMinor != beacon.mMinor) return false;
		 return mProximityUUID.equals(beacon.mProximityUUID);
	 }

	 public int hashCode() {
		 int result = mProximityUUID.hashCode();
		 result = 31 * result + mMajor;
		 result = 31 * result + mMinor;
		 return result;
	 }

	 public void setHadwareAddress() {
		 L.w("Beacon Address set [ UUID : " + mProximityUUID + " major : " + mMajor + " minor : " + mMinor + "]");
	 }
}
