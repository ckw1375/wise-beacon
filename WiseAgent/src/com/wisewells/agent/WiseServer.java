package com.wisewells.agent;


public class WiseServer {
	public static String requestCode() {
		return String.valueOf((int) (Math.random() * 10000000));
	}
	
	public static int requestMinor() {
//		return (int) (Math.random() * 10000000);
		return 4000;
	}	
	
	public static int requestMajor() {
//		return (int) (Math.random() * 10000000);
		return 0;
	}
	
	public static String requestUuid() {
//		return UUID.randomUUID().toString();	
		return "e2c56db5-dffb-48d2-b060-d0f5a71096e0";
	}
}
