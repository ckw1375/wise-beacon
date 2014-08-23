package com.wisewells.agent.model;

import java.util.Arrays;

import android.database.Cursor;

public class Utils {
	public static int[] getColumnIndexes(String[] columns, Cursor c) {
		int[] indexes = new int[columns.length];
		for(int i=0; i<columns.length; i++) {
			indexes[i] = c.getColumnIndex(columns[i]);
		}
		
		return indexes;
	}
	
	public static String[] makeNewColumStringArray(String[] originColumns, String... addedColumns) {
		String[] newColumns = Arrays.copyOf(originColumns, addedColumns.length);
		int last = originColumns.length;
		
		for(int i=0; i<addedColumns.length; i++) {
			newColumns[last + i] = addedColumns[i];
		}
		
		return newColumns;
	}
}
