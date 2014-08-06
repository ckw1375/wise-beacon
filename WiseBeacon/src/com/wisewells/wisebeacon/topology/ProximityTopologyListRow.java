package com.wisewells.wisebeacon.topology;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;

public class ProximityTopologyListRow extends FrameLayout {

	private ProximityTopologyListData mData;
	private TextView mBeaconName;
	private EditText mRangeEdit;
	private TextView mRangeText;
	
	public ProximityTopologyListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.list_row_proximity_topology, this);
		mBeaconName = (TextView) findViewById(R.id.txt_beacon_name);
		mRangeEdit = (EditText) findViewById(R.id.edit_range);
		mRangeEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().equals("")) {
					L.i("infinity");
					mData.setRange(Double.POSITIVE_INFINITY);
					return;
				}
				
				if(Double.parseDouble(s.toString()) == 0.0) {
					L.i("infinity");
					mData.setRange(Double.POSITIVE_INFINITY);
					return;
				}
				
				mData.setRange(Double.parseDouble(s.toString()));
				L.i("not infinity  : " + mData.getRange());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mRangeText = (TextView) findViewById(R.id.txt_range);
	}

	public void setMode(EditMode mode) {
		switch(mode) {
		case DISPLAY:
			mRangeEdit.setVisibility(View.INVISIBLE);
			mRangeText.setVisibility(View.VISIBLE);
			break;
		case MAKE_NEW:
		case MODIFY:
			mRangeEdit.setVisibility(View.VISIBLE);
			mRangeText.setVisibility(View.INVISIBLE);
			break;
		}
	}
	
	public void setData(ProximityTopologyListData data) {
		mData = data;
		mBeaconName.setText(data.getBeacon().getName());
	}
}
