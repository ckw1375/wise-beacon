package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.service.Sector;
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
	
	public void setData(Sector data) {
		mSectorName.setText(data.getName());
		mSampleNumber.setText(data.getSampleNumber() + "");
	}
}
