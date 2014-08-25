package com.wisewells.agent.model;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.os.RemoteException;

import com.wisewells.agent.db.DBController;
import com.wisewells.agent.db.DB.DBTopology;
import com.wisewells.sdk.aidl.RPCListener;

public class TopologyModel {

	private final DBController mDB;
	
	public TopologyModel(Context context) {
		mDB = DBController.getInstance(context);
	}
	
	public void addProximityTopology(String serviceCode, String groupCode, List<String> beaconCodes, 
			List<Double> ranges, RPCListener listener) throws RemoteException {
		
		if(addTopology(serviceCode, groupCode, "Proximity") < 0) {
			listener.onFail("Add Topology is failed");
			return;
		}
		
		ContentValues values = new ContentValues();
		
	}
	
	private long addTopology(String serviceCode, String groupCode, String type) {
		int id = (int) (Math.random() * 100000);
		
		String date = Utils.getCurrentDate();
		String time = Utils.getCurrentTime();
		
		ContentValues values = new ContentValues();
		values.put(DBTopology._ID, id);
		values.put(DBTopology.__SERVICE_CODE, serviceCode);
		values.put(DBTopology.__GROUP_CODE, groupCode);
		values.put(DBTopology.TYPE, type);
		values.put(DBTopology.UPDATE_DATE, date);
		values.put(DBTopology.UPDATE_TIME, time);
		
		return mDB.insert(DBTopology.TABLE_NAME, values);
	}
}
