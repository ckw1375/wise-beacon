package com.wisewells.sdk.datas;

import java.util.HashSet;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.topology.Topology;

public class Service implements Parcelable{
	
	private String name;
	private String code;
	private String topologyCode;
	private String parentCode;
	private HashSet<String> childCodes;
	
	public static final Parcelable.Creator<Service> CREATOR = new Creator<Service>() {
		
		@Override
		public Service[] newArray(int size) {
			return new Service[size];
		}
		
		@Override
		public Service createFromParcel(Parcel source) {
			return new Service(source);
		}
	};

	public Service(String name, String code) {
		init();
		this.name = name;
		this.code = code;
	}
	
	public Service(Parcel p) {
		init();
		name = p.readString();
		code = p.readString();
		topologyCode = p.readString();
		parentCode = p.readString();		
		childCodes = (HashSet<String>) p.readSerializable();
	}
	
	private void init() {
		childCodes = new HashSet<String>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(code);
		dest.writeString(topologyCode);
		dest.writeString(parentCode);
		dest.writeSerializable(childCodes);
	}
	
	public void attachTo(Topology t) {
		topologyCode = t.getCode();
		t.setServiceCode(this.code);
	}
	
	public void addChild(Service s) {
		childCodes.add(s.getCode());
		s.setParentCode(this.code);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTopologyCode() {
		return topologyCode;
	}

	public void setTopologyCode(String topologyCode) {
		this.topologyCode = topologyCode;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public HashSet<String> getChildCodes() {
		return childCodes;
	}

	public void setChildCodes(HashSet<String> childCodes) {
		this.childCodes = childCodes;
	}
}
