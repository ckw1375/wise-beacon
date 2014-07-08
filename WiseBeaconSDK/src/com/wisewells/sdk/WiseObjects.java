package com.wisewells.sdk;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.group.BeaconGroup;
import com.wisewells.sdk.datas.topology.Topology;

/**
 * @file	WiseObjects.java
 * @author 	Mingook
 * @date	2014. 7. 7.
 * @description
 * This class is for managing all objects made tree structure.
 */

public class WiseObjects implements Parcelable {
	
	private Bundle beacons;
	private Bundle beaconGroups;
	private Bundle services;
	private Bundle topologies;
	
	public static WiseObjects instance;
	public static Parcelable.Creator<WiseObjects> CREATOR = new Creator<WiseObjects>() {
		
		@Override
		public WiseObjects[] newArray(int size) {
			return new WiseObjects[size];
		}
		
		@Override
		public WiseObjects createFromParcel(Parcel source) {
			return new WiseObjects(source);
		}
	};
	
	public static WiseObjects getInstance() {
		if(instance == null)
			instance = new WiseObjects();
		
		return instance;
	}
	
	private WiseObjects() {
		init();
	}
		
	public WiseObjects(Bundle beacons, Bundle beaconGroups, Bundle services, Bundle topologies) {
		this.beacons = beacons;
		this.beaconGroups = beaconGroups;
		this.services = services;
		this.topologies = topologies;
	}

	private WiseObjects(Parcel p) {
		beacons = p.readBundle();
		beaconGroups = p.readBundle();
		services = p.readBundle();
		topologies = p.readBundle();
	}
		
	private void init() {
		beacons = new Bundle(Beacon.class.getClassLoader());
		beaconGroups = new Bundle(BeaconGroup.class.getClassLoader());
		services = new Bundle(Service.class.getClassLoader());
		topologies = new Bundle(Topology.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBundle(beacons);
		dest.writeBundle(beaconGroups);
		dest.writeBundle(services);
		dest.writeBundle(topologies);
	}
	
	public Beacon getBeacon(String code) {
		return (Beacon) beacons.get(code);
	}

	public BeaconGroup getBeaconGroup(String code) {
		return (BeaconGroup) beaconGroups.get(code);
	}
	
	public Service getService(String code) {
		return (Service) services.get(code);
	}
	
	public Topology getTopology(String code) {
		return (Topology) topologies.get(code);
	}
	
	public void putBeacon(String code, Beacon beacon) {
		beacons.putParcelable(code, beacon);		
	}
	
	public void putBeaconGroup(String code, BeaconGroup beaconGroup) {
		beaconGroups.putParcelable(code, beaconGroup);
	}
	
	public void putService(String code, Service service) {
		services.putParcelable(code, service);
	}
	
	public void putTopology(String code, Topology topology) {
		topologies.putParcelable(code, topology);
	}
}
