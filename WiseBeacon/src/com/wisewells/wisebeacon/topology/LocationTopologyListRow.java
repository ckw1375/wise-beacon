package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.R;

public class LocationTopologyListRow extends FrameLayout {

	private TextView mTopologyNameView;
	private EditText mXCoordinateView;
	private EditText mYCorrdinateView;
	
	public LocationTopologyListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_location_topology, this);
		mTopologyNameView = (TextView) findViewById(R.id.location_txt_name);
		mXCoordinateView = (EditText) findViewById(R.id.location_edit_x_coordinate);
		mYCorrdinateView = (EditText) findViewById(R.id.location_edit_y_coordinate);
	}
	
	public void setData(LocationTopologyListData data) {
		mTopologyNameView.setText(data.getBeaconName());
	}
}
