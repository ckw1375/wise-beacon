package com.wisewells.wisebeacon;

import java.util.ArrayList;
import java.util.UUID;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.UuidGroup;

public class Dummy {
	public static ArrayList<Beacon> getBeacons() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		for(int i=0; i<10; i++) {
			Beacon beacon = new Beacon("3F-43-DE-FD", UUID.randomUUID().toString(), 1, 2, 3, 4);
			beacon.setCode("code " + i);
			beacons.add(beacon);
		}
		
		return beacons;
	}	
	
	public static UuidGroup getUUidGroup() {
		UuidGroup g = new UuidGroup("지오다노");
		g.setCode("uuid group A");
		g.setUuid("DF-36-34-A3");
		return g;
	}
}
