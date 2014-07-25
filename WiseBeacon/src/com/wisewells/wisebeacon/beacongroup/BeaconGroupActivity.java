package com.wisewells.wisebeacon.beacongroup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.GetBeaconGroupListener;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.UuidGroup;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.beacongroup.BeaconGroupDialog.ConfirmListener;

public class BeaconGroupActivity extends Activity {

	public static final String EXTRA_UUID_GROUP_NAME = "uuid group name";
	public static final String EXTRA_MAJOR_GROUP = "major group";

	private UuidGroup mSelectedUuidGroup;
	
	private WiseManager mWiseManager;
	private ListView mListView;
	private BeaconGroupListAdapter mListAdapter;
	private Button mAddButton;
	private Spinner mSpinner;
	private ArrayAdapter<BeaconGroupSpinnerData> mSpinnerAdapter;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacon_group);
		
		mWiseManager = WiseManager.getInstance(this);
		
		mListAdapter = new BeaconGroupListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.group_listview);		
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				onBeaconGroupListClicked(position);
			}
		});
		
		mAddButton = (Button) findViewById(R.id.group_add_button);
		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddButtonClicked();
			}
		});

		mSpinnerAdapter = new ArrayAdapter<BeaconGroupSpinnerData>(this, android.R.layout.simple_spinner_dropdown_item);		
		
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
		if(mSelectedUuidGroup != null) displayMajorGroupsInListView(mSelectedUuidGroup);
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
		BeaconGroupDialog dialog = new BeaconGroupDialog();
		dialog.setConfirmListener(new ConfirmListener() {
			@Override
			public void onConfirmButtonClicked(String str) {
				try {
					mWiseManager.addBeaconGroup(str, mSelectedUuidGroup.getCode());
					
					/*
					 * 
					 * 이렇게 하면 안됨! 리스터 달아서 하자!!
					 */
					displayMajorGroupsInListView(mSelectedUuidGroup);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		dialog.show(getFragmentManager(), "dialog");
		
		/*Intent intent = new Intent(this, AddGroupActivity.class);
		intent.putExtra(EXTRA_UUID_GROUP_CODE, mUuidGroupCode);
		startActivity(intent);*/
	}
	
	private void onBeaconGroupListClicked(int position) {
		Intent intent = new Intent(this, DetailBeaconGroupActivity.class);
		intent.putExtra(EXTRA_UUID_GROUP_NAME, mSelectedUuidGroup.getName());
		intent.putExtra(EXTRA_MAJOR_GROUP, mListAdapter.getItem(position));
		startActivity(intent);
	}

	private void onSpinnerItemSelected(int position) {
		mSelectedUuidGroup = mSpinnerAdapter.getItem(position).getUuidGroup();		
		displayMajorGroupsInListView(mSelectedUuidGroup);
	}
	
	private void displayMajorGroupsInListView(UuidGroup uuidGroup) {
		mWiseManager.getMajorGroups(uuidGroup.getCode(), new GetBeaconGroupListener() {
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
				ArrayList<BeaconGroupSpinnerData> datas = new ArrayList<BeaconGroupSpinnerData>();
				for(BeaconGroup group: groups) {
					datas.add(new BeaconGroupSpinnerData((UuidGroup) group));
				}
				
				mSpinnerAdapter.clear();
				mSpinnerAdapter.addAll(datas);
			}
		});
	}
}
