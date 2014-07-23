package com.wisewells.wisebeacon.topology;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.wisewells.wisebeacon.R;

public class TopologyActivity extends Activity {

	private Button mAddButton;
	private ListView mListView;
	private TopologyListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topology_activity);
		
		mAddButton = (Button) findViewById(R.id.btn_add_topology);
		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddTopologyClicked();
			}
		});
		
		mAdapter = new TopologyListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.list_topology);
		mListView.setAdapter(mAdapter);		
	}

	private void onAddTopologyClicked() {
		Intent intent = new Intent(this, AddTopologyActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topology, menu);
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
}
