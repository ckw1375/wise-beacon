package com.wisewells.wisebeacon.beacongroup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.GetBeaconGroupListener;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.UuidGroup;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;

public class GroupActivity extends Activity {

	public static final String EXTRA_UUID_GROUP_CODE = "uuidcode";

	private String mUuidGroupCode;
	
	private WiseManager mWiseManager;
	private ListView mListView;
	private GroupAdapter mListAdapter;
	private Button mAddButton;
	private Spinner mSpinner;
	private ArrayAdapter<GroupSpinnerData> mSpinnerAdapter;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_activity);
		
		mWiseManager = WiseManager.getInstance(this);
		
		mListAdapter = new GroupAdapter(this);
		
		mListView = (ListView) findViewById(R.id.group_listview);		
		mListView.setAdapter(mListAdapter);
		
		mAddButton = (Button) findViewById(R.id.group_add_button);
		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddButtonClicked();
			}
		});
		
		mSpinnerAdapter = new ArrayAdapter<GroupSpinnerData>(this, android.R.layout.simple_spinner_dropdown_item);		
		
		mSpinner = (Spinner) findViewById(R.id.group_spin_uuigroup);
		mSpinner.setAdapter(mSpinnerAdapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onSpinnerItemSelected(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		displayUuidGroupsInSpinner();		
		if(mUuidGroupCode != null) displayMajorGroupsInListView(mUuidGroupCode);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onAddButtonClicked() {
		Intent intent = new Intent(this, AddGroupActivity.class);
		intent.putExtra(EXTRA_UUID_GROUP_CODE, mUuidGroupCode);
		startActivity(intent);
	}

	private void onSpinnerItemSelected(int position) {
		mUuidGroupCode = mSpinnerAdapter.getItem(position).getCode();		
		displayMajorGroupsInListView(mUuidGroupCode);
	}
	
	private void displayMajorGroupsInListView(String uuidGroupCode) {
		mWiseManager.getMajorGroups(mUuidGroupCode, new GetBeaconGroupListener() {
			@Override
			public void onResponseBeaconGroup(List<BeaconGroup> groups) {
				mListAdapter.replaceWith(groups);
			}
		});		
	}
	
	private void displayUuidGroupsInSpinner() {
		mWiseManager.getUuidGroups(new GetBeaconGroupListener() {
			@Override
			public void onResponseBeaconGroup(List<BeaconGroup> groups) {
				ArrayList<GroupSpinnerData> datas = new ArrayList<GroupSpinnerData>();
				for(BeaconGroup group: groups) {
					datas.add(new GroupSpinnerData((UuidGroup) group));
				}
				
				mSpinnerAdapter.clear();
				mSpinnerAdapter.addAll(datas);
			}
		});
	}
}
