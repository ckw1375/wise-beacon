package com.wisewells.agent;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.db.DB.DBBeacon;
import com.wisewells.wisebeacon.db.DB.DBBeaconGroup;
import com.wisewells.wisebeacon.db.DBController;

public class ObjectManager {
	private final DBController mDBController;	
	
	public ObjectManager(Context context) {
		mDBController = DBController.getInstance(context);
	}
	
	public BeaconGroup addBeaconGroup(int depth, String name, String parentCode) {
		String code = WiseServer.requestCode();
		String uuid = WiseServer.requestUuid();
		Integer major = depth == 0 ? null : WiseServer.requestMajor();
		
		ContentValues values = new ContentValues();
		values.put(DBBeaconGroup._CODE, code);
		values.put(DBBeaconGroup.DEPTH, depth);
		values.put(DBBeaconGroup.NAME, name);
		values.put(DBBeaconGroup.__PARENT_CODE, parentCode);
		
		mDBController.insert(DBBeaconGroup.TABLE_NAME, values);
		
		BeaconGroup group = new BeaconGroup(depth, name);
		group.setCode(code);
		return group;
	}
	
	public ArrayList<BeaconGroup> getBeaconGroups(String parentCode) {
		String sql;
		if(parentCode == null) {
			sql = String.format("SELECT * FROM %s WHERE %s IS NULL", 
					DBBeaconGroup.TABLE_NAME, DBBeaconGroup.__PARENT_CODE);
		}
		else {
			sql = String.format("SELECT * FROM %s WHERE %s='%s';", 
					DBBeaconGroup.TABLE_NAME, DBBeaconGroup.__PARENT_CODE, parentCode);
		}
		L.d("query : " + sql);
		
		Cursor c = mDBController.rawQuery(sql, null);
		ArrayList<BeaconGroup> groups = new ArrayList<BeaconGroup>();
		if(c == null)
			return groups;
		
		while(c.moveToNext()) {
			String _code = c.getString(0);
			String name = c.getString(1);
			int depth = c.getInt(2);
			String __parentCode = c.getString(3);

			BeaconGroup group = new BeaconGroup(depth, name, _code, __parentCode, null, null, null, null);
			groups.add(group);
		}
		
		c.close();
		return groups;
	}
	
	public ArrayList<Beacon> getBeaconsInGroup(String groupCode) {
		String sql = String.format("SELECT * FROM %s WHERE %s='%s';", 
				DBBeacon.TABLE_NAME, DBBeacon.__GROUP_CODE, groupCode);
		L.d("query : " + sql);
		
		Cursor c = mDBController.rawQuery(sql, null);
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		if(c == null) 
			return beacons;
		
		while(c.moveToNext()) {
			String _code = c.getString(0);
			String name = c.getString(1);
			String maker = c.getString(2);
			String image = c.getString(3);
			String mac = c.getString(4);
			float tx = c.getFloat(5);
			float measured = c.getFloat(6);
			float interval = c.getFloat(7);
			float battery = c.getFloat(8);
			String minor = c.getString(9);
			String __groupCode = c.getString(10);
			
		}
		
		return null;
	}
}
 