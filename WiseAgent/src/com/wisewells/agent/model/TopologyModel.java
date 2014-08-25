package com.wisewells.agent.model;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.RemoteException;

import com.wisewells.agent.db.DB.DBProximity;
import com.wisewells.agent.db.DB.DBTopology;
import com.wisewells.agent.db.DBController;
import com.wisewells.sdk.aidl.RPCListener;
import com.wisewells.sdk.utils.L;

public class TopologyModel {

	private final DBController mDB;
	
	public TopologyModel(Context context) {
		mDB = DBController.getInstance(context);
	}
	
	public void addProximityTopology(String serviceCode, String groupCode, List<String> beaconCodes, 
			double[] ranges, RPCListener listener) throws RemoteException {
		
		if(beaconCodes.size() != ranges.length) {
			listener.onFail("Beacon and Range number is not same.");
			return;
		}
		
		int topologyId = addTopology(serviceCode, groupCode, "Proximity");
		
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			for(int i=0; i<beaconCodes.size(); i++) {
				ContentValues values = new ContentValues();
				values.put(DBProximity.__TOPOLOGY_ID, topologyId);
				values.put(DBProximity.__BEACON_CODE, beaconCodes.get(i));
				values.put(DBProximity.RANGE, ranges[i]);
			}
			db.setTransactionSuccessful();
		} catch(SQLiteException e) {
			L.e("Error while transaction.");
		} finally {
			db.endTransaction();
			db.close();
		}
		
	}
	
	private int addTopology(String serviceCode, String groupCode, String type) {
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
		mDB.insert(DBTopology.TABLE_NAME, values);
		
		return id; 
	}
}
