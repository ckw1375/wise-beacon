package com.wisewells.wisebeacon.service;

import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;
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
		mBeaconGroupName = (TextView) findViewById(R.id.txt_beacon_group_name);
		mTopologyType = (TextView) findViewById(R.id.txt_topology_type);
	}
	
	public void setData(ServiceListData data) {
		Service service = data.getService();
		BeaconGroup group = data.getBeaconGroup();
		Topology topology = data.getTopology();
		
		if(service != null) mServiceName.setText(service.getName());
		else mServiceName.setText("없음");
		
		if(group != null) mBeaconGroupName.setText(group.getName());
		else mBeaconGroupName.setText("없음");
		
		if(topology != null) mTopologyType.setText(topology.getName());
		else mTopologyType.setText("없음");
	}
}
