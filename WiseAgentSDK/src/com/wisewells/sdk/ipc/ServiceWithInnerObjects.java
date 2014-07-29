package com.wisewells.sdk.ipc;

import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;

public class ServiceWithInnerObjects {
	private Service service;
	private Topology topology;
	private BeaconGroup beaconGroup;
	
	public ServiceWithInnerObjects(Service service, Topology topology, BeaconGroup beaconGroup) {
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
	
	
}
