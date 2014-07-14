package com.wisewells.sdk.datas;

import java.util.HashSet;

import android.os.Parcel;
import android.os.Parcelable;

public class Beacon implements Parcelable {

	private String code;	
	private String beaconGroupCode;
	private double interval;
	
	/**
	 * 	These attributes is gotten from Beacon Hardware
	 */
	private String macAddress;
	private String uuid;
	private int major;
	private int minor;
	private double txPower;	
	private double rssi;

	public static final Parcelable.Creator<Beacon> CREATOR = new Creator<Beacon>() {

		@Override
		public Beacon[] newArray(int size) {
			return new Beacon[size];
		}

		@Override
		public Beacon createFromParcel(Parcel source) {
			return new Beacon(source);
		}
	};

	public Beacon(String macAddress, String uuid, int major, int minor, double txPower, double rssi) {
		super();
		this.macAddress = macAddress;
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
		this.txPower = txPower;
		this.rssi = rssi;
	}

	private Beacon(Parcel in) {
		code = in.readString();		
		beaconGroupCode = in.readString();
		interval = in.readDouble();
		macAddress = in.readString();
		uuid = (String) in.readSerializable();
		major = in.readInt();
		minor = in.readInt();
		txPower = in.readDouble();		
		rssi = in.readDouble();
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if((o == null) || (getClass() != o.getClass())) return false;
		
		Beacon beacon = (Beacon) o;
		
		if(this.major != beacon.major) return false;
		if(this.minor != beacon.minor) return false;		
		return this.uuid.equals(beacon.uuid);
	}
	
	@Override
	public int hashCode() {
		int result = this.uuid.hashCode();
		result = 31 * result + this.major;
		result = 31 * result + this.minor;
		return result;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);		
		dest.writeSerializable(beaconGroupCode);
		dest.writeDouble(interval);
		dest.writeString(macAddress);
		dest.writeSerializable(uuid);
		dest.writeInt(major);
		dest.writeInt(minor);
		dest.writeDouble(txPower);		
		dest.writeDouble(rssi);
	}
	
	public void setAddress(String uuid, int major, int minor) {
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
		
		/*
		 * 실제 Beacon의 Address 값을 바꿔주는 동작 추가
		 */
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getInterval() {
		return interval;
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public double getTxPower() {
		return txPower;
	}

	public void setTxPower(double txPower) {
		this.txPower = txPower;
	}

	public double getRssi() {
		return rssi;
	}

	public void setRssi(double rssi) {
		this.rssi = rssi;
	}
	
	public void setBeaconGroupCode(String code) {
		beaconGroupCode = code;
	}
}
