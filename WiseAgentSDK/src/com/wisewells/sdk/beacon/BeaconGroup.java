package com.wisewells.sdk.beacon;

import java.util.HashSet;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.L;

public class BeaconGroup implements Parcelable{
	
	public static final int DEPTH_ROOT = 1;
	public static final int DEPTH_LEAF = 2;
	
	private int depth;	// 1 or 2
	private String name;
	private String code;
	private String parentCode;	
	
	// 곧 삭제!!!! 
	private HashSet<String> childCodes;
	private HashSet<String> topologyCodes;
	
	private String uuid;
	private Integer major;
	
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

	public BeaconGroup(int depth, String name) {
		init();
		if(depth > DEPTH_LEAF || depth < 1)
			throw new RuntimeException("BeaconGroup depth can be 1 or 2");
		this.depth = depth;
		this.name = name;
	}

	public BeaconGroup(int depth, String name, String code, String parentCode,
			HashSet<String> childCodes, HashSet<String> topologyCodes,
			String uuid, Integer major) {
		this.depth = depth;
		this.name = name;
		this.code = code;
		this.parentCode = parentCode;
		this.childCodes = childCodes;
		this.topologyCodes = topologyCodes;
		this.uuid = uuid;
		this.major = major;
		
		if(this.childCodes == null) this.childCodes = new HashSet<String>();
		if(this.topologyCodes == null) this.topologyCodes = new HashSet<String>();
	}

	private BeaconGroup(Parcel p) {
		init();
		depth = p.readInt();
		code = p.readString();
		name = p.readString();
		parentCode = p.readString();
		childCodes = (HashSet<String>) p.readSerializable();
		topologyCodes = (HashSet<String>) p.readSerializable();
		uuid = p.readString();
		major = (Integer) p.readSerializable();
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
		dest.writeInt(depth);
		dest.writeString(code);
		dest.writeString(name);
		dest.writeString(parentCode);
		dest.writeSerializable(childCodes);
		dest.writeSerializable(topologyCodes);
		dest.writeString(uuid);
		dest.writeSerializable(major);
	}
	
	@Override
	public String toString() {
		return this.name + "(" + this.code + ")";
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
	private void setParentCode(String parent) {
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
	
	public void addTopologyCode(String code) {
		topologyCodes.add(code);
	}
	
	public void setName(String name) {
		this.name = name;		
	}
	
	public String getName() {
		return this.name;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getMajor() {
		return this.major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getDepth() {
		return this.depth;
	}
	
	public boolean addBeacon(Beacon beacon) {
		if(this.depth != DEPTH_LEAF) {
			L.w("Can't add beacon. Only MAX_DEPTH BeaconGroup can add beacon.");
			return false;
		}
		
		childCodes.add(beacon.getCode());
		beacon.setBeaconGroupCode(this.code);
		return true;
	}
}
