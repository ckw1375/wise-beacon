package com.wisewells.agent.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;

import com.wisewells.agent.WiseServer;
import com.wisewells.agent.db.DB.DBBeaconGroup;
import com.wisewells.agent.db.DBController;
import com.wisewells.sdk.aidl.RPCListener;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.utils.IpcUtils;
import com.wisewells.sdk.utils.L;

public class BeaconGroupModel {
	
	private static final String[] ALL_COLUMNS = {
		DBBeaconGroup._CODE,
		DBBeaconGroup.NAME,
		DBBeaconGroup.DEPTH,
		DBBeaconGroup.__PARENT_CODE,
		DBBeaconGroup.UUID,
		DBBeaconGroup.MAJOR,
		DBBeaconGroup.UPDATE_DATE,
		DBBeaconGroup.UPDATE_TIME
	};
	
	private final DBController mDB;
	
	public BeaconGroupModel(Context context) {
		mDB = DBController.getInstance(context);
	}
	
	public void add(int depth, String name, String parentCode, RPCListener listener) throws RemoteException {
		String code = WiseServer.requestCode();
		String uuid = WiseServer.requestUuid();
		Integer major = WiseServer.requestMajor();
		
		String date = Utils.getCurrentDate();
		String time = Utils.getCurrentTime();
		
		ContentValues values = new ContentValues();
		values.put(DBBeaconGroup._CODE, code);
		values.put(DBBeaconGroup.NAME, name);
		values.put(DBBeaconGroup.DEPTH, depth);
		if(parentCode != null) { 
			values.put(DBBeaconGroup.__PARENT_CODE, parentCode);
		}
		values.put(DBBeaconGroup.UUID, uuid);
		values.put(DBBeaconGroup.MAJOR, major);
		values.put(DBBeaconGroup.UPDATE_DATE, date);
		values.put(DBBeaconGroup.UPDATE_TIME, time);
		
		mDB.insert(DBBeaconGroup.TABLE_NAME, values);
		
		BeaconGroup group = new BeaconGroup(depth, name, code, parentCode, uuid, major, date, time);
		Bundle data = new Bundle();
		data.putParcelable(IpcUtils.BUNDLE_KEY, group);
		listener.onSuccess(data);
	}
	
	public BeaconGroup get(String code) {
		Cursor c = mDB.query(DBBeaconGroup.TABLE_NAME, ALL_COLUMNS, DBBeaconGroup._CODE, code);
		
		int[] idx = Utils.getColumnIndexes(ALL_COLUMNS, c);
		BeaconGroup group = null;
		if(c.moveToNext()) {
			group = makeBeaconGroup(c, idx);
		}
		
		c.close();
		return group;
	}
	
	public ArrayList<BeaconGroup> getAll() {
		Cursor c = mDB.selectAll(DBBeaconGroup.TABLE_NAME);
		
		ArrayList<BeaconGroup> result = new ArrayList<BeaconGroup>();
		int[] indexes = Utils.getColumnIndexes(ALL_COLUMNS, c);
		while(c.moveToNext()) {
			BeaconGroup group = makeBeaconGroup(c, indexes);
			if(group != null) {
				result.add(group);
			}
		}

		c.close();
		return result;
	}
	
	public ArrayList<BeaconGroup> getChildren(String parentCode) {
		Cursor c = mDB.query(DBBeaconGroup.TABLE_NAME, ALL_COLUMNS, DBBeaconGroup.__PARENT_CODE, parentCode);
		
		ArrayList<BeaconGroup> result = new ArrayList<BeaconGroup>();
		int[] idx = Utils.getColumnIndexes(ALL_COLUMNS, c);
		while(c.moveToNext()) {
			BeaconGroup group = makeBeaconGroup(c, idx);
			result.add(group);
		}

		c.close();
		return result;
	}
	
	private BeaconGroup makeBeaconGroup(Cursor c, int[] indexes) {

		String code = c.getString(indexes[0]);
		String name = c.getString(indexes[1]);
		int depth = c.getInt(indexes[2]);
		String p_code = c.getString(indexes[3]);
		String uuid = c.getString(indexes[4]);
		int major = c.getInt(indexes[5]);
		String date = c.getString(indexes[6]);
		String time = c.getString(indexes[7]);

		return new BeaconGroup(depth, name, code, p_code, uuid, major, date, time);
	}
}
