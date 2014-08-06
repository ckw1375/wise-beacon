package com.wisewells.wisebeacon.beacongroup;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.wisebeacon.common.BaseArrayAdapter;

public class AddBeaconToGroupBeaconListAdapter extends BaseArrayAdapter<Beacon> {

	public AddBeaconToGroupBeaconListAdapter(Context context) {
		super(context);
	}
	
	public AddBeaconToGroupBeaconListAdapter(Context context, ArrayList<Beacon> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AddBeaconToGroupBeaconListRow view = null;
		
		if(convertView != null) view = (AddBeaconToGroupBeaconListRow) convertView;
		else view = new AddBeaconToGroupBeaconListRow(mContext);
			
		view.setData(getItem(position));		
		
		return view;
	}
}
