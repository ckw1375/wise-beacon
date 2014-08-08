package com.wisewells.sdk;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.estimote.sdk.internal.Preconditions;
import com.wisewells.sdk.aidl.IWiseAgent;
import com.wisewells.sdk.aidl.TopologyStateChangeListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.MajorGroup;
import com.wisewells.sdk.beacon.UuidGroup;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.L;

public class WiseManager {
	
	private static final String ACTION_NAME_WISE_AGENT = "com.wisewells.agent.WiseAgent";
	private static final String ANDROID_MANIFEST_CONDITIONS_MSG = 
			"AndroidManifest.xml does not contain android.permission.BLUETOOTH or "
			+ "android.permission.BLUETOOTH_ADMIN permissions. ";			
	
	private final Context mContext;
	private final InternalServiceConnection mServiceConnection;

	private IWiseAgent mAgent;
	private ServiceReadyCallback mReadyCallback;
	
	private static WiseManager sInstance;
	
	public static WiseManager getInstance(Context context) {
		if(sInstance == null) sInstance = new WiseManager(context);
		return sInstance;
	}

	private WiseManager(Context context) {
		mContext = ((Context)Preconditions.checkNotNull(context));
		mServiceConnection = new InternalServiceConnection();
	}

	public boolean hasBluetooth() {
		return mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
	}

	public boolean isBluetoothEnabled() {
		if (!checkPermissions()) {
			L.e(ANDROID_MANIFEST_CONDITIONS_MSG);
			return false;
		}

		BluetoothManager bluetoothManager = (BluetoothManager)mContext.getSystemService("bluetooth");
		BluetoothAdapter adapter = bluetoothManager.getAdapter();
		return (adapter != null) && (adapter.isEnabled());
	}
	
	public boolean checkPermissions() {
		PackageManager pm = mContext.getPackageManager();
		int bluetoothPermission = pm.checkPermission("android.permission.BLUETOOTH", mContext.getPackageName());
		int bluetoothAdminPermission = pm.checkPermission("android.permission.BLUETOOTH_ADMIN", mContext.getPackageName());

		return (bluetoothPermission == PackageManager.PERMISSION_GRANTED) && 
				(bluetoothAdminPermission == PackageManager.PERMISSION_GRANTED);
	}

	public void connect(ServiceReadyCallback callback) {
		if (!checkPermissions()) {
			L.e(ANDROID_MANIFEST_CONDITIONS_MSG);
		}
		mReadyCallback = ((ServiceReadyCallback)Preconditions.checkNotNull(callback, "callback cannot be null"));
		if (isConnectedToService()) {
			callback.onServiceReady();
		}

		Intent intent = new Intent(ACTION_NAME_WISE_AGENT);
		intent.putExtra("package name", mContext.getPackageName());
		boolean bound = mContext.bindService(intent, mServiceConnection, 1);

		if (!bound)
			L.w("Fail to bind service");
	}


	public void disconnect() {
		if (!isConnectedToService()) {
			L.i("Not disconnecting because was not connected to service");
			return;
		}

		mContext.unbindService(mServiceConnection);
	}

	private boolean isConnectedToService() {
		return mAgent != null;
	}
	
	public void startTracking(String packageName, String serviceCode, TopologyStateChangeListener listener)
			throws RemoteException {
		mAgent.startTracking(packageName, serviceCode, listener);
	}
	
	public void stopTracking(String packageName) throws RemoteException{
		mAgent.stopTracking(packageName);
	}
	
	public void addUuidGroup(String name) throws RemoteException {
		mAgent.addUuidGroup(name);
	}
	
	public void addMajorGroup(String name, String parentCode) throws RemoteException {
		mAgent.addMajorGroup(name, parentCode);
	}
	
	public void addBeaconsToBeaconGroup(String groupCode, ArrayList<Beacon> beacons) throws RemoteException {
		mAgent.addBeaconsToBeaconGroup(groupCode, beacons);
	}
	
	public void addBeaconToBeaconGroup(String groupCode, Beacon beacon) throws RemoteException {
		mAgent.addBeaconToBeaconGroup(groupCode, beacon);
	}
	
	public void modifyBeaconGroup(BeaconGroup group) throws RemoteException {
	}

	public void deleteBeaconGroup(String code) {
	}
	
	public void modifyBeacon(Beacon beacon) {
		
	}

	public void deleteBeacon(String code) {

	}
	
	public void addService(String name, String parentCode) throws RemoteException {
		mAgent.addService(name, parentCode);
	}

	public void modifyService(Service service) {

	}

	public void deleteService(String code) {

	}
	
	public void modifyTopology(Topology topology) {
		
	}

	public void deleteTopology(String code) {
		
	}
	
	public List<Service> getRootServices() throws RemoteException {
		return mAgent.getRootServices();
	}
	
	public List<Service> getChildServices(String parentCode) throws RemoteException {
		return mAgent.getChildServices(parentCode);
	}
	
	public List<UuidGroup> getUuidGroups() throws RemoteException {
		return mAgent.getUuidGroups();
	}

	public List<MajorGroup> getMajorGroups(String uuidGroupCode) throws RemoteException {
		return mAgent.getMajorGroups(uuidGroupCode);
	}
	
	/*public List<BeaconGroup> getBeaconGroups(ArrayList<String> codes) throws RemoteException {
		return  mAgent.getBeaconGroups(codes);
	}*/
	
	public List<Beacon> getBeacons(String groupCode) throws RemoteException {
		return mAgent.getBeacons(groupCode);
	}
	
	public BeaconGroup getBeaconGroup(String code) throws RemoteException {
		Bundle bundle = mAgent.getBeaconGroup(code);
		bundle.setClassLoader(BeaconGroup.class.getClassLoader());
		return bundle.getParcelable(IPC.BUNDLE_DATA1);
	}
	
	public List<BeaconGroup> getBeaconGroupsInAuthority() throws RemoteException {
		Bundle bundle = mAgent.getBeaconGroupsInAuthority();
		bundle.setClassLoader(BeaconGroup.class.getClassLoader());
		return bundle.getParcelableArrayList(IPC.BUNDLE_DATA1);
	}

	public Topology getTopology(String code) throws RemoteException {
		Bundle bundle = mAgent.getTopology(code);
		bundle.setClassLoader(Topology.class.getClassLoader());
		return bundle.getParcelable(IPC.BUNDLE_DATA1);
	}
	
	public void addLocationTopology(String serviceCode, String groupCode) throws RemoteException {
		
	}

	public void addProximityTopology(String serviceCode, String groupCode, 
			String[] beaconCodes, double[] ranges) throws RemoteException {
		
		mAgent.addProximityTopology(serviceCode, groupCode, beaconCodes, ranges);
	}

	public void addSectorTopology() throws RemoteException {
		mAgent.addSectorTopology();
	}
	
	public void startReceiving() throws RemoteException {
		mAgent.startReceiving();
	}
	
	public void stopReceiving() throws RemoteException {
		mAgent.stopReceiving();
	}
	
	public List<Beacon> getAllNearbyBeacons() throws RemoteException {
		return mAgent.getAllNearbyBeacons();
	}
	
	private class InternalServiceConnection implements ServiceConnection {
		private InternalServiceConnection() {
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			mAgent = IWiseAgent.Stub.asInterface(service);
			WiseManager.this.mReadyCallback.onServiceReady();
		}

		public void onServiceDisconnected(ComponentName name) {
			L.e("Service disconnected, crashed? " + name);
			WiseManager.this.mAgent = null;
		}
	}

	public static abstract interface ServiceReadyCallback {
		public abstract void onServiceReady();
	}
}
