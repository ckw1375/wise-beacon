package com.wisewells.agent.model;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.wisewells.agent.db.DB.DBBeacon;
import com.wisewells.agent.db.DB.DBBeaconGroup;
import com.wisewells.agent.db.DBController;
import com.wisewells.sdk.beacon.Beacon;

public class BeaconModel {
	
	private static final String[] ALL_COLUMNS = {
		DBBeacon._CODE,	
		DBBeacon.NAME, 
		DBBeacon.__MAKER,
		DBBeacon.IMAGE,
		DBBeacon.MACADDRESS,
		DBBeacon.TX_POWER,
		DBBeacon.MEASURED_POWER,
		DBBeacon.INTERVAL,
		DBBeacon.BATTERY,
		DBBeacon.MINOR,
		DBBeacon.__GROUP_CODE,
		DBBeacon.UPDATE_DATE,
		DBBeacon.UPDATE_TIME
	};
	
	private final DBController mDB;
	
	public BeaconModel(Context context) {
		mDB = DBController.getInstance(context);
	}
	
	public ArrayList<Beacon> getBeaconsInGroup(String groupCode) {
		ArrayList<Beacon> result = new ArrayList<Beacon>();
		
		Cursor c = mDB.joinQuery(DBBeacon.TABLE_NAME, DBBeaconGroup.TABLE_NAME, 
				DBBeacon.__GROUP_CODE, DBBeaconGroup._CODE);

		String[] columns = Utils.makeNewColumStringArray(ALL_COLUMNS, DBBeaconGroup.UUID, DBBeaconGroup.MAJOR); 
		int[] idx = Utils.getColumnIndexes(columns, c);
		
		while(c.moveToNext()) {
			String code = c.getString(idx[0]);
			String name = c.getString(idx[1]);
			String maker = c.getString(idx[2]);
			String image = c.getString(idx[3]);
			String mac = c.getString(idx[4]);
			float tx = c.getFloat(idx[5]);
			float measured = c.getFloat(idx[6]);
			float interval = c.getFloat(idx[7]);
			float battery = c.getFloat(idx[8]);
			int minor = c.getInt(idx[9]);
			String g_code = c.getString(idx[10]);
			String date = c.getString(idx[11]);
			String time = c.getString(idx[12]);
			String uuid = c.getString(idx[13]);
			int major = c.getInt(idx[14]);
			
			Beacon beacon = new Beacon(code, name, g_code, mac, 
					uuid, major, minor, battery, tx, measured, 
					interval, maker, image, date, time);
			result.add(beacon);
		}
		
		return result;
	}
}
