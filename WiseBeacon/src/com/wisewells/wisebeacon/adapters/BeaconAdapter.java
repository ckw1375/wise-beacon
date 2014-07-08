package com.wisewells.wisebeacon.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.BaseArrayAdapter;
import com.wisewells.wisebeacon.views.BeaconRowView;

public class BeaconAdapter extends BaseArrayAdapter<Beacon> {

	public BeaconAdapter(Context context) {
		super(context);
	}
	
	public BeaconAdapter(Context context, ArrayList<Beacon> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BeaconRowView view = null;
		
		if(convertView != null) view = (BeaconRowView) convertView;
		else view = new BeaconRowView(mContext);
			
		view.setData(getItem(position));		
		
		return view;
	}
}
