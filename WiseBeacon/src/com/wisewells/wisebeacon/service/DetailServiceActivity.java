package com.wisewells.wisebeacon.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.topology.LocationTopologyFragment;

public class DetailServiceActivity extends Activity {

	private WiseManager mWiseManager;
	private Service mService;
	private Topology mTopology;
	private BeaconGroup mBeaconGroup;
	private Service mParentService;
	
	private TextView mParentServiceName;
	private TextView mChildServiceName;
	private TextView mBeaconGroupName;
	private TextView mTopologyType;	
	
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
			
		mBeaconGroupAdapter = new ArrayAdapter<BeaconGroup>(this, android.R.layout.simple_spinner_dropdown_item);
		mBeaconGroupSpinner = (Spinner) findViewById(R.id.spinner_beacon_group);
		mBeaconGroupSpinner.setAdapter(mBeaconGroupAdapter);		
		
		mTopologyTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		mTopologyTypeSpinner = (Spinner) findViewById(R.id.spinner_topology_type);
		mTopologyTypeSpinner.setAdapter(mTopologyTypeAdapter);
		
		initBeaconGroupView();
		initTopologyTypeView();		
		initFragmentArea();
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
	
	private void initFragmentArea() {
		Fragment fragment = null;
		
		if(mTopology == null) fragment = new LocationTopologyFragment();
		else {
			switch(mTopology.getType()) {
			case Topology.TYPE_LOCATION: fragment = new Fragment(); break;
			case Topology.TYPE_PROXIMITY: fragment = new Fragment(); break;
			case Topology.TYPE_SECTOR: fragment = new Fragment(); break;
			}
		}
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.layout_fragment_container, fragment);
		ft.commit();
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
