package com.wisewells.wisebeacon.beacongroup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.wisewells.sdk.Region;
import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.DummyListener;
import com.wisewells.sdk.WiseManager.RangingListener;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.wisebeacon.R;

public class AddGroupActivity extends Activity {
	
	private static final Region RANGING_REGION = new Region("beacons", null, null, null);
	
	private WiseManager mWiseManager;
	
	private EditText mNameView;
	private ListView mListView;;
	private AddGroupListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_group);
		
		mAdapter = new AddGroupListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.group_add_beacon_list);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		mListView.setAdapter(mAdapter);
		
		mNameView = (EditText) findViewById(R.id.group_add_name);
		
		mWiseManager = WiseManager.getInstance(this);
//		mWiseManager.setDummyListener(new DummyListener() {
//			
//			@Override
//			public void onDummyBeacon(List<Beacon> beacons) {
//				mAdapter.replaceWith(beacons);
//			}
//		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		receiveAroundBeaconInformation();
//		mWiseManager.testStartMakingDummy();
	}	
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
			mWiseManager.stopRanging(RANGING_REGION);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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
		String uuidGroupCode = getIntent().getStringExtra(BeaconGroupActivity.EXTRA_UUID_GROUP_CODE);
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		
		SparseBooleanArray sb = mListView.getCheckedItemPositions();
		for(int i=0; i<mAdapter.getCount(); i++) { 
			if(sb.get(i)) {
				beacons.add(mAdapter.getItem(i));
			}
		}
		
		WiseManager manager = WiseManager.getInstance(this);		
		try {
//			manager.addBeaconGroup(mNameView.getText().toString(), uuidGroupCode, beacons);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void receiveAroundBeaconInformation() {
		mWiseManager.setRangingListener(new RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
				mAdapter.replaceWith(beacons);
			}
		});
		
		try {
			mWiseManager.startRanging(RANGING_REGION);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
