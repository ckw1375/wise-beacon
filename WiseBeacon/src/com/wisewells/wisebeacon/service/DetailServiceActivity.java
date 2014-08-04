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
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.topology.LocationTopologyFragment;
import com.wisewells.wisebeacon.topology.ProximityTopologyFragment;
import com.wisewells.wisebeacon.topology.SectorTopologyFragment;
import com.wisewells.wisebeacon.topology.TopologyFragment;

public class DetailServiceActivity extends Activity {

	public static final String BUNDLE_BEACONS = "beacons";
	
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
	private Button mSaveButton;
	private Button mCancelButton;
	
	private Spinner mBeaconGroupSpinner;
	private Spinner mTopologyTypeSpinner;
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
		
		mSaveButton = (Button) findViewById(R.id.btn_save_topology);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSaveButtonClicked();
			}
		});
		
		mCancelButton = (Button) findViewById(R.id.btn_cancel);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCancelButtonClicked();
			}
		});
			
		mBeaconGroupAdapter = new ArrayAdapter<BeaconGroup>(this, android.R.layout.simple_spinner_dropdown_item);
		mBeaconGroupSpinner = (Spinner) findViewById(R.id.spinner_beacon_group);
		mBeaconGroupSpinner.setAdapter(mBeaconGroupAdapter);
		mBeaconGroupSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onBeaconGroupSelected(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		mTopologyTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		mTopologyTypeSpinner = (Spinner) findViewById(R.id.spinner_topology_type);
		mTopologyTypeSpinner.setAdapter(mTopologyTypeAdapter);
		mTopologyTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onTopologyTypeSelected(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		initBeaconGroupView();
		initTopologyTypeView();		
	}
	
	private void onCancelButtonClicked() {
		
	}

	private void onSaveButtonClicked() {
		
	}

	private void onBeaconGroupSelected(int position) {
		try {
			mBeaconsInGroup = new ArrayList<Beacon>(
					mWiseManager.getBeacons(mBeaconGroupAdapter.getItem(position).getCode()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		if(mFragment != null) mFragment.updateListViewWith(mBeaconsInGroup);
	}
	
	private void onTopologyTypeSelected(int position) {
		switch(position) {
		case TOPOLOGY_TYPE_LOCATION: mFragment = new LocationTopologyFragment(); break;
		case TOPOLOGY_TYPE_PROXIMITY: mFragment = new ProximityTopologyFragment(); break;
		case TOPOLOGY_TYPE_SECTOR: mFragment = new SectorTopologyFragment(); break;
		}

		Bundle args = new Bundle();
		args.putParcelableArrayList(BUNDLE_BEACONS, mBeaconsInGroup);
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
