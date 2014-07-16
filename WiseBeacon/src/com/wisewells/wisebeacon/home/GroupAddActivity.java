package com.wisewells.wisebeacon.home;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.wisewells.sdk.Region;
import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseObjects;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.MajorGroup;
import com.wisewells.sdk.datas.MinorGroup;
import com.wisewells.wisebeacon.R;

public class GroupAddActivity extends Activity {

	private static final Region TEST_REGION = new Region("wisewells", null, null, null);
	
	private WiseManager mWiseManager;
	
	private EditText mNameView;
	private ListView mListView;;
	private GroupAddListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_add);
		
		mAdapter = new GroupAddListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.group_add_beacon_list);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		mListView.setAdapter(mAdapter);
		
		mNameView = (EditText) findViewById(R.id.group_add_name);		
		
		mWiseManager = new WiseManager(this);
		mWiseManager.setRangingListener(new WiseManager.RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region paramRegion, final List<Beacon> beacons) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mAdapter.replaceWith(beacons);
					}
				});
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		// Check if device supports Bluetooth Low Energy.
		if (!mWiseManager.hasBluetooth()) {
			Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
			return;
		}

		// If Bluetooth is not enabled, let user enable it.
		if (!mWiseManager.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1234);
		} else {
			connectToService();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1234) {
			if (resultCode == Activity.RESULT_OK) {
				connectToService();
			} else {
				Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
				getActionBar().setSubtitle("Bluetooth not enabled");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void connectToService() {
		getActionBar().setSubtitle("Scanning...");
		mAdapter.replaceWith(Collections.<Beacon>emptyList());
		mWiseManager.connect(new WiseManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					mWiseManager.startRanging(TEST_REGION);
				} catch (RemoteException e) {
					Toast.makeText(GroupAddActivity.this, "Cannot start ranging, something terrible happened",
							Toast.LENGTH_LONG).show();					
				}
			}
		});
	}

	@Override
	protected void onStop() {
		try {
			mWiseManager.stopRanging(TEST_REGION);
		} catch (RemoteException e) {
			Log.d("TEST", "Error while stopping ranging", e);
		}

		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.group_add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.done) {
			saveBeaconGroup();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveBeaconGroup() {
		
	}
	
	
}
