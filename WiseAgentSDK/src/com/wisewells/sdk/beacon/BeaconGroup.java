package com.wisewells.sdk.beacon;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.utils.L;

public class BeaconGroup implements Parcelable{
	
	public static final int DEPTH_ROOT = 1;
	public static final int DEPTH_LEAF = 2;
	
	private int depth;	// 1 or 2
	private String name;
	private String code;
	private String parentCode;	
	private String uuid;
	private Integer major;
	private String updateDate;
	private String updateTime;
	
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
		if(depth > DEPTH_LEAF || depth < 1)
			throw new RuntimeException("BeaconGroup depth can be 1 or 2");
		this.depth = depth;
		this.name = name;
	}

	public BeaconGroup(int depth, String name, String code, String parentCode,
			String uuid, Integer major) {
		this.depth = depth;
		this.name = name;
		this.code = code;
		this.parentCode = parentCode;
		this.uuid = uuid;
		this.major = major;
	}

	private BeaconGroup(Parcel in) {
		depth = in.readInt();
		code = in.readString();
		name = in.readString();
		parentCode = in.readString();
		uuid = in.readString();
		major = (Integer) in.readSerializable();
		updateDate = in.readString();
		updateTime = in.readString();
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
		dest.writeString(uuid);
		dest.writeSerializable(major);
		dest.writeString(updateDate);
		dest.writeString(updateTime);
	}
	
	@Override
	public String toString() {
		return this.name + "(" + this.code + ")";
	}

	public void addChild(BeaconGroup child) {
		child.setParentCode(this.code);
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
		
		beacon.setBeaconGroupCode(this.code);
		return true;
	}
}
