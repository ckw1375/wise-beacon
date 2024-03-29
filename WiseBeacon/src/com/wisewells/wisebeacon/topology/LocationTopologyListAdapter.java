package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.wisebeacon.common.BaseArrayAdapter;

public class LocationTopologyListAdapter extends BaseArrayAdapter<LocationTopologyListData> {

	public LocationTopologyListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LocationTopologyListRow view = (LocationTopologyListRow) convertView;
		if(view == null) view = new LocationTopologyListRow(mContext);
		view.setData(getItem(position));
		return view;
	}
}
