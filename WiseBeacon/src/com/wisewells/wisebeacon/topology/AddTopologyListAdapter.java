package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.wisebeacon.BaseArrayAdapter;

public class AddTopologyListAdapter extends BaseArrayAdapter<BeaconGroup> {

	public AddTopologyListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AddTopologyListRow view = (AddTopologyListRow) convertView;
		if(view == null) view = new AddTopologyListRow(mContext);
		view.setData(getItem(position));
		return view;
	}

}
