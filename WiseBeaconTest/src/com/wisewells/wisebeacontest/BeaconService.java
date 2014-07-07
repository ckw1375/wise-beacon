package com.wisewells.wisebeacontest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BeaconService extends Service {
	public BeaconService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("Service", "onBind");		
		return null;		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("Service", "onCreate");		
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("Service", "onUnbind");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("Service", "onDestroy");
	}
}
