package com.wisewells.sdk.datas;

import java.util.UUID;

import com.wisewells.sdk.datas.group.MinorGroup;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

public class Beacon implements Parcelable {

	private String code;
	private String macAddress;
	private String beaconGroupCode;
	private UUID uuid;
	private int major;
	private int minor;
	private double txPower;
	private double interval;
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

	public Beacon() {
		
	}
	
	public Beacon(String code, String macAddress, String beaconGroup, String uuid, int major,
			int minor, double txPower, double interval, double rssi) {
		
		super();
		this.code = code;
		this.macAddress = macAddress;
		this.beaconGroupCode = beaconGroup;
		this.uuid = UUID.fromString(uuid);
		this.major = major;
		this.minor = minor;
		this.txPower = txPower;
		this.interval = interval;
		this.rssi = rssi;
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
		beaconGroupCode = p.readString();
		uuid = (UUID) p.readSerializable();
		major = p.readInt();
		minor = p.readInt();
		txPower = p.readDouble();
		interval = p.readDouble();
		rssi = p.readDouble();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(macAddress);
		dest.writeString(beaconGroupCode);
		dest.writeSerializable(uuid);
		dest.writeInt(major);
		dest.writeInt(minor);
		dest.writeDouble(txPower);
		dest.writeDouble(interval);
		dest.writeDouble(rssi);
	}
	
	public void setAddress(UUID uuid, int major, int minor) {
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
		
		/*
		 * 실제 Beacon의 Address 값을 바꿔주는 동작 추가
		 */
	}
	
	public void attach(MinorGroup parent) {
		
	}
	
	public void detach() {
		
	}

	public String getCode() {
		return code;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public String getBeaconGroupCode() {
		return beaconGroupCode;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public double getTxPower() {
		return txPower;
	}

	public double getInterval() {
		return interval;
	}

	public double getRssi() {
		return rssi;
	}
}
