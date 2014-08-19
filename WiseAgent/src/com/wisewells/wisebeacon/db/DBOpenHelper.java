package com.wisewells.wisebeacon.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.db.DB.DbBeacon;
import com.wisewells.wisebeacon.db.DB.DbBeaconGroup;

public class DBOpenHelper extends SQLiteOpenHelper {
	public DBOpenHelper(Context context) {
		super(context, DB.DB_NAME, null, DB.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		L.d("DB OnCreate");
		
		String beaconGroupSql = 
				"CREATE TABLE " + DbBeaconGroup.TABLE_NAME + "("
				+ DbBeaconGroup._CODE + " VARCHAR(20) PRIMARY KEY, "
				+ DbBeaconGroup.NAME + " VARCHAR(2000), "
				+ DbBeaconGroup.DEPTH + " INTEGER, "
				+ DbBeaconGroup.__PARENT_CODE + " VARCHAR(20) "
				+ "REFERENCES " + DbBeaconGroup.TABLE_NAME + "(" + DbBeaconGroup._CODE + ") ON DELETE CASCADE, "
				+ DbBeaconGroup.UPDATE_DATE + " VARCHAR(8), " 
				+ DbBeaconGroup.UPDATE_TIME + " VARCHAR(6)"
				+ ");";
		
		String beaconSql = 
				"CREATE TABLE " + DbBeacon.TABLE_NAME + "("
				+ DbBeacon._CODE + " VARCHAR(20) PRIMARY KEY, "
				+ DbBeacon.NAME + " VARCHAR(100), "
				+ DbBeacon.MAKER + " VARCHAR(100), "
				+ DbBeacon.IMAGE + " VARCHAR(200), "
				+ DbBeacon.MACADDRESS + " VARCHAR(100), "
				+ DbBeacon.TX_POWER + " FLOAT(10,4), "
				+ DbBeacon.MEASURED_POWER + " FLOAT(10,4), "
				+ DbBeacon.INTERVAL + " FLOAT(10,4), "
				+ DbBeacon.BATTERY + " FLOAT(10,4), "
				+ DbBeacon.MINOR + " VARCHAR(20), "
				+ DbBeacon.__GROUP_CODE + " VARCHAR(20) "
				+ "REFERENCES " + DbBeaconGroup.TABLE_NAME + "(" + DbBeaconGroup._CODE + ") ON DELETE CASCADE"
				+ ");";
		
		db.execSQL(beaconGroupSql);
		db.execSQL(beaconSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		L.d("DB OnUpgrade");
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
