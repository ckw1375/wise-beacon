package com.wisewells.wisebeacon.service;

import com.wisewells.sdk.datas.Service;

public class ServiceSpinnerData {
	private Service service;
	
	public ServiceSpinnerData(Service service) {
		this.service  = service;
	}
	
	@Override
	public String toString() {
		return this.service.getName();
	}
	
	public Service getService() {
		return this.service;
	}
}
