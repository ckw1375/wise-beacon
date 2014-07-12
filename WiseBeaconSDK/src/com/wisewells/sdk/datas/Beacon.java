package com.wisewells.sdk.datas;

import java.util.HashSet;
import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.group.MinorGroup;

public class Beacon implements Parcelable {

	private String code;
	private String macAddress;
	private HashSet<String> beaconGroupCodes;
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
	
	public Beacon(String code, String macAddress, String uuid, int major,
			int minor, double txPower, double interval, double rssi) {
		
		super();
		init();
		this.code = code;
		this.macAddress = macAddress;
		this.uuid = UUID.fromString(uuid);
		this.major = major;
		this.minor = minor;
		this.txPower = txPower;
		this.interval = interval;
		this.rssi = rssi;		
	}

	private Beacon(Parcel p) {
		init();
		code = p.readString();
		macAddress = p.readString();
		beaconGroupCodes = (HashSet<String>) p.readSerializable();
		uuid = (UUID) p.readSerializable();
		major = p.readInt();
		minor = p.readInt();
		txPower = p.readDouble();
		interval = p.readDouble();
		rssi = p.readDouble();
	}
	
	private void init() {
		beaconGroupCodes = new HashSet<String>();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(macAddress);
		dest.writeSerializable(beaconGroupCodes);
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
	
	public void attachTo(MinorGroup parent) {
		
	}
	
	public void addBeaconGroupCode(String code) {
		beaconGroupCodes.add(code);
	}
	
	public void detach() {
		
	}

	public String getCode() {
		return code;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public HashSet<String> getBeaconGroupCodes() {
		return beaconGroupCodes;
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
