package com.wisewells.wisebeacon.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String TAG = "DBOpenHelper";	
	
	public DBOpenHelper(Context context) {
		super(context, DB.DB_NAME, null, DB.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "DB OnCreate");
//		String sql = "CREATE TABLE " + DB.FolderWord.TABLE_NAME + "("
//				+ DB.FolderWord._ID + " integer PRIMARY KEY autoincrement, "
//				+ DB.FolderWord.FOLDER_ID + " integer,"
//				+ DB.FolderWord.WORD + " text,"
//				+ DB.FolderWord.MEAN + " text," 
//				+ DB.FolderWord.IS_BACKUP + " integer);";
		
		String sql = "CREATE TABLE " + DB.WiseObjects.TABLE_NAME + "("
				+ DB.WiseObjects._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DB.WiseObjects.TEST + " BLOB);";
		db.execSQL(sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "DB OnUpgrade");
	}

}
