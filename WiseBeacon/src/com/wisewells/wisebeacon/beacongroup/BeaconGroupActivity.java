package com.wisewells.wisebeacon.beacongroup;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.WiseManager.EditBeaconGroupListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.common.BaseActivity;
import com.wisewells.wisebeacon.view.OneEditTwoButtonsDialog;
import com.wisewells.wisebeacon.view.OneEditTwoButtonsDialog.DialogListener;
import com.wisewells.wisebeacon.view.TitleDialogSpinner;
import com.wisewells.wisebeacon.view.TitleDialogSpinner.OnSpinnerItemSelectedListener;
import com.wisewells.wisebeacon.view.TitleDialogSpinnerAdapter;

public class BeaconGroupActivity extends BaseActivity {

	public static final String EXTRA_ROOT_GROUP_NAME = "root group name";
	public static final String EXTRA_LEAF_GROUP = "leaf group";
	public static final String EXTRA_BEACONS_IN_GROUP = "beacons in group";

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
		setTitle("비콘 그룹 관리");
		setDescription("시스템에 등록된 비콘그룹 정보 및 현재 모바일로 수신되는 비콘 그룹 정보를 목록으로 조회합니다.");
		
		mWiseManager = WiseManager.getInstance(this);
		
		mListAdapter = new BeaconGroupListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.group_listview);		
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				onBeaconGroupListItemClicked(position);
			}
		});
		
		mAddRootButton = (ImageView) findViewById(R.id.img_add_major_group);
		mAddRootButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddLeafButtonClicked();
			}
		});
		
		mAddLeafButton = (ImageView) findViewById(R.id.img_add_uuid_group);
		mAddLeafButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddRootButtonClicked();
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
		
		updateRootGroupSpinner();		
		if(mSelectedRootGroup != null) updateLeafGroupListView(mSelectedRootGroup);
	}
	
	private void onAddRootButtonClicked() {
		OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
		dialog.setPrompt("비콘 그룹 생성");
		dialog.setEditTitle("그룹명");
		dialog.setDialogListener(new DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {
				mWiseManager.addBeaconGroup(BeaconGroup.DEPTH_ROOT, str, null, new EditBeaconGroupListener() {
					@Override
					public void onSuccess(BeaconGroup beaconGroup) {
						L.d(beaconGroup.toString() + " is added");
						mSpinnerAdapter.add(new BeaconGroupSpinnerData(beaconGroup));
					}
					@Override
					public void onFail() {
					}
				});
			}
		});

		dialog.show(getFragmentManager(), "dialog");
	}

	private void onAddLeafButtonClicked() {	
		if(mSelectedRootGroup == null) {
			Toast.makeText(this, "상위 그룹을 선택해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
		dialog.setPrompt("비콘 그룹 생성");
		dialog.setEditTitle("그룹명");
		dialog.setDialogListener(new DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {
				mWiseManager.addBeaconGroup(BeaconGroup.DEPTH_LEAF, str, mSelectedRootGroup.getCode(), new EditBeaconGroupListener() {
					@Override
					public void onSuccess(BeaconGroup beaconGroup) {
						mListAdapter.add(makeBeaconGroupListData(beaconGroup));
					}
					@Override
					public void onFail() {
					}
				});
			}
		});

		dialog.show(getFragmentManager(), "dialog");
	}
	
	private void onBeaconGroupListItemClicked(int position) {
		Intent intent = new Intent(this, DetailBeaconGroupActivity.class);
		intent.putExtra(EXTRA_ROOT_GROUP_NAME, mSelectedRootGroup.getName());
		intent.putExtra(EXTRA_LEAF_GROUP, mListAdapter.getItem(position).getBeaconGroup());
		
		ArrayList<Beacon> beacons = new ArrayList<Beacon>(mListAdapter.getItem(position).getBeacons());
		intent.putParcelableArrayListExtra(EXTRA_BEACONS_IN_GROUP, beacons);
		startActivity(intent);
	}

	private void onSpinnerItemSelected(int position) {
		mSelectedRootGroup = mSpinnerAdapter.getItem(position).getRootGroup();		
		updateLeafGroupListView(mSelectedRootGroup);
	}
	
	private void updateLeafGroupListView(BeaconGroup rootGroup) {
		List<BeaconGroup> childGroups = new ArrayList<BeaconGroup>();
		childGroups = mWiseManager.getBeaconGroups(rootGroup.getCode());

		List<BeaconGroupListData> items = new ArrayList<BeaconGroupListData>();
		for(BeaconGroup group : childGroups) {
			BeaconGroupListData item = makeBeaconGroupListData(group);
			items.add(item);
		}
		
		mListAdapter.replaceWith(items);
	}
	
	private BeaconGroupListData makeBeaconGroupListData(BeaconGroup group) {
		List<Beacon> beacons = mWiseManager.getBeaconsInGroup(group.getCode());
		
		BeaconVector beaconVector = new BeaconVector(beacons.size());
		for(int i=0; i<beaconVector.getSize(); i++) {
			Beacon beacon = beacons.get(i);
			beaconVector.set(i, beacon.getRegion());
		}

		boolean isNearbyGroup = checkIsThereBeaconGroupAroundHere(beacons);
		return new BeaconGroupListData(group, beacons, isNearbyGroup);
	}
	
	private boolean checkIsThereBeaconGroupAroundHere(List<Beacon> beaconsInGroup) {
		BeaconVector beaconVector = new BeaconVector(beaconsInGroup.size());
		for(int i=0; i<beaconVector.getSize(); i++) {
			Beacon beacon = beaconsInGroup.get(i);
			beaconVector.set(i, beacon.getRegion());
		}

		boolean[] isNearbyBeacon = mWiseManager.isNearbyBeacon(beaconVector);
		if(isNearbyBeacon == null)
			return false;
		
		for(int i=0; i<isNearbyBeacon.length; i++) {
			if(isNearbyBeacon[i]) {
				return true;
			}
		}
		return false;
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
	
	@Override
	protected void onResume() {
		super.onResume();
		handler.post(updateLeafGroupState);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(updateLeafGroupState);
	}
	
	private Handler handler = new Handler();
	private Runnable updateLeafGroupState = new Runnable() {
		@Override
		public void run() {
			for(BeaconGroupListData item : mListAdapter.getItems()) {
				boolean b = checkIsThereBeaconGroupAroundHere(item.getBeacons());
				L.d(b + "");
				item.setIsNearbyGroup(b);
			}
			handler.postDelayed(this, 500);
		}
	};
}
