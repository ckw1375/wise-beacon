package com.wisewells.wisebeacon.beacongroup;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.beacon.Beacon;
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

	public void setData(BeaconGroupListData data) {
		mNameView.setText(data.getBeaconGroup().getName());
		
		List<Beacon> beacons = data.getBeacons();
		mChildCountView.setText(beacons.size() + "");
	}
}
