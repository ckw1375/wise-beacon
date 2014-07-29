package com.wisewells.sdk.datas;

import android.os.Parcel;
import android.os.Parcelable;

public class Topology implements Parcelable {

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

	/**
	 * 직접 호출 되지 않고 BeaconGroup의 attachTo함수를 통해 수행된다.
	 * @param beaconGroupCode
	 */
	protected void setBeaconGroupCode(String beaconGroupCode) {
		this.beaconGroupCode = beaconGroupCode;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * 직접 호출 되지 않고 Service의 attachTo 함수를 통해 수행된다.
	 * @param serviceCode
	 */
	protected void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
}
