package com.wisewells.agent.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.RemoteException;

import com.wisewells.agent.db.DB.DBProximity;
import com.wisewells.agent.db.DB.DBTopology;
import com.wisewells.agent.db.DBController;
import com.wisewells.sdk.aidl.RPCListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.service.ProximityTopology;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.L;

public class TopologyModel {

	private static final String[] TOPOLOGY_COLUMNS = {
		DBTopology._ID,
		DBTopology.__SERVICE_CODE,
		DBTopology.__GROUP_CODE,
		DBTopology.TYPE,
		DBTopology.UPDATE_DATE,
		DBTopology.UPDATE_TIME 
	};
	
	private static final String[] PROXIMITY_COLUMNS = {
		DBProximity.__TOPOLOGY_ID,
		DBProximity.__BEACON_CODE,
		DBProximity.RANGE
	};
	
	private static final String[] LOCATION_COLUMNS = {

	};
	
	private static final String[] SECTOR_COLUMNS = {

	};
	
	private final DBController mDB;
	private final BeaconModel mBeaconModel;
	private final BeaconGroupModel mGroupModel;
	
	public TopologyModel(Context context) {
		mDB = DBController.getInstance(context);
		mBeaconModel = new BeaconModel(context);
		mGroupModel = new BeaconGroupModel(context);
	}
	
	public void addProximityTopology(String serviceCode, String groupCode, List<String> beaconCodes, 
			double[] ranges, RPCListener listener) throws RemoteException {
		
		if(beaconCodes.size() != ranges.length) {
			listener.onFail("Beacon and Range number is not same.");
			return;
		}
		
		int topologyId = addTopology(serviceCode, groupCode, Topology.TYPE_PROXIMITY);
		
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
	
	private int addTopology(String serviceCode, String groupCode, int type) {
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
	
	public Topology get(int id) {
		Cursor c = mDB.query(DBTopology.TABLE_NAME, TOPOLOGY_COLUMNS, DBTopology._ID, String.valueOf(id));
		
		int[] indexes = Utils.getColumnIndexes(TOPOLOGY_COLUMNS, c);
		if(!c.moveToNext()) {
			return null;
		}
		
		return makeTopology(c, indexes);
	}

	public Topology getTopologyRelatedTo(String serviceCode) {
		Cursor c = mDB.query(DBTopology.TABLE_NAME, TOPOLOGY_COLUMNS, DBTopology.__SERVICE_CODE, serviceCode);
		
		int[] indexes = Utils.getColumnIndexes(TOPOLOGY_COLUMNS, c);
		if(!c.moveToNext()) {
			return null;
		}
		
		return makeTopology(c, indexes);
	}
	
	private Topology makeTopology(Cursor c, int[] indexes) {
		int id = c.getInt(indexes[0]);
		String serviceCode = c.getString(indexes[1]);
		String groupCode = c.getString(indexes[2]);
		int type = c.getInt(indexes[3]);
		String date = c.getString(indexes[4]);
		String time = c.getString(indexes[5]);
		
		Topology topology = null;
		
		switch(type) {
		case Topology.TYPE_PROXIMITY:
			topology = readProximityFromDatabase(id, serviceCode, groupCode, type, date, time);
			break;
		case Topology.TYPE_LOCATION:
			topology = readLocationFromDatabase(id, serviceCode, groupCode, type, date, time);
			break;
		case Topology.TYPE_SECTOR:
			topology = readSectorFromDatabase(id, serviceCode, groupCode, type, date, time);
			break;
		}
		
		return topology;
	}
	
	private Topology readProximityFromDatabase(int id, String serviceCode, String groupCode, 
			int type, String updateDate, String updateTime) {

		Cursor c = mDB.query(DBProximity.TABLE_NAME, PROXIMITY_COLUMNS, DBProximity.__TOPOLOGY_ID, String.valueOf(id));
		
		int[] indexes = Utils.getColumnIndexes(PROXIMITY_COLUMNS, c);
		ArrayList<String> beaconCodes = new ArrayList<String>();
		ArrayList<Double> ranges = new ArrayList<Double>();
		while(c.moveToNext()) {
			String bc = c.getString(indexes[1]);
			beaconCodes.add(bc);
			double range = c.getDouble(indexes[2]);
			ranges.add(range);
		}
		
		BeaconVector bv = new BeaconVector(beaconCodes.size());
		int ind = 0;
		for(String code : beaconCodes) {
			bv.set(ind, mBeaconModel.get(code).getRegion());
			ind++;
		}
		
		return new ProximityTopology(id, type, groupCode, serviceCode, updateDate, updateTime, bv, ranges);
	}
	
	private Topology readLocationFromDatabase(int id, String serviceCode, String groupCode, int type, 
			String updateDate, String updateTime) {
				return null;

	}

	private Topology readSectorFromDatabase(int id, String serviceCode, String groupCode, int type, 
			String updateDate, String updateTime) {
				return null;

	}
}
