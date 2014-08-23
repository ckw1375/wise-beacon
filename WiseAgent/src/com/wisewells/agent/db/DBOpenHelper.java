package com.wisewells.agent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wisewells.agent.db.DB.DBBeacon;
import com.wisewells.agent.db.DB.DBBeaconGroup;
import com.wisewells.agent.db.DB.DBService;
import com.wisewells.agent.db.DB.DBTopology;
import com.wisewells.sdk.utils.L;

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
				+ DBBeaconGroup.UUID + " VARCHAR(200), "
				+ DBBeaconGroup.MAJOR + " INTEGER, " 
				+ DBBeaconGroup.UPDATE_DATE + " VARCHAR(8), " 
				+ DBBeaconGroup.UPDATE_TIME + " VARCHAR(6)"
				+ ");";
		db.execSQL(beaconGroupSql);
		
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
				+ DBBeacon.MINOR + " INTEGER, "
				+ DBBeacon.__GROUP_CODE + " VARCHAR(20) "
				+ "REFERENCES " + DBBeaconGroup.TABLE_NAME + "(" + DBBeaconGroup._CODE + ") ON DELETE CASCADE, "
				+ DBBeacon.UPDATE_DATE + " VARCHAR(8), "
				+ DBBeacon.UPDATE_TIME + " VARCHAR(6)"
				+ ");";
		db.execSQL(beaconSql);
		
		String serviceSql =
				"CREATE TABLE " + DBService.TABLE_NAME + "("
				+ DBService._CODE + " VARCHAR(20) PRIMARY KEY, "
				+ DBService.NAME + " VARCHAR(200), "
				+ DBService.DEPTH + " INTEGER, "
				+ DBService.__PARENT_CODE + " VARCHAR(20) "
				+ "REFERENCES " + DBService.TABLE_NAME + "(" + DBService._CODE + ") ON DELETE CASCADE, "
				+ DBService.UPDATE_DATE + " VARCHAR(8), "
				+ DBService.UPDATE_TIME + " VARCHAR(6)"
				+ ");";
		db.execSQL(serviceSql);
		
		String topologySql = 
				"CREATE TABLE " + DBTopology.TABLE_NAME + "("
						+ DBTopology._ID + " INTEGER PRIMARY KEY, "
						+ DBTopology.__SERVICE_CODE + " VARCHAR(20) "
						+ "REFERENCES " + DBService.TABLE_NAME + "(" + DBService._CODE + ") ON DELETE CASCADE, "
						+ DBTopology.__GROUP_CODE + " VARCHAR(20) "
						+ "REFERENCES " + DBBeaconGroup.TABLE_NAME + "(" + DBBeaconGroup._CODE + ") ON DELETE CASCADE, "
						+ DBTopology.TYPE + " VARCHAR(1), "
						+ DBTopology.UPDATE_DATE + " VARCHAR(8), "
						+ DBTopology.UPDATE_TIME + " VARCHAR(6)"
						+ ");";
		db.execSQL(topologySql);
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
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}
}