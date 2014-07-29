package com.wisewells.wisebeacon.service;

import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;

public class ServiceListData {
	private Service service;
	private Topology topology;
	private BeaconGroup beaconGroup;
	
	public ServiceListData(Service service, Topology topology, BeaconGroup beaconGroup) {
		this.service = service;
		this.topology = topology;
		this.beaconGroup = beaconGroup;
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
