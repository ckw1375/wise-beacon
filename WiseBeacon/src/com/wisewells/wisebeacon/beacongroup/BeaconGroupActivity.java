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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.EditBeaconGroupListener;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.view.OneEditTwoButtonsDialog;
import com.wisewells.wisebeacon.view.TitleDialogSpinner;
import com.wisewells.wisebeacon.view.TitleDialogSpinnerAdapter;
import com.wisewells.wisebeacon.view.OneEditTwoButtonsDialog.DialogListener;
import com.wisewells.wisebeacon.view.TitleDialogSpinner.OnSpinnerItemSelectedListener;

public class BeaconGroupActivity extends Activity {

	public static final String EXTRA_ROOT_GROUP_NAME = "root group name";
	public static final String EXTRA_LEAF_GROUP = "leaf group";

	private BeaconGroup mSelectedRootGroup;
	
	private WiseManager mWiseManager;
	private ListView mListView;
	private BeaconGroupListAdapter mListAdapter;
	private ImageView mAddRootButton;
	private ImageView mAddLeafButton;
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
		
		mAddRootButton = (ImageView) findViewById(R.id.img_add_major_group);
		mAddRootButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddMajorButtonClicked();
			}
		});
		
		mAddLeafButton = (ImageView) findViewById(R.id.img_add_uuid_group);
		mAddLeafButton.setOnClickListener(new View.OnClickListener() {
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
		updateRootGroupSpinner();		
		if(mSelectedRootGroup != null) updateLeafGroupListView(mSelectedRootGroup);
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
		dialog.setDialogListener(new DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {
				mWiseManager.addBeaconGroup(BeaconGroup.DEPTH_ROOT, str, null, new EditBeaconGroupListener() {
					@Override
					public void onEditSuccess(BeaconGroup beaconGroup) {
						L.d(beaconGroup.toString() + " is added");
						mSpinnerAdapter.add(new BeaconGroupSpinnerData(beaconGroup));
					}
					@Override
					public void onEditFail() {
					}
				});
			}
		});

		dialog.show(getFragmentManager(), "dialog");
	}

	private void onAddMajorButtonClicked() {		
		OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
		dialog.setPrompt("비콘 그룹 생성");
		dialog.setEditTitle("그룹명");
		dialog.setDialogListener(new DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {
				mWiseManager.addBeaconGroup(BeaconGroup.DEPTH_LEAF, str, mSelectedRootGroup.getCode(), new EditBeaconGroupListener() {
					@Override
					public void onEditSuccess(BeaconGroup beaconGroup) {
						mListAdapter.add(beaconGroup);
					}
					@Override
					public void onEditFail() {
					}
				});
			}
		});

		dialog.show(getFragmentManager(), "dialog");
	}
	
	private void onBeaconGroupListClicked(int position) {
		Intent intent = new Intent(this, DetailBeaconGroupActivity.class);
		intent.putExtra(EXTRA_ROOT_GROUP_NAME, mSelectedRootGroup.getName());
		intent.putExtra(EXTRA_LEAF_GROUP, mListAdapter.getItem(position));
		startActivity(intent);
	}

	private void onSpinnerItemSelected(int position) {
		mSelectedRootGroup = mSpinnerAdapter.getItem(position).getRootGroup();		
		updateLeafGroupListView(mSelectedRootGroup);
	}
	
	private void updateLeafGroupListView(BeaconGroup rootGroup) {
		List<BeaconGroup> childGroups = new ArrayList<BeaconGroup>();
		childGroups = mWiseManager.getBeaconGroups(rootGroup.getCode());
		
		List<BeaconGroup> param = new ArrayList<BeaconGroup>(childGroups);
		mListAdapter.replaceWith(param);
	}
	
	private void updateRootGroupSpinner() {
		List<BeaconGroup> groups = new ArrayList<BeaconGroup>();
		groups = mWiseManager.getBeaconGroups(null);
		
		ArrayList<BeaconGroupSpinnerData> datas = new ArrayList<BeaconGroupSpinnerData>();
		for(BeaconGroup group: groups) {
			datas.add(new BeaconGroupSpinnerData(group));
		}
		
		mSpinnerAdapter.clear();
		mSpinnerAdapter.addAll(datas);
	}
}
