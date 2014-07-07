package com.wisewells.wisebeacon.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.R;

public class BeaconListItemView extends FrameLayout {

	private TextView mUuidView;
	private TextView mMajorView;
	private TextView mMinorView;
	
	public BeaconListItemView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_beacon_list_item, this);
		
		mUuidView = (TextView) findViewById(R.id.beacon_list_uuid);
		mMajorView = (TextView) findViewById(R.id.beacon_list_major);
		mMinorView = (TextView) findViewById(R.id.beacon_list_minor);
	}
	
	public void setData(Beacon beacon) {
		mUuidView.setText(beacon.getUuid().toString());
		mMajorView.setText(beacon.getMajor() + "");
		mMinorView.setText(beacon.getMinor() + "");
	}
}
