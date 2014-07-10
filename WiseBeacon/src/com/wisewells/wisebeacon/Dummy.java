package com.wisewells.wisebeacon;

import java.util.ArrayList;
import java.util.UUID;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.group.UuidGroup;

public class Dummy {
	public static ArrayList<Beacon> getBeacons() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		beacons.add(new Beacon("A", "mac A", UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("B", "mac B", UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("C", "mac C", UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("D", "mac D", UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("E", "mac E", UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("F", "mac F", UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		
		return beacons;
	}	
	
	public static UuidGroup getUUidGroup() {
		UuidGroup g = new UuidGroup("uuid A", "UuidGroup A", UUID.randomUUID().toString());
		return g;
	}
	
	
}
