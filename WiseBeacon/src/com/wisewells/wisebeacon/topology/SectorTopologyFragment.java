package com.wisewells.wisebeacon.topology;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.service.SectorTopology;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;

public class SectorTopologyFragment extends BaseTopologyFragment {
	
	public static final String BUNDLE_BEACONS = "beacons";
	
	private EditMode mMode;
	private SectorTopology mTopology;
	private ArrayList<Beacon> mBeaconsInGroup;
	
	private SectorTopologyListAdapter mAdapter;
	private ListView mListView;
	
	private ImageView mModifyButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBeaconsInGroup = getArguments().getParcelableArrayList(DetailServiceActivity.BUNDLE_BEACONS);
		
		try {
			mTopology = (SectorTopology )getArguments().getParcelable(DetailServiceActivity.BUNDLE_TOPOLOGY);
		} catch(ClassCastException e) {
			L.e("Topoloy type error");
		}
		if(mTopology == null) mMode = EditMode.MAKE_NEW;
		else mMode = EditMode.DISPLAY;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sector_topology, container, false);
		
		mModifyButton = (ImageView) v.findViewById(R.id.img_modify_sector);
		mModifyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SampleCollectionActivity.class);
				intent.putParcelableArrayListExtra(BUNDLE_BEACONS, mBeaconsInGroup);
				startActivityForResult(intent, 1234);
			}
		});
		
		mAdapter = new SectorTopologyListAdapter(getActivity());		
		mListView = (ListView) v.findViewById(R.id.list_sector);
		mListView.setAdapter(mAdapter);
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != 1234 || resultCode != Activity.RESULT_OK)
			return;
		
	};
	
	@Override
	public void replaceListViewData(List<Beacon> beacons) {
	}
	
	@Override
	public void saveTopology() {
	}
}
