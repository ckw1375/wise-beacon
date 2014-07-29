package com.wisewells.sdk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.estimote.sdk.internal.Preconditions;
import com.wisewells.sdk.aidl.IWiseAgent;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.MajorGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;
import com.wisewells.sdk.datas.UuidGroup;
import com.wisewells.sdk.ibeacon.MonitoringResult;
import com.wisewells.sdk.ibeacon.RangingResult;
import com.wisewells.sdk.ibeacon.Region;
import com.wisewells.sdk.ibeacon.ScanPeriodData;
import com.wisewells.sdk.utils.L;

public class WiseManager {
	
	private static final String ACTION_NAME_WISE_AGENT = "com.wisewells.agent.WiseAgent";
	private static final String ANDROID_MANIFEST_CONDITIONS_MSG = 
			"AndroidManifest.xml does not contain android.permission.BLUETOOTH or "
			+ "android.permission.BLUETOOTH_ADMIN permissions. ";			
	
	private final Context mContext;
	private final InternalServiceConnection mServiceConnection;
	private final Messenger mIncomingMessenger;
	private final Set<String> mRangedRegionIds;
	private final Set<String> mMonitoredRegionIds;

	private Messenger mSendingMessenger;
	private RangingListener mRangingListener;
	private MonitoringListener mMonitoringListener;
	private ErrorListener mErrorListener;
	private ServiceReadyCallback mReadyCallback;
	private ScanPeriodData mForegroundScanPeriod;
	private ScanPeriodData mBackgroundScanPeriod;	
	
	private static WiseManager sInstance;
	
	public static WiseManager getInstance(Context context) {
		if(sInstance == null) sInstance = new WiseManager(context);
		return sInstance;
	}
	
	private WiseManager(Context context) {
		mContext = ((Context)Preconditions.checkNotNull(context));
		mServiceConnection = new InternalServiceConnection();
		mIncomingMessenger = new Messenger(new IncomingHandler());
		mRangedRegionIds = new HashSet<String>();
		mMonitoredRegionIds = new HashSet<String>();
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

	/*public boolean checkPermissionsAndService() {
		PackageManager pm = mContext.getPackageManager();
		int bluetoothPermission = pm.checkPermission("android.permission.BLUETOOTH", mContext.getPackageName());
		int bluetoothAdminPermission = pm.checkPermission("android.permission.BLUETOOTH_ADMIN", mContext.getPackageName());

		Intent intent = new Intent(mContext, WiseAgent.class);
		List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY);

		return (bluetoothPermission == PackageManager.PERMISSION_GRANTED) && 
				(bluetoothAdminPermission == PackageManager.PERMISSION_GRANTED) && 
				(resolveInfo.size() > 0);
	}*/
	
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

		boolean bound = mContext.bindService(new Intent(ACTION_NAME_WISE_AGENT), mServiceConnection, 1);

		if (!bound)
			L.w("Fail to bind service");
	}


	public void disconnect() {
		if (!isConnectedToService()) {
			L.i("Not disconnecting because was not connected to service");
			return;
		}

		CopyOnWriteArraySet<String> tempRangedRegionIds = new CopyOnWriteArraySet<String>(mRangedRegionIds);
		for (String regionId : tempRangedRegionIds) {
			try {
				internalStopRanging(regionId);
			} catch (RemoteException e) {
				L.e("Swallowing error while disconnect/stopRanging", e);
			}
		}

		CopyOnWriteArraySet<String> tempMonitoredRegionIds = new CopyOnWriteArraySet<String>(mMonitoredRegionIds);
		for (String regionId : tempMonitoredRegionIds) {
			try {
				internalStopMonitoring(regionId);
			} catch (RemoteException e) {
				L.e("Swallowing error while disconnect/stopMonitoring", e);
			}
		}

		mContext.unbindService(mServiceConnection);
		mSendingMessenger = null;
	}


	public void setRangingListener(RangingListener listener) {
		mRangingListener = ((RangingListener)Preconditions.checkNotNull(listener, "listener cannot be null"));
	}

	public void setMonitoringListener(MonitoringListener listener) {
		mMonitoringListener = ((MonitoringListener)Preconditions.checkNotNull(listener, "listener cannot be null"));
	}

	public void setErrorListener(ErrorListener listener) {
		mErrorListener = listener;
		if ((isConnectedToService()) && (listener != null))
			registerErrorListenerInService();
	}

	public void setForegroundScanPeriod(long scanPeriodMillis, long waitTimeMillis) {
		if (isConnectedToService())
			setScanPeriod(new ScanPeriodData(scanPeriodMillis, waitTimeMillis), 10);
		else
			mForegroundScanPeriod = new ScanPeriodData(scanPeriodMillis, waitTimeMillis);
	}

	public void setBackgroundScanPeriod(long scanPeriodMillis, long waitTimeMillis) {
		if (isConnectedToService())
			setScanPeriod(new ScanPeriodData(scanPeriodMillis, waitTimeMillis), 9);
		else
			mBackgroundScanPeriod = new ScanPeriodData(scanPeriodMillis, waitTimeMillis);
	}

	private void setScanPeriod(ScanPeriodData scanPeriodData, int msgId) {
		Message scanPeriodMsg = Message.obtain(null, msgId);
		scanPeriodMsg.obj = scanPeriodData;
		try {
			mSendingMessenger.send(scanPeriodMsg);
		} catch (RemoteException e) {
			L.e("Error while setting scan periods: " + msgId);
		}
	}

	private void registerErrorListenerInService() {
		Message registerMsg = Message.obtain(null, 7);
		registerMsg.replyTo = mIncomingMessenger;
		try {
			mSendingMessenger.send(registerMsg);
		} catch (RemoteException e) {
			L.e("Error while registering error listener");
		}
	}

	public void startRanging(Region region) throws RemoteException {
		if (!isConnectedToService()) {
			L.i("Not starting ranging, not connected to service");
			return;
		}
		Preconditions.checkNotNull(region, "region cannot be null");

		if (mRangedRegionIds.contains(region.getIdentifier())) {
			L.i("Region already ranged but that's OK: " + region);
		}
		mRangedRegionIds.add(region.getIdentifier());
		
		Bundle data = new Bundle();
		data.putParcelable(IPC.BUNDLE_DATA1, region);

		Message startRangingMsg = Message.obtain(null, IPC.MSG_START_RANGING);
		startRangingMsg.setData(data);
		startRangingMsg.replyTo = mIncomingMessenger;
		try {
			mSendingMessenger.send(startRangingMsg);
		} catch (RemoteException e) {
			L.e("Error while starting ranging", e);
			throw e;
		}
	}

	public void stopRanging(Region region) throws RemoteException {
		if (!isConnectedToService()) {
			L.i("Not stopping ranging, not connected to service");
			return;
		}

		Preconditions.checkNotNull(region, "region cannot be null");
		internalStopRanging(region.getIdentifier());
	}

	private void internalStopRanging(String regionId) throws RemoteException {
		mRangedRegionIds.remove(regionId);
		
		Bundle data = new Bundle();
		data.putString(IPC.BUNDLE_DATA1, regionId);
		
		Message stopRangingMsg = Message.obtain(null, IPC.MSG_STOP_RANGING);
		stopRangingMsg.setData(data);
		try {
			mSendingMessenger.send(stopRangingMsg);
		} catch (RemoteException e) {
			L.e("Error while stopping ranging", e);
			throw e;
		}
	}

	public void startMonitoring(Region region) throws RemoteException {
		if (!isConnectedToService()) {
			L.i("Not starting monitoring, not connected to service");
			return;
		}
		Preconditions.checkNotNull(region, "region cannot be null");

		if (mMonitoredRegionIds.contains(region.getIdentifier())) {
			L.i("Region already monitored but that's OK: " + region);
		}

		mMonitoredRegionIds.add(region.getIdentifier());

		Message startMonitoringMsg = Message.obtain(null, 4);
		startMonitoringMsg.obj = region;
		startMonitoringMsg.replyTo = mIncomingMessenger;
		try {
			mSendingMessenger.send(startMonitoringMsg);
		} catch (RemoteException e) {
			L.e("Error while starting monitoring", e);
			throw e;
		}
	}

	public void stopMonitoring(Region region) throws RemoteException {
		if (!isConnectedToService()) {
			L.i("Not stopping monitoring, not connected to service");
			return;
		}
		Preconditions.checkNotNull(region, "region cannot be null");
		internalStopMonitoring(region.getIdentifier());
	}


	private void internalStopMonitoring(String regionId) throws RemoteException {
		mMonitoredRegionIds.remove(regionId);
		Message stopMonitoringMsg = Message.obtain(null, 5);
		stopMonitoringMsg.obj = regionId;
		try {
			mSendingMessenger.send(stopMonitoringMsg);
		} catch (RemoteException e) {
			L.e("Error while stopping ranging");
			throw e;
		}
	}

	private boolean isConnectedToService() {
		return mSendingMessenger != null;
	}
	
	public void registerMessenger() {
		Message registerMsg = Message.obtain(null, IPC.MSG_MESSENGER_REGISTER);
		Bundle data = new Bundle();
		data.putString(IPC.BUNDLE_DATA1, mContext.getPackageName());
		registerMsg.setData(data);
		registerMsg.replyTo = mIncomingMessenger;
		
		try {
			mSendingMessenger.send(registerMsg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void unregisterMessenger() {
		Message unregisterMsg = Message.obtain(null, IPC.MSG_MESSENGER_UNREGISTER);
		Bundle data = new Bundle();
		data.putString(IPC.BUNDLE_DATA1, mContext.getPackageName());
		unregisterMsg.setData(data);
		
		try {
			mSendingMessenger.send(unregisterMsg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void startTracking() {
		
	}
	
	public void stopTracking() {
		
	}
	
	/*public void addBeaconGroup(String name, String parentCode, ArrayList<Beacon> beacons) throws RemoteException {
		Bundle data = new Bundle();
		data.putString(IPC.BUNDLE_DATA1, name);
		data.putString(IPC.BUNDLE_DATA2, parentCode);
		data.putParcelableArrayList(IPC.BUNDLE_DATA3, beacons);
		
		Message msg = Message.obtain(null, IPC.MSG_BEACON_GROUP_ADD);
		msg.setData(data);
		
		try {
			mSendingMessenger.send(msg);
		} catch (RemoteException e) {
			L.e("Error while adding Beacon Group");
			throw e;
		}
	}*/
	
	public void addBeaconGroup(String name, String parentCode) throws RemoteException {
		mAgent.addBeaconGroup(name, parentCode);
	}
	
	public void addBeaconsToBeaconGroup(String groupCode, ArrayList<Beacon> beacons) throws RemoteException {
		mAgent.addBeaconsToBeaconGroup(groupCode, beacons);
	}
	
	public void addBeaconToBeaconGroup(String groupCode, Beacon beacon) throws RemoteException {
		mAgent.addBeaconToBeaconGroup(groupCode, beacon);
	}
	
	public void modifyBeaconGroup(BeaconGroup group) throws RemoteException {
		Bundle data = new Bundle();
		data.putParcelable(IPC.BUNDLE_DATA1, group);
		
		Message msg = Message.obtain(null, IPC.MSG_BEACON_GROUP_MODIFY);
		msg.setData(data);
		
		try {
			mSendingMessenger.send(msg);
		} catch (RemoteException e) {
			L.e("Error while modifying Beacon Group");
			throw e;
		}		
	}

	public void deleteBeaconGroup(String code) {

	}

	/*public void addBeacon(Beacon beacon) {
		Message msg = Message.obtain(null, IPC.MSG_BEACON_ADD, beacon);
		try {
			mSendingMessenger.send(msg);
		} catch (RemoteException e) {
			L.e("Error while adding beacon");
		}
	}*/
	
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

	public void addTopology(Topology topology) {
		
	}
	
	public void modifyTopology(Topology topology) {
		
	}

	public void deleteTopology(String code) {
		
	}
	
	public List<Service> getServices(String parentCode) throws RemoteException {
		return mAgent.getServices(parentCode);
	}
	
	public List<UuidGroup> getUuidGroups() throws RemoteException {
		return mAgent.getUuidGroups();
	}

	public List<MajorGroup> getMajorGroups(String uuidGroupCode) throws RemoteException {
		return mAgent.getMajorGroups(uuidGroupCode);
	}
	
	public List<BeaconGroup> getBeaconGroups(ArrayList<String> codes) throws RemoteException {
		return  mAgent.getBeaconGroups(codes);
	}
	
	public List<Beacon> getBeacons(String groupCode) throws RemoteException {
		return mAgent.getBeacons(groupCode);
	}
	
	public List<Topology> getTopologies(ArrayList<String> codes) throws RemoteException {
		return mAgent.getTopologies(codes);
	}

	private class IncomingHandler extends Handler {
		private IncomingHandler() {

		}

		public void handleMessage(Message msg) {
			
			Bundle data = msg.getData();
			
			switch (msg.what) {
			case IPC.MSG_RANGING_RESPONSE:
				if (WiseManager.this.mRangingListener != null) {
					data.setClassLoader(RangingResult.class.getClassLoader());
					RangingResult rangingResult = (RangingResult) data.getParcelable(IPC.BUNDLE_DATA1);
					mRangingListener.onBeaconsDiscovered(rangingResult.region, rangingResult.beacons);
				}
				break;
			case IPC.MSG_MONITORING_RESPONSE:
				if (WiseManager.this.mMonitoringListener != null) {
					MonitoringResult monitoringResult = (MonitoringResult)msg.obj;
					if (monitoringResult.state == Region.State.INSIDE)
						WiseManager.this.mMonitoringListener.onEnteredRegion(monitoringResult.region);
					else
						WiseManager.this.mMonitoringListener.onExitedRegion(monitoringResult.region);
				}
				break;
			case IPC.MSG_ERROR_RESPONSE:
				if (WiseManager.this.mErrorListener != null) {
					Integer errorId = (Integer)msg.obj;
					WiseManager.this.mErrorListener.onError(errorId);
				}
				break;
			default:
				L.d("Unknown message: " + msg);
			}
		}
	}

	IWiseAgent mAgent;
	
	private class InternalServiceConnection implements ServiceConnection {
		private InternalServiceConnection() {
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			mAgent = IWiseAgent.Stub.asInterface(service);
			
			WiseManager.this.mSendingMessenger = new Messenger(service);
			
			registerMessenger();
			
			/*if (WiseManager.this.mErrorListener != null) {
				WiseManager.this.registerErrorListenerInService();
			}*/

			if (WiseManager.this.mForegroundScanPeriod != null) {
				WiseManager.this.setScanPeriod(WiseManager.this.mForegroundScanPeriod, 9);
				WiseManager.this.mForegroundScanPeriod = null;
			}

			if (WiseManager.this.mBackgroundScanPeriod != null) {
				WiseManager.this.setScanPeriod(WiseManager.this.mBackgroundScanPeriod, 10);
				WiseManager.this.mBackgroundScanPeriod = null;
			}

			if (WiseManager.this.mReadyCallback != null) {
				WiseManager.this.mReadyCallback.onServiceReady();
				WiseManager.this.mReadyCallback = null;
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			L.e("Service disconnected, crashed? " + name);
			unregisterMessenger();
			WiseManager.this.mSendingMessenger = null;
		}
	}

	public static abstract interface ErrorListener  {
		public abstract void onError(Integer code);
	}

	public static abstract interface MonitoringListener {
		public abstract void onEnteredRegion(Region region);
		public abstract void onExitedRegion(Region region);
	}

	public static abstract interface RangingListener {
		public abstract void onBeaconsDiscovered(Region region, List<Beacon> beacons);
	}

	public static abstract interface ServiceReadyCallback {
		public abstract void onServiceReady();
	}
}
