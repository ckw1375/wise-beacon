package com.wisewells.wisebeacon.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.GetBeaconGroupListener;
import com.wisewells.sdk.WiseManager.GetServiceListener;
import com.wisewells.sdk.WiseManager.GetTopologyListener;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.topology.TopologyActivity;

public class ServiceActivity extends Activity {

	public static final String EXTRA_SERVICE_OBJECT = "service";
	
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
				mWiseManager.addService(str, mSelectedRootService.getCode());

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
		mWiseManager.getServices(mSelectedRootService.getCode(), new GetServiceListener() {
			@Override
			public void onResponseService(List<Service> services) {
					mListAdapter.replaceWith(services);
			}
		});
	}
	
	private void onListItemClicked(int position) {
		Service service = mListAdapter.getItem(position);
		
		Intent intent = new Intent(this, TopologyActivity.class);
		intent.putExtra(EXTRA_SERVICE_OBJECT, service);
		startActivity(intent);
	}
	
	private void receiveRootService() {
		mWiseManager.getServices(null, new GetServiceListener() {
			@Override
			public void onResponseService(List<Service> services) {
				ArrayList<ServiceSpinnerData> datas = new ArrayList<ServiceSpinnerData>();
				datas.add(new ServiceSpinnerData("선택하세요."));
				
				for(Service service : services) {
					datas.add(new ServiceSpinnerData(service));
				}
				
				mSpinnerAdapter.addAll(datas);
			}
		});
	}
	
	private void receiveRowLankService() {
		if(mSelectedRootService == null) return;
		
		mWiseManager.getServices(mSelectedRootService.getCode(), new GetServiceListener() {
			@Override
			public void onResponseService(List<Service> services) {
//				mListAdapter.replaceWith(services);
				receiveTopologysInService(services);
			}
		});
	}
	
	private void receiveTopologysInService(List<Service> services) {
		ArrayList<String> topologyCodes = new ArrayList<String>();
		for(Service service : services) {
			topologyCodes.add(service.getTopologyCode());
		}
		
		mWiseManager.getTopologies(topologyCodes, new GetTopologyListener() {
			@Override
			public void onResponseTopology(List<Topology> topologies) {
				receiveBeaconGroupsInService(topologies);
			}
		});
	}
	
	private void receiveBeaconGroupsInService(List<Topology> topologies) {
		ArrayList<String> groupCodes = new ArrayList<String>();
		for(Topology topology : topologies) {
			groupCodes.add(topology.getBeaconGroupCode());
		}
		
		mWiseManager.getBeaconGroups(groupCodes, new GetBeaconGroupListener() {
			@Override
			public void onResponseBeaconGroup(List<BeaconGroup> groups) {
				
			}
		});
	}
}
