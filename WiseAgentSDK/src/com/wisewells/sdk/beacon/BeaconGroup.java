package com.wisewells.sdk.beacon;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.utils.L;

public class BeaconGroup implements Parcelable{
	
	public static final int DEPTH_ROOT = 1;
	public static final int DEPTH_LEAF = 2;
	
	private int mDepth;	// 1 or 2
	private String mName;
	private String mCode;
	private String mParentCode;	
	private String mUuid;
	private Integer mMajor;
	private String mUpdateDate;
	private String mUpdateTime;
	
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
		if(depth > DEPTH_LEAF || depth < DEPTH_ROOT)
			throw new RuntimeException("BeaconGroup depth is wrong");
		mDepth = depth;
		mName = name;
	}
	
	public BeaconGroup(int depth, String name, String code, String parentCode,
			String uuid, int major) {
		mDepth = depth;
		mName = name;
		mCode = code;
		mParentCode = parentCode;
		mUuid = uuid;
		mMajor = major;
	}
	
	public BeaconGroup(int depth, String name, String code, String parentCode, 
			String uuid, int major, String updateDate, String updateTime) {
		this(depth, name, code, parentCode, uuid, major);
		mUpdateDate = updateDate;
		mUpdateTime = updateTime;
	}

	

	private BeaconGroup(Parcel in) {
		mDepth = in.readInt();
		mCode = in.readString();
		mName = in.readString();
		mParentCode = in.readString();
		mUuid = in.readString();
		mMajor = (Integer) in.readSerializable();
		mUpdateDate = in.readString();
		mUpdateTime = in.readString();
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mDepth);
		dest.writeString(mCode);
		dest.writeString(mName);
		dest.writeString(mParentCode);
		dest.writeString(mUuid);
		dest.writeSerializable(mMajor);
		dest.writeString(mUpdateDate);
		dest.writeString(mUpdateTime);
	}
	
	@Override
	public String toString() {
		return mName + "(" + mCode + ")";
	}

	public void addChild(BeaconGroup child) {
		child.setParentCode(mCode);
	}
	
	public String getCode() {
		return mCode;
	}

	public void setCode(String code) {
		mCode = code;
	}

	public String getParentCode() {
		return mParentCode;
	}

	/**
	 * 이 함수는 직접 사용되지 않는다
	 * 부모 노드에 add하면 수행된다.
	 * @param parent
	 */
	private void setParentCode(String parent) {
		mParentCode = parent;
	}
	
	public void setName(String name) {
		mName = name;		
	}
	
	public String getName() {
		return mName;
	}

	public String getUuid() {
		return mUuid;
	}

	public void setUuid(String uuid) {
		mUuid = uuid;
	}

	public int getMajor() {
		return mMajor;
	}

	public void setMajor(int major) {
		mMajor = major;
	}

	public int getDepth() {
		return mDepth;
	}
	
	public boolean addBeacon(Beacon beacon) {
		if(mDepth != DEPTH_LEAF) {
			L.w("Can't add beacon. Only MAX_DEPTH BeaconGroup can add beacon.");
			return false;
		}
		
		beacon.setBeaconGroupCode(mCode);
		return true;
	}
}
