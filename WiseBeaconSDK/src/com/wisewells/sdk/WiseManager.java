package com.wisewells.sdk;

public class WiseManager {
	
	private static WiseManager instance;
	
	private WiseManager() {
		
	}

	public static WiseManager getInstance() {
		if(instance == null) 
			instance = new WiseManager();
		
		return instance;
	}		
}
