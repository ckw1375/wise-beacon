package com.wisewells.wisebeacon.topology;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.wisebeacon.R;

public class LocationTopologyFragment extends BaseTopologyFragment {
	
	private List<Beacon> mBeaconsInGroup;
	
	private LocationTopologyListAdapter mAdapter;
	private ListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBeaconsInGroup = getArguments().getParcelableArrayList(DetailServiceActivity.BUNDLE_BEACONS);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_location_topology, container, false);
		
		mAdapter = new LocationTopologyListAdapter(getActivity());
		mListView = (ListView) v.findViewById(R.id.list_location_topology_setting);
		mListView.setAdapter(mAdapter);
		initListDatas();
		return v;
	}
	
	private void initListDatas() {
		mAdapter.clear();
		for(Beacon beacon : mBeaconsInGroup) {
			mAdapter.add(new LocationTopologyListData(beacon));		
		}
	}
	
	@Override
	public void replaceListViewData(List<Beacon> beacons) {
		mBeaconsInGroup = beacons;
		initListDatas();
	}
	
	@Override
	public void saveTopology() {
		// TODO Auto-generated method stub
		
	}
}
