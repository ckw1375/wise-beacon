package com.wisewells.wisebeacon.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.BaseArrayAdapter;
import com.wisewells.wisebeacon.views.BeaconListItemView;

public class BeaconListAdapter extends BaseArrayAdapter<Beacon> {

	public BeaconListAdapter(Context context) {
		super(context);
	}
	
	public BeaconListAdapter(Context context, ArrayList<Beacon> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BeaconListItemView view = null;
		
		if(convertView != null) view = (BeaconListItemView) convertView;
		else view = new BeaconListItemView(mContext);
			
		view.setData(getItem(position));		
		
		return view;
	}
}
