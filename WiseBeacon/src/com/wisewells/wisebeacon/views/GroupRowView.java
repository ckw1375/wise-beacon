package com.wisewells.wisebeacon.views;

import com.wisewells.sdk.datas.group.BeaconGroup;
import com.wisewells.wisebeacon.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GroupRowView extends FrameLayout {

	private TextView mNameView;
	
	public GroupRowView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.row_group, this);
		
		mNameView = (TextView) findViewById(R.id.group_name);
	}

	public void setData(BeaconGroup beaconGroup) {
		mNameView.setText(beaconGroup.getName());
	}
}
