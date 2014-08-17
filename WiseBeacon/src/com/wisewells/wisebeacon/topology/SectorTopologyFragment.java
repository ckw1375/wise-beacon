package com.wisewells.wisebeacon.topology;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.TopologyStateListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.Region;
import com.wisewells.sdk.service.LocationTopology.Coordinate;
import com.wisewells.sdk.service.Sector;
import com.wisewells.sdk.service.SectorTopology;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;

public class SectorTopologyFragment extends BaseTopologyFragment {
	
	public static final String BUNDLE_BEACONS = "beacons";
	
	private EditMode mMode;
	private WiseManager mWiseManager;
	private SectorTopology mTopology;
	private ArrayList<Beacon> mBeaconsInGroup;
	private BeaconGroup mBeaconGroup;
	private Service mService;
	
	private SectorTopologyListAdapter mAdapter;
	private ListView mListView;
	
	private ImageView mModifyButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mWiseManager = WiseManager.getInstance(getActivity());
		mBeaconsInGroup = getArguments().getParcelableArrayList(DetailServiceActivity.BUNDLE_BEACONS);
		mBeaconGroup = getArguments().getParcelable(DetailServiceActivity.BUNDLE_GROUP);
		mService = getArguments().getParcelable(DetailServiceActivity.BUNDLE_SERVICE);
		
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
		if(mMode == EditMode.DISPLAY)
			mAdapter.addAll(mTopology.getSectors());
		
		mListView = (ListView) v.findViewById(R.id.list_sector);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TopologyUtils.IO_MODE = true;
				TopologyUtils.wrtieSampleDataToTextFile(getActivity(), mAdapter.getItem(position));
			}
		});
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != 1234 || resultCode != Activity.RESULT_OK)
			return;
		
		ArrayList<Sector> sectors = data.getParcelableArrayListExtra(SampleCollectionActivity.EXTRA_SECTORS_WITH_SAMPLES);
		if(sectors == null)
			return;
		mAdapter.replaceWith(sectors);
	};
	
	private int mHighlightPosition = -1;
	private int mPrevPosition = -1;
	private void highlightCurrentSector(String sectorName) {
		if(sectorName == null) {
			L.i("Phone is not in any sector");
			if(mHighlightPosition >= 0) { 
				setListViewItemColor(mHighlightPosition, Color.TRANSPARENT);
			}
			return;
		}
		
		L.i("Phone is in Sector " + sectorName);
		mHighlightPosition = mAdapter.indexOf(sectorName);
		if(mHighlightPosition != mPrevPosition) {
			if(mPrevPosition >= 0) {
				setListViewItemColor(mPrevPosition, Color.TRANSPARENT);
			}
			if(mHighlightPosition >= 0) {
				setListViewItemColor(mHighlightPosition, Color.LTGRAY);
				mPrevPosition = mHighlightPosition;
			}
		}		
	}
	
	private void setListViewItemColor(int position, int color) {
		View view = mListView.getChildAt(position);
		if(view == null) return;
		view.setBackgroundColor(color);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mWiseManager.startTrackingTopologyState(getActivity().getPackageName(), mService.getCode(), new TopologyStateListener() {
			@Override
			public void onSectorChanged(String sectorName) {
				highlightCurrentSector(sectorName);
			}
			@Override
			public void onProximityChanged(Region region) {
			}
			@Override
			public void onLocationChanged(Coordinate coordinate) {
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mMode == EditMode.DISPLAY) {
			mWiseManager.stopTrackingTopologyState(getActivity().getPackageName());
		}
	}
	
	@Override
	public void replaceListViewData(List<Beacon> beacons) {
	}
	
	@Override
	public void saveTopology() {
		ArrayList<String> beaconCodes = new ArrayList<String>();
		for(Beacon beacon : mBeaconsInGroup) 
			beaconCodes.add(beacon.getCode());
		
		mWiseManager.addSectorTopology(mService.getCode(), mBeaconGroup.getCode(), 
				beaconCodes, mAdapter.getItems());
		getActivity().finish();
	}
}
