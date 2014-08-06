package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.wisebeacon.R;

public class LocationTopologyListRow extends FrameLayout {

	private TextView mBeaconName;
	private EditText mXCoordinate;
	private EditText mYCorrdinate;
	
	public LocationTopologyListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_location_topology, this);
		mBeaconName = (TextView) findViewById(R.id.txt_beacon_name);
		mXCoordinate = (EditText) findViewById(R.id.edit_x_coordinate);
		mYCorrdinate = (EditText) findViewById(R.id.edit_y_coordinate);
	}
	
	public void setData(LocationTopologyListData data) {
		mBeaconName.setText(data.getBeacon().getName());
	}
}
