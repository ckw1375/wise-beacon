package com.wisewells.wisebeacon.beacongroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.wisebeacon.R;

public class GroupListRow extends FrameLayout {

	private TextView mNameView;
	private TextView mChildCountView;
	
	public GroupListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.group_list_row, this);
		
		mNameView = (TextView) findViewById(R.id.group_txt_name);
		mChildCountView = (TextView) findViewById(R.id.group_txt_child_count);
	}

	public void setData(BeaconGroup beaconGroup) {
		mNameView.setText(beaconGroup.getName());
		mChildCountView.setText(String.valueOf(beaconGroup.getChildCodes().size()));
	}
}
