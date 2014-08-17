package com.wisewells.wisebeacon.topology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.widget.Toast;

import com.wisewells.sdk.beacon.RssiVector;
import com.wisewells.sdk.service.Sector;
import com.wisewells.sdk.utils.L;

public class TopologyUtils {
	public static boolean IO_MODE;
		
	public static void wrtieSampleDataToTextFile(Activity context, Sector sector) {
		if(IO_MODE == false)
			return;

		File dir = context.getExternalFilesDir(null);
		if(!dir.exists()) {
			dir.mkdirs();
			L.e(dir.getAbsolutePath() + " MAKE!!!!!!!!!!!");
		}
		
		File file = new File(dir.getAbsolutePath() + "/sample.txt");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			L.e(file.getAbsolutePath() + " MAKE!!!!!!!!!!!!!!!!!!!!!!");
		}
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file.getAbsolutePath(), true);
			writeSectorInformation(sector, out);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null)
				try {
					out.flush();
					out.close();
					L.e("out close");
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		Toast.makeText(context, file.getAbsolutePath() + " >> " + sector.getName() + " 기록됨.", Toast.LENGTH_LONG).show();
	}
	
	private static void writeSectorInformation(Sector sector, FileOutputStream out) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("======" + sector.getName() + "=====\n");

		for(RssiVector rv : sector.getSamples()) {
			for(int i=0; i<rv.getSize(); i++) {
				sb.append(rv.get(i));
				if(i == rv.getSize() - 1) sb.append("\n");
				else sb.append(",");
			}
		}
		out.write(sb.toString().getBytes());
	}
}
