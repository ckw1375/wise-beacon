package com.wisewells.wisebeacon.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.group.BeaconGroup;
import com.wisewells.wisebeacon.BaseArrayAdapter;
import com.wisewells.wisebeacon.views.BeaconRowView;
import com.wisewells.wisebeacon.views.GroupRowView;

public class GroupAdapter extends BaseArrayAdapter<BeaconGroup> {
	
	public GroupAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupRowView view = null;
		
		if(convertView != null) view = (GroupRowView) convertView;
		else view = new GroupRowView(mContext);
			
		view.setData(getItem(position));		
		
		return view;
	}
}
