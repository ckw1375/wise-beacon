package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.common.BaseArrayAdapter;

public class SectorTopologyListAdapter extends BaseArrayAdapter<SectorTopologyListData> {

	public SectorTopologyListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SectorTopologyListRow view = (SectorTopologyListRow) convertView;
		if(view == null) view = new SectorTopologyListRow(mContext);
		view.setData(getItem(position));
		return view;
	}
}
