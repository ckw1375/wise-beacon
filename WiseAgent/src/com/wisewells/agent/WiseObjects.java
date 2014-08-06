package com.wisewells.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.MajorGroup;
import com.wisewells.sdk.beacon.MinorGroup;
import com.wisewells.sdk.beacon.UuidGroup;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.IpcUtils;

/**
 * @file	WiseObjects.java
 * @author 	Mingook
 * @date	2014. 7. 7.
 * @description
 * This class is for managing all objects made tree structure.
 */

public class WiseObjects implements Parcelable {
	
	private HashMap<String, Beacon> mBeacons;
	private HashMap<String, BeaconGroup> mBeaconGroups;
	private HashMap<String, Service> mServices;
	private HashMap<String, Topology> mTopologies;
	
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
		mBeacons = (HashMap<String, Beacon>) IpcUtils.readMapFromParcel(p, Beacon.class.getClassLoader());;
		mBeaconGroups = (HashMap<String, BeaconGroup>) IpcUtils.readMapFromParcel(p, BeaconGroup.class.getClassLoader());
		mServices = (HashMap<String, Service>) IpcUtils.readMapFromParcel(p, Service.class.getClassLoader());
		mTopologies = (HashMap<String, Topology>) IpcUtils.readMapFromParcel(p, Topology.class.getClassLoader());
	}
		
	private void init() {
		mBeacons = new HashMap<String, Beacon>();
		mBeaconGroups = new HashMap<String, BeaconGroup>();
		mServices = new HashMap<String, Service>();
		mTopologies = new HashMap<String, Topology>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		IpcUtils.writeMapToParcel(dest, mBeacons);
		IpcUtils.writeMapToParcel(dest, mBeaconGroups);
		IpcUtils.writeMapToParcel(dest, mServices);
		IpcUtils.writeMapToParcel(dest, mTopologies);
	}
	
	public Beacon getBeacon(String code) {
		return (Beacon) mBeacons.get(code);
	}

	public BeaconGroup getBeaconGroup(String code) {
		return (BeaconGroup) mBeaconGroups.get(code);
	}
	
	public Service getService(String code) {
		return (Service) mServices.get(code);
	}
	
	public Topology getTopology(String code) {
		return (Topology) mTopologies.get(code);
	}
	
	public ArrayList<Beacon> getBeacons() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>(mBeacons.values());
		return beacons;
	}
	
	public ArrayList<Beacon> getBeaconsInGroup(String groupCode) {
		ArrayList<Beacon> beaconsInGroup = new ArrayList<Beacon>();
		BeaconGroup group = mBeaconGroups.get(groupCode);
		
		if(group instanceof UuidGroup) {
			for(String code : group.getChildCodes()) {
				beaconsInGroup.addAll(getBeaconsInGroup(code));
			}
		}
		
		if(group instanceof MajorGroup) {
			for(String childCode : group.getChildCodes()) {
				MinorGroup minor = (MinorGroup) mBeaconGroups.get(childCode);
				for(String beaconCode : minor.getChildCodes()) {
					beaconsInGroup.add(mBeacons.get(beaconCode));
				}
			}
		}
		return beaconsInGroup;
	}
	
	public ArrayList<BeaconGroup> getBeaconGroups() {
		ArrayList<BeaconGroup> beaconGroups = new ArrayList<BeaconGroup>(mBeaconGroups.values());
		return beaconGroups;
	}
	
	public ArrayList<UuidGroup> getUuidGroups() {
		ArrayList<UuidGroup> uuidGroups = new ArrayList<UuidGroup>();
		ArrayList<BeaconGroup> beaconGroups = getBeaconGroups();
		for(BeaconGroup beaconGroup : beaconGroups) {
			if(beaconGroup instanceof UuidGroup) uuidGroups.add((UuidGroup) beaconGroup);
		}
		
		return uuidGroups;
	}
	
	public ArrayList<MajorGroup> getMajorGroups(String uuidGroupCode) {	
		Set<String> majorCodes = mBeaconGroups.get(uuidGroupCode).getChildCodes();
		
		ArrayList<MajorGroup> majorGroups = new ArrayList<MajorGroup>();
		
		for(String code : majorCodes) {
			majorGroups.add((MajorGroup) mBeaconGroups.get(code));
		}
		return majorGroups;
	}
	
	public ArrayList<BeaconGroup> getBeaconGroupsInAuthority() {
		ArrayList<BeaconGroup> groups = new ArrayList<BeaconGroup>(mBeaconGroups.values());
		ArrayList<BeaconGroup> willReturn = new ArrayList<BeaconGroup>();
		
		for(BeaconGroup group : groups) {
			if(group instanceof MinorGroup)
				continue;
			willReturn.add(group);
		}
		
		return willReturn;
	}
	
	
	public ArrayList<Service> getServices() {
		ArrayList<Service> s = new ArrayList<Service>(mServices.values());
		return s;
	}
	
	public ArrayList<Topology> getTopologies() {
		ArrayList<Topology> t = new ArrayList<Topology>(mTopologies.values());
		return t;
	}
	
	public void putBeacon(Beacon beacon) {
		mBeacons.put(beacon.getCode(), beacon);		
	}
	
	public void putBeaconGroup(BeaconGroup beaconGroup) {
		mBeaconGroups.put(beaconGroup.getCode(), beaconGroup);		
	}
	
	public void putService(Service service) {
		mServices.put(service.getCode(), service);
	}
	
	public void putTopology(Topology topology) {
		mTopologies.put(topology.getCode(), topology);
	}
}
