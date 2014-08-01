package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.Topology;
import com.wisewells.wisebeacon.common.BaseArrayAdapter;

public class TopologyListAdapter extends BaseArrayAdapter<Topology> {

	public TopologyListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TopologyListRow view = (TopologyListRow) convertView;
		if(view == null) view = new TopologyListRow(mContext);
		view.setData(getItem(position));
		return view;
	}
}
