package com.wisewells.agent;

import java.util.UUID;

public class WiseServer {
	public static String requestCode(Class<?> clz) {
		StringBuilder code = new StringBuilder(clz.getName());
		code.append((int) (Math.random() * 10000000));
		return code.toString();
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
