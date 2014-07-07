package com.wisewells.sdk.services;

import com.wisewells.sdk.utils.L;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WiseAgent extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		L.d("onUnBind");
		return null;
	}	
	
	@Override
	public boolean onUnbind(Intent intent) {				
		L.i("onUnBind");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		L.d("onCreate");
	}
}
