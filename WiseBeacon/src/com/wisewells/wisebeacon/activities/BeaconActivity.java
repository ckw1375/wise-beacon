package com.wisewells.wisebeacon.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.adapters.BeaconListAdapter;

public class BeaconActivity extends Activity {

	private BeaconManager mBeaconManager;
	
	private ListView mListView;
	private BeaconListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacon);
		
		mAdapter = new BeaconListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.beacon_listview);
		mListView.setAdapter(mAdapter);
		
		mBeaconManager = new BeaconManager(this);
		mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region paramRegion, List<Beacon> paramList) {
				List<com.wisewells.sdk.datas.Beacon> beacons = new ArrayList<com.wisewells.sdk.datas.Beacon>();
				for(Beacon b : paramList) {
					beacons.add(new com.wisewells.sdk.datas.Beacon(b));
				}
				mAdapter.replace(beacons);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		connectToService();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.beacon, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
	        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);
	 
	        for(int i=0; i<rs.size(); i++){
	            ActivityManager.RunningServiceInfo rsi = rs.get(i);
	            L.d("Package Name : " + rsi.service.getPackageName());
	        }
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void connectToService() {
		mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					mBeaconManager.startRanging(new Region("wise", null, null, null));
				} catch (RemoteException e) {
					
				}
			}
		});
	}
}
