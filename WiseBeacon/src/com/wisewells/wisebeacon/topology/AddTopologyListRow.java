package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.wisebeacon.R;

public class AddTopologyListRow extends FrameLayout implements Checkable {

	private boolean mChecked;
	
	private View mBackgroundView;
	private TextView mNameView;
	private TextView mNumberView;
	
	public AddTopologyListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_add_topology, this);
		mBackgroundView = findViewById(R.id.add_topology_row_background);
		mNameView = (TextView) findViewById(R.id.add_topology_txt_group_name);
		mNumberView = (TextView) findViewById(R.id.add_topology_txt_child_number);
	}
	
	public void setData(BeaconGroup group) {
		mNameView.setText(group.getName());
		mNumberView.setText(String.valueOf(group.getChildCodes().size()));
	}

	@Override
	public void setChecked(boolean checked) {
		mChecked = checked;
		toggleBackground();
	}
	
	private void toggleBackground() {
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
		mChecked = !mChecked;
	}
}
