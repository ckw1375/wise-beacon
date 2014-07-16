package com.wisewells.wisebeacon.home;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.BaseArrayAdapter;

public class GroupAddListAdapter extends BaseArrayAdapter<Beacon> {

	public GroupAddListAdapter(Context context) {
		super(context);
	}
	
	public GroupAddListAdapter(Context context, ArrayList<Beacon> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupAddListRow view = null;
		
		if(convertView != null) view = (GroupAddListRow) convertView;
		else view = new GroupAddListRow(mContext);
			
		view.setData(getItem(position));		
		
		return view;
	}
}
