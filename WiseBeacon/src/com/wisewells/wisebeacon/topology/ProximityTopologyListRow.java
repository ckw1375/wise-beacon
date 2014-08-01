package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.R;

public class ProximityTopologyListRow extends FrameLayout {

	private TextView mBeaconName;
	private EditText mRange;
	
	public ProximityTopologyListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_proximity_topology, this);
		mBeaconName = (TextView) findViewById(R.id.txt_beacon_name);
		mRange = (EditText) findViewById(R.id.edit_range);
	}
	
	public void setData(ProximityTopologyListData data) {
		mBeaconName.setText(data.getBeacon().getName());
	}
}
