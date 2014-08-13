package com.wisewells.wisebeacon.beacongroup;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.MajorGroup;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.common.OneEditTwoButtonsDialog;
import com.wisewells.wisebeacon.common.OneEditTwoButtonsDialog.DialogListener;

public class AddBeaconToGroupActivity extends Activity {

	private WiseManager mWiseManager;
	private MajorGroup mSelectedBeaconGroup;
	
	private TextView mUuidGroupNameView;
	private TextView mMajorGroupNameView;
	private Button mAddBeaconToGroupButton;
	private ListView mListView;;
	private AddBeaconToGroupBeaconListAdapter mAdapter;
	private ListView mBeaconInGroupList;
	private DetailBeaconGroupBeaconListAdapter mBeaconInGroupAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_beacon_to_group);
		mWiseManager = WiseManager.getInstance(this);
		
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

		mBeaconInGroupAdapter = new DetailBeaconGroupBeaconListAdapter(this);
		
		mBeaconInGroupList = (ListView) findViewById(R.id.list_beacon_in_group);
		mBeaconInGroupList.setAdapter(mBeaconInGroupAdapter);
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
		try {
			mWiseManager.startReceiving();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		h.post(mUpdateBeaconList);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
			mWiseManager.stopReceiving();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		h.removeCallbacks(mUpdateBeaconList);
	}
	
	final Handler h = new Handler();
	private Runnable mUpdateBeaconList = new Runnable() {
		@Override
		public void run() {
			mAdapter.replaceWith(mWiseManager.getAllNearbyBeacons());
			h.postDelayed(mUpdateBeaconList, 100);
		}
	};
	
	private void onAddBeaconClicked() {
		OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
		dialog.setPrompt("비콘 추가");
		dialog.setEditTitle("비콘명");
		dialog.setListener(new DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {
				Beacon beacon = mAdapter.getItem(mListView.getCheckedItemPosition());				
				beacon.setName(str);
				try {
					mWiseManager.addBeaconToBeaconGroup(mSelectedBeaconGroup.getCode(), beacon);
					mBeaconInGroupAdapter.replaceWith(mWiseManager.getBeacons(mSelectedBeaconGroup.getCode()));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		
		dialog.show(getFragmentManager(), "dialog");
	}
}
