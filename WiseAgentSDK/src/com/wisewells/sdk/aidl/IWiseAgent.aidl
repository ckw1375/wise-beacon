package com.wisewells.sdk.aidl;
import com.wisewells.sdk.datas.UuidGroup;
import com.wisewells.sdk.datas.MajorGroup;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.Topology;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Region;

import com.wisewells.sdk.aidl.RangingListener;
import com.wisewells.sdk.aidl.MonitoringListener;

/*
 *	AIDL에서는 Parcel.CREATOR을 클래스에서 직접 지정해줘서, 다형성을 살릴 수 없다.
 * 	이를 해결하고자 Bundle을 이용한다.
 */
 
interface IWiseAgent {

	// BeaconGroup
	void addUuidGroup(String name);
	void addMajorGroup(String name, String parentCode);
	void addBeaconsToBeaconGroup(String groupCode, in List<Beacon> beacons); // 이거 안쓰이나???
	void addBeaconToBeaconGroup(String groupCode, in Beacon beacon);
	List<UuidGroup> getUuidGroups();
	List<MajorGroup> getMajorGroups(String uuidGroupCode);
	List<BeaconGroup> getBeaconGroups(in List<String> codes);
	Bundle getBeaconGroupsInAuthority();
	BeaconGroup getBeaconGroup(String code);
	
	// Beacon
	List<Beacon> getBeacons(String groupCode);
	
	// Topology
	Bundle getTopology(String code);
	
	// Service
	void addService(String name, String parentCode);
	List<Service> getServices(String parentCode);
	
	// Receive Beacon
	void startReceiving();
	void stopReceiving();
	List<Beacon> getAllNearbyBeacons();
		
	// Callback
}