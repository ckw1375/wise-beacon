package com.wisewells.wisebeacon.topology;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.wisebeacon.service.DetailServiceActivity;

public abstract class TopologyFragment extends Fragment {
	protected List<Beacon> mBeaconsInGroup;
	protected BaseAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBeaconsInGroup = getArguments().getParcelableArrayList(DetailServiceActivity.BUNDLE_BEACONS);
	}
	
	public abstract void replaceListViewData(List<Beacon> beacons);
}
