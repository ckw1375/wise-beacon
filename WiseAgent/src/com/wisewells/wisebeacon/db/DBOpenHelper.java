package com.wisewells.wisebeacon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.db.DB.DBBeacon;
import com.wisewells.wisebeacon.db.DB.DBBeaconGroup;

public class DBOpenHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "wisebeacon.db";
	private final static int DB_VERSION = 2;
	
	public DBOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		L.d("DB OnCreate");
		
		String beaconGroupSql = 
				"CREATE TABLE " + DBBeaconGroup.TABLE_NAME + "("
				+ DBBeaconGroup._CODE + " VARCHAR(20) PRIMARY KEY, "
				+ DBBeaconGroup.NAME + " VARCHAR(2000), "
				+ DBBeaconGroup.DEPTH + " INTEGER, "
				+ DBBeaconGroup.__PARENT_CODE + " VARCHAR(20) "
				+ "REFERENCES " + DBBeaconGroup.TABLE_NAME + "(" + DBBeaconGroup._CODE + ") ON DELETE CASCADE, "
				+ DBBeaconGroup.UPDATE_DATE + " VARCHAR(8), " 
				+ DBBeaconGroup.UPDATE_TIME + " VARCHAR(6)"
				+ ");";
		
		String beaconSql = 
				"CREATE TABLE " + DBBeacon.TABLE_NAME + "("
				+ DBBeacon._CODE + " VARCHAR(20) PRIMARY KEY, "
				+ DBBeacon.NAME + " VARCHAR(100), "
				+ DBBeacon.__MAKER + " VARCHAR(100), "
				+ DBBeacon.IMAGE + " VARCHAR(200), "
				+ DBBeacon.MACADDRESS + " VARCHAR(100), "
				+ DBBeacon.TX_POWER + " FLOAT(10,4), "
				+ DBBeacon.MEASURED_POWER + " FLOAT(10,4), "
				+ DBBeacon.INTERVAL + " FLOAT(10,4), "
				+ DBBeacon.BATTERY + " FLOAT(10,4), "
				+ DBBeacon.MINOR + " VARCHAR(20), "
				+ DBBeacon.__GROUP_CODE + " VARCHAR(20) "
				+ "REFERENCES " + DBBeaconGroup.TABLE_NAME + "(" + DBBeaconGroup._CODE + ") ON DELETE CASCADE"
				+ ");";
		
		db.execSQL(beaconGroupSql);
		db.execSQL(beaconSql);
		
		ContentValues groupValues = new ContentValues();
		groupValues.put(DBBeaconGroup._CODE, "group");
		groupValues.put(DBBeaconGroup.NAME, "지오다노");
		groupValues.put(DBBeaconGroup.DEPTH, 1);
		db.insert(DBBeaconGroup.TABLE_NAME, null, groupValues);
		
		ContentValues beaconValues = new ContentValues();
		beaconValues.put(DBBeacon._CODE, "beacon");
		beaconValues.put(DBBeacon.__GROUP_CODE, "group");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		L.d("DB OnUpgrade");
		db.execSQL("DROP TABLE " + DBBeaconGroup.TABLE_NAME);
		db.execSQL("DROP TABLE " + DBBeacon.TABLE_NAME);
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
	        // Enable foreign key constraints
	        db.execSQL("	");
	    }
	}
}
