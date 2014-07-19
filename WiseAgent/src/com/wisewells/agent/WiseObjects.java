package com.wisewells.agent;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;
import com.wisewells.sdk.utils.ParcelUtils;

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
		beacons = (HashMap<String, Beacon>) ParcelUtils.readMapFromParcel(p, Beacon.class.getClassLoader());;
		beaconGroups = (HashMap<String, BeaconGroup>) ParcelUtils.readMapFromParcel(p, BeaconGroup.class.getClassLoader());
		services = (HashMap<String, Service>) ParcelUtils.readMapFromParcel(p, Service.class.getClassLoader());
		topologies = (HashMap<String, Topology>) ParcelUtils.readMapFromParcel(p, Topology.class.getClassLoader());
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
		ParcelUtils.writeMapToParcel(dest, beacons);
		ParcelUtils.writeMapToParcel(dest, beaconGroups);
		ParcelUtils.writeMapToParcel(dest, services);
		ParcelUtils.writeMapToParcel(dest, topologies);
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
	
	public ArrayList<Beacon> getBeacons() {
		ArrayList<Beacon> b = new ArrayList<Beacon>(this.beacons.values());
		return b;
	}
	
	public ArrayList<BeaconGroup> getBeaconGroups() {
		ArrayList<BeaconGroup> bg = new ArrayList<BeaconGroup>(this.beaconGroups.values());
		return bg;
	}
	
	public ArrayList<Service> getServices() {
		ArrayList<Service> s = new ArrayList<Service>(this.services.values());
		return s;
	}
	
	public ArrayList<Topology> getTopologies() {
		ArrayList<Topology> t = new ArrayList<Topology>(this.topologies.values());
		return t;
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