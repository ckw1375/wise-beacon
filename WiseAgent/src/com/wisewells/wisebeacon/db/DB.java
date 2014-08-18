package com.wisewells.wisebeacon.db;

import android.provider.BaseColumns;

public class DB {
	public final static String DB_NAME = "wisebeacon.db";
	public final static int DB_VERSION = 1;
	
	public static boolean intToBool(int val) {
		return val == 1 ? true : false;
	}
	
	public static int boolToInt(boolean b) {
		return b ? 1 : 0;
	}
	
	public static class DbBeaconGroup implements BaseColumns {
		public static final String TABLE_NAME = "BeaconGroup";
		public static final String _CODE = "_code";	/* VARCHAR(20) */
		public static final String NAME = "name";	/* VARCHAR(2000) */
		public static final String DEPTH = "depth"; /* INT */
		public static final String __PARENT_CODE = "parentcode"; /* VARCHAR(20) */
		public static final String UPDATE_DATE = "update_date"; /* VARCHAR(8) */
		public static final String UPDATE_TIME = "update_time"; /* VARCHAR(6) */
	}
	
	public static class DbBeacon implements BaseColumns {
		public static final String TABLE_NAME = "Beacon";
		public static final String _CODE = "_code";	/* VARCHAR(20) */
		public static final String NAME = "name"; /* VARCHAR(100) */
		public static final String MAKER = "maker";	/* VARCHAR(100) */
		public static final String IMAGE = "image"; /* VARCHAR(200) */
		public static final String MACADDRESS = "macaddr"; /* VARCHAR(100) */
		public static final String TX_POWER = "tx_pw"; /* FLOAT(10,4) */
		public static final String MEASURED_POWER = "measure_pw"; /* FLOAT(10,4) */
		public static final String INTERVAL = "interval"; /* FLOAT(10,4) */
		public static final String BATTERY = "battery"; /* FLOAT(10,4) */
		public static final String MINOR = "minor"; /* VARCHAR(20) */
		public static final String __GROUP_CODE = "bg_code"; /* VARCHAR(20) */
		//등록일자, 시간, 업데이트시간???
	}
}
