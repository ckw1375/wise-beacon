package com.wisewells.sdk.aidl;
import com.wisewells.sdk.beacon.Region;
import com.wisewells.sdk.service.LocationTopology;

interface TopologyStateChangeListener {
	oneway void onSectorChanged(String sectorName);
	oneway void onProximityChanged(in com.wisewells.sdk.beacon.Region region);
	oneway void onLocationChanged(in com.wisewells.sdk.service.LocationTopology.Coordinate coordinate);
}
