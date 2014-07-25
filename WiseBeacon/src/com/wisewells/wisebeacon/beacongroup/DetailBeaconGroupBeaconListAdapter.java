package com.wisewells.wisebeacon.beacongroup;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.BaseArrayAdapter;

public class DetailBeaconGroupBeaconListAdapter extends BaseArrayAdapter<Beacon> {

	public DetailBeaconGroupBeaconListAdapter(Context context) {
		super(context);
	}
	
	public DetailBeaconGroupBeaconListAdapter(Context context, ArrayList<Beacon> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DetailBeaconGroupBeaconListRow view = null;
		
		if(convertView != null) view = (DetailBeaconGroupBeaconListRow) convertView;
		else view = new DetailBeaconGroupBeaconListRow(mContext);
			
		view.setData(getItem(position));		
		
		return view;
	}
}
