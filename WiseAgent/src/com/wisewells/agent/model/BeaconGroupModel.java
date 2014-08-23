package com.wisewells.agent.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.wisewells.agent.WiseServer;
import com.wisewells.agent.db.DB.DBBeaconGroup;
import com.wisewells.agent.db.DBController;
import com.wisewells.sdk.beacon.BeaconGroup;

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
	
	public void add(int depth, String name, String parentCode, ResultListener listener) {
		String code = WiseServer.requestCode();
		String uuid = WiseServer.requestUuid();
		Integer major = WiseServer.requestMajor();
		
		BeaconGroup group = new BeaconGroup(depth, name, code, parentCode, uuid, major);
		
		ContentValues values = new ContentValues();
		values.put(DBBeaconGroup._CODE, code);
		values.put(DBBeaconGroup.NAME, name);
		values.put(DBBeaconGroup.DEPTH, depth);
		if(parentCode != null) { 
			values.put(DBBeaconGroup.__PARENT_CODE, parentCode);
		}
		values.put(DBBeaconGroup.UUID, uuid);
		values.put(DBBeaconGroup.MAJOR, major);
		
		mDB.insert(DBBeaconGroup.TABLE_NAME, values);
		
		ArrayList<BeaconGroup> result = new ArrayList<BeaconGroup>();
		result.add(group);
		listener.onSuccess(result);
	}
	
	public void get(String code) {
		
	}
	
	public ArrayList<BeaconGroup> getChildren(String parentCode) {
		Cursor c = mDB.query(DBBeaconGroup.TABLE_NAME, ALL_COLUMNS, DBBeaconGroup.__PARENT_CODE, parentCode);
		
		ArrayList<BeaconGroup> result = new ArrayList<BeaconGroup>();
		
		int[] idx = Utils.getColumnIndexes(ALL_COLUMNS, c);
		while(c.moveToNext()) {
			String code = c.getString(idx[0]);
			String name = c.getString(idx[1]);
			int depth = c.getInt(idx[2]);
			String p_code = c.getString(idx[3]);
			String uuid = c.getString(idx[4]);
			int major = c.getInt(idx[5]);
			String date = c.getString(idx[6]);
			String time = c.getString(idx[7]);
			
			BeaconGroup group = new BeaconGroup(depth, name, code, p_code, uuid, major, date, time);
			result.add(group);
		}

		c.close();
		return result;
	}
	
	public interface ResultListener {
		void onSuccess(ArrayList<BeaconGroup> groups);
		void onFail();
	}
}
