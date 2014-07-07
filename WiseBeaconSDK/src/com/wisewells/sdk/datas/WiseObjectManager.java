package com.wisewells.sdk.datas;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.group.BeaconGroup;
import com.wisewells.sdk.datas.topology.Topology;

/**
 * @file	WiseObjectManager.java
 * @author 	Mingook
 * @date	2014. 7. 7.
 * @description
 * This class is for managing all objects related to WiseBeaconProject.
 */

public class WiseObjectManager implements Parcelable {
	
	private Bundle beacons;
	private Bundle beaconGroups;
	private Bundle services;
	private Bundle topologies;
	
	public static WiseObjectManager instance;
	public static Parcelable.Creator<WiseObjectManager> CREATOR = new Creator<WiseObjectManager>() {
		
		@Override
		public WiseObjectManager[] newArray(int size) {
			return new WiseObjectManager[size];
		}
		
		@Override
		public WiseObjectManager createFromParcel(Parcel source) {
			return new WiseObjectManager(source);
		}
	};
	
	public static WiseObjectManager getInstance() {
		if(instance == null)
			instance = new WiseObjectManager();
		
		return instance;
	}
	
	private WiseObjectManager() {
		init();
	}
		
	public WiseObjectManager(Bundle beacons, Bundle beaconGroups, Bundle services, Bundle topologies) {
		this.beacons = beacons;
		this.beaconGroups = beaconGroups;
		this.services = services;
		this.topologies = topologies;
	}

	private WiseObjectManager(Parcel p) {
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
}
