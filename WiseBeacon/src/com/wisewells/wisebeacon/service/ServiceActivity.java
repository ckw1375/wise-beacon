package com.wisewells.wisebeacon.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.wisewells.sdk.datas.Service;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.topology.TopologyActivity;

public class ServiceActivity extends Activity {

	public static final String EXTRA_SERVICE_OBJECT = "service";
	
	private Button mAddButton;
	private ListView mListView;
	private ServiceListAdapter mListAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_activity);
		
		mAddButton = (Button) findViewById(R.id.service_btn_add);
		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddButtonClicked();
			}
		});
		
		mListAdapter = new ServiceListAdapter(this);		
		
		mListView = (ListView) findViewById(R.id.service_listview);
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListItemClicked(position);
			}
		});
		
		dummy();
	}

	private void onAddButtonClicked() {
		
	}
	
	private void onListItemClicked(int position) {
		Service service = mListAdapter.getItem(position);
		
		Intent intent = new Intent(this, TopologyActivity.class);
		intent.putExtra(EXTRA_SERVICE_OBJECT, service);
		startActivity(intent);
	}
	
	private void dummy() {
		Service service = new Service("지오다노");
		service.setCode("giodarno");
		
		mListAdapter.add(service);
	}
}
