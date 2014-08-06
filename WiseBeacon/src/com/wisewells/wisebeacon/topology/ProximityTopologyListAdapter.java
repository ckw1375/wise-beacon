package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.wisebeacon.common.BaseArrayAdapter;

public class ProximityTopologyListAdapter extends BaseArrayAdapter<ProximityTopologyListData> {

	private EditMode mMode;
	
	public void setMode(EditMode mode) {
		mMode = mode;
		
	}
	
	public void changeMode(EditMode mode) {
		mMode = mode;
		notifyDataSetChanged();
	}
	
	public ProximityTopologyListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ProximityTopologyListRow view = (ProximityTopologyListRow) convertView;
		if(view == null) view = new ProximityTopologyListRow(mContext);
		view.setData(getItem(position));
		view.setMode(mMode);
		return view;
	}
}

