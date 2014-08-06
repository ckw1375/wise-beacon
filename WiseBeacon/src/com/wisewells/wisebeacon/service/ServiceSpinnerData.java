package com.wisewells.wisebeacon.service;

import com.wisewells.sdk.service.Service;


public class ServiceSpinnerData {
	private String spinnerContent;
	private Service service;

	public ServiceSpinnerData(String spinnerContent) {
		this.spinnerContent = spinnerContent;
	}
	
	public ServiceSpinnerData(Service service) {
		this.service  = service;
		this.spinnerContent = service.getName();
	}
	
	public Service getService() {
		return this.service;
	}
	
	public void setSpinnerContent(String str) {
		this.spinnerContent = str;
	}
	
	@Override
	public String toString() {
		return this.spinnerContent;
	}
}
