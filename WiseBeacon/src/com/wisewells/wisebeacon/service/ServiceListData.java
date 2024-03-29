package com.wisewells.wisebeacon.service;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.Topology;

public class ServiceListData {
	private Service service;
	private Topology topology;
	private BeaconGroup beaconGroup;
	
	public ServiceListData(final WiseManager manager, Service service) {
		this.service = service;
		this.topology = null;
		this.beaconGroup = null;
		
		String tc = service.getTopologyCode();
		if(tc == null || tc.equals("")) 
			return;
		this.topology = manager.getTopology(tc);
		
		String gc = this.topology.getBeaconGroupCode();
		if(this.topology == null || gc == null || gc.equals(""))
			return;
		this.beaconGroup = manager.getBeaconGroup(gc);
	}

	public Service getService() {
		return this.service;
	}

	public Topology getTopology() {
		return this.topology;
	}

	public BeaconGroup getBeaconGroup() {
		return this.beaconGroup;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}

	public void setBeaconGroup(BeaconGroup beaconGroup) {
		this.beaconGroup = beaconGroup;
	}
}
