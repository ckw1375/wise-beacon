package com.wisewells.sdk.aidl;

import com.wisewells.sdk.beacon.UuidGroup;
import com.wisewells.sdk.beacon.MajorGroup;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.Region;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.aidl.TopologyStateChangeListener;
import com.wisewells.sdk.aidl.EditObjectListener;

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
	//List<BeaconGroup> getBeaconGroups(in List<String> codes);
	Bundle getBeaconGroupsInAuthority();
	Bundle getBeaconGroup(String code);
	
	// Beacon
	List<Beacon> getBeacons(String groupCode);
	BeaconVector getBeaconVector(String groupCode);
	
	// Topology
	Bundle getTopology(String code);
	void addProximityTopology(String serviceCode, String groupCode, in String[] beaconCodes, in double[] ranges);
	void addLocationTopology();
	void addSectorTopology();
	
	// Service
	void addService(String name, String parentCode, in EditObjectListener listener);
	List<Service> getRootServices();
	List<Service> getChildServices(String parentCode);
	
	// Receive Beacon
	void startReceiving();
	void stopReceiving();
	List<Beacon> getAllNearbyBeacons();
		
	// Callback
	void startTracking(String packageName, String serviceCode, in TopologyStateChangeListener listener);
	void stopTracking(String packageName);
}