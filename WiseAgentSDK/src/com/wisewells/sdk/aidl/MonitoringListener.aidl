package com.wisewells.sdk.aidl;
import com.wisewells.sdk.ibeacon.Region;

interface MonitoringListener {
	oneway void onEnteredRegion(in com.wisewells.sdk.ibeacon.Region region);
	oneway void onExitedRegion(in com.wisewells.sdk.ibeacon.Region region);
}
