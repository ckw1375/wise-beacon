package com.wisewells.wisebeacon.beacongroup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.wisebeacon.common.BaseArrayAdapter;

public class BeaconGroupListAdapter extends BaseArrayAdapter<BeaconGroupListData> {
	
	public BeaconGroupListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BeaconGroupListRow view = null;
		
		if(convertView != null) view = (BeaconGroupListRow) convertView;
		else view = new BeaconGroupListRow(mContext);
			
		view.setData(getItem(position));		
		
		return view;
	}
}
