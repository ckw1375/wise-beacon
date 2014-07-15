package com.wisewells.sdk;

public abstract class MSG {
	/**
	 *	WiseWells Constants
	 *	Manager send these messages
	 */
	public static final int MESSENGER_REGISTER 		= 101;
	public static final int MESSENGER_UNREGISTER 	= 102;
	public static final int OBSERVING_START			= 103;
	public static final int OBSERVING_STOP			= 104;
	public static final int BEACON_GROUP_SET 		= 105;
	public static final int BEACON_GROUP_DELETE 	= 106;
	public static final int BEACON_SET 				= 107;
	public static final int BEACON_DELETE 			= 108;
	public static final int SERVICE_SET 			= 109;
	public static final int SERVICE_DELETE			= 110;
	public static final int TOPOLOGY_SET			= 111;
	public static final int TOPOLOGY_DELETE			= 112;
	
	/**
	 *	WiseWells Constants
	 *	Agent send these messages
	 */
	public static final int TEST 	= 201;
	
	
	
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
