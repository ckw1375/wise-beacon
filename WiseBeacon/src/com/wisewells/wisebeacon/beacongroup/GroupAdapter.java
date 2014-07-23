package com.wisewells.wisebeacon.beacongroup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.wisebeacon.BaseArrayAdapter;

public class GroupAdapter extends BaseArrayAdapter<BeaconGroup> {
	
	public GroupAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupListRow view = null;
		
		if(convertView != null) view = (GroupListRow) convertView;
		else view = new GroupListRow(mContext);
			
		view.setData(getItem(position));		
		
		return view;
	}
}
