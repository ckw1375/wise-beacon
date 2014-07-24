package com.wisewells.wisebeacon.topology;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.GetBeaconGroupListener;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;

public class AddTopologyActivity extends Activity {

	private static final String TOPOLOGY_TYPE_PROXIMITY = "Proximity";
	private static final String TOPOLOGY_TYPE_SECTOR = "Sector";
	private static final String TOPOLOGY_TYPE_LOCATION = "Location";
	private static final String[] TOPOLOGY_TYPES = 
		{ TOPOLOGY_TYPE_PROXIMITY, TOPOLOGY_TYPE_SECTOR, TOPOLOGY_TYPE_LOCATION };	
	
	public static final String EXTRA_BEACON_GROUP_CODE = "groupcode";
	
	private WiseManager mWiseManager;
	
	private EditText mNameView;
	private Spinner mSpinner;
	private ArrayAdapter<String> mSpinnerAdapter;
	private ListView mListView;
	private AddTopologyListAdapter mListAdapter;
	private Button mDoneButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_topology);
		mWiseManager = WiseManager.getInstance(this);
		
		mNameView = (EditText) findViewById(R.id.add_topology_edit_name);
		
		mSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);		
		mSpinnerAdapter.addAll(TOPOLOGY_TYPES);
		
		mSpinner = (Spinner) findViewById(R.id.add_topology_spinner);
		mSpinner.setAdapter(mSpinnerAdapter);
		
		mListAdapter = new AddTopologyListAdapter(this);		
		
		mListView = (ListView) findViewById(R.id.add_topology_listview);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		mListView.setAdapter(mListAdapter);
		
		mDoneButton = (Button) findViewById(R.id.add_topology_btn_done);
		mDoneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDoneButtonClicked();
			}
		});
		
		displayMajorGroupsInListView("wise");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_topology, menu);
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
	
	private void displayMajorGroupsInListView(String uuidGroupCode) {
		mWiseManager.getMajorGroups(uuidGroupCode, new GetBeaconGroupListener() {
			@Override
			public void onResponseBeaconGroup(List<BeaconGroup> groups) {
				mListAdapter.replaceWith(groups);
			}
		});		
	}
	
	private void onDoneButtonClicked() {
		int type = mSpinner.getSelectedItemPosition();
		Intent intent = null;
		
		if(TOPOLOGY_TYPES[type] == TOPOLOGY_TYPE_PROXIMITY) L.i("pro");
		if(TOPOLOGY_TYPES[type] == TOPOLOGY_TYPE_SECTOR) L.i("sector");;
		if(TOPOLOGY_TYPES[type] == TOPOLOGY_TYPE_LOCATION) intent = new Intent(this, LocationTopologyActivity.class);
		
		BeaconGroup group = mListAdapter.getItem(mListView.getCheckedItemPosition());
		intent.putExtra(EXTRA_BEACON_GROUP_CODE, group.getCode());
		
		startActivity(intent);
	}
}
