package com.wisewells.sdk.datas.topology;

import android.os.Parcel;
import android.os.Parcelable;

public class Topology implements Parcelable{
	
	protected String code;
	protected String name;
	protected String beaconGroup;
	protected String service;
	
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
	
	public Topology() {
		
	}
	
	public Topology(String code, String name, String beaconGroup, String service) {
		this.code = code;
		this.name = name;
		this.beaconGroup = beaconGroup;
		this.service = service;
	}

	public Topology(Parcel p) {
		code = p.readString();
		name = p.readString();
		beaconGroup = p.readString();
		service = p.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(name);
		dest.writeString(beaconGroup);
		dest.writeString(service);
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

	public String getBeaconGroup() {
		return beaconGroup;
	}

	public void setBeaconGroup(String beaconGroup) {
		this.beaconGroup = beaconGroup;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
}
