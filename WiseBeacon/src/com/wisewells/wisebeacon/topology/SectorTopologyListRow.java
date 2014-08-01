package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.R;

public class SectorTopologyListRow extends FrameLayout {

	private TextView mSectorName;
	private TextView mSampleNumber;
	
	public SectorTopologyListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_sector_topology, this);
		mSectorName = (TextView) findViewById(R.id.txt_sector_name);
		mSampleNumber = (TextView) findViewById(R.id.txt_sample_number);
	}
	
	public void setData(SectorTopologyListData data) {
		mSectorName.setText(data.getBeacon().getName());
	}
}
