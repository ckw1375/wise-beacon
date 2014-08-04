package com.wisewells.wisebeacon.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WiseObjectsTable {
	
	private DBOpenHelper mDBHelper;
	
	public WiseObjectsTable(Context context) {
		mDBHelper = new DBOpenHelper(context);
	}
	
	private Cursor query(String[] columns, String selection) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		return db.query(DB.WiseObjects.TABLE_NAME, columns, selection, null, null, null, null);		
	}
	
	private long insert(Byte object) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DB.WiseObjects.TEST, object);
		
		long result = db.insert(DB.WiseObjects.TABLE_NAME, null, values);
		db.close();
		
		return result;
	}
	
	public static byte[] serialize(Object obj) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(obj);
	    return out.toByteArray();
	}
	
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return is.readObject();
	}
}
