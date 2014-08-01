package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.common.BaseArrayAdapter;

public class ProximityTopologyListAdapter extends BaseArrayAdapter<ProximityTopologyListData> {

	public ProximityTopologyListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ProximityTopologyListRow view = (ProximityTopologyListRow) convertView;
		if(view == null) view = new ProximityTopologyListRow(mContext);
		view.setData(getItem(position));
		return view;
	}
}
