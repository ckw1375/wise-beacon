package com.wisewells.sdk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.estimote.sdk.internal.Preconditions;
import com.wisewells.sdk.ipc.IPC;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public class IpcUtils {
	
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