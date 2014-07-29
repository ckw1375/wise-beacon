package com.wisewells.wisebeacon.service;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.Service;
import com.wisewells.wisebeacon.BaseArrayAdapter;

public class ServiceListAdapter extends BaseArrayAdapter<ServiceListData> {
	
	public ServiceListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ServiceListRow view = (ServiceListRow) convertView;
		if(view == null) view = new ServiceListRow(mContext);
		view.setData(getItem(position));
		return view;
	}
}
