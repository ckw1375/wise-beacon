package com.wisewells.sdk.aidl;
import com.wisewells.sdk.datas.Region;

interface MonitoringListener {
	oneway void onEnteredRegion(in com.wisewells.sdk.datas.Region region);
	oneway void onExitedRegion(in com.wisewells.sdk.datas.Region region);
}
