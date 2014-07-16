package com.wisewells.wisebeacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wisewells.sdk.WiseManager;
import com.wisewells.wisebeacon.home.HomeActivity;

public class SplashActivity extends Activity {

	private WiseManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		manager = new WiseManager(this);
		// Check if device supports Bluetooth Low Energy.
		if (!manager.hasBluetooth()) {
			Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
			return;
		}

		// If Bluetooth is not enabled, let user enable it.
		if (!manager.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1234);
		} else {
			connectToService();
		}
		
		
	}
	
	private void connectToService() {
		getActionBar().setSubtitle("Scanning...");
		manager.connect(new WiseManager.ServiceReadyCallback() {
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
