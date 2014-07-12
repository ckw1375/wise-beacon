package com.wisewells.sdk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

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
	
	public static void writeMapToParcel(Parcel dest, Map<String, ? extends Parcelable> map) {
		Bundle bundle = new Bundle();
		Set<String> keySet = map.keySet();
		String[] keys = new String[keySet.size()];
		
		int i=0;
		for(String key : keySet) {
			keys[i++] = key;			
			bundle.putParcelable(key, map.get(key));			
		}
		
		dest.writeStringArray(keys);
		dest.writeBundle(bundle);
	}	

	public static HashMap<String, ? extends Parcelable> readMapFromParcel(Parcel in, ClassLoader valueLoader) {
		String[] keys = in.createStringArray();
		Bundle bundle = in.readBundle(valueLoader);
		HashMap<String, Parcelable> map = new HashMap<String, Parcelable>();

		for(String key : keys) 
			map.put(key, bundle.getParcelable(key));

		return map;
	}
}
