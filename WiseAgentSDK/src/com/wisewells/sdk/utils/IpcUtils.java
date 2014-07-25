package com.wisewells.sdk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public static void sendDataWithMessenger(int messageType, Messenger from, Messenger to, Object... datas) {
		Bundle bundle = new Bundle();
		int key = 0;
		for(Object data : datas) {
			if(data instanceof Integer) bundle.putInt(IPC.BUNDLE_KEYS[key], (Integer) data);			
			else if(data instanceof String) bundle.putString(IPC.BUNDLE_KEYS[key], (String) data);
			else if(data instanceof List<?>) {
				
				Object obj = ((ArrayList<Object>) data).get(0);				
				if(obj instanceof String) 
					bundle.putStringArrayList(IPC.BUNDLE_KEYS[key], (ArrayList<String>)data);
				else if(obj instanceof Parcelable) 
					bundle.putParcelableArrayList(IPC.BUNDLE_KEYS[key], (ArrayList<Parcelable>)data);
			}
			else if(data instanceof Parcelable) {
				bundle.putParcelable(IPC.BUNDLE_KEYS[key], (Parcelable) data);
			}
			
			key++;
		}
		
		Message message = Message.obtain(null, messageType);
		message.setData(bundle);
		message.replyTo = from;
		
		try {
			to.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendDataWithMessenger(int messageType, Messenger from, Messenger to) {
		Message message = Message.obtain(null, messageType);
		message.replyTo = from;
		
		try {
			to.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
