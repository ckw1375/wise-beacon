package com.wisewells.agent;

import java.util.ArrayList;
import java.util.UUID;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.UuidGroup;

public class Dummy {
	public static ArrayList<Beacon> getBeacons() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		for(int i=0; i<10; i++) {
			Beacon beacon = new Beacon(String.format("3F-43-DE-%02d", i), UUID.randomUUID().toString(), 1*i, 2*i, 3*i, 4*i);
			beacon.setCode("code " + i);
			beacons.add(beacon);
		}
		
		return beacons;
	}	
	
	public static UuidGroup getUUidGroup() {
		UuidGroup g = new UuidGroup("지오다노");
		g.setCode("wise");
		g.setUuid("DF-36-34-A3");
		return g;
	}
	
	public static UuidGroup getUUidGroup2() {
		UuidGroup g = new UuidGroup("교보문고");
		g.setCode("wise2");
		g.setUuid("AC-36-11-A3");
		return g;
	}
}
