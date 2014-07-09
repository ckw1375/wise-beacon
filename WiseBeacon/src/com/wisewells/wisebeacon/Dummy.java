package com.wisewells.wisebeacon;

import java.util.ArrayList;
import java.util.UUID;

import com.wisewells.sdk.datas.Beacon;

public class Dummy {
	public static ArrayList<Beacon> getBeacons() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		beacons.add(new Beacon("A", "mac A", null, UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("B", "mac B", null, UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("C", "mac C", null, UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("D", "mac D", null, UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("E", "mac E", null, UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		beacons.add(new Beacon("F", "mac F", null, UUID.randomUUID().toString(), 1, 2, 10, 15, 20));
		
		return beacons;
	}	
}
