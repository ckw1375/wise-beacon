package com.wisewells.sdk;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.group.BeaconGroup;
import com.wisewells.sdk.datas.topology.Topology;
import com.wisewells.sdk.utils.Utils;

/**
 * @file	WiseObjects.java
 * @author 	Mingook
 * @date	2014. 7. 7.
 * @description
 * This class is for managing all objects made tree structure.
 */

public class WiseObjects implements Parcelable {
	
	private HashMap<String, Beacon> beacons;
	private HashMap<String, BeaconGroup> beaconGroups;
	private HashMap<String, Service> services;
	private HashMap<String, Topology> topologies;
	
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

	private WiseObjects(Parcel p) {
		beacons = (HashMap<String, Beacon>) Utils.readMapFromParcel(p, Beacon.class.getClassLoader());;
		beaconGroups = (HashMap<String, BeaconGroup>) Utils.readMapFromParcel(p, BeaconGroup.class.getClassLoader());
		services = (HashMap<String, Service>) Utils.readMapFromParcel(p, Service.class.getClassLoader());
		topologies = (HashMap<String, Topology>) Utils.readMapFromParcel(p, Topology.class.getClassLoader());
	}
		
	private void init() {
		beacons = new HashMap<String, Beacon>();
		beaconGroups = new HashMap<String, BeaconGroup>();
		services = new HashMap<String, Service>();
		topologies = new HashMap<String, Topology>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Utils.writeMapToParcel(dest, beacons);
		Utils.writeMapToParcel(dest, beaconGroups);
		Utils.writeMapToParcel(dest, services);
		Utils.writeMapToParcel(dest, topologies);
	}
	
	public void attach() {
		
	}
	
	public void detach() {
		
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
	
	public void putBeacon(Beacon beacon) {
		beacons.put(beacon.getCode(), beacon);		
	}
	
	public void putBeaconGroup(BeaconGroup beaconGroup) {
		beaconGroups.put(beaconGroup.getCode(), beaconGroup);
	}
	
	public void putService(Service service) {
		services.put(service.getCode(), service);
	}
	
	public void putTopology(Topology topology) {
		topologies.put(topology.getCode(), topology);
	}
}
