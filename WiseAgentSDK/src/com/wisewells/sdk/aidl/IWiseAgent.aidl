package com.wisewells.sdk.aidl;
import com.wisewells.sdk.datas.UuidGroup;
import com.wisewells.sdk.datas.MajorGroup;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.Topology;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.ibeacon.Region;

interface IWiseAgent {
	// BeaconGroup
	void addBeaconGroup(String name, String parentCode);
	void addBeaconsToBeaconGroup(String groupCode, in List<Beacon> beacons); // 이거 안쓰이나???
	void addBeaconToBeaconGroup(String groupCode, in Beacon beacon);
	List<UuidGroup> getUuidGroups();
	List<MajorGroup> getMajorGroups(String uuidGroupCode);
	List<BeaconGroup> getBeaconGroups(in List<String> codes);
	
	// Beacon
	List<Beacon> getBeacons(String groupCode);
	
	// Topology
	List<Topology> getTopologies(in List<String> codes);
	
	// Service
	void addService(String name, String parentCode);
	List<Service> getServices(String parentCode);
	
	// Estimote
	void startRanging(in Region region);
	void stopRanging(in Region region);
	void startMonitoring(in Region region);
	void stopMonitoring(in Region region);
	void setForegroundScanPeriod(long scanPeriodMillis, long waitTimeMillis);
	void setBackgroundScanPeriod(long scanPeriodMillis, long waitTimeMillis);
}