package com.wisewells.wisebeacon.activities;

import java.io.ObjectOutputStream.PutField;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.wisewells.sdk.WiseObjects;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.group.BeaconGroup;
import com.wisewells.wisebeacon.Dummy;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.adapters.BeaconAdapter;

public class GroupAddActivity extends Activity {

	private EditText mNameView;
	private ListView mListView;;
	private BeaconAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_add);
		
		mAdapter = new BeaconAdapter(this, Dummy.getBeacons());
		
		mListView = (ListView) findViewById(R.id.group_add_beacon_list);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		mListView.setAdapter(mAdapter);
		
		mNameView = (EditText) findViewById(R.id.group_add_name);		
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
			saveBeaconGroup();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveBeaconGroup() {
		
		SparseBooleanArray sba = mListView.getCheckedItemPositions();
		for(int i=0; i<mListView.getCount(); i++) {			
			if(!sba.get(i)) continue;				
			Beacon b = mAdapter.getItem(i);
			WiseObjects.getInstance().putBeacon("Beacon" + i, b);
		}
	}
}
