package com.wisewells.sdk.datas;

import java.util.UUID;

import com.wisewells.sdk.datas.group.MinorGroup;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

public class Beacon implements Parcelable {

	private String code;
	private String macAddress;
	private UUID uuid;
	private int major;
	private int minor;
	private double txPower;
	private double interval;
	private double rssi;
	private MinorGroup parent;

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

	public Beacon() {
		
	}
	
	public Beacon(String code, String macAddress, String uuid, int major,
			int minor, double txPower, double interval, double rssi,
			MinorGroup parent) {
		
		super();
		this.code = code;
		this.macAddress = macAddress;
		this.uuid = UUID.fromString(uuid);
		this.major = major;
		this.minor = minor;
		this.txPower = txPower;
		this.interval = interval;
		this.rssi = rssi;
		this.parent = parent;
	}

	public Beacon(com.estimote.sdk.Beacon b) {
		this.macAddress = b.getMacAddress();
		this.uuid = UUID.fromString(b.getProximityUUID());
		this.major = b.getMajor();
		this.minor = b.getMinor();
	}

	private Beacon(Parcel p) {
		code = p.readString();
		macAddress = p.readString();
		uuid = (UUID) p.readSerializable();
		major = p.readInt();
		minor = p.readInt();
		txPower = p.readDouble();
		interval = p.readDouble();
		rssi = p.readDouble();
		parent = p.readParcelable(MinorGroup.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(macAddress);
		dest.writeSerializable(uuid);
		dest.writeInt(major);
		dest.writeInt(minor);
		dest.writeDouble(txPower);
		dest.writeDouble(interval);
		dest.writeDouble(rssi);
		dest.writeParcelable(parent, 0);
	}
	
	public void setAddress(UUID uuid, int major, int minoir) {
		setUuid(uuid);
		setMajor(major);
		setMinor(minor);
		
		/*
		 * 실제 Beacon의 Address 값을 바꿔주는 동작 추가
		 */
	}
	
	public void attach(MinorGroup parent) {
		setParent(parent);
	}
	
	public void detach() {
		setParent(null);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public UUID getUuid() {
		return uuid;
	}
	
	public void setUuid(UUID uuid) {
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

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public double getTxPower() {
		return txPower;
	}

	public void setTxPower(double txPower) {
		this.txPower = txPower;
	}

	public double getInterval() {
		return interval;
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}

	public double getRssi() {
		return rssi;
	}

	public void setRssi(double rssi) {
		this.rssi = rssi;
	}

	public MinorGroup getParent() {
		return parent;
	}

	public void setParent(MinorGroup parent) {
		this.parent = parent;
	}
}
