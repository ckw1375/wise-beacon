package com.wisewells.wisebeacon.service;

import com.wisewells.sdk.datas.Service;
import com.wisewells.wisebeacon.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ServiceListRow extends FrameLayout {

	private TextView mServiceName;
	private TextView mBeaconGroupName;
	private TextView mTopologyType;
	
	public ServiceListRow(Context context) {
		super(context);
		
		LayoutInflater.from(context).inflate(R.layout.list_row_service, this);
		mServiceName = (TextView) findViewById(R.id.txt_service_name);
		mBeaconGroupName = (TextView) findViewById(R.id.txt_beacongroup_name);
		mTopologyType = (TextView) findViewById(R.id.txt_topology_type);
	}
	
	public void setData(Service service) {
		mServiceName.setText(service.getName());
		
	}
}
