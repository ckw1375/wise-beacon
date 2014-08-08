package com.wisewells.wisebeacon.topology;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.service.DetailServiceActivity;

public class SectorTopologyFragment extends TopologyFragment {
	
	private List<Beacon> mBeaconsInGroup;
	
	private SectorTopologyListAdapter mAdapter;
	private ListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBeaconsInGroup = getArguments().getParcelableArrayList(DetailServiceActivity.BUNDLE_BEACONS);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sector_topology, container, false);
		
		mAdapter = new SectorTopologyListAdapter(getActivity());		
		mListView = (ListView) v.findViewById(R.id.list_sector);
		mListView.setAdapter(mAdapter);
		initListDatas();
		return v;
	}
	
	private void initListDatas() {
		mAdapter.clear();
		for(Beacon beacon : mBeaconsInGroup) {
			mAdapter.add(new SectorTopologyListData(beacon));		
		}
	}
	
	@Override
	public void replaceListViewData(List<Beacon> beacons) {
		mBeaconsInGroup = beacons;
		initListDatas();
	}
}
