package com.wisewells.sdk.datas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class BeaconGroup implements Parcelable{
		
	protected String name;
	
	protected String code;
	protected String parentCode;	
	protected HashSet<String> childCodes;
	protected HashSet<String> topologyCodes;
	
	public static final Parcelable.Creator<BeaconGroup> CREATOR = new Creator<BeaconGroup>() {
		
		@Override
		public BeaconGroup[] newArray(int size) {
			return new BeaconGroup[size];
		}
		
		@Override
		public BeaconGroup createFromParcel(Parcel source) {
			return new BeaconGroup(source);
		}
	};

	public BeaconGroup(String name) {
		init();
		this.name = name;
	}

	protected BeaconGroup(Parcel p) {
		init();
		
		code = p.readString();
		name = p.readString();
		parentCode = p.readString();
		childCodes = (HashSet<String>) p.readSerializable();
		topologyCodes = (HashSet<String>) p.readSerializable();
	}
	
	private void init() {
		childCodes = new HashSet<String>();
		topologyCodes = new HashSet<String>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(name);
		dest.writeString(parentCode);
		dest.writeSerializable(childCodes);
		dest.writeSerializable(topologyCodes);
	}
	
	public List<Beacon> getBeaconsInGroup() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		
		if(this instanceof MinorGroup) { 
			ArrayList<Beacon> temp = ((MinorGroup) this).getBeacons();
			for(Beacon b : temp)
				beacons.add(b);
		}
		
		/*WiseObjects manager = WiseObjects.getInstance();
		for(String child : childCodes) {
			BeaconGroup bg = manager.getBeaconGroup(child);
			bg.getBeaconsInGroup();
		}*/
		
		return beacons;
	}

	public void addChild(BeaconGroup child) {
		this.childCodes.add(child.getCode());
		child.setParentCode(this.code);
	}
	
	public void attachTo(Topology t) {
		topologyCodes.add(t.getCode());
		t.setBeaconGroupCode(this.code);
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParentCode() {
		return parentCode;
	}

	/**
	 * 이 함수는 직접 사용되지 않는다
	 * 부모 노드에 add하면 수행된다.
	 * @param parent
	 */
	protected void setParentCode(String parent) {
		this.parentCode = parent;
	}
	
	public HashSet<String> getChildCodes() {
		return this.childCodes;
	}
	
	private void setChildCodes(HashSet<String> codes) {
		this.childCodes = codes;
	}
	
	public void setTopologyCodes(HashSet<String> t) {
		topologyCodes = t;
	}
	
	public void setName(String name) {
		this.name = name;		
	}
	
	public String getName() {
		return this.name;
	}
}
