package com.wisewells.wisebeacon.topology;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.wisewells.wisebeacon.R;

public class LocationTopologyActivity extends Activity {

	private Button mCompleteButton;
	private ListView mListView;
	private LocationTopologyListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_topology);
		displayBeaconsInGroup();
		
		mCompleteButton = (Button) findViewById(R.id.btn_complete);
		mCompleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCompleteButtonClicked();
			}
		});
		
		mAdapter = new LocationTopologyListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.list_beacons_in_topology_activity);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.location_topology, menu);
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
	
	private void onCompleteButtonClicked() {
		
	}
	
	private void displayBeaconsInGroup() {

//		String groupCode = getIntent().getStringExtra(AddTopologyActivity.EXTRA_BEACON_GROUP_CODE);
//		WiseManager.getInstance(this).getBeacons(groupCode, new GetBeaconListener() {
//			@Override
//			public void onResponseBeacon(List<Beacon> beacons) {
//				ArrayList<LocationTopologyListData> datas = new ArrayList<LocationTopologyListData>();
//				
//				for(Beacon beacon : beacons) {
//					datas.add(new LocationTopologyListData(beacon));
//				}
//				
//				mAdapter.replaceWith(datas);
//			}
//		});
	}
}
