package com.wisewells.wisebeacon.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.protocols.ParcelableString;
import com.wisewells.sdk.protocols.RangingResult;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;

public class GroupActivity extends Activity {

	private WiseManager mWiseManager;
	private ListView mListView;
	private GroupAdapter mAdapter;
	private Button mAddButton;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		
		Message str = getIntent().getParcelableExtra("TEST");
		
		
		mAdapter = new GroupAdapter(this);
		
		mListView = (ListView) findViewById(R.id.group_listview);		
		mListView.setAdapter(mAdapter);
		
		mAddButton = (Button) findViewById(R.id.group_add_button);
		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddButtonClick();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
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

	private void onAddButtonClick() {
		Intent intent = new Intent(this, GroupAddActivity.class);
		startActivity(intent);
	}
}