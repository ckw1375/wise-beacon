package com.wisewells.agent.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;

import com.wisewells.agent.WiseServer;
import com.wisewells.agent.db.DB.DBBeacon;
import com.wisewells.agent.db.DB.DBBeaconGroup;
import com.wisewells.agent.db.DBController;
import com.wisewells.sdk.aidl.RPCListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.utils.IpcUtils;

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
		DBBeacon.UPDATE_TIME,
		DBBeaconGroup.UUID,
		DBBeaconGroup.MAJOR
	};
	
	private final DBController mDB;
	private final BeaconGroupModel mGroupModel;
	
	public BeaconModel(Context context) {
		mDB = DBController.getInstance(context);
		mGroupModel = new BeaconGroupModel(context);
	}
	
	public ArrayList<Beacon> getAllBeaconsInGroup(String groupCode) {
		ArrayList<Beacon> result = new ArrayList<Beacon>();
		
		Cursor c = mDB.joinQuery(DBBeacon.TABLE_NAME, DBBeaconGroup.TABLE_NAME, 
				DBBeacon.__GROUP_CODE, DBBeaconGroup._CODE);

		int[] idx = Utils.getColumnIndexes(ALL_COLUMNS, c);
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
		
		c.close();
		return result;
	}
	
	public void addTo(String groupCode, Beacon beacon, RPCListener listener) throws RemoteException {
		BeaconGroup group = mGroupModel.get(groupCode);
		if(group == null) {
			listener.onFail("Group Code is wrong");
			return;
		}
		
		String uuid = group.getUuid();
		int major = group.getMajor();
		
		String code = WiseServer.requestCode();
		int minor = WiseServer.requestMinor();
		
		String date = Utils.getCurrentDate();
		String time = Utils.getCurrentTime();
		
		ContentValues values = new ContentValues();
		values.put(DBBeacon._CODE, code);
		values.put(DBBeacon.NAME , beacon.getName());
		values.put(DBBeacon.__MAKER, "maker");
		values.put(DBBeacon.IMAGE, "image");
		values.put(DBBeacon.MACADDRESS, beacon.getMacAddress());
		values.put(DBBeacon.TX_POWER, beacon.getTxPower());
		values.put(DBBeacon.MEASURED_POWER, beacon.getMeasuredPower());
		values.put(DBBeacon.INTERVAL, 10);
		values.put(DBBeacon.BATTERY, beacon.getBattery());
		values.put(DBBeacon.MINOR, minor);
		values.put(DBBeacon.__GROUP_CODE, groupCode);
		values.put(DBBeacon.UPDATE_DATE, date);
		values.put(DBBeacon.UPDATE_TIME, time);
		
		mDB.insert(DBBeacon.TABLE_NAME, values);
		
		beacon.setCode(code);
		beacon.setAddress(uuid, major, minor);
		beacon.setUpdateDate(date);
		beacon.setUpdateTime(time);

		Bundle data = new Bundle();
		data.putParcelable(IpcUtils.BUNDLE_KEY, beacon);
		listener.onSuccess(data);
	}
	
	public interface ResultListener {
		void onSuccess(Beacon beacon);
		void onFail();
	}
}
