package com.wisewells.sdk.ipc;

public abstract class IPC {
	/**
	 *	WiseWells Constants
	 *	Send	: Manager
	 *	Handle	: Agent
	 */
	public static final int MSG_MESSENGER_REGISTER 		= 101;
	public static final int MSG_MESSENGER_UNREGISTER 	= 102;
	
	public static final int MSG_TRACKING_START			= 111;
	public static final int MSG_TRACKING_STOP			= 112;
	
	public static final int MSG_BEACON_GROUP_ADD		= 120;
	public static final int MSG_BEACON_GROUP_MODIFY 	= 121;
	public static final int MSG_BEACON_GROUP_DELETE 	= 122;
	public static final int MSG_ADD_BEACON_TO_BEACON_GROUP		= 123;
	public static final int MSG_UUID_GROUP_LIST_GET		= 124;
	public static final int MSG_MAJOR_GROUP_LIST_GET	= 125;
	public static final int MSG_BEACON_GROUP_LIST_GET_WITH_CODE = 126;
	
	public static final int MSG_BEACON_ADD				= 130;
	public static final int MSG_BEACON_MODIFY 			= 131;
	public static final int MSG_BEACON_DELETE 			= 132;
	public static final int MSG_BEACON_LIST_GET			= 133;
	
	public static final int MSG_SERVICE_ADD				= 140;
	public static final int MSG_SERVICE_MODIFY 			= 141;
	public static final int MSG_SERVICE_DELETE			= 142;
	public static final int MSG_SERVICE_LIST_GET		= 143;
	
	public static final int MSG_TOPOLOGY_ADD			= 150;
	public static final int MSG_TOPOLOGY_MODIFY			= 151;
	public static final int MSG_TOPOLOGY_DELETE			= 152;		
	public static final int MSG_TOPOLOGY_LIST_GET_WITH_CODE	= 153;
	
	public static final int MSG_DUMMY_BEACON_START		= 1000;
	public static final int MSG_DUMMY_BEACON_STOP		= 1001;
	public static final int MSG_RESPONSE_DUMMY_BEACON	= 1002;
	
	/**
	 *	WiseWells Constants
	 *	Send	: Agent
	 *	Handle	: Manager
	 *	
	 */
	public static final int MSG_RESPONSE_OBJECT 			= 201;
	public static final int MSG_RESPONSE_TRACKING			= 202;
	public static final int MSG_RESPONSE_UUID_GROUP_LIST	= 203;
	public static final int MSG_RESPONSE_MAJOR_GROUP_LIST	= 204;
	public static final int MSG_RESPONSE_BEACON_LIST		= 205;
	public static final int MSG_RESPONSE_SERVICE_LIST		= 206;
	public static final int MSG_RESPONSE_BEACON_GROUP_LIST_WITH_CODE	= 207;
	public static final int MSG_RESPONSE_TOPOLOGY_LIST_WITH_CODE		= 208;
	
	/**
	 * Bundle Data in Message Object
	 */
	public static final String BUNDLE_DATA1	= "data1";
	public static final String BUNDLE_DATA2	= "data2";			
	public static final String BUNDLE_DATA3	= "data3";
	public static final String[] BUNDLE_KEYS = {
		"data1", "data2", "data3", "data4", "data5"
	};
	
	/**
	 *	Estimote Constants 
	 */
	public static final int MSG_START_RANGING 				= 1;
	public static final int MSG_STOP_RANGING 				= 2;
	public static final int MSG_RANGING_RESPONSE 			= 3;
	public static final int MSG_START_MONITORING 			= 4;
	public static final int MSG_STOP_MONITORING				= 5;
	public static final int MSG_MONITORING_RESPONSE 		= 6;
	public static final int MSG_REGISTER_ERROR_LISTENER 	= 7;
	public static final int MSG_ERROR_RESPONSE 				= 8;
	public static final int MSG_SET_FOREGROUND_SCAN_PERIOD 	= 9;
	public static final int MSG_SET_BACKGROUND_SCAN_PERIOD 	= 10;
	public static final int MSG_ERROR_COULD_NOT_START_LOW_ENERGY_SCANNING = -1;
}
