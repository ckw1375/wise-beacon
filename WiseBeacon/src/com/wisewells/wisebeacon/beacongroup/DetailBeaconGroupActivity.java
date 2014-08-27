package com.wisewells.wisebeacon.beacongroup;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.common.BaseActivity;

public class DetailBeaconGroupActivity extends BaseActivity {
	
	public static final String EXTRA_ROOT_GROUP_NAME = "root";
	public static final String EXTRA_LEAF_GROUP = "leaf";
	
	private WiseManager mWiseManager;
	private BeaconGroup mSelectedBeaconGroup;
	private ArrayList<Beacon> mBeaconsInGroup;
	
	private TextView mRootGroupNameView;
	private TextView mLeafGroupNameView;
	private TextView mBeaconNumberInGroupView;
	private ImageView mAddBeaconButton;
	private ListView mListView;;
	private DetailBeaconGroupBeaconListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_beacon_group);
		
		final String rootGroupName = getIntent().getStringExtra(BeaconGroupActivity.EXTRA_ROOT_GROUP_NAME);
		mSelectedBeaconGroup = getIntent().getParcelableExtra(BeaconGroupActivity.EXTRA_LEAF_GROUP);
		mBeaconsInGroup = getIntent().getParcelableArrayListExtra(BeaconGroupActivity.EXTRA_BEACONS_IN_GROUP); 
				
		mRootGroupNameView = (TextView) findViewById(R.id.txt_uuid_group_name);
		mRootGroupNameView.setText(rootGroupName);
		
		mLeafGroupNameView = (TextView) findViewById(R.id.txt_major_group_name);
		mLeafGroupNameView.setText(mSelectedBeaconGroup.getName());
		
		mBeaconNumberInGroupView = (TextView) findViewById(R.id.txt_beacon_number_in_group);
		mBeaconNumberInGroupView.setText(mBeaconsInGroup.size() + "");
		
		mAddBeaconButton = (ImageView) findViewById(R.id.img_add_beacon_to_group);
		mAddBeaconButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DetailBeaconGroupActivity.this, AddBeaconToGroupActivity.class);
				intent.putExtra(EXTRA_ROOT_GROUP_NAME, rootGroupName);
				intent.putExtra(EXTRA_LEAF_GROUP, mSelectedBeaconGroup);
				startActivity(intent);
			}
		});
		
		mAdapter = new DetailBeaconGroupBeaconListAdapter(this, mBeaconsInGroup);
		
		mListView = (ListView) findViewById(R.id.list_beacons_in_group);
		mListView.setAdapter(mAdapter);
		mListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.list_header_beacon, null, false));
		
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
		mAdapter.replaceWith(mWiseManager.getBeaconsInGroup(mSelectedBeaconGroup.getCode()));
	}
}
