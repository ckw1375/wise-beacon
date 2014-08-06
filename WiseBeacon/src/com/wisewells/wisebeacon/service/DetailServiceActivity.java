package com.wisewells.wisebeacon.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.Topology;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.common.TitleDialogSpinner;
import com.wisewells.wisebeacon.topology.LocationTopologyFragment;
import com.wisewells.wisebeacon.topology.ProximityTopologyFragment;
import com.wisewells.wisebeacon.topology.SectorTopologyFragment;
import com.wisewells.wisebeacon.topology.TopologyFragment;

public class DetailServiceActivity extends Activity {

	public static final String BUNDLE_BEACONS = "beacons";
	public static final String BUNDLE_TOPOLOGY = "topology";
	public static final String BUNDLE_SERVICE = "service";
	public static final String BUNDLE_GROUP = "group";

	private static final int TOPOLOGY_TYPE_PROXIMITY = 0;
	private static final int TOPOLOGY_TYPE_SECTOR = 1;
	private static final int TOPOLOGY_TYPE_LOCATION = 2;
	
	private static final String[] TOPOLOGY_TYPE = {
		"Proximity", "Sector", "Location"
	};
	
	private WiseManager mWiseManager;
	private Service mService;
	private Topology mTopology;
	private BeaconGroup mBeaconGroup;
	private Service mParentService;
	private ArrayList<Beacon> mBeaconsInGroup;
	private TopologyFragment mFragment;
	
	private TextView mParentServiceName;
	private TextView mChildServiceName;
	private TextView mBeaconGroupName;
	private TextView mTopologyType;	
	
	private TitleDialogSpinner mBeaconGroupSpinner;
	private TitleDialogSpinner mTopologyTypeSpinner;
	private ArrayAdapter<BeaconGroup> mBeaconGroupAdapter;
	private ArrayAdapter<String> mTopologyTypeAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_service);
		getIntentDatas();
		
		mWiseManager = WiseManager.getInstance(this);
		
		mParentServiceName = (TextView) findViewById(R.id.txt_parent_service_name);
		mParentServiceName.setText(mParentService.getName());
		
		mChildServiceName = (TextView) findViewById(R.id.txt_child_service_name);
		mChildServiceName.setText(mService.getName());
		
		mBeaconGroupName = (TextView) findViewById(R.id.txt_beacon_group_name);
		
		mTopologyType = (TextView) findViewById(R.id.txt_topology_type);
			
		mBeaconGroupAdapter = new ArrayAdapter<BeaconGroup>(this, android.R.layout.simple_spinner_dropdown_item);
		mBeaconGroupSpinner = (TitleDialogSpinner) findViewById(R.id.custom_spinner_beacon_group);
		mBeaconGroupSpinner.setAdapter(mBeaconGroupAdapter);
		mBeaconGroupSpinner.setFragmentManager(getFragmentManager());
		mBeaconGroupSpinner.setListener(new TitleDialogSpinner.TitleSpinnerListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onBeaconGroupSelected(position);
			}
		});
		
		mTopologyTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		mTopologyTypeSpinner = (TitleDialogSpinner) findViewById(R.id.custom_spinner_topology_type);
		mTopologyTypeSpinner.setAdapter(mTopologyTypeAdapter);
		mTopologyTypeSpinner.setFragmentManager(getFragmentManager());
		mTopologyTypeSpinner.setListener(new TitleDialogSpinner.TitleSpinnerListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onTopologyTypeSelected(position);
			}
		});
		
		initBeaconGroupView();
		initTopologyTypeView();
	}
	
	private void onBeaconGroupSelected(int position) {
		mBeaconGroup = mBeaconGroupAdapter.getItem(position);
		
		try {
			mBeaconsInGroup = new ArrayList<Beacon>(
					mWiseManager.getBeacons(mBeaconGroup.getCode()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		if(mFragment != null) mFragment.updateListViewWith(mBeaconsInGroup);
	}
	
	private void onTopologyTypeSelected(int position) {
		changeFragment(position);
	}
	
	private void changeFragment(int position) {
		switch(position) {
		case TOPOLOGY_TYPE_LOCATION: mFragment = new LocationTopologyFragment(); break;
		case TOPOLOGY_TYPE_PROXIMITY: mFragment = new ProximityTopologyFragment(); break;
		case TOPOLOGY_TYPE_SECTOR: mFragment = new SectorTopologyFragment(); break;
		}

		Bundle args = new Bundle();
		args.putParcelableArrayList(BUNDLE_BEACONS, mBeaconsInGroup);
		args.putParcelable(BUNDLE_TOPOLOGY, mTopology);
		args.putParcelable(BUNDLE_SERVICE, mService);
		args.putParcelable(BUNDLE_GROUP, mBeaconGroup);
		mFragment.setArguments(args);

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.layout_fragment_container, mFragment);
		ft.commit();
	}
	
	private void initBeaconGroupView() {
		if(mBeaconGroup == null) {
			mBeaconGroupName.setVisibility(View.GONE);
			mBeaconGroupSpinner.setVisibility(View.VISIBLE);
			mBeaconGroupAdapter.clear();
			mBeaconGroupAdapter.addAll(receiveBeaconGroups());
		}
		else {
			mBeaconGroupSpinner.setVisibility(View.GONE);
			mBeaconGroupName.setVisibility(View.VISIBLE);
			mBeaconGroupName.setText(mBeaconGroup.getName());
		}
	}
	
	private void initTopologyTypeView() {
		if(mTopology == null) {
			mTopologyType.setVisibility(View.GONE);
			mTopologyTypeSpinner.setVisibility(View.VISIBLE);
			mTopologyTypeAdapter.clear();
			mTopologyTypeAdapter.add("Proximity");
			mTopologyTypeAdapter.add("Sector");
			mTopologyTypeAdapter.add("Location");
		}
		else {
			mTopologyTypeSpinner.setVisibility(View.GONE);
			mTopologyType.setVisibility(View.VISIBLE);
			mTopologyType.setText(mTopology.getTypeName());
		}
	}
	
	private List<BeaconGroup> receiveBeaconGroups() {
		List<BeaconGroup> groups = new ArrayList<BeaconGroup>();
		try {
			groups = mWiseManager.getBeaconGroupsInAuthority();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return groups;
	}
	
	private void getIntentDatas() {
		mService = getIntent().getParcelableExtra(ServiceActivity.EXTRA_SERVICE);
		mTopology = getIntent().getParcelableExtra(ServiceActivity.EXTRA_TOPOLOGY);
		mBeaconGroup = getIntent().getParcelableExtra(ServiceActivity.EXTRA_BEACON_GROUP);
		mParentService = getIntent().getParcelableExtra(ServiceActivity.EXTRA_PARENT_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
