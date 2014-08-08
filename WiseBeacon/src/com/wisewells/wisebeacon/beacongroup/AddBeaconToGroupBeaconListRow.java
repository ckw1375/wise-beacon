package com.wisewells.wisebeacon.beacongroup;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.wisebeacon.R;

public class AddBeaconToGroupBeaconListRow extends FrameLayout implements Checkable {

	private View mBackgroundView;
	private TextView mUuidView;
	private TextView mMajorView;
	private TextView mMinorView;
	private TextView mDistanceView;
	
	private boolean mChecked;
	
	public AddBeaconToGroupBeaconListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_beacon_temp, this);
		
		mBackgroundView = findViewById(R.id.row_background);
		mUuidView = (TextView) findViewById(R.id.row_uuid);
		mMajorView = (TextView) findViewById(R.id.row_major);
		mMinorView = (TextView) findViewById(R.id.row_minor);
		mDistanceView = (TextView) findViewById(R.id.row_distance);
	}
	
	public void setData(Beacon beacon) {
		mUuidView.setText(beacon.getProximityUUID());
		mMajorView.setText(beacon.getMajor() + "");
		mMinorView.setText(beacon.getMinor() + "");
		mDistanceView.setText(beacon.getDistance() + "");;
	}

	@Override
	public void setChecked(boolean checked) {
		mChecked = checked;
		notifyCheckChanged();
	}

	private void notifyCheckChanged() {
		if(mChecked)
			mBackgroundView.setBackgroundColor(Color.LTGRAY);
		else
			mBackgroundView.setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}
}
