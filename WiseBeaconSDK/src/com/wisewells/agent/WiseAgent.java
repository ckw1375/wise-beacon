 package com.wisewells.agent;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;

import com.estimote.sdk.internal.Preconditions;
import com.wisewells.sdk.MSG;
import com.wisewells.sdk.Region;
import com.wisewells.sdk.Utils;
import com.wisewells.sdk.WiseObjects;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.group.BeaconGroup;
import com.wisewells.sdk.datas.group.MinorGroup;
import com.wisewells.sdk.datas.group.UuidGroup;
import com.wisewells.sdk.datas.topology.Topology;
import com.wisewells.sdk.utils.L;

public class WiseAgent extends Service {
	
	static final long EXPIRATION_MILLIS = TimeUnit.SECONDS.toMillis(10L);
	
	private static final String THREAD_NAME_AGENT = "WiseAgentThread";
	private static final String ACTION_NAME_START_SCAN = "com.wisewells.agent.startScan";
	private static final String ACTION_NAME_AFTER_SCAN = "com.wisewells.agent.afterScan";
	
	private static final Intent INTENT_START_SCAN = new Intent(ACTION_NAME_START_SCAN);
	private static final Intent INTENT_AFTER_SCAN = new Intent(ACTION_NAME_AFTER_SCAN);
	
	private final WiseObjects mWiseObjects;
	private final Messenger mMessenger;
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
	private Messenger mSendingMessenger;
	private BroadcastReceiver mBluetoothReceiver;
	private BroadcastReceiver mStartScanReceiver;
	private BroadcastReceiver mAfterScanReceiver;
	private PendingIntent mStartScanPendingIntent;	
	private PendingIntent mAfterScanPendingIntent;
	private ScanPeriodData mForegroundScanPeriod;
	private ScanPeriodData mBackgroundScanPeriod;

	public WiseAgent() {
		mWiseObjects = WiseObjects.getInstance();
		mMessenger = new Messenger(new IncomingHandler());
		mLeScanCallback = new InternalLeScanCallback();
		mBeaconsFoundInScanCycle = new ConcurrentHashMap<Beacon, Long>();
		mRangedRegions = new ArrayList<RangingRegion>();
		mMonitoredRegions = new ArrayList<MonitoringRegion>();
		mForegroundScanPeriod = new ScanPeriodData(TimeUnit.SECONDS.toMillis(1L), TimeUnit.SECONDS.toMillis(0L));
		mBackgroundScanPeriod = new ScanPeriodData(TimeUnit.SECONDS.toMillis(5L), TimeUnit.SECONDS.toMillis(30L));
	}

	public void onCreate() {
		super.onCreate();
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
		return mMessenger.getBinder();
	}
 
	private void startRanging(RangingRegion rangingRegion) {
		checkNotOnUiThread();
		L.v("Start ranging: " + rangingRegion.region);
		Preconditions.checkNotNull(mAdapter, "Bluetooth adapter cannot be null");
		mRangedRegions.add(rangingRegion);
		startScanning();
	}

	private void stopRanging(String regionId) {
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

	public void startMonitoring(MonitoringRegion monitoringRegion) {
		checkNotOnUiThread();
		L.v("Starting monitoring: " + monitoringRegion.region);
		Preconditions.checkNotNull(mAdapter, "Bluetooth adapter cannot be null");
		mMonitoredRegions.add(monitoringRegion);
		startScanning();
   }

	public void stopMonitoring(String regionId) {
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
		if (mSendingMessenger != null) {
			Message errorMsg = Message.obtain(null, 8);
			errorMsg.obj = errorId;
			try {
				
				mSendingMessenger.send(errorMsg);
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
	
	/*
	 *	Set, Delete 관련 메소드들은 모두 실제로는 
	 *	1. 네트워크 통신 후 완료되면
	 *	2. Local DB에 저장하고
	 *	3. WiseObjects를 갱신하고
	 *	4. Manager를 통해 App에 완료사항을 알려준다. 
	 */	
	public void addBeaconGroup(BeaconGroup group) {
		String code = WiseServer.requestCode(BeaconGroup.class);
		group.setCode(code);		
		if(group instanceof UuidGroup) {
			
		}
	}
	
	public void modifyBeaconGroup(BeaconGroup group) {
		
	}

	public void deleteBeaconGroup(String code) {

	}

	public void addBeacon(Beacon beacon) {
		// 1. 서버에 비콘 등록을 요청하고, 비콘의 코드와 minor값을 받아온다.
		// 2. 받아온 minor값을 minor그룹을 하나 만들고 beacon에 코드와 부모코드 값을 modify 해준다.
		// 3. 이런 정보를 DB에 저장하고 WiseObjects에도 추가
		String code = WiseServer.requestCode(Beacon.class);
		int minor = WiseServer.requestMinor();
							
		MinorGroup group = new MinorGroup("minor" + minor);
		group.setMinor(minor);
		group.setCode(String.valueOf(minor));		
		
		beacon.setCode(code);
		beacon.setBeaconGroupCode(group.getCode());
		
		group.addBeacon(beacon);
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
 
	private class InternalLeScanCallback implements BluetoothAdapter.LeScanCallback {
		private InternalLeScanCallback() {
		
		}
 
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			WiseAgent.this.checkNotOnUiThread();
			Beacon beacon = Utils.beaconFromLeScan(device, rssi, scanRecord);
			//if ((beacon == null) || (!EstimoteBeacons.isEstimoteBeacon(beacon))) {
			//	L.v("Device " + device + " is not an Estimote beacon");
			//	return;
			//}
			
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
			WiseAgent.this.mHandler.post(new Runnable()
			{
				public void run() {
					switch (what) {
						case MSG.START_RANGING:
							RangingRegion rangingRegion = new RangingRegion((Region)obj, replyTo);
							WiseAgent.this.startRanging(rangingRegion);
							break;
						case MSG.STOP_RANGING:
							String rangingRegionId = (String)obj;
							WiseAgent.this.stopRanging(rangingRegionId);
							break;
						case MSG.START_MONITORING:
							MonitoringRegion monitoringRegion = new MonitoringRegion((Region)obj, replyTo);
							WiseAgent.this.startMonitoring(monitoringRegion);
							break;
						case MSG.STOP_MONITORING:
							String monitoredRegionId = (String)obj;
							WiseAgent.this.stopMonitoring(monitoredRegionId);
							break;
						case MSG.REGISTER_ERROR_LISTENER:
							WiseAgent.this.mSendingMessenger = replyTo;
							break;
						case MSG.SET_FOREGROUND_SCAN_PERIOD:
							L.d("Setting foreground scan period: " + WiseAgent.this.mForegroundScanPeriod);
							WiseAgent.this.mForegroundScanPeriod = ((ScanPeriodData)obj);
							break;
						case MSG.SET_BACKGROUND_SCAN_PERIOD:
							L.d("Setting background scan period: " + WiseAgent.this.mBackgroundScanPeriod);
							WiseAgent.this.mBackgroundScanPeriod = ((ScanPeriodData)obj);
							break;
						case 3:
						case 6:
						case 8:
						case MSG.MESSENGER_REGISTER:						
						case MSG.MESSENGER_UNREGISTER:
						case MSG.TRACKING_START:
						case MSG.TRACKING_STOP:
						case MSG.BEACON_GROUP_ADD:
						case MSG.BEACON_GROUP_MODIFY:
						case MSG.BEACON_GROUP_DELETE:
						case MSG.BEACON_ADD:
						case MSG.BEACON_MODIFY:
						case MSG.BEACON_DELETE:
						case MSG.SERVICE_ADD:
						case MSG.SERVICE_MODIFY:
						case MSG.SERVICE_DELETE:
						case MSG.TOPOLOGY_ADD:
						case MSG.TOPOLOGY_MODIFY:
						case MSG.TOPOLOGY_DELETE:
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
				if (Utils.isBeaconInRegion(beacon, monitoredRegion.region)) {
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
 
		private List<MonitoringRegion> findExitedRegions(long currentTimeMillis)
		{	
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
					Message rangingResponseMsg = Message.obtain(null, 3);
					rangingResponseMsg.obj = new RangingResult(rangingRegion.region, rangingRegion.getSortedBeacons());
					rangingRegion.replyTo.send(rangingResponseMsg);
				} catch (RemoteException e) {
					L.e("Error while delivering responses", e);
				}
			}
       
			for (MonitoringRegion didEnterMonitor : enteredMonitors) {
				Message monitoringResponseMsg = Message.obtain(null, 6);
				monitoringResponseMsg.obj = new MonitoringResult(didEnterMonitor.region, Region.State.INSIDE);
				try
				{
					didEnterMonitor.replyTo.send(monitoringResponseMsg);
				} catch (RemoteException e) {
					L.e("Error while delivering responses", e);
				}
			}
			
			for (MonitoringRegion didEnterMonitor : exitedMonitors) {
				Message monitoringResponseMsg = Message.obtain(null, 6);
				monitoringResponseMsg.obj = new MonitoringResult(didEnterMonitor.region, Region.State.OUTSIDE);
				try
				{
					didEnterMonitor.replyTo.send(monitoringResponseMsg);
				} catch (RemoteException e) {
					L.e("Error while delivering responses", e);
				}
			}
		}
 		
		public void run()
		{
			WiseAgent.this.checkNotOnUiThread();
			long now = System.currentTimeMillis();
			WiseAgent.this.stopScanning();
			processRanging();
			List enteredRegions = findEnteredRegions(now);
			List exitedRegions = findExitedRegions(now);
			removeNotSeenBeacons(now);
			WiseAgent.this.mBeaconsFoundInScanCycle.clear();
			invokeCallbacks(enteredRegions, exitedRegions);
			if (WiseAgent.this.scanWaitTimeMillis() == 0L)
				WiseAgent.this.startScanning();
			else
				WiseAgent.this.setAlarm(WiseAgent.this.mStartScanPendingIntent, WiseAgent.this.scanWaitTimeMillis());
		}
	}
}

