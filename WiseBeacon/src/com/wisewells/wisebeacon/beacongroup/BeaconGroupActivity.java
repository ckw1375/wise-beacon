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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.MajorGroup;
import com.wisewells.sdk.beacon.UuidGroup;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.common.OneEditTwoButtonsDialog;
import com.wisewells.wisebeacon.common.OneEditTwoButtonsDialog.DialogListener;
import com.wisewells.wisebeacon.common.TitleDialogSpinner;
import com.wisewells.wisebeacon.common.TitleDialogSpinner.OnSpinnerItemSelectedListener;
import com.wisewells.wisebeacon.common.TitleDialogSpinnerAdapter;

public class BeaconGroupActivity extends Activity {

	public static final String EXTRA_UUID_GROUP_NAME = "uuid group name";
	public static final String EXTRA_MAJOR_GROUP = "major group";

	private UuidGroup mSelectedUuidGroup;
	
	private WiseManager mWiseManager;
	private ListView mListView;
	private BeaconGroupListAdapter mListAdapter;
	private ImageView mAddMajorButton;
	private ImageView mAddUuidButton;
	private TitleDialogSpinner mSpinner;
	private TitleDialogSpinnerAdapter<BeaconGroupSpinnerData> mSpinnerAdapter;
		
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
		
		mAddMajorButton = (ImageView) findViewById(R.id.img_add_major_group);
		mAddMajorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddMajorButtonClicked();
			}
		});
		
		mAddUuidButton = (ImageView) findViewById(R.id.img_add_uuid_group);
		mAddUuidButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddUuidButtonClicked();
			}
		});

		mSpinnerAdapter = new TitleDialogSpinnerAdapter<BeaconGroupSpinnerData>(this);		
		
		mSpinner = (TitleDialogSpinner) findViewById(R.id.custom_spinner_uuidgroup);
		mSpinner.setAdapter(mSpinnerAdapter);
		mSpinner.setFragmentManager(getFragmentManager());
		mSpinner.setOnItemSelectedListener(new OnSpinnerItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onSpinnerItemSelected(position);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		updateUuidGroupSpinner();		
		if(mSelectedUuidGroup != null) updateMajorGroupListView(mSelectedUuidGroup);
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
	
	private void onAddUuidButtonClicked() {
		OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
		dialog.setPrompt("비콘 그룹 생성");
		dialog.setEditTitle("그룹명");
		dialog.setListener(new DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {
				try {
					mWiseManager.addUuidGroup(str);
					
					/*
					 * 
					 * add beacon이 완료된것이 확인되면! (리스터 이용) display 해줘야 한다!!
					 */
					updateUuidGroupSpinner();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		dialog.show(getFragmentManager(), "dialog");
	}

	private void onAddMajorButtonClicked() {		
		OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
		dialog.setPrompt("비콘 그룹 생성");
		dialog.setEditTitle("그룹명");
		dialog.setListener(new DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {
				try {
					mWiseManager.addMajorGroup(str, mSelectedUuidGroup.getCode());
					
					/*
					 * 
					 * add beacon이 완료된것이 확인되면! (리스터 이용) display 해줘야 한다!!
					 */
					updateMajorGroupListView(mSelectedUuidGroup);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		dialog.show(getFragmentManager(), "dialog");
	}
	
	private void onBeaconGroupListClicked(int position) {
		Intent intent = new Intent(this, DetailBeaconGroupActivity.class);
		intent.putExtra(EXTRA_UUID_GROUP_NAME, mSelectedUuidGroup.getName());
		intent.putExtra(EXTRA_MAJOR_GROUP, mListAdapter.getItem(position));
		startActivity(intent);
	}

	private void onSpinnerItemSelected(int position) {
		mSelectedUuidGroup = (UuidGroup) mSpinnerAdapter.getItem(position).getUuidGroup();		
		updateMajorGroupListView(mSelectedUuidGroup);
	}
	
	private void updateMajorGroupListView(UuidGroup uuidGroup) {
		List<MajorGroup> groups = new ArrayList<MajorGroup>();
		try {
			groups = mWiseManager.getMajorGroups(uuidGroup.getCode());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		List<BeaconGroup> param = new ArrayList<BeaconGroup>(groups);
		mListAdapter.replaceWith(param);
	}
	
	private void updateUuidGroupSpinner() {
		List<UuidGroup> groups = new ArrayList<UuidGroup>();
		
		try {
			groups = mWiseManager.getUuidGroups();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		ArrayList<BeaconGroupSpinnerData> datas = new ArrayList<BeaconGroupSpinnerData>();
		for(BeaconGroup group: groups) {
			datas.add(new BeaconGroupSpinnerData(group));
		}
		
		mSpinnerAdapter.clear();
		mSpinnerAdapter.addAll(datas);
	}
}
