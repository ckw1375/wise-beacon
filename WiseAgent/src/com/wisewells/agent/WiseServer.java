package com.wisewells.agent;

import java.util.UUID;

public class WiseServer {
	public static String requestCode() {
		return String.valueOf((int) (Math.random() * 10000000));
	}
	
	public static int requestMinor() {
		return (int) (Math.random() * 10000000);
	}	
	
	public static int requestMajor() {
		return (int) (Math.random() * 10000000);
	}
	
	public static String requestUuid() {
		return UUID.randomUUID().toString();	
	}
}
