package com.wisewells.wisebeacon.beacongroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.wisebeacon.R;

public class BeaconGroupListRow extends FrameLayout {

	private TextView mNameView;
	private TextView mChildCountView;
	
	public BeaconGroupListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_group, this);
		
		mNameView = (TextView) findViewById(R.id.group_txt_name);
		mChildCountView = (TextView) findViewById(R.id.group_txt_child_count);
	}

	public void setData(BeaconGroup beaconGroup) {
		mNameView.setText(beaconGroup.getName());
		mChildCountView.setText(String.valueOf(beaconGroup.getChildCodes().size()));
	}
}
