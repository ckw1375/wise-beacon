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

public class AddGroupListRow extends FrameLayout implements Checkable {

	private View mBackgroundView;
	private TextView mUuidView;
	private TextView mMajorView;
	private TextView mMinorView;
	
	private boolean mChecked;
	
	public AddGroupListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_add_group, this);
		
		mBackgroundView = findViewById(R.id.row_background);
		mUuidView = (TextView) findViewById(R.id.row_uuid);
		mMajorView = (TextView) findViewById(R.id.row_major);
		mMinorView = (TextView) findViewById(R.id.row_minor);
	}
	
	public void setData(Beacon beacon) {
		mUuidView.setText(beacon.getUuid().toString());
		mMajorView.setText(beacon.getMajor() + "");
		mMinorView.setText(beacon.getMinor() + "");
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
