package com.wisewells.wisebeacontest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.wisewells.wisebeacontest.data.Content;

public class ParcelActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parcel);
		
		Content content = getIntent().getParcelableExtra(MainActivity.INTENT_PARCEL);
		Log.i("TEST", content.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.parcel, menu);
		return true;
	}

}
