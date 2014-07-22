package com.wisewells.wisebeacon.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.wisebeacon.R;

public class GroupRowView extends FrameLayout {

	private TextView mNameView;
	private TextView mChildCountView;
	
	public GroupRowView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.row_group, this);
		
		mNameView = (TextView) findViewById(R.id.group_txt_name);
		mChildCountView = (TextView) findViewById(R.id.group_txt_child_count);
	}

	public void setData(BeaconGroup beaconGroup) {
		mNameView.setText(beaconGroup.getName());
		mChildCountView.setText(String.valueOf(beaconGroup.getChildCodes().size()));
	}
}
