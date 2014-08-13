package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wisewells.wisebeacon.common.BaseArrayAdapter;

public class SampleCollectionListAdapter extends BaseArrayAdapter<SampleCollectionListData> {

	public SampleCollectionListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SampleCollectionListRow view = (SampleCollectionListRow) convertView;
		if(view == null) view = new SampleCollectionListRow(mContext);
		return view;
	}
}
