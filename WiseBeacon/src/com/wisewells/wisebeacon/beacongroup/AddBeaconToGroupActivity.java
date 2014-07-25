package com.wisewells.wisebeacon.beacongroup;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wisewells.sdk.Region;
import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.RangingListener;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.MajorGroup;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.beacongroup.AddBeaconToGroupBeaconNameDialog.ConfirmListener;

public class AddBeaconToGroupActivity extends Activity {

	private static final Region RANGING_REGION = new Region("beacons", null, null, null);
	
	private WiseManager mWiseManager;
	private MajorGroup mSelectedBeaconGroup;
	
	private TextView mUuidGroupNameView;
	private TextView mMajorGroupNameView;
	private Button mAddBeaconToGroupButton;
	private ListView mListView;;
	private AddBeaconToGroupBeaconListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_beacon_to_group);
		
		String uuidGroupName = getIntent().getStringExtra(DetailBeaconGroupActivity.EXTRA_UUID_GROUP_NAME);
		mSelectedBeaconGroup = getIntent().getParcelableExtra(DetailBeaconGroupActivity.EXTRA_MAJOR_GROUP);
		
		mUuidGroupNameView = (TextView) findViewById(R.id.txt_uuid_group_name);
		mUuidGroupNameView.setText(uuidGroupName);
		
		mMajorGroupNameView = (TextView) findViewById(R.id.txt_major_group_name);
		mMajorGroupNameView.setText(mSelectedBeaconGroup.getName());
		
		mAddBeaconToGroupButton = (Button) findViewById(R.id.btn_add_beacon_to_group);
		mAddBeaconToGroupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddBeaconClicked();
			}
		});
		
		mAdapter = new AddBeaconToGroupBeaconListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.list_beacon_list);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		mListView.setAdapter(mAdapter);
		
		mWiseManager = WiseManager.getInstance(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_beacon_to_group, menu);
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
	
	@Override
	protected void onResume() {
		super.onResume();
		receiveAroundBeaconInformation();
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
	
	private void onAddBeaconClicked() {
		AddBeaconToGroupBeaconNameDialog dialog = new AddBeaconToGroupBeaconNameDialog();
		dialog.setConfirmListener(new ConfirmListener() {
			@Override
			public void onConfirmButtonClicked(String str) {
				Beacon beacon = mAdapter.getItem(mListView.getCheckedItemPosition());				
				beacon.setName(str);
				try {
					mWiseManager.addBeaconToBeaconGroup(mSelectedBeaconGroup.getCode(), beacon);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		dialog.show(getFragmentManager(), "dialog");
	}
}