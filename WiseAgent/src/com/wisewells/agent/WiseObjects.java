package com.wisewells.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.IpcUtils;

/**
 * @file	WiseObjects.java
 * @author 	Mingook
 * @date	2014. 7. 7.
 * @description
 * Beacon, Group, Service, Topology와 같이 WiseAgent가 관리하는 모든객체를 관리하는 클래스.
 * Database에 모든 객체를 저장, 관리하며 필요한 객체를 찾아 리턴해준다.
 */

public class WiseObjects implements Parcelable {
	
	private HashMap<String, Beacon> mBeacons;
	private HashMap<String, BeaconGroup> mBeaconGroups;
	private HashMap<String, Service> mServices;
	private HashMap<String, Topology> mTopologies;
		
	public WiseObjects() {
		init();
	}
	
	private WiseObjects(Parcel in) {
		mBeacons = (HashMap<String, Beacon>) IpcUtils.readMapFromParcel(in, Beacon.class.getClassLoader());
		mBeaconGroups = (HashMap<String, BeaconGroup>) IpcUtils.readMapFromParcel(in, BeaconGroup.class.getClassLoader());
		mServices = (HashMap<String, Service>) IpcUtils.readMapFromParcel(in, Service.class.getClassLoader());
		mTopologies = (HashMap<String, Topology>) IpcUtils.readMapFromParcel(in, Topology.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		IpcUtils.writeMapToParcel(dest, mBeaconGroups);
		IpcUtils.writeMapToParcel(dest, mBeacons);
		IpcUtils.writeMapToParcel(dest, mServices);
		IpcUtils.writeMapToParcel(dest, mTopologies);
	};
		
	private void init() {
		mBeacons = new HashMap<String, Beacon>();
		mBeaconGroups = new HashMap<String, BeaconGroup>();
		mServices = new HashMap<String, Service>();
		mTopologies = new HashMap<String, Topology>();
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
	
	/**
	 * @param groupCode
	 * @return 비콘그룹에 포함된 모든 비콘
	 */
	public ArrayList<Beacon> getAllBeaconsInGroup(String groupCode) {
		ArrayList<Beacon> beaconsInGroup = new ArrayList<Beacon>();
		BeaconGroup group = mBeaconGroups.get(groupCode);
		
		if(group.getDepth() != BeaconGroup.DEPTH_LEAF) {
			for(String code : group.getChildCodes()) {
				beaconsInGroup.addAll(getAllBeaconsInGroup(code));
			}
		}
		
		if(group.getDepth() == BeaconGroup.DEPTH_LEAF) {
			for(String beaconCode : group.getChildCodes()) {
				beaconsInGroup.add(mBeacons.get(beaconCode));
			}
		}
		return beaconsInGroup;
	}
	
	public ArrayList<BeaconGroup> getBeaconGroups(String parentCode) {
		if(parentCode == null)
			return getRootBeaconGroups();
		else
			return getNotRootBeaconGroups(parentCode);
	}
	
	private ArrayList<BeaconGroup> getNotRootBeaconGroups(String parentCode) {
		Set<String> codes = mBeaconGroups.get(parentCode).getChildCodes();

		ArrayList<BeaconGroup> willReturn = new ArrayList<BeaconGroup>();
		for(String code : codes) {
			willReturn.add(mBeaconGroups.get(code));
		}
		return willReturn;
	}

	private ArrayList<BeaconGroup> getRootBeaconGroups() {
		ArrayList<BeaconGroup> willReturn = new ArrayList<BeaconGroup>();
		ArrayList<BeaconGroup> beaconGroups = new ArrayList<BeaconGroup>(mBeaconGroups.values());
		for(BeaconGroup beaconGroup : beaconGroups) {
			if(beaconGroup.getDepth() == BeaconGroup.DEPTH_ROOT) willReturn.add(beaconGroup);
		}
		
		return willReturn;
	}

	/*
	 * Parameter로 로그인 정보를 받아서 권한에 맞는 그룹만 넘겨줘야 함.
	 * 현재는 Uuid, Major 모든 그룹을 다 리턴해줌.
	 */
	public ArrayList<BeaconGroup> getBeaconGroupsInAuthority() {
		ArrayList<BeaconGroup> groups = new ArrayList<BeaconGroup>(mBeaconGroups.values());
		return groups;
	}
	
	public List<Topology> getAllTopologiesInService(String serviceCode) {
		Service service = mServices.get(serviceCode);
		ArrayList<Topology> topologies = new ArrayList<Topology>();
		String topologyCode = service.getTopologyCode();
		if(topologyCode != null) topologies.add(mTopologies.get(topologyCode));
		
		Set<String> codes = service.getChildCodes();
		for(String code : codes) {
			Service s = mServices.get(code);
			topologyCode = s.getTopologyCode();
			
			if(topologyCode == null) continue;
			topologies.add(mTopologies.get(topologyCode));
		}
		
		return topologies;
	}
	
	public List<Service> getRootServices() throws RemoteException {
		ArrayList<Service> services = getServices();
		ArrayList<Service> rootServices = new ArrayList<Service>();

		for (Service service : services) {
				if (service.getTreeLevel() == Service.SERVICE_TREE_ROOT)
					rootServices.add(service);
		}

		return rootServices;
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
