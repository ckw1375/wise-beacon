package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.sdk.service.Sector;
import com.wisewells.wisebeacon.common.BaseArrayAdapter;

public class SectorTopologyListAdapter extends BaseArrayAdapter<Sector> {

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
	
	public int indexOf(String sectorName) {
		int pos = 0;
		for(Sector sector : mItems) {
			if(sector.getName().equals(sectorName)) {
				return pos;
			}
			pos++;
		}
		
		return -1;
	}
}
