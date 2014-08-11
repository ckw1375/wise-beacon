package com.wisewells.wisebeacon.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.EditServiceListener;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.common.OneEditTwoButtonsDialog;
import com.wisewells.wisebeacon.common.TitleDialogSpinner;
import com.wisewells.wisebeacon.common.TitleDialogSpinnerAdapter;

public class ServiceActivity extends Activity {

	public static final String EXTRA_SERVICE = "service";
	public static final String EXTRA_TOPOLOGY = "topology";
	public static final String EXTRA_BEACON_GROUP = "group";
	public static final String EXTRA_PARENT_SERVICE = "parent";
	
	private WiseManager mWiseManager;
	private Service mSelectedRootService;
	
	private ImageView mAddRootServiceButton;
	private ImageView mAddServiceButton;
	private	TitleDialogSpinner mRootServiceSpinner;
	private TitleDialogSpinnerAdapter<ServiceSpinnerData> mSpinnerAdapter;
	private ListView mChildSerivceListView;
	private ServiceListAdapter mListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service);
		
		mWiseManager = WiseManager.getInstance(this);
		
		mAddRootServiceButton = (ImageView) findViewById(R.id.img_add_root_service);
		mAddRootServiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddRootServiceClicked();
			}
		});
		
		mAddServiceButton = (ImageView) findViewById(R.id.img_add_service);
		mAddServiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddButtonClicked();
			}
		});
		
		mSpinnerAdapter = new TitleDialogSpinnerAdapter<ServiceSpinnerData>(this);
		
		mListAdapter = new ServiceListAdapter(this);		
		
		mChildSerivceListView = (ListView) findViewById(R.id.list_low_rank_service);
		mChildSerivceListView.setAdapter(mListAdapter);
		mChildSerivceListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListItemClicked(position);
			}
		});
		
		mRootServiceSpinner = (TitleDialogSpinner) findViewById(R.id.custom_title_spinner);
		mRootServiceSpinner.setFragmentManager(getFragmentManager());
		mRootServiceSpinner.setPrompt("상위서비스");
		mRootServiceSpinner.setAdapter(mSpinnerAdapter);
		mRootServiceSpinner.setOnItemSelectedListener(new TitleDialogSpinner.OnSpinnerItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				L.d("ServiceActivity item select");
				onRootServiceSelected(position);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		receiveRootService();
	}
	
	private void onAddRootServiceClicked() {
		OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
		dialog.setPrompt("서비스 생성");
		dialog.setEditTitle("서비스 명");
		dialog.setListener(new OneEditTwoButtonsDialog.DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {

				mWiseManager.addService(str, null, new EditServiceListener() {
					@Override
					public void onEditSuccess(Service service) {
						L.d("on edit success");
						mSpinnerAdapter.add(new ServiceSpinnerData(service));
					}
					@Override
					public void onEditFail() {

					}
				}); 

			}
		});
		
		dialog.show(getFragmentManager(), "dialog");
	}

	private void onAddButtonClicked() {
		OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
		dialog.setPrompt("서비스 생성");
		dialog.setEditTitle("서비스 명");
		dialog.setListener(new OneEditTwoButtonsDialog.DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {
				mWiseManager.addService(str, mSelectedRootService.getCode(), new EditServiceListener() {
					@Override
					public void onEditSuccess(Service service) {
						mListAdapter.add(new ServiceListData(mWiseManager, service));
						mListAdapter.notifyDataSetChanged();
					}
					@Override
					public void onEditFail() {

					}
				});
			}
		});
		
		dialog.show(getFragmentManager(), "dialog");
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
		services = mWiseManager.getRootServices();
		
		ArrayList<ServiceSpinnerData> datas = new ArrayList<ServiceSpinnerData>();		
		for(Service service : services) {
			datas.add(new ServiceSpinnerData(service));
		}
		
		mSpinnerAdapter.clear();
		mSpinnerAdapter.addAll(datas);
	}
	
	private void receiveRowLankService() {
		if(mSelectedRootService == null) return;
		
		List<Service> services = new ArrayList<Service>();
		services = mWiseManager.getChildServices(mSelectedRootService.getCode());
		
		List<ServiceListData> datas = new ArrayList<ServiceListData>();
		for(Service service : services) 
			datas.add(new ServiceListData(mWiseManager, service));
		mListAdapter.replaceWith(datas);
	}
}
