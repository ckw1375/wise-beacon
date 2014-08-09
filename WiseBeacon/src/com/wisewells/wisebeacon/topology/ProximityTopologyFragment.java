package com.wisewells.wisebeacon.topology;

import java.util.List;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.aidl.TopologyStateChangeListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.Region;
import com.wisewells.sdk.service.ProximityTopology;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.LocationTopology.Coordinate;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.service.DetailServiceActivity;

public class ProximityTopologyFragment extends TopologyFragment {
	
	private EditMode mMode;
	
	private WiseManager mWiseManager;	
	private List<Beacon> mBeaconsInGroup;
	private ProximityTopology mTopology;
	private BeaconGroup mBeaconGroup;
	private Service mService;
	
	private ImageView mModifyView;
	private ImageView mDoneView;
	
	private ViewGroup mSaveCancelGroup;
	private Button mSaveButton;
	private Button mCancelButton;
	private Button mDisplayListButton;
	
 	
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
			try {
				mWiseManager.startTracking(getActivity().getPackageName(), mService.getCode(), listener);
			} catch (Exception e) { 
				L.e("Start Tacking Error");
			}
		}
	}
	
	@Override
	public void onStop() {
		try {
			mWiseManager.stopTracking(getActivity().getPackageName());
		} catch (RemoteException e) {
			L.e("Stop Tracking Error");
		}
		super.onStop();
	};
	
	
	private int mPosition = -1;
	TopologyStateChangeListener listener = new TopologyStateChangeListener.Stub() {
		@Override
		public void onSectorChanged(String sector) throws RemoteException {
		}
		@Override
		public void onProximityChanged(Region region) throws RemoteException {
			List<ProximityTopologyListData> datas = mAdapter.getItems();

			for(int i=0; i<mAdapter.getCount(); i++) {
				if(mAdapter.getItem(i).getBeacon().getRegion().equals(region)) {
					mPosition = i;
					 break;
				}
			}
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mAdapter.setBackgoundPosition(mPosition);
				}
			});
			
		}
		@Override
		public void onLocationChanged(Coordinate coord) throws RemoteException {
			
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_proximity_topology, container, false);
		
		mModifyView = (ImageView) v.findViewById(R.id.img_modify);
		
		mDoneView = (ImageView) v.findViewById(R.id.img_done);
		
		mSaveCancelGroup = (ViewGroup) v.findViewById(R.id.layout_save_cancel_group);
		
		mSaveButton = (Button) v.findViewById(R.id.btn_save);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSaveButtonClicked();
			}
		});
		
		mCancelButton = (Button) v.findViewById(R.id.btn_cancel);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		
		mDisplayListButton = (Button) v.findViewById(R.id.btn_display_list);
		
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
			mModifyView.setVisibility(View.INVISIBLE);
			mDoneView.setVisibility(View.INVISIBLE);
			mDisplayListButton.setVisibility(View.INVISIBLE);
			mSaveCancelGroup.setVisibility(View.VISIBLE);
			break;
		case DISPLAY:
			mModifyView.setVisibility(View.VISIBLE);
			mDoneView.setVisibility(View.INVISIBLE);
			mDisplayListButton.setVisibility(View.VISIBLE);
			mSaveCancelGroup.setVisibility(View.INVISIBLE);
			break;
		case MODIFY:
			mModifyView.setVisibility(View.INVISIBLE);
			mDoneView.setVisibility(View.VISIBLE);
			mDisplayListButton.setVisibility(View.INVISIBLE);
			mSaveCancelGroup.setVisibility(View.INVISIBLE);
			break;
		}
	}
	
	private void onSaveButtonClicked() {
		int size = mAdapter.getCount();
		String[] beaconCodes = new String[size];
		double[] ranges = new double[size];
		
		for(int i=0; i<size; i++) {
			ProximityTopologyListData item = mAdapter.getItem(i);
			beaconCodes[i] = item.getBeacon().getCode();
			ranges[i] = item.getRange();
		}
		
		try {
			mWiseManager.addProximityTopology(mService.getCode(), mBeaconGroup.getCode(), beaconCodes, ranges);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		getActivity().finish();
	}
	
	@Override
	public void replaceListViewData(List<Beacon> beacons) {
		mBeaconsInGroup = beacons;
		initAdapterDatas();
	}
}
