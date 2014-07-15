package com.wisewells.sdk;

public abstract class MSG {
	/**
	 *	WiseWells Constants
	 *	Manager send these messages
	 */
	public static final int MESSENGER_REGISTER 		= 101;
	public static final int MESSENGER_UNREGISTER 	= 102;
	public static final int TRACKING_START			= 111;
	public static final int TRACKING_STOP			= 112;
	public static final int BEACON_GROUP_ADD		= 120;
	public static final int BEACON_GROUP_MODIFY 	= 121;
	public static final int BEACON_GROUP_DELETE 	= 122;
	public static final int BEACON_ADD				= 130;
	public static final int BEACON_MODIFY 			= 131;
	public static final int BEACON_DELETE 			= 132;
	public static final int SERVICE_ADD				= 140;
	public static final int SERVICE_MODIFY 			= 141;
	public static final int SERVICE_DELETE			= 142;
	public static final int TOPOLOGY_ADD			= 150;
	public static final int TOPOLOGY_MODIFY			= 151;
	public static final int TOPOLOGY_DELETE			= 152;
	
	/**
	 *	WiseWells Constants
	 *	Agent send these messages
	 */
	public static final int RESPONSE_OBJECT 		= 201;
	public static final int RESPONSE_TRACKING		= 202;
	
	/**
	 *	Estimote Constants 
	 */
	public static final int START_RANGING 				= 1;
	public static final int STOP_RANGING 				= 2;
	public static final int RANGING_RESPONSE 			= 3;
	public static final int START_MONITORING 			= 4;
	public static final int STOP_MONITORING				= 5;
	public static final int MONITORING_RESPONSE 		= 6;
	public static final int REGISTER_ERROR_LISTENER 	= 7;
	public static final int ERROR_RESPONSE 				= 8;
	public static final int SET_FOREGROUND_SCAN_PERIOD 	= 9;
	public static final int SET_BACKGROUND_SCAN_PERIOD 	= 10;
	public static final int ERROR_COULD_NOT_START_LOW_ENERGY_SCANNING = -1;
}
