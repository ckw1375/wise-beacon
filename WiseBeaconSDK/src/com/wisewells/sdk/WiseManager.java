package com.wisewells.sdk;

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
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.estimote.sdk.internal.Preconditions;
import com.wisewells.agent.MonitoringResult;
import com.wisewells.agent.RangingResult;
import com.wisewells.agent.ScanPeriodData;
import com.wisewells.agent.WiseAgent;
import com.wisewells.agent.WiseServer;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.group.BeaconGroup;
import com.wisewells.sdk.datas.topology.Topology;
import com.wisewells.sdk.utils.L;

public class WiseManager
{
	private static final String ANDROID_MANIFEST_CONDITIONS_MSG = 
			"AndroidManifest.xml does not contain android.permission.BLUETOOTH or "
			+ "android.permission.BLUETOOTH_ADMIN permissions. "
			+ "WiseAgent(Service) may be also not declared in AndroidManifest.xml.";
	private static final String ANDROID_MANIFEST_AGENT_MSG = 
			"Could not bind service: make sure that com.wisewells.sdk.WiseAgent."
			+ "BeaconService is declared in AndroidManifest.xml";
	
	private final Context mContext;
	private final InternalServiceConnection mServiceConnection;
	private final Messenger mIncomingMessenger;
	private final Set<String> mRangedRegionIds;
	private final Set<String> mMonitoredRegionIds;
	private Messenger mErrorReplyTo;
	private RangingListener mRangingListener;
	private MonitoringListener mMonitoringListener;
	private ErrorListener mErrorListener;
	private ServiceReadyCallback mReadyCallback;
	private ScanPeriodData mForegroundScanPeriod;
	private ScanPeriodData mBackgroundScanPeriod;

	public WiseManager(Context context) {
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
		if (!checkPermissionsAndService()) {
			L.e(ANDROID_MANIFEST_CONDITIONS_MSG);
			return false;
		}

		BluetoothManager bluetoothManager = (BluetoothManager)mContext.getSystemService("bluetooth");
		BluetoothAdapter adapter = bluetoothManager.getAdapter();
		return (adapter != null) && (adapter.isEnabled());
	}

	public boolean checkPermissionsAndService() {
		PackageManager pm = mContext.getPackageManager();
		int bluetoothPermission = pm.checkPermission("android.permission.BLUETOOTH", mContext.getPackageName());
		int bluetoothAdminPermission = pm.checkPermission("android.permission.BLUETOOTH_ADMIN", mContext.getPackageName());

		Intent intent = new Intent(mContext, WiseAgent.class);
		List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY);

		return (bluetoothPermission == PackageManager.PERMISSION_GRANTED) && 
				(bluetoothAdminPermission == PackageManager.PERMISSION_GRANTED) && 
				(resolveInfo.size() > 0);
	}

	public void connect(ServiceReadyCallback callback) {
		if (!checkPermissionsAndService()) {
			L.e(ANDROID_MANIFEST_CONDITIONS_MSG);
		}
		mReadyCallback = ((ServiceReadyCallback)Preconditions.checkNotNull(callback, "callback cannot be null"));
		if (isConnectedToService()) {
			callback.onServiceReady();
		}

		boolean bound = mContext.bindService(new Intent(mContext, WiseAgent.class), mServiceConnection, 1);

		if (!bound)
			L.w(ANDROID_MANIFEST_AGENT_MSG);
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
		mErrorReplyTo = null;
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
			mErrorReplyTo.send(scanPeriodMsg);
		} catch (RemoteException e) {
			L.e("Error while setting scan periods: " + msgId);
		}
	}

	private void registerErrorListenerInService() {
		Message registerMsg = Message.obtain(null, 7);
		registerMsg.replyTo = mIncomingMessenger;
		try {
			mErrorReplyTo.send(registerMsg);
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

		Message startRangingMsg = Message.obtain(null, 1);
		startRangingMsg.obj = region;
		startRangingMsg.replyTo = mIncomingMessenger;
		try {
			mErrorReplyTo.send(startRangingMsg);
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
		Message stopRangingMsg = Message.obtain(null, 2);
		stopRangingMsg.obj = regionId;
		try {
			mErrorReplyTo.send(stopRangingMsg);
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
			mErrorReplyTo.send(startMonitoringMsg);
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
			mErrorReplyTo.send(stopMonitoringMsg);
		} catch (RemoteException e) {
			L.e("Error while stopping ranging");
			throw e;
		}
	}

	private boolean isConnectedToService() {
		return mErrorReplyTo != null;
	}
	
	public void startTracking() {
		
	}
	
	public void stopTracking() {
		
	}
	
	public void addBeaconGroup(BeaconGroup group) {
		
	}
	
	public void modifyBeaconGroup(BeaconGroup group) {
		
	}

	public void deleteBeaconGroup(String code) {

	}

	public void addBeacon(Beacon beacon) {
		
	}
	
	public void modifyBeacon(Beacon beacon) {
		
	}

	public void deleteBeacon(String code) {

	}
	
	public void addService(Service service) {
		
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

	private class IncomingHandler extends Handler {
		private IncomingHandler() {

		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG.RANGING_RESPONSE:
				if (WiseManager.this.mRangingListener != null) {
					RangingResult rangingResult = (RangingResult)msg.obj;
					WiseManager.this.mRangingListener.onBeaconsDiscovered(rangingResult.region, rangingResult.beacons);
				}
				break;
			case MSG.MONITORING_RESPONSE:
				if (WiseManager.this.mMonitoringListener != null) {
					MonitoringResult monitoringResult = (MonitoringResult)msg.obj;
					if (monitoringResult.state == Region.State.INSIDE)
						WiseManager.this.mMonitoringListener.onEnteredRegion(monitoringResult.region);
					else
						WiseManager.this.mMonitoringListener.onExitedRegion(monitoringResult.region);
				}
				break;
			case MSG.ERROR_RESPONSE:
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

	private class InternalServiceConnection implements ServiceConnection {
		private InternalServiceConnection() {
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			WiseManager.this.mErrorReplyTo = new Messenger(service);
			
			if (WiseManager.this.mErrorListener != null) {
				WiseManager.this.registerErrorListenerInService();
			}

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
			WiseManager.this.mErrorReplyTo = null;
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
