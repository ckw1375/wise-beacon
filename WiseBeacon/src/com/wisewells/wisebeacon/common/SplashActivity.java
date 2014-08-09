package com.wisewells.wisebeacon.common;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wisewells.sdk.WiseManager;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.home.HomeActivity;

public class SplashActivity extends Activity {

	private WiseManager mManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mManager = WiseManager.getInstance(this);
		connectToService();
	}
	
	private void connectToService() {
		mManager.connect(new WiseManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
						startActivity(intent);
					}
				}, 1000);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.splash, menu);
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
