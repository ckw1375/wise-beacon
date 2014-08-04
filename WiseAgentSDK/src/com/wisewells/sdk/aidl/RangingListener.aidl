package com.wisewells.sdk.aidl;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.Region;

interface RangingListener {
	oneway void onBeaconsDiscovered(in com.wisewells.sdk.datas.Region region, in List<Beacon> beacons);
}