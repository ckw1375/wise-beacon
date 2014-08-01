 
package com.wisewells.agent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;

import com.estimote.sdk.internal.Preconditions;
import com.wisewells.sdk.IPC;
import com.wisewells.sdk.aidl.IWiseAgent;
import com.wisewells.sdk.aidl.IWiseAgent.Stub;
import com.wisewells.sdk.aidl.MonitoringListener;
import com.wisewells.sdk.aidl.RangingListener;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.MajorGroup;
import com.wisewells.sdk.datas.MinorGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.Topology;
import com.wisewells.sdk.datas.UuidGroup;
import com.wisewells.sdk.ibeacon.Region;
import com.wisewells.sdk.ibeacon.ScanPeriodData;
import com.wisewells.sdk.utils.BeaconUtils;
import com.wisewells.sdk.utils.L;

public class WiseAgent extends android.app.Service {

	static final boolean DEBUG_MODE = true;
	
	static final long EXPIRATION_MILLIS = TimeUnit.SECONDS.toMillis(10L);
	
	private static final String THREAD_NAME_AGENT = "WiseAgentThread";
	private static final String ACTION_NAME_START_SCAN = "com.wisewells.agent.startScan";
	private static final String ACTION_NAME_AFTER_SCAN = "com.wisewells.agent.afterScan";
	
	private static final Intent INTENT_START_SCAN = new Intent(ACTION_NAME_START_SCAN);
	private static final Intent INTENT_AFTER_SCAN = new Intent(ACTION_NAME_AFTER_SCAN);

	private final Map<String, Messenger> mConnectedMessengers;
	private final WiseObjects mWiseObjects;
	private final Messenger mIncomingMessenger;
	private final BluetoothAdapter.LeScanCallback mLeScanCallback;
	private final ConcurrentHashMap<Beacon, Long> mBeaconsFoundInScanCycle;
	private final List<RangingRegion> mRangedRegions;
	private final List<MonitoringRegion> mMonitoredRegions;
	private BluetoothAdapter mAdapter;
	private AlarmManager mAlarmManager;
	private HandlerThread mHandlerThread;
	private Handler mHandler;
	private Runnable mAfterScanCycleTask;
	private boolean mScanning;
	private Messenger mErrorReplyTo;
	private BroadcastReceiver mBluetoothReceiver;
	private BroadcastReceiver mStartScanReceiver;
	private BroadcastReceiver mAfterScanReceiver;
	private PendingIntent mStartScanPendingIntent;	
	private PendingIntent mAfterScanPendingIntent;
	private ScanPeriodData mForegroundScanPeriod;
	private ScanPeriodData mBackgroundScanPeriod;
	
	private RangingListener mRangingListener;
	private MonitoringListener mMonitoringListener;
	
	private void makeDummyData() {
		mWiseObjects.putBeaconGroup(Dummy.getUUidGroup());
		mWiseObjects.putBeaconGroup(Dummy.getUUidGroup2());
		mWiseObjects.putService(Dummy.getRootService());
		mWiseObjects.putService(Dummy.getRootService2());
	}

	public WiseAgent() {
		mConnectedMessengers = new HashMap<String, Messenger>();
		mWiseObjects = WiseObjects.getInstance();
		mIncomingMessenger = new Messenger(new IncomingHandler());
		mLeScanCallback = new InternalLeScanCallback();
		mBeaconsFoundInScanCycle = new ConcurrentHashMap<Beacon, Long>();
		mRangedRegions = new ArrayList<RangingRegion>();
		mMonitoredRegions = new ArrayList<MonitoringRegion>();
		mForegroundScanPeriod = new ScanPeriodData(TimeUnit.SECONDS.toMillis(1L), TimeUnit.SECONDS.toMillis(0L));
		mBackgroundScanPeriod = new ScanPeriodData(TimeUnit.SECONDS.toMillis(5L), TimeUnit.SECONDS.toMillis(30L));
		
		makeDummyData();
	}

	public void onCreate() {
		super.onCreate();		
		if(DEBUG_MODE) android.os.Debug.waitForDebugger();
		L.i("Creating service");
		
		mAlarmManager = ((AlarmManager)getSystemService("alarm"));
		BluetoothManager bluetoothManager = (BluetoothManager)getSystemService("bluetooth");
		mAdapter = bluetoothManager.getAdapter();
		mAfterScanCycleTask = new AfterScanCycleTask();
		mHandlerThread = new HandlerThread(THREAD_NAME_AGENT, 10);
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());

		mBluetoothReceiver = createBluetoothBroadcastReceiver();
		mStartScanReceiver = createScanStartBroadcastReceiver();
		mAfterScanReceiver = createAfterScanBroadcastReceiver();
		registerReceiver(mBluetoothReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
		registerReceiver(mStartScanReceiver, new IntentFilter(ACTION_NAME_START_SCAN));
		registerReceiver(mAfterScanReceiver, new IntentFilter(ACTION_NAME_AFTER_SCAN));
		mStartScanPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, INTENT_START_SCAN, 0);
		mAfterScanPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, INTENT_AFTER_SCAN, 0);		
	}

	public void onDestroy() {
		L.i("Service destroyed");
		unregisterReceiver(mBluetoothReceiver);
		unregisterReceiver(mStartScanReceiver);
		unregisterReceiver(mAfterScanReceiver);

		if (mAdapter != null) {
			stopScanning();
		}

		removeAfterScanCycleCallback();
		mHandlerThread.quit();

		super.onDestroy();
	}
 
	public IBinder onBind(Intent intent) {
		return mBinder;
//		return mIncomingMessenger.getBinder();
	}

	private void startScanning() {
		if (mScanning) {
			L.d("Scanning already in progress, not starting one more");			
			return;
		}
    
		if ((mMonitoredRegions.isEmpty()) && (mRangedRegions.isEmpty())) {
			L.d("Not starting scanning, no monitored on ranged regions");      
			return;
		}
    
		if (!mAdapter.isEnabled()) {
			L.d("Bluetooth is disabled, not starting scanning");      
			return;
		}
     
		if (!mAdapter.startLeScan(mLeScanCallback)) {
			L.wtf("Bluetooth adapter did not start le scan");
			sendError(Integer.valueOf(-1));      
			return;
		}
		
		mScanning = true;
		removeAfterScanCycleCallback();
		setAlarm(mAfterScanPendingIntent, scanPeriodTimeMillis());	
	}

	private void stopScanning() {
		try {
			mScanning = false;
			mAdapter.stopLeScan(mLeScanCallback);
		} catch (Exception e) {
			L.wtf("BluetoothAdapter throws unexpected exception", e);
		}
	}
 
	private void sendError(Integer errorId) {
		if (mErrorReplyTo != null) {
			Message errorMsg = Message.obtain(null, 8);
			errorMsg.obj = errorId;
			try {
				
				mErrorReplyTo.send(errorMsg);
			} catch (RemoteException e) {
				L.e("Error while reporting message, funny right?", e);
			}
		}
	}

   
	private long scanPeriodTimeMillis() {
		if (!mRangedRegions.isEmpty()) {
			return mForegroundScanPeriod.scanPeriodMillis;
		}
    
		return mBackgroundScanPeriod.scanPeriodMillis;
	}

	private long scanWaitTimeMillis() {
		if (!mRangedRegions.isEmpty()) {
			return mForegroundScanPeriod.waitTimeMillis;
		}

		return mBackgroundScanPeriod.waitTimeMillis;
	}

	private void setAlarm(PendingIntent pendingIntent, long delayMillis) {
		mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
				SystemClock.elapsedRealtime() + delayMillis, pendingIntent);
	}
 
	private void checkNotOnUiThread() {
		Preconditions.checkArgument(Looper.getMainLooper().getThread() != Thread.currentThread(),
						"This cannot be run on UI thread, starting BLE scan can be expensive");
		Preconditions.checkNotNull(Boolean.valueOf(mHandlerThread.getLooper() == Looper.myLooper()),
						"It must be executed on service's handlerThread");
	}
 
	private BroadcastReceiver createBluetoothBroadcastReceiver() {
		return new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())) {
					int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
					if (state == BluetoothAdapter.STATE_OFF) 
						WiseAgent.this.mHandler.post(new Runnable() {
							public void run() {
								L.i("Bluetooth is OFF: stopping scanning");
								WiseAgent.this.removeAfterScanCycleCallback();
								WiseAgent.this.stopScanning();
								WiseAgent.this.mBeaconsFoundInScanCycle.clear();
							}
						});

					else if (state == BluetoothAdapter.STATE_ON)
						WiseAgent.this.mHandler.post(new Runnable() {
							public void run() {
								if ((!WiseAgent.this.mMonitoredRegions.isEmpty()) || (!WiseAgent.this.mRangedRegions.isEmpty())) {
									L.i(String.format("Bluetooth is ON: resuming scanning (monitoring: %d ranging:%d)", 
											new Object[] { Integer.valueOf(WiseAgent.this.mMonitoredRegions.size()), Integer.valueOf(WiseAgent.this.mRangedRegions.size()) }));									WiseAgent.this.startScanning();
								}
							}
						});
				}
			}
		};
	}
 
	private void removeAfterScanCycleCallback() {
		this.mHandler.removeCallbacks(this.mAfterScanCycleTask);
		this.mAlarmManager.cancel(this.mAfterScanPendingIntent);
		this.mAlarmManager.cancel(this.mStartScanPendingIntent);
	}

	private BroadcastReceiver createAfterScanBroadcastReceiver() {
		return new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				WiseAgent.this.mHandler.post(WiseAgent.this.mAfterScanCycleTask);
			}
		};
	}

	private BroadcastReceiver createScanStartBroadcastReceiver() {
		return new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				WiseAgent.this.mHandler.post(new Runnable() {
					public void run() {
						WiseAgent.this.startScanning();
					}
				});
			}
		};
	}
	
	public void startTracking() {
		
	}

	public void stopTracking() {

	}
	
	public void modifyBeaconGroup(BeaconGroup group) {
		mWiseObjects.putBeaconGroup(group);
	}

	public void deleteBeaconGroup(String code) {

	}

	/*public void addBeacon(Beacon beacon) {
		String code = WiseServer.requestCode();
		int minor = WiseServer.requestMinor();
							
		MinorGroup group = new MinorGroup("minor" + minor);
		group.setMinor(minor);
		group.setCode(String.valueOf(minor));		
		
		beacon.setCode(code);
		beacon.setBeaconGroupCode(group.getCode());
		
		group.addBeacon(beacon);
		
		mWiseObjects.putBeacon(beacon);
		mWiseObjects.putBeaconGroup(group);
	}*/
	
	public void modifyBeacon(Beacon beacon) {
		
	}

	public void deleteBeacon(String code) {

	}
	
	public void addService(String name, String parentCode) {
		String code = WiseServer.requestCode();
		
		Service service = new Service(name);
		service.setCode(code);
		
		mWiseObjects.getService(parentCode).addChild(service);		
		mWiseObjects.putService(service);
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

	private class InternalLeScanCallback implements BluetoothAdapter.LeScanCallback {
		private InternalLeScanCallback() {
		
		}
 
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			WiseAgent.this.checkNotOnUiThread();
			Beacon beacon = BeaconUtils.beaconFromLeScan(device, rssi, scanRecord);
			WiseAgent.this.mBeaconsFoundInScanCycle.put(beacon, Long.valueOf(System.currentTimeMillis()));
		}		
	}
 
	private class IncomingHandler extends Handler {
		private IncomingHandler() {
		
		}
 
		public void handleMessage(Message msg) {
			final int what = msg.what;
			final Object obj = msg.obj;
			final Messenger replyTo = msg.replyTo;
			final Bundle data = msg.getData();
			
			WiseAgent.this.mHandler.post(new Runnable() {
				public void run() {
					switch (what) {
						case IPC.MSG_REGISTER_ERROR_LISTENER:
							WiseAgent.this.mErrorReplyTo = replyTo;
							break;
						case IPC.MSG_SET_FOREGROUND_SCAN_PERIOD:
							L.d("Setting foreground scan period: " + WiseAgent.this.mForegroundScanPeriod);
							WiseAgent.this.mForegroundScanPeriod = ((ScanPeriodData)obj);
							break;
						case IPC.MSG_SET_BACKGROUND_SCAN_PERIOD:
							L.d("Setting background scan period: " + WiseAgent.this.mBackgroundScanPeriod);
							WiseAgent.this.mBackgroundScanPeriod = ((ScanPeriodData)obj);
							break;
						case IPC.MSG_MESSENGER_REGISTER:
							mConnectedMessengers.put(data.getString(IPC.BUNDLE_DATA1), replyTo);
							L.i("Register Messenger From " + data.getString(IPC.BUNDLE_DATA1));
							break;
						case IPC.MSG_MESSENGER_UNREGISTER:
							mConnectedMessengers.remove(data.getString(IPC.BUNDLE_DATA1));
							L.i("Unregister Messenger From " + data.getString(IPC.BUNDLE_DATA1));
							break;
						case IPC.MSG_TRACKING_START:
							break;
						case IPC.MSG_TRACKING_STOP:
							break;
						case IPC.MSG_BEACON_GROUP_MODIFY:
						{
							BeaconGroup beaconGroup = data.getParcelable(IPC.BUNDLE_DATA1);
						}
							break;
						case IPC.MSG_BEACON_GROUP_DELETE:
							break;
						case IPC.MSG_BEACON_ADD:
							break;
						case IPC.MSG_BEACON_MODIFY:
							break;
						case IPC.MSG_BEACON_DELETE:
							break;
						case IPC.MSG_SERVICE_MODIFY:
							break;
						case IPC.MSG_SERVICE_DELETE:
							break;
						case IPC.MSG_TOPOLOGY_ADD:
							break;
						case IPC.MSG_TOPOLOGY_MODIFY:
							break;
						case IPC.MSG_TOPOLOGY_DELETE:
							break;						

						case 3:
						case 6:
						case 8:
						default:
							L.d("Unknown message: what=" + what + " obj=" + obj);
					}
				}
			});
		}
	}
 
	private class AfterScanCycleTask implements Runnable {
		private AfterScanCycleTask() {
    
		}

		private void processRanging() {
			
			for (RangingRegion rangedRegion : WiseAgent.this.mRangedRegions){
				rangedRegion.processFoundBeacons(WiseAgent.this.mBeaconsFoundInScanCycle);
			}
		}

		private List<MonitoringRegion> findEnteredRegions(long currentTimeMillis) {
			
			List<MonitoringRegion> didEnterRegions = new ArrayList<MonitoringRegion>();
			for (Entry<Beacon, Long> entry : WiseAgent.this.mBeaconsFoundInScanCycle.entrySet()) {
				for (MonitoringRegion monitoringRegion : matchingMonitoredRegions((Beacon)entry.getKey())) {
					monitoringRegion.processFoundBeacons(WiseAgent.this.mBeaconsFoundInScanCycle);
					if (monitoringRegion.markAsSeen(currentTimeMillis)) {
						didEnterRegions.add(monitoringRegion);
					}
				}
			}
       
			return didEnterRegions;
		}
 
		private List<MonitoringRegion> matchingMonitoredRegions(Beacon beacon) {
			
			List<MonitoringRegion> results = new ArrayList<MonitoringRegion>();
			for (MonitoringRegion monitoredRegion : WiseAgent.this.mMonitoredRegions) {
				if (BeaconUtils.isBeaconInRegion(beacon, monitoredRegion.region)) {
					results.add(monitoredRegion);
				}
			}
       
			return results;
		}
 
		private void removeNotSeenBeacons(long currentTimeMillis) {
			
			for (RangingRegion rangedRegion : WiseAgent.this.mRangedRegions) {
				rangedRegion.removeNotSeenBeacons(currentTimeMillis);
			}
       
			for (MonitoringRegion monitoredRegion : WiseAgent.this.mMonitoredRegions){
				monitoredRegion.removeNotSeenBeacons(currentTimeMillis);
			}
		}
 
		private List<MonitoringRegion> findExitedRegions(long currentTimeMillis) {	
			
			List<MonitoringRegion> didExitMonitors = new ArrayList<MonitoringRegion>();
			for (MonitoringRegion monitoredRegion : WiseAgent.this.mMonitoredRegions) {
				if (monitoredRegion.didJustExit(currentTimeMillis)) {
					didExitMonitors.add(monitoredRegion);
				}
			}
      
			return didExitMonitors;
		}
 
		private void invokeCallbacks(List<MonitoringRegion> enteredMonitors, List<MonitoringRegion> exitedMonitors) {
			for (RangingRegion rangingRegion : WiseAgent.this.mRangedRegions) {
				try {
					mRangingListener.onBeaconsDiscovered(rangingRegion.region, (List<Beacon>) rangingRegion.getSortedBeacons());
				} catch (RemoteException e) {
					L.e("Error while delivering responses", e);
				}
			}
       
			for (MonitoringRegion didEnterMonitor : enteredMonitors) {
				try {
					mMonitoringListener.onEnteredRegion(didEnterMonitor.region);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
			for (MonitoringRegion didEnterMonitor : exitedMonitors) {
				try {
					mMonitoringListener.onExitedRegion(didEnterMonitor.region);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
 		
		public void run()
		{
			WiseAgent.this.checkNotOnUiThread();
			long now = System.currentTimeMillis();
			WiseAgent.this.stopScanning();
			
			processRanging();
			List<MonitoringRegion> enteredRegions = findEnteredRegions(now);
			List<MonitoringRegion> exitedRegions = findExitedRegions(now);
			
			removeNotSeenBeacons(now);
			
			WiseAgent.this.mBeaconsFoundInScanCycle.clear();
			
			invokeCallbacks(enteredRegions, exitedRegions);
			
			if (WiseAgent.this.scanWaitTimeMillis() == 0L)
				WiseAgent.this.startScanning();
			else
				WiseAgent.this.setAlarm(WiseAgent.this.mStartScanPendingIntent, WiseAgent.this.scanWaitTimeMillis());
		}
	}
	
	IWiseAgent.Stub mBinder = new Stub() {
		@Override
		public void addBeaconGroup(String name, String parentCode) throws RemoteException {
			int major = WiseServer.requestMajor();

			MajorGroup majorGroup = new MajorGroup(name);
			majorGroup.setMajor(major);
			majorGroup.setCode(WiseServer.requestCode());

			UuidGroup uuidGroup = (UuidGroup) mWiseObjects.getBeaconGroup(parentCode);
			uuidGroup.addChild(majorGroup);

			mWiseObjects.putBeaconGroup(uuidGroup);
			mWiseObjects.putBeaconGroup(majorGroup);
		}

		@Override
		public void addBeaconsToBeaconGroup(String groupCode, List<Beacon> beacons) 
				throws RemoteException {
			
			BeaconGroup group = mWiseObjects.getBeaconGroup(groupCode);
			
			for(Beacon beacon : beacons) {
				int minor = WiseServer.requestMinor();
				MinorGroup minorGroup = new MinorGroup("minor");
				minorGroup.setMinor(minor);
				minorGroup.setCode(WiseServer.requestCode());
				minorGroup.addBeacon(beacon);		
				
//				beacon.setAddress(((UuidGroup) mWiseObjects.getBeaconGroup(parentCode)).getUuid(), major, minor);
				
				group.addChild(minorGroup);
				
				mWiseObjects.putBeaconGroup(minorGroup);
				mWiseObjects.putBeacon(beacon);
			}
			
			mWiseObjects.putBeaconGroup(group);
		}

		@Override
		public void addBeaconToBeaconGroup(String groupCode, Beacon beacon)
				throws RemoteException {
			
			BeaconGroup group = mWiseObjects.getBeaconGroup(groupCode);
			
			beacon.setCode(WiseServer.requestCode());
			
			int minor = WiseServer.requestMinor();
			MinorGroup minorGroup = new MinorGroup("minor");
			minorGroup.setMinor(minor);
			minorGroup.setCode(WiseServer.requestCode());
			minorGroup.addBeacon(beacon);	
			
			group.addChild(minorGroup);
			
			mWiseObjects.putBeaconGroup(minorGroup);
			mWiseObjects.putBeacon(beacon);
		}

		@Override
		public List<UuidGroup> getUuidGroups() throws RemoteException {
			return mWiseObjects.getUuidGroups();
		}

		@Override
		public List<MajorGroup> getMajorGroups(String uuidGroupCode)
				throws RemoteException {
			return mWiseObjects.getMajorGroups(uuidGroupCode);
		}

		@Override
		public List<BeaconGroup> getBeaconGroups(List<String> codes)
				throws RemoteException {
			
			ArrayList<BeaconGroup> groups = new ArrayList<BeaconGroup>();
			for(String code : codes) {
				groups.add(mWiseObjects.getBeaconGroup(code));
			}
			
			return groups;
		}

		@Override
		public List<Beacon> getBeacons(String groupCode) throws RemoteException {
			ArrayList<Beacon> beacons = mWiseObjects.getBeaconsInGroup(groupCode);
			return beacons;
		}

//		@Override
//		public List<Topology> getTopologies(List<String> codes)
//				throws RemoteException {
//			
//			ArrayList<Topology> topologies = new ArrayList<Topology>();
//			for(String code : codes) {
//				topologies.add(mWiseObjects.getTopology(code));
//			}
//			return topologies;
//		}

		@Override
		public void addService(String name, String parentCode)
				throws RemoteException {
			
			String code = WiseServer.requestCode();
			
			Service service = new Service(name);
			service.setCode(code);
			
			mWiseObjects.getService(parentCode).addChild(service);		
			mWiseObjects.putService(service);
		}

		@Override
		public List<Service> getServices(String parentCode)
				throws RemoteException {
			
			ArrayList<Service> services = mWiseObjects.getServices();
			ArrayList<Service> willReturn = new ArrayList<Service>();
			
			for(Service service : services) {
				if(parentCode == null) {
					if(service.getTreeLevel() == Service.SERVICE_TREE_ROOT) willReturn.add(service);
				}
				else if(service.getParentCode() != null && service.getParentCode().equals(parentCode)) 
					willReturn.add(service);
			}
			
			return willReturn;
		}

		@Override
		public void setBackgroundScanPeriod(long scanPeriodMillis, long waitTimeMillis)
				throws RemoteException {
			
		}

		@Override
		public void setForegroundScanPeriod(long scanPeriodMillis, long waitTimeMillis)
				throws RemoteException {
			
		}

		@Override
		public void startMonitoring(Region region) throws RemoteException {
			checkNotOnUiThread();
			
			MonitoringRegion monitoringRegion = new MonitoringRegion(region);			
			
			L.v("Starting monitoring: " + monitoringRegion.region);			
			Preconditions.checkNotNull(mAdapter, "Bluetooth adapter cannot be null");
			
			mMonitoredRegions.add(monitoringRegion);
			startScanning();
		}

		@Override
		public void startRanging(Region region) throws RemoteException {
			checkNotOnUiThread();
			
			RangingRegion rangingRegion = new RangingRegion(region);	
			
			L.v("Start ranging: " + rangingRegion.region);
			Preconditions.checkNotNull(mAdapter, "Bluetooth adapter cannot be null");
			
			mRangedRegions.add(rangingRegion);
			startScanning();
		}

		@Override
		public void stopMonitoring(String regionId) throws RemoteException {
			L.v("Stopping monitoring: " + regionId);
			checkNotOnUiThread();
			Iterator<MonitoringRegion> iterator = mMonitoredRegions.iterator();
			while (iterator.hasNext()) {
				MonitoringRegion monitoringRegion = (MonitoringRegion)iterator.next();
				if (regionId.equals(monitoringRegion.region.getIdentifier())) {
					iterator.remove();
				}
			}
	     
			if ((mMonitoredRegions.isEmpty()) && (mRangedRegions.isEmpty())) {
				removeAfterScanCycleCallback();
				stopScanning();
				mBeaconsFoundInScanCycle.clear();
			}
		}

		@Override
		public void stopRanging(String regionId) throws RemoteException {
			checkNotOnUiThread();
			L.v("Stopping ranging: " + regionId);		
			Iterator<RangingRegion> iterator = mRangedRegions.iterator();
			while (iterator.hasNext()) {
				RangingRegion rangingRegion = (RangingRegion)iterator.next();
				if (regionId.equals(rangingRegion.region.getIdentifier())) {
					iterator.remove();
				}
			}
	     
			if ((mRangedRegions.isEmpty()) && (mMonitoredRegions.isEmpty())) {
				removeAfterScanCycleCallback();
				stopScanning();
				mBeaconsFoundInScanCycle.clear();
			}
		}

		@Override
		public void registerMonitoringListener(MonitoringListener listener)
				throws RemoteException {
			mMonitoringListener = listener;
		}

		@Override
		public void registerRangingListener(RangingListener listener)
				throws RemoteException {
			mRangingListener = listener;
		}

		@Override
		public BeaconGroup getBeaconGroup(String code) throws RemoteException {
			return new MajorGroup("test group");
		}

		@Override
		public Bundle getTopology(String code) throws RemoteException {
			Bundle bundle = new Bundle();
			bundle.putParcelable(IPC.BUNDLE_DATA1, mWiseObjects.getTopology(code));
			return bundle;
		}

		@Override
		public Bundle getBeaconGroupsInAuthority() throws RemoteException {
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList(IPC.BUNDLE_DATA1, mWiseObjects.getBeaconGroupsInAuthority());
			return bundle;
		}
	};
}

