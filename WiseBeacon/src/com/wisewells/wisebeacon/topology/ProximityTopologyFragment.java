package com.wisewells.wisebeacon.topology;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.EditTopologyListener;
import com.wisewells.sdk.WiseManager.TopologyStateListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.Region;
import com.wisewells.sdk.service.LocationTopology.Coordinate;
import com.wisewells.sdk.service.ProximityTopology;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;

public class ProximityTopologyFragment extends BaseTopologyFragment {
	
	private EditMode mMode;
	
	private WiseManager mWiseManager;	
	private ArrayList<Beacon> mBeaconsInGroup;
	private ProximityTopology mTopology;
	private BeaconGroup mBeaconGroup;
	private Service mService;
	
	private ImageView mModifyButton;
	private ImageView mDoneButton;
	
	private ProximityTopologyListAdapter mAdapter;
	private ListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mWiseManager = WiseManager.getInstance(getActivity());		
		mBeaconsInGroup = getArguments().getParcelableArrayList(DetailServiceActivity.BUNDLE_BEACONS);
		mBeaconGroup = getArguments().getParcelable(DetailServiceActivity.BUNDLE_GROUP);
		mService = getArguments().getParcelable(DetailServiceActivity.BUNDLE_SERVICE);
		
		try {
			mTopology = (ProximityTopology )getArguments().getParcelable(DetailServiceActivity.BUNDLE_TOPOLOGY);
		} catch(ClassCastException e) {
			L.e("Topoloy type error");
		}
		if(mTopology == null) mMode = EditMode.MAKE_NEW;
		else mMode = EditMode.DISPLAY;
	}	
	
	@Override
	public void onResume() {
		super.onResume();
		if(mMode == EditMode.DISPLAY) {
			mWiseManager.startTrackingTopologyState(getActivity().getPackageName(), mService.getCode(), listener);
		}
	}
	
	@Override
	public void onPause() {
		mWiseManager.stopTrackingTopologyState(getActivity().getPackageName());
		super.onStop();
	};
	
	
	private int mPosition;
	TopologyStateListener listener = new TopologyStateListener() {
		@Override
		public void onSectorChanged(String sector) {
			
		}
		@Override
		public void onProximityChanged(Region region) {
			List<ProximityTopologyListData> datas = mAdapter.getItems();
			mPosition = -1;
			for(int i=0; i<mAdapter.getCount(); i++) {
				if(mAdapter.getItem(i).getBeacon().getRegion().equals(region)) {
					mPosition = i;
					 break;
				}
			}
			mAdapter.setBackgoundPosition(mPosition);
		}
		@Override
		public void onLocationChanged(Coordinate coord) {
			
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_proximity_topology, container, false);
		
		mModifyButton = (ImageView) v.findViewById(R.id.img_modify);
		
		mDoneButton = (ImageView) v.findViewById(R.id.img_done);
		
		mAdapter = new ProximityTopologyListAdapter(getActivity());		
		mListView = (ListView) v.findViewById(R.id.list_proximity_topology);
		mListView.setAdapter(mAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		initAdapterDatas();
		setVisibleAccordingToMode();
		return v;
	}
	
	private void initAdapterDatas() {
		mAdapter.clear();
		for(Beacon beacon : mBeaconsInGroup) {
			mAdapter.add(new ProximityTopologyListData(beacon));	
		}
		mAdapter.changeMode(mMode);
	}
	
	private void setVisibleAccordingToMode() {
		switch(mMode) {
		case MAKE_NEW:
			mModifyButton.setVisibility(View.INVISIBLE);
			mDoneButton.setVisibility(View.INVISIBLE);
			break;
		case DISPLAY:
			mModifyButton.setVisibility(View.VISIBLE);
			mDoneButton.setVisibility(View.INVISIBLE);
			break;
		case MODIFY:
			mModifyButton.setVisibility(View.INVISIBLE);
			mDoneButton.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	@Override
	public void saveTopology() {
		int size = mAdapter.getCount();
		List<String> beaconCodes = new ArrayList<String>();
		double[] ranges = new double[size];
		
		for(int i=0; i<size; i++) {
			ProximityTopologyListData item = mAdapter.getItem(i);
			beaconCodes.add(item.getBeacon().getCode());
			ranges[i] = item.getRange();
		}
		
		mWiseManager.addProximityTopology(mService.getCode(), mBeaconGroup.getCode(), beaconCodes, ranges, new EditTopologyListener() {
			@Override
			public void onSuccess(Topology topology) {
				getActivity().finish();
			}
			@Override
			public void onFail() {
			}
		});
		getActivity().finish();
	}
	
	@Override
	public void replaceListViewData(List<Beacon> beacons) {
		mBeaconsInGroup.clear();
		mBeaconsInGroup.addAll(beacons);		
		initAdapterDatas();
	}
}
