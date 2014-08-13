package com.wisewells.wisebeacon.topology;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.wisewells.wisebeacon.R;

public class SampleCollectionActivity extends Activity {
	
	private Button mAddSector;
	private Button mResetAllSamples;
	
	private ListView mListView;
	private SampleCollectionListAdapter mAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_collection);
		
		mAddSector = (Button) findViewById(R.id.btn_add_sector);
		mAddSector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mResetAllSamples = (Button) findViewById(R.id.btn_reset_all_samples);
		mResetAllSamples.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mAdapter = new SampleCollectionListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.list_sample_collection);
		mListView.setAdapter(mAdapter);
	}
	
	
}
