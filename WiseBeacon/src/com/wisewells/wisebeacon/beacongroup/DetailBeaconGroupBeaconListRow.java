package com.wisewells.wisebeacon.beacongroup;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.R;

public class DetailBeaconGroupBeaconListRow extends FrameLayout {

	private TextView mBeaconName;
	private TextView mBeaconDistance;
	
	public DetailBeaconGroupBeaconListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_beacon, this);
		
		mBeaconName = (TextView) findViewById(R.id.txt_beacon_name);
		mBeaconDistance = (TextView) findViewById(R.id.txt_beacon_distance);
	}
	
	public void setData(Beacon beacon) {
		mBeaconName.setText(beacon.getName());
		mBeaconDistance.setText("xx m");
	}
}
