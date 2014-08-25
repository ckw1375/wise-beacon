package com.wisewells.agent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBController {
	private static DBController sInstance;
	
	private DBOpenHelper mDBHelper;
	
	public static DBController getInstance(Context context) {
		if(sInstance == null) sInstance = new DBController(context);
		return sInstance;
	}
	
	private DBController(Context context){
		mDBHelper = new DBOpenHelper(context);
	}
	
	public long insert(String table, ContentValues values) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long result = db.insert(table, null, values);
		db.close();
		
		return result;
	}
	
	public long delete(String table, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long result = db.delete(table, whereClause, whereArgs);
		db.close();
		return result;
	}
	
	public long update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long result = db.update(table, values, whereClause, whereArgs);
		db.close();
		return result;
	}
	
	public Cursor selectAll(String table) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM ?;", new String[]{ table });
		return c;
	}
	
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor c = db.rawQuery(sql, selectionArgs);
		return c;
	}
	
	public Cursor query(String table, String[] columns, String selectionColumn, String selectionArg) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String selection = selectionColumn + "='?'";; 
		String[] selectionArgs = { selectionArg };
		if(selectionArg == null) {
			selection = selectionColumn + " IS NULL";
		}
		
		Cursor c = db.query(table, columns, selection, selectionArgs, null, null, null);
		return c;
	}
	
	public Cursor joinQuery(String leftTable, String rightTable, String leftColumn, String rightColumn) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		
		String sql = String.format("SELECT * FROM %s JOIN %s ON %s=%s;",
				leftTable, rightTable, leftColumn, rightColumn);
		Cursor c = db.rawQuery(sql, null);
		return c;
	}
}
