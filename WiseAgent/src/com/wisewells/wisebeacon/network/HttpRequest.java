package com.wisewells.wisebeacon.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.wisewells.sdk.utils.L;

public class HttpRequest {
	private HttpURLConnection mConnection;
	private OutputStream mOutStream;
	private BufferedReader mReader;
	
	public JSONObject send(String url, JSONObject json) {
		String response = null;
		
		try {
			initConnection(url);
			sendRequest(json);
			response = receiveResponse();
			if(response == null) {
				return null;
			}
		} finally {
			clearResource();
		}
		return convertStringToJson(response);
	}
	
	private void clearResource() {
		try {
			if(mConnection != null) mConnection.disconnect();
			if(mOutStream != null) mOutStream.close();
			if(mReader != null) mReader.close();
		} catch(IOException e) {
			L.e("Clear Resource Error");
		}
	}
	
	private void initConnection(String url) {
		try {
			mConnection = (HttpURLConnection) new URL(url).openConnection();
			mConnection.setRequestMethod("POST");
			mConnection.setDoInput(true);
			mConnection.setDoOutput(true);
			mConnection.setUseCaches(false);
			mConnection.setConnectTimeout(15000);
			mConnection.setReadTimeout(10000);
			mConnection.setRequestProperty("Content-Type", "application/json;charset=utf8");
//			httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			mConnection.setRequestProperty("Accept", "application/json;charset=tuf8");
		} catch (MalformedURLException e) {
			L.e("url error");
		} catch (ProtocolException e) {
			L.e("set request mettod with post error");
		} catch (IOException e) {
			L.e("url connection error");
		}
	}
	
	private void sendRequest(JSONObject json) {
		try {
			mOutStream = new BufferedOutputStream(mConnection.getOutputStream());
			mOutStream.write(json.toString().getBytes());
			mOutStream.flush();
		} catch (IOException e) {
			L.e("Error while sending request");
		}
	}
	
	private String receiveResponse() {
		try {
			if(mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				mReader = new BufferedReader(new InputStreamReader(mConnection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String buf;
				while((buf = mReader.readLine()) != null) {
					sb.append(buf);
				}
				
				return sb.toString();
			}
		} catch (IOException e) {
			L.e("receive response error");
		}

		return null;
	}
	
	private JSONObject convertStringToJson(String str) {
		JSONObject json = null;
		try {
			json = new JSONObject(str);
		} catch (JSONException e) {
			L.e("JSON Error");
		}
		
		return json;
	}
}
