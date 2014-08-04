package com.wisewells.wisebeacon.db;

import android.provider.BaseColumns;

public class DB {
	public final static String DB_NAME = "wisebeacon.sqlite";
	public final static int DB_VERSION = 1;

	
	public static boolean intToBool(int val) {
		return val == 1 ? true : false;
	}
	
	public static int boolToInt(boolean b) {
		return b ? 1 : 0;
	}
	
	public static class Dictionary implements BaseColumns {
		public static final String TABLE_NAME = "dictionary";
		public static final String WORD = "word";
		public static final String MEAN = "mean";
	}
	
	public static class WiseObjects implements BaseColumns {
		public static final String TABLE_NAME = "wiseobjects";
		public static final String TEST = "test";
	}
}
