package com.wisewells.sdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class Service implements Parcelable{
	
	public static final int DEPTH_ROOT = 1;
	public static final int DEPTH_LEAF = 2;
	
	private String mCode;
	private String mName;
	private String mParentCode;
	private String mUpdateDate;
	private String mUpdateTime;
	private int mDepth;
	
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

	public Service(int depth, String name) {
		mDepth = depth;
		mName = name;
	}
	
	public Service(int depth, String name, String code, String updateDate, String updateTime) {
		mDepth = depth;
		mName = name;
		mCode = code;
		mUpdateDate = updateDate;
		mUpdateTime = updateTime;
	}
	
	private Service(Parcel in) {
		mCode = in.readString();
		mName = in.readString();
		mParentCode = in.readString();
		mUpdateDate = in.readString();
		mUpdateTime = in.readString();
		mDepth = in.readInt();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mCode);
		dest.writeString(mName);
		dest.writeString(mParentCode);
		dest.writeString(mUpdateDate);
		dest.writeString(mUpdateTime);
		dest.writeInt(mDepth);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
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
	 * @param parentCode
	 */
	void setParentCode(String parentCode) {
		mParentCode = parentCode;
	}

	public int getDepth() {
		return mDepth;
	}
}
