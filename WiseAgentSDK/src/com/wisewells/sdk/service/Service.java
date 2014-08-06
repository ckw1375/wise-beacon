package com.wisewells.sdk.service;

import java.util.HashSet;

import android.os.Parcel;
import android.os.Parcelable;

public class Service implements Parcelable{
	
	public static final int SERVICE_TREE_ROOT = 0;
	public static final int SERVICE_TREE_NODE_1 = 1;
//	public static final int SERVICE_TREE_NODE_2 = 2;
	
	private String name;
	
	private String code;
	private String topologyCode;
	private String parentCode;
	private HashSet<String> childCodes;
	/**
	 * Zero is root node in tree data structure.
	 */
	private int treeLevel;
	
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

	public Service(String name) {
		init();
		this.name = name;
	}
	
	private Service(Parcel in) {
		init();
		name = in.readString();
		code = in.readString();
		topologyCode = in.readString();
		parentCode = in.readString();		
		childCodes = (HashSet<String>) in.readSerializable();
		treeLevel = in.readInt();
	}
	
	private void init() {
		childCodes = new HashSet<String>();
		treeLevel = SERVICE_TREE_ROOT;
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
		dest.writeInt(treeLevel);
	}
	
	public void attachTo(Topology t) {
		topologyCode = t.getCode();
		t.setServiceCode(this.code);
	}
	
	public void addChild(Service s) {
		childCodes.add(s.getCode());
		
		s.setTreeLevel(treeLevel + 1);
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

	/**
	 * 이 함수는 직접 사용되지 않는다
	 * 부모 노드에 add하면 수행된다.
	 * @param parentCode
	 */
	void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public HashSet<String> getChildCodes() {
		return childCodes;
	}

	private void setChildCodes(HashSet<String> childCodes) {
		this.childCodes = childCodes;
	}
	
	public int getTreeLevel() {
		return this.treeLevel;
	}
	
	private void setTreeLevel(int level) {
		this.treeLevel = level;
	}
}
