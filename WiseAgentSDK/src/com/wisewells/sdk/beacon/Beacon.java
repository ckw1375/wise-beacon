package com.wisewells.sdk.beacon;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.estimote.sdk.internal.Objects;
import com.wisewells.sdk.utils.BeaconUtils;
import com.wisewells.sdk.utils.L;


public class Beacon implements Parcelable {

	// static values(stored data)
	private String code;
	private String name;
	private String beaconGroupCode;
	private String macAddress;
	private String proximityUUID;
	private int major;
	private int minor;
	private double battery;
	private double txPower;
	private double measuredPower;
	private double interval;
	private String maker;
	private String image;
	private String updateDate;
	private String updateTime;

	// dynamic values(not sotred data)
	private double rssi;
	private double distance;

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
		this.proximityUUID = BeaconUtils.normalizeProximityUUID(proximityUUID);
		this.name = name;
		this.macAddress = macAddress;
		this.major = major;
		this.minor = minor;
		this.measuredPower = measuredPower;
		this.rssi = rssi;
		this.distance = distance;
	}

	public Beacon(String macAddress, String uuid, int major, int minor, double measuredPower, double rssi) {
		super();
		this.macAddress = macAddress;
		this.proximityUUID = uuid;
		this.major = major;
		this.minor = minor;
		this.measuredPower = measuredPower;
		this.rssi = rssi;
	}

	private Beacon(Parcel in) {
		this.code = in.readString();
		this.name = in.readString();
		this.beaconGroupCode = in.readString();
		this.macAddress = in.readString();
		this.proximityUUID = in.readString();	   
		this.major = in.readInt();
		this.minor = in.readInt();
		this.battery = in.readDouble();
		this.txPower = in.readDouble();
		this.measuredPower = in.readDouble();
		this.interval = in.readDouble();
		this.maker = in.readString();
		this.image = in.readString();
		this.rssi = in.readDouble();
		this.distance = in.readDouble();
		this.updateDate = in.readString();
		this.updateTime = in.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.code);
		dest.writeString(this.name);
		dest.writeString(this.beaconGroupCode);
		dest.writeString(this.macAddress);
		dest.writeString(this.proximityUUID);		
		dest.writeInt(this.major);
		dest.writeInt(this.minor);
		dest.writeDouble(this.battery);
		dest.writeDouble(this.txPower);
		dest.writeDouble(this.measuredPower);
		dest.writeDouble(this.interval);
		dest.writeString(this.maker);
		dest.writeString(this.image);
		dest.writeDouble(this.rssi);
		dest.writeDouble(this.distance);
		dest.writeString(this.updateDate);
		dest.writeString(this.updateTime);
	}

	public String getProximityUUID() {
		return this.proximityUUID;
	}

	public String getName() {
		return this.name;
	}

	public String getMacAddress() {
		return this.macAddress;
	}

	public String getCode() {
		return this.code;
	}

	public int getMajor() {
		return this.major;
	}

	public int getMinor() {
		return this.minor;
	}

	public double getMeasuredPower() {
		return this.measuredPower;
	}

	public double getRssi() {
		return this.rssi;
	}

	public double getDistance() {
		return this.distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Region getRegion() {
		return new Region(proximityUUID, major, minor);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 이 함수는 직접 사용되지 않는다
	 * MinorGroup에 addBeacon을 통해 beacon을 추가하면  추가된다.
	 * @param code
	 */
	 void setBeaconGroupCode(String code) {
		 beaconGroupCode = code;
	 }

	 public String toString() {
		 return Objects.toStringHelper(this).add("macAddress", this.macAddress).add("proximityUUID", this.proximityUUID).add("major", this.major).add("minor", this.minor).add("measuredPower", this.measuredPower).add("rssi", this.rssi).toString();
	 }

	 public boolean equals(Object o) {
		 if (this == o) return true;
		 if ((o == null) || (getClass() != o.getClass())) return false;

		 Beacon beacon = (Beacon)o;

		 if (this.major != beacon.major) return false;
		 if (this.minor != beacon.minor) return false;
		 return this.proximityUUID.equals(beacon.proximityUUID);
	 }

	 public int hashCode() {
		 int result = this.proximityUUID.hashCode();
		 result = 31 * result + this.major;
		 result = 31 * result + this.minor;
		 return result;
	 }

	 public void setHadwareAddress() {
		 L.w("Beacon Address set [ UUID : " + proximityUUID + " major : " + major + " minor : " + minor + "]");
	 }
}
