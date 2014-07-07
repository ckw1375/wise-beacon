package com.wisewells.sdk.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class Utils {
	
	public static int checkRunningService(Context context, String serviceFullName) {
		int count = 0;
		
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		for(RunningServiceInfo info : am.getRunningServices(Integer.MAX_VALUE)) {
			if(serviceFullName.equals(info.service.getClassName().toString())) 
				count ++;
		}
		
		return count;
	}
}
