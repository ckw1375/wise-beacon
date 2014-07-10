package com.wisewells.sdk.datas.topology;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.group.BeaconGroup;

public class Topology implements Parcelable{
	
	protected String code;
	protected String name;
	protected String beaconGroupCode;
	protected String serviceCode;
	
	public static final Parcelable.Creator<Topology> CREATOR = new Creator<Topology>() {
		
		@Override
		public Topology[] newArray(int size) {
			return new Topology[size];
		}
		
		@Override
		public Topology createFromParcel(Parcel source) {
			return new Topology(source);
		}
	};
	
	public Topology(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public Topology(Parcel p) {
		code = p.readString();
		name = p.readString();
		beaconGroupCode = p.readString();
		serviceCode = p.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(name);
		dest.writeString(beaconGroupCode);
		dest.writeString(serviceCode);
	}
	
	public void attachTo(BeaconGroup bg) {
		beaconGroupCode = bg.getCode();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBeaconGroupCode() {
		return beaconGroupCode;
	}

	public void setBeaconGroupCode(String beaconGroupCode) {
		this.beaconGroupCode = beaconGroupCode;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
}
