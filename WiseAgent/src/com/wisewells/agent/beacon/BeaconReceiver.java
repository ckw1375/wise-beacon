package com.wisewells.agent.beacon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.Region;
import com.wisewells.sdk.utils.BeaconUtils;

public class BeaconReceiver {
	
	private Context mContext;
	private BluetoothAdapter mAdapter;
	private BluetoothAdapter.LeScanCallback mLeScanCallback;
	private AlarmManager mAlarmManager;
	private Handler mHandler;
	private long mScanTimeMillis;
	private long mIdleTimeMillis;
	private BroadcastReceiver mBluetoothReceiver;
	private BroadcastReceiver mScanReceiver;
	private BroadcastReceiver mIdleReceiver;
	private PendingIntent mScanPendingIntent;
	private PendingIntent mIdlePendingIntent;
	private BeaconTracker mTracker;
	private boolean mActive; // True when the ble scan is activated.
	private boolean mBluetoothOn; // True when the bluetooth device is ON.
	private boolean mScanning; // True when it is in the scanning state.

	public BeaconReceiver(Context nContext, Handler nHandler, BeaconTracker nTracker) {
		mContext = nContext;
		
		BluetoothManager manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		mAdapter = manager.getAdapter();		
		if (mAdapter == null || !mAdapter.isEnabled()) { 
			mBluetoothOn = false;
		}
		else { 
			mBluetoothOn = true;
		}
		
		mLeScanCallback = new ScanCallback();
		
		mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		
		mHandler = nHandler;
		
		mScanTimeMillis = 1000; // default value
		mIdleTimeMillis = 0; // default value
		
		mBluetoothReceiver = new BluetoothBroadcastReceiver();
		mScanReceiver = new scanBroadcastReceiver();
		mIdleReceiver = new IdleBroadcastReceiver();
		
		mContext.registerReceiver(mBluetoothReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"), null, mHandler);
		mContext.registerReceiver(mScanReceiver, new IntentFilter("beaconReceiver.SCAN"), null, mHandler);
		mContext.registerReceiver(mIdleReceiver, new IntentFilter("beaconReceiver.IDLE"), null, mHandler);
		
		mScanPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("beaconReceiver.SCAN"), 0);
		mIdlePendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("beaconReceiver.IDLE"), 0);
		
		mTracker = nTracker;
		mActive = false;
		mScanning = false;
	}

	public boolean setScanIdleTimeMillis(long nScanTimeMillis, long nIdleTimeMillis) {
		if (nScanTimeMillis <= 0 || nIdleTimeMillis < 0)
			return false;
		
		mScanTimeMillis = nScanTimeMillis;
		mIdleTimeMillis = nIdleTimeMillis;
		
		return true;
	}

	public boolean isActive() {
		return mActive;
	}

	public void activate() {
		mActive = true;
		removeAlarm();
		startScan();
	}

	public void deactivate() {
		mActive = false;
		removeAlarm();
		startIdel();
	}

	void startScan() {
		Log.d("BluetoothReceiver", "startScan(), " + "Thread:" + Thread.currentThread().getId());
		mHandler.post(new Runnable() {
			public void run() {
				if (mBluetoothOn == true && mActive == true) {
					mAdapter.startLeScan(mLeScanCallback);
					mScanning = true;
					if (mIdleTimeMillis > 0)
						setAlarm(mIdlePendingIntent, mScanTimeMillis);
				}
			}
		});
	}

	void startIdel() {
		Log.d("BluetoothReceiver", "startIdel(), " + "Thread:" + Thread.currentThread().getId());
		mHandler.post(new Runnable() {
			public void run() {
				mAdapter.stopLeScan(mLeScanCallback);
				mScanning = false;
				if (mBluetoothOn == true && mActive == true)
					setAlarm(mScanPendingIntent, mIdleTimeMillis);
			}
		});
	}

	private void setAlarm(PendingIntent pendingIntent, long delayMillis) {
		mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + delayMillis, pendingIntent);
	}

	private void removeAlarm() {
		mAlarmManager.cancel(mScanPendingIntent);
		mAlarmManager.cancel(mIdlePendingIntent);
	}

	public void onDestroy() {
		removeAlarm();
		mActive = false;
		startIdel();

		mContext.unregisterReceiver(this.mBluetoothReceiver);
		mContext.unregisterReceiver(this.mScanReceiver);
		mContext.unregisterReceiver(this.mIdleReceiver);
	}

	public boolean isScanning() {
		return mScanning;
	}
	
	private class ScanCallback implements BluetoothAdapter.LeScanCallback {
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			mHandler.post(new ScanProcessing(mTracker, device, rssi, scanRecord));
		}
	}

	public static class ScanProcessing implements Runnable {
		BeaconTracker tracker;
		BluetoothDevice device;
		int rssi;
		byte[] scanRecord;

		public ScanProcessing(BeaconTracker nTracker, BluetoothDevice nDevice, int nRssi, byte[] nScanRecord) {
			tracker = nTracker;
			device = nDevice;
			rssi = nRssi;
			scanRecord = nScanRecord;
		}

		public void run() {
			Log.d("BluetoothReceiver", "ScanProcessing, " + "Thread:" + Thread.currentThread().getId());
			Beacon beacon = BeaconUtils.beaconFromLeScan(device, rssi, scanRecord);
			Region r = new Region(beacon.getProximityUUID(), beacon.getMajor(), beacon.getMinor());
			tracker.update(r, beacon.getMacAddress(), (double) rssi, (double) beacon.getMeasuredPower());
		}
	}

	private class BluetoothBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())) {
				int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
				if (state == 12) { // Bluetooth is ON
					mBluetoothOn = true;
					removeAlarm();
					startScan();
				} else if (state == 10) { // Bluetooth is OFF
					mBluetoothOn = false;
					removeAlarm();
					startIdel();
				}
			}
		}
	}

	private class scanBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			BeaconReceiver.this.startScan();
		}
	}

	private class IdleBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			BeaconReceiver.this.startIdel();
		}
	}
}
