package com.wisewells.sdk.datas;

import android.os.Parcel;
import android.os.Parcelable;

import com.estimote.sdk.internal.Objects;
import com.wisewells.sdk.utils.BeaconUtils;


public class Beacon implements Parcelable {

	private String code;
	private String beaconGroupCode;
	private String name;	
	private String proximityUUID;
	private String macAddress;
	private int major;
	private int minor;
	private double measuredPower;
	private double rssi;
	private double distance;
	private double interval;

    public static final Parcelable.Creator<Beacon> CREATOR = new Parcelable.Creator<Beacon>() {
       	public Beacon createFromParcel(Parcel source) {
       		return new Beacon(source);
       	}
       	public Beacon[] newArray(int size) {
       		return new Beacon[size];
       	}
   };

   public Beacon(String proximityUUID, String name, String macAddress, 
		   int major, int minor, double measuredPower, double rssi, double distance) {
	   	this.proximityUUID = BeaconUtils.normalizeProximityUUID(proximityUUID);
	   	this.name = name;
	   	this.macAddress = macAddress;
	   	this.major = major;
	   	this.minor = minor;
	   	this.measuredPower = measuredPower;
	   	this.rssi = rssi;
	   	this.distance = distance;
    }
   
   public Beacon(String macAddress, String uuid, int major, int minor, double measuredPower, double rssi) {
		super();
		this.macAddress = macAddress;
		this.proximityUUID = uuid;
		this.major = major;
		this.minor = minor;
		this.measuredPower = measuredPower;
		this.rssi = rssi;
   }
   
   private Beacon(Parcel parcel) {
	   this.code = parcel.readString();
	   this.beaconGroupCode = parcel.readString();
	   this.name = parcel.readString();
	   this.proximityUUID = parcel.readString();	   
	   this.macAddress = parcel.readString();
	   this.major = parcel.readInt();
	   this.minor = parcel.readInt();
	   this.measuredPower = parcel.readDouble();
	   this.rssi = parcel.readDouble();
	   this.distance = parcel.readDouble();
	   this.interval = parcel.readDouble();
   }

   public int describeContents() {
	   return 0;
   }
   
   public void writeToParcel(Parcel dest, int flags) {
	   dest.writeString(this.code);
	   dest.writeString(this.beaconGroupCode);
	   dest.writeString(this.name);
	   dest.writeString(this.proximityUUID);	   
	   dest.writeString(this.macAddress);
	   dest.writeInt(this.major);
	   dest.writeInt(this.minor);
	   dest.writeDouble(this.measuredPower);
	   dest.writeDouble(this.rssi);
	   dest.writeDouble(this.distance);
	   dest.writeDouble(interval);
   	}

   public String getProximityUUID() { return this.proximityUUID; }
   public String getName() { return this.name; }
   public String getMacAddress() { return this.macAddress; }
   public String getCode() { return this.code; }
   public int getMajor() { return this.major; }
   public int getMinor() { return this.minor; }
   public double getMeasuredPower() { return this.measuredPower; }
   public double getRssi() { return this.rssi; }
   public double getDistance() { return this.distance; }
   
   public void setCode(String code) { this.code = code; }
   public void setName(String name) { this.name = name; }
   
   /**
	 * 이 함수는 직접 사용되지 않는다
	 * MinorGroup에 addBeacon을 통해 beacon을 추가하면  추가된다.
	 * @param code
	 */
	void setBeaconGroupCode(String code) {
		beaconGroupCode = code;
	}
   
   public String toString() {
	   return Objects.toStringHelper(this).add("macAddress", this.macAddress).add("proximityUUID", this.proximityUUID).add("major", this.major).add("minor", this.minor).add("measuredPower", this.measuredPower).add("rssi", this.rssi).toString();
   }

   public boolean equals(Object o) {
	   if (this == o) return true;
	   if ((o == null) || (getClass() != o.getClass())) return false;
	   
	   Beacon beacon = (Beacon)o;
 
	   if (this.major != beacon.major) return false;
	   if (this.minor != beacon.minor) return false;
	   return this.proximityUUID.equals(beacon.proximityUUID);
   }

   public int hashCode() {
	   int result = this.proximityUUID.hashCode();
	   result = 31 * result + this.major;
	   result = 31 * result + this.minor;
	   return result;
   }
}
