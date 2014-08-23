package com.wisewells.agent.db;


public class DB {
	public static boolean intToBool(int val) {
		return val == 1 ? true : false;
	}
	
	public static int boolToInt(boolean b) {
		return b ? 1 : 0;
	}
	
	public static class DBBeaconGroup {
		public static final String TABLE_NAME = "BeaconGroup";
		
		public static final String _CODE = "g_code";	/* VARCHAR(20) */
		public static final String NAME = "g_name";	/* VARCHAR(2000) */
		public static final String DEPTH = "g_depth"; /* INT */
		public static final String __PARENT_CODE = "g_parent_code"; /* VARCHAR(20) */
		public static final String UUID = "g_uuid"; /* VARCHAR(200) */
		public static final String MAJOR = "g_major"; /* INT */
		public static final String UPDATE_DATE = "g_update_date"; /* VARCHAR(8) */
		public static final String UPDATE_TIME = "g_update_time"; /* VARCHAR(6) */
	}
	
	public static class DBBeacon {
		public static final String TABLE_NAME = "Beacon";
		
		public static final String _CODE = "b_code";	/* VARCHAR(20) */
		public static final String NAME = "b_name"; /* VARCHAR(100) */
		public static final String __MAKER = "b_maker";	/* VARCHAR(100) */
		public static final String IMAGE = "b_image"; /* VARCHAR(200) */
		public static final String MACADDRESS = "b_macaddr"; /* VARCHAR(100) */
		public static final String TX_POWER = "b_tx_pw"; /* FLOAT(10,4) */
		public static final String MEASURED_POWER = "b_measure_pw"; /* FLOAT(10,4) */
		public static final String INTERVAL = "b_interval"; /* FLOAT(10,4) */
		public static final String BATTERY = "b_battery"; /* FLOAT(10,4) */
		public static final String MINOR = "b_minor"; /* INTEGER */
		public static final String __GROUP_CODE = "b_group_code"; /* VARCHAR(20) */
		public static final String UPDATE_DATE = "b_update_date"; /* VARCHAR(8) */
		public static final String UPDATE_TIME = "b_update_time"; /* VARCHAR(6) */
	}
	
	public static class DBService {
		public static final String TABLE_NAME = "Service";
		
		public static final String _CODE = "s_code";	/* VARCHAR(20) */
		public static final String NAME = "s_name";	/* VARCHAR(200) */
		public static final String DEPTH = "s_depth";	/* INTEGER */
		public static final String __PARENT_CODE = "s_parent_code";	/* VARCHAR(20) */
		public static final String UPDATE_DATE = "s_update_date";	/* VARCHAR(8) */
		public static final String UPDATE_TIME = "s_update_time";	/* VARCHAR(6) */
	}
	
	public static class DBTopology {
		public static final String TABLE_NAME = "Topology";
		
		public static final String _ID = "t_id";	/* INTEGER */
		public static final String __SERVICE_CODE = "t_service_code";	/* VARCHAR(20) */
		public static final String __GROUP_CODE = "t_group_code";	/* VARCHAR(20) */
		public static final String TYPE = "t_type";	/* VARCHAR(1) */
		public static final String UPDATE_DATE = "t_update_date";	/* VARCHAR(8) */
		public static final String UPDATE_TIME = "t_update_time";	/* VARCHAR(6) */ 
	}
	
}
