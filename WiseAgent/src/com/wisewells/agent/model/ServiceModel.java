package com.wisewells.agent.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;

import com.wisewells.agent.WiseServer;
import com.wisewells.agent.db.DB.DBService;
import com.wisewells.agent.db.DB.DBTopology;
import com.wisewells.agent.db.DBController;
import com.wisewells.sdk.aidl.RPCListener;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.utils.IpcUtils;

public class ServiceModel {
	
	private static final String[] ALL_COLUMNS = {
		DBService._CODE,
		DBService.NAME,
		DBService.DEPTH,
		DBService.__PARENT_CODE,
		DBService.UPDATE_DATE,
		DBService.UPDATE_TIME,
	};
	
	private final DBController mDB;
	
	public ServiceModel(Context context) {
		mDB = DBController.getInstance(context);
	}
	
	public void add(int depth, String name, String parentCode, RPCListener listener) throws RemoteException {
		
		String code = WiseServer.requestCode();
		
		String date = Utils.getCurrentDate();
		String time = Utils.getCurrentTime();
		
		ContentValues values = new ContentValues();
		values.put(DBService._CODE, code);
		values.put(DBService.NAME, name);
		values.put(DBService.DEPTH, depth);
		if(parentCode != null) {
			values.put(DBService.__PARENT_CODE, parentCode);
		}
		values.put(DBService.UPDATE_DATE, Utils.getCurrentDate());
		values.put(DBService.UPDATE_TIME, Utils.getCurrentTime());
		
		mDB.insert(DBService.TABLE_NAME, values);
		
		Service service = new Service(depth, name, code, date, time);
		
		Bundle data = new Bundle();
		data.putParcelable(IpcUtils.BUNDLE_KEY, service);
		listener.onSuccess(data);
	}
	
	public ArrayList<Service> getRootServices() {
		Cursor c = mDB.query(DBService.TABLE_NAME, ALL_COLUMNS, DBService.DEPTH, "1");
		
		ArrayList<Service> result = new ArrayList<Service>();
		int[] indexes = Utils.getColumnIndexes(ALL_COLUMNS, c);
		while(c.moveToNext()) {
			result.add(makeService(c, indexes));
		}
		
		c.close();
		return result;
	}
	
	public ArrayList<Service> getChildren(String parentCode) {
		Cursor c = mDB.query(DBService.TABLE_NAME, ALL_COLUMNS, DBService.__PARENT_CODE, parentCode);

		ArrayList<Service> result = new ArrayList<Service>();
		int[] indexes = Utils.getColumnIndexes(ALL_COLUMNS, c);
		while(c.moveToNext()) {
			result.add(makeService(c, indexes));
		}

		c.close();
		return result;
	}
	
	private Service makeService(Cursor c, int[] indexes) {
		String code = c.getString(indexes[0]);
		String name = c.getString(indexes[1]);
		int depth = c.getInt(indexes[2]);
		String parentCode = c.getString(indexes[3]);
		String updateDate = c.getString(indexes[4]);
		String updateTime = c.getString(indexes[5]);
		
		return new Service(depth, name, parentCode, updateDate, updateTime);
	}
}
