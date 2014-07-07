package com.wisewells.sdk.datas.topology;

import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.group.BeaconGroup;

import android.os.Parcel;
import android.os.Parcelable;

public class Topology implements Parcelable{
	
	protected String code;
	protected String name;
	protected BeaconGroup beaconGroup;
	protected Service service;
	
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
	
	public Topology(Parcel p) {
		code = p.readString();
		name = p.readString();
		beaconGroup = p.readParcelable(BeaconGroup.class.getClassLoader());
		service = p.readParcelable(Service.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(name);
		dest.writeParcelable(beaconGroup, 0);
		dest.writeParcelable(service, 0);
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

	public BeaconGroup getBeaconGroup() {
		return beaconGroup;
	}

	public void setBeaconGroup(BeaconGroup beaconGroup) {
		this.beaconGroup = beaconGroup;
	}

	public Service getServiceContent() {
		return service;
	}

	public void setServiceContent(Service content) {
		this.service = content;
	}
}
