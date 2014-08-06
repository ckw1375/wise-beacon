package com.wisewells.wisebeacon.beacongroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.MajorGroup;
import com.wisewells.wisebeacon.R;

public class DetailBeaconGroupActivity extends Activity {
	
	public static final String EXTRA_UUID_GROUP_NAME = "uuid";
	public static final String EXTRA_MAJOR_GROUP = "major";
	
	private WiseManager mWiseManager;
	private MajorGroup mSelectedBeaconGroup;
	
	private TextView mUuidGroupNameView;
	private TextView mMajorGroupNameView;
	private TextView mBeaconNumberInGroupView;
	private Button mAddBeaconButton;
	private ListView mListView;;
	private DetailBeaconGroupBeaconListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_beacon_group);
		
		final String uuidGroupName = getIntent().getStringExtra(BeaconGroupActivity.EXTRA_UUID_GROUP_NAME);
		mSelectedBeaconGroup = getIntent().getParcelableExtra(BeaconGroupActivity.EXTRA_MAJOR_GROUP);
		
		mUuidGroupNameView = (TextView) findViewById(R.id.txt_uuid_group_name);
		mUuidGroupNameView.setText(uuidGroupName);
		
		mMajorGroupNameView = (TextView) findViewById(R.id.txt_major_group_name);
		mMajorGroupNameView.setText(mSelectedBeaconGroup.getName());
		
		mBeaconNumberInGroupView = (TextView) findViewById(R.id.txt_beacon_number_in_group);
		mBeaconNumberInGroupView.setText(String.valueOf(mSelectedBeaconGroup.getChildCodes().size()));
		
		mAddBeaconButton = (Button) findViewById(R.id.btn_add_beacon_to_group);
		mAddBeaconButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DetailBeaconGroupActivity.this, AddBeaconToGroupActivity.class);
				intent.putExtra(EXTRA_UUID_GROUP_NAME, uuidGroupName);
				intent.putExtra(EXTRA_MAJOR_GROUP, mSelectedBeaconGroup);
				startActivity(intent);
			}
		});
		
		mAdapter = new DetailBeaconGroupBeaconListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.list_beacons_in_group);
		mListView.setAdapter(mAdapter);
		
		mWiseManager = WiseManager.getInstance(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		displayBeaconsInGroup();
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
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayBeaconsInGroup() {
		try {
			mAdapter.replaceWith(mWiseManager.getBeacons(mSelectedBeaconGroup.getCode()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
