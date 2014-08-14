package com.wisewells.wisebeacon.topology;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.RssiVector;
import com.wisewells.sdk.service.Sector;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.common.OneEditTwoButtonsDialog;
import com.wisewells.wisebeacon.common.OneEditTwoButtonsDialog.DialogListener;

public class SampleCollectionActivity extends Activity {

	public static final String EXTRA_SECTORS_WITH_SAMPLES = "sector";
	
	private static final int COLLECTION_INTERVAL = 300;
	
	private WiseManager mWiseManager;
	private ArrayList<Beacon> mBeaconsInGroup;
	private ArrayList<String> mBeaconCodes;
	private ArrayList<Sector> mSectors;
	private Sector mNowSamplingSector;
	private int mNowSamplingPosition;
	
	private Handler mHandler;
	
	private Button mAddSector;
	private Button mResetAllSamples;
	private Button mSave;
	private Button mCancel;
	
	private ListView mListView;
	private SampleCollectionListAdapter mAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_collection);
		initBeaconData();
		mWiseManager = WiseManager.getInstance(this);
		
		mHandler = new Handler();
		mSectors = new ArrayList<Sector>();
		
		mAddSector = (Button) findViewById(R.id.btn_add_sector);
		mAddSector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddSectorClicked();
			}
		});
		
		mResetAllSamples = (Button) findViewById(R.id.btn_reset_all_samples);
		mResetAllSamples.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onResetAllSampleClicked();
			}
		});
		
		mSave = (Button) findViewById(R.id.btn_save);
		mSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putParcelableArrayListExtra(EXTRA_SECTORS_WITH_SAMPLES, mSectors);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		mCancel = (Button) findViewById(R.id.btn_cancel);
		mCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		mAdapter = new SampleCollectionListAdapter(this);
		
		mListView = (ListView) findViewById(R.id.list_sample_collection);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onSectorListItemClicked(position);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mWiseManager.startReceiving();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mWiseManager.stopReceiving();
		stopSampling();
	}
	
	private void initBeaconData() {
		mBeaconsInGroup = getIntent().getParcelableArrayListExtra(SectorTopologyFragment.BUNDLE_BEACONS);
		mBeaconCodes = new ArrayList<String>();
		for(Beacon beacon : mBeaconsInGroup) {
			mBeaconCodes.add(beacon.getCode());
		}
	}
	
	private void onAddSectorClicked() {
		OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
		dialog.setPrompt("Sector 추가");
		dialog.setEditTitle("Sector 명");
		dialog.setDialogListener(new DialogListener() {
			@Override
			public void onOkButtonClicked(String str) {
				Sector sector = new Sector(str);
				mSectors.add(sector);
				mAdapter.add(new SampleCollectionListData(sector, 0, 0));
			}
		});
		
		dialog.show(getFragmentManager(), "dialog");
	}
	
	private void onResetAllSampleClicked() {
		
	}
	
	private boolean mSampling = false;
	private int mSamplingPosition = -1;
	private void onSectorListItemClicked(int position) {
		if(!mSampling) {
			mNowSamplingSector = mAdapter.getItem(position).getSector();
			mNowSamplingPosition = position;
			startSampling();
			mSampling = true;
			mListView.getChildAt(position).setBackgroundColor(Color.BLUE);
			mSamplingPosition = position;
		}
		else if(mSampling && mSamplingPosition == position) {
			stopSampling();
			mSampling = false;
			mListView.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
		}
	}
	
	private void startSampling() {
		if(mNowSamplingSector == null || mSampling)
			return;
		mHandler.post(mSamplingRunnable);
	}
	
	private void stopSampling() {
		if(!mSampling)
			return;
		mHandler.removeCallbacks(mSamplingRunnable);
		mNowSamplingSector = null;
	}
	
	private Runnable mSamplingRunnable = new Runnable() {
		@Override
		public void run() {
			RssiVector rssi = mWiseManager.getAverageRssiVector(mBeaconCodes);
			if(rssi == null) {
				L.d("rssivector is null");
			} 
			else {
				mNowSamplingSector.addSectorSample(rssi);
				mAdapter.updateSampleNumber(mSamplingPosition);
				L.i("Sample : " + rssi.toString());
			}
			mHandler.postDelayed(mSamplingRunnable, COLLECTION_INTERVAL);
		}
	};
}
