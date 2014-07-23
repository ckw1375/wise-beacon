package com.wisewells.wisebeacon.beacongroup;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.BaseArrayAdapter;

public class AddGroupListAdapter extends BaseArrayAdapter<Beacon> {

	public AddGroupListAdapter(Context context) {
		super(context);
	}
	
	public AddGroupListAdapter(Context context, ArrayList<Beacon> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AddGroupListRow view = null;
		
		if(convertView != null) view = (AddGroupListRow) convertView;
		else view = new AddGroupListRow(mContext);
			
		view.setData(getItem(position));		
		
		return view;
	}
}
