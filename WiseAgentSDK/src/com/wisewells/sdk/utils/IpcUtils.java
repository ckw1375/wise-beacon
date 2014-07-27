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
	
	
	
	public static void sendMessage(int messageType, Messenger sender, Messenger receiver, Object... datas) throws RemoteException {
		Preconditions.checkNotNull(messageType, "Message Type must be not null");
		Preconditions.checkNotNull(receiver, "to parameter must be not null");
		
		Message message = Message.obtain(null, messageType);		
		if(sender != null) message.replyTo = sender;		
		if(datas != null && datas.length > 0) message.setData(__getBundleForMessage(datas));
		
		try {
			receiver.send(message);
		} catch (RemoteException e) {
			L.e("Error in sendMessage");
			throw e;
		}
	}
	
	private static Bundle __getBundleForMessage(Object... datas) {
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
		
		return bundle;
	}
	
	/*public static void sendMessage(int messageType, Messenger from, Messenger to) {
		Message message = Message.obtain(null, messageType);
		message.replyTo = from;
		
		try {
			to.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}*/
}
