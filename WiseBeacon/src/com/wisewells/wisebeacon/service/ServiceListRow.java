package com.wisewells.wisebeacon.service;

import com.wisewells.sdk.datas.Service;
import com.wisewells.wisebeacon.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ServiceListRow extends FrameLayout {

	private TextView mNameView;
	
	public ServiceListRow(Context context) {
		super(context);
		
		LayoutInflater.from(context).inflate(R.layout.list_row_service, this);
		mNameView = (TextView) findViewById(R.id.service_txt_name);
	}
	
	public void setData(Service service) {
		mNameView.setText(service.getName());
	}
}
