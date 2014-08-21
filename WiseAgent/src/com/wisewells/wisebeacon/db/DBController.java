package com.wisewells.wisebeacon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBController {
	private static DBController sInstance;
	
	private DBOpenHelper mDbHelper;
	
	public static DBController getInstance(Context context) {
		if(sInstance == null) sInstance = new DBController(context);
		return sInstance;
	}
	
	private DBController(Context context){
		mDbHelper = new DBOpenHelper(context);
	}
	
	public long insert(String table, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long result = db.insert(table, null, values);
		db.close();
		
		return result;
	}
	
	public long delete(String table, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long result = db.delete(table, whereClause, whereArgs);
		db.close();
		return result;
	}
	
	public long update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long result = db.update(table, values, whereClause, whereArgs);
		db.close();
		return result;
	}
	
	public Cursor selectAll(String table) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM ?;", new String[]{ table });
		return c;
	}
	
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = db.rawQuery(sql, selectionArgs);
		return c;
	}
}
