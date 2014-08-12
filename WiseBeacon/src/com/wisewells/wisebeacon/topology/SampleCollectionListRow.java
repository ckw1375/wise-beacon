package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wisewells.wisebeacon.R;

public class SampleCollectionListRow extends FrameLayout {

	private TextView mSectorName;
	private TextView mSampleNumber;
	private ImageView mSectorState;
	private ImageView mSampleReset;
	
	public SampleCollectionListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_sample, this);
		
		mSectorName = (TextView) findViewById(R.id.txt_sector_name);
		mSampleNumber = (TextView) findViewById(R.id.txt_sample_number);
		mSectorState = (ImageView) findViewById(R.id.img_sample_state);
		mSampleReset = (ImageView) findViewById(R.id.Img_sample_reset);
	}
	
	
}
