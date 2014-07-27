package com.wisewells.wisebeacon.service;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.GetServiceListener;
import com.wisewells.sdk.datas.Service;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.topology.TopologyActivity;

public class ServiceActivity extends Activity {

	public static final String EXTRA_SERVICE_OBJECT = "service";
	
	private WiseManager mWiseManager;
	
	private Button mAddButton;
	private Spinner mSpinner;
	private ArrayAdapter<String> mSpinnerAdapter;
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
		
		mSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		
		mSpinner = (Spinner) findViewById(R.id.spinner_root_service);
		mSpinner.setAdapter(mSpinnerAdapter);
		
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

	private void onAddButtonClicked() {
		
	}
	
	private void onListItemClicked(int position) {
		Service service = mListAdapter.getItem(position);
		
		Intent intent = new Intent(this, TopologyActivity.class);
		intent.putExtra(EXTRA_SERVICE_OBJECT, service);
		startActivity(intent);
	}
	
	private void displayRootService() {
		mWiseManager.getService(Service.SERVICE_TREE_ROOT, new GetServiceListener() {
			@Override
			public void onResponseService(List<Service> services) {
				
			}
		});
	}
}
