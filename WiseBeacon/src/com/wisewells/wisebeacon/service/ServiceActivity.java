package com.wisewells.wisebeacon.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.topology.TopologyActivity;

public class ServiceActivity extends Activity {

	public static final String EXTRA_SERVICE = "service";
	public static final String EXTRA_TOPOLOGY = "topology";
	public static final String EXTRA_BEACON_GROUP = "group";
	public static final String EXTRA_PARENT_SERVICE = "parent";
	
	private WiseManager mWiseManager;
	private Service mSelectedRootService;
	
	private Button mAddButton;
	private Spinner mSpinner;
	private ArrayAdapter<ServiceSpinnerData> mSpinnerAdapter;
	private ListView mListView;
	private ServiceListAdapter mListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service);
		
		mWiseManager = WiseManager.getInstance(this);
		
		mAddButton = (Button) findViewById(R.id.btn_add_service);
		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddButtonClicked();
			}
		});
		
		mSpinnerAdapter = new ArrayAdapter<ServiceSpinnerData>(this, android.R.layout.simple_spinner_dropdown_item);
		
		mSpinner = (Spinner) findViewById(R.id.spinner_root_service);
		mSpinner.setAdapter(mSpinnerAdapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onRootServiceSelected(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		mListAdapter = new ServiceListAdapter(this);		
		
		mListView = (ListView) findViewById(R.id.list_low_rank_service);
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListItemClicked(position);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		receiveRootService();
		
	}

	private void onAddButtonClicked() {
		ServiceDialog dialog = new ServiceDialog();
		dialog.show(getFragmentManager(), "dialog");
		dialog.setConfirmListener(new ServiceDialog.ConfirmListener() {
			
			@Override
			public void onConfirmButtonClicked(String str) {
				try {
					mWiseManager.addService(str, mSelectedRootService.getCode());
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				/*
				 * add beacon이 완료된것이 확인되면! (리스터 이용) display 해줘야 한다!!
				 */
				receiveRowLankService();
			}
		});
	}
	
	private void onRootServiceSelected(int position) {
		Service service = mSpinnerAdapter.getItem(position).getService();
		if(service == null) {
			mListAdapter.clear();
			mSelectedRootService = null;
			return;
		}
		
		mSelectedRootService = service;
		receiveRowLankService();
	}
	
	private void onListItemClicked(int position) {
		Service service = mListAdapter.getItem(position).getService();
		Topology topology = mListAdapter.getItem(position).getTopology();
		BeaconGroup group = mListAdapter.getItem(position).getBeaconGroup();
		
		Intent intent = new Intent(this, DetailServiceActivity.class);
		intent.putExtra(EXTRA_SERVICE, service);
		intent.putExtra(EXTRA_TOPOLOGY, topology);
		intent.putExtra(EXTRA_BEACON_GROUP, group);
		intent.putExtra(EXTRA_PARENT_SERVICE, mSelectedRootService);
		
		startActivity(intent);
	}
	
	private void receiveRootService() {
		List<Service> services = new ArrayList<Service>();
		try {
			services = mWiseManager.getRootServices();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		ArrayList<ServiceSpinnerData> datas = new ArrayList<ServiceSpinnerData>();
		datas.add(new ServiceSpinnerData("선택하세요."));
		
		for(Service service : services) {
			datas.add(new ServiceSpinnerData(service));
		}
		
		mSpinnerAdapter.clear();
		mSpinnerAdapter.addAll(datas);
	}
	
	private void receiveRowLankService() {
		if(mSelectedRootService == null) return;
		
		List<Service> services = new ArrayList<Service>();
		try {
			services = mWiseManager.getServices(mSelectedRootService.getCode());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		List<ServiceListData> datas = new ArrayList<ServiceListData>();
		for(Service service : services)
			datas.add(new ServiceListData(service, null, null));
		
		receiveTopologysInService(datas);
	}
	
	private void receiveTopologysInService(List<ServiceListData> datas) {
		for(ServiceListData data : datas) {
			Service service = data.getService();
			if(service == null) {
				continue;
			}
			
			String topologyCode = service.getTopologyCode();
			if(topologyCode == null) {
				continue;				
			}
			
			try {
				Topology topology = mWiseManager.getTopology(topologyCode);
				data.setTopology(topology);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		receiveBeaconGroupsInService(datas);
	}
	
	private void receiveBeaconGroupsInService(List<ServiceListData> datas) {
		for(ServiceListData data : datas) {
			Topology topology = data.getTopology();
			if(topology == null) {
				continue;
			}
			
			String groupCode = topology.getBeaconGroupCode();
			if(groupCode == null) {
				continue;				
			}
			
			try {
				BeaconGroup beaconGroup = mWiseManager.getBeaconGroup(groupCode);
				data.setBeaconGroup(beaconGroup);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		mListAdapter.replaceWith(datas);
	}
}
