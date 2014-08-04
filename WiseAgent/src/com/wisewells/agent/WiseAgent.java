package com.wisewells.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.estimote.sdk.internal.Preconditions;
import com.wisewells.agent.beacon.BeaconReceiver;
import com.wisewells.agent.beacon.BeaconTracker;
import com.wisewells.agent.beacon.BeaconTracker.Filter;
import com.wisewells.sdk.IPC;
import com.wisewells.sdk.aidl.IWiseAgent;
import com.wisewells.sdk.aidl.IWiseAgent.Stub;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.MajorGroup;
import com.wisewells.sdk.datas.MinorGroup;
import com.wisewells.sdk.datas.Region;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.datas.UuidGroup;
import com.wisewells.sdk.utils.L;

public class WiseAgent extends android.app.Service {

	static final boolean DEBUG_MODE = false;

	static final long EXPIRATION_MILLIS = TimeUnit.SECONDS.toMillis(10L);

	private static final String THREAD_NAME_AGENT = "WiseAgentThread";

	private final WiseObjects mWiseObjects;
	private final HandlerThread mHandlerThread;
	private final Handler mHandler;
	private final BeaconTracker mBeaconTracker;
	private BeaconReceiver mBeaconReceiver;

	private void makeDummyData() {
		mWiseObjects.putService(Dummy.getRootService());
		mWiseObjects.putService(Dummy.getRootService2());
	}

	public WiseAgent() {
		mWiseObjects = WiseObjects.getInstance();
		mHandlerThread = new HandlerThread(THREAD_NAME_AGENT, 10);
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		mBeaconTracker = new BeaconTracker();
		
		BeaconTracker.Filter filter = new Filter();
		filter.add(new Region(null, null, null));
		
		mBeaconTracker.setFilter(filter);

		makeDummyData();
	}

	public void onCreate() {
		super.onCreate();
		if (DEBUG_MODE)
			android.os.Debug.waitForDebugger();
		L.i("Creating service");

		mBeaconReceiver = new BeaconReceiver(this, mHandler, mBeaconTracker);
	}

	public void onDestroy() {
		L.i("Service destroyed");

		mHandlerThread.quit();
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void checkNotOnUiThread() {
		Preconditions
				.checkArgument(Looper.getMainLooper().getThread() != Thread
						.currentThread(),
						"This cannot be run on UI thread, starting BLE scan can be expensive");
		Preconditions
				.checkNotNull(
						Boolean.valueOf(mHandlerThread.getLooper() == Looper
								.myLooper()),
						"It must be executed on service's handlerThread");
	}

	public void startTracking() {

	}

	public void stopTracking() {

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
					default:
						L.d("Unknown message: what=" + what + " obj=" + obj);
					}
				}
			});
		}
	}

	IWiseAgent.Stub mBinder = new Stub() {

		@Override
		public void addUuidGroup(String name) {
			UuidGroup uuidGroup = new UuidGroup(name);
			uuidGroup.setUuid(WiseServer.requestUuid());
			uuidGroup.setCode(WiseServer.requestCode());

			mWiseObjects.putBeaconGroup(uuidGroup);
		}

		@Override
		public void addMajorGroup(String name, String parentCode)
				throws RemoteException {
			MajorGroup majorGroup = new MajorGroup(name);
			majorGroup.setMajor(WiseServer.requestMajor());
			majorGroup.setCode(WiseServer.requestCode());

			mWiseObjects.getBeaconGroup(parentCode).addChild(majorGroup);
			;
			mWiseObjects.putBeaconGroup(majorGroup);
		}

		@Override
		public void addBeaconsToBeaconGroup(String groupCode,
				List<Beacon> beacons) throws RemoteException {

			BeaconGroup group = mWiseObjects.getBeaconGroup(groupCode);

			for (Beacon beacon : beacons) {
				int minor = WiseServer.requestMinor();
				MinorGroup minorGroup = new MinorGroup("minor");
				minorGroup.setMinor(minor);
				minorGroup.setCode(WiseServer.requestCode());
				minorGroup.addBeacon(beacon);

				// beacon.setAddress(((UuidGroup)
				// mWiseObjects.getBeaconGroup(parentCode)).getUuid(), major,
				// minor);

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
			for (String code : codes) {
				groups.add(mWiseObjects.getBeaconGroup(code));
			}

			return groups;
		}

		@Override
		public List<Beacon> getBeacons(String groupCode) throws RemoteException {
			ArrayList<Beacon> beacons = mWiseObjects
					.getBeaconsInGroup(groupCode);
			return beacons;
		}

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

			for (Service service : services) {
				if (parentCode == null) {
					if (service.getTreeLevel() == Service.SERVICE_TREE_ROOT)
						willReturn.add(service);
				} else if (service.getParentCode() != null
						&& service.getParentCode().equals(parentCode))
					willReturn.add(service);
			}

			return willReturn;
		}

		@Override
		public BeaconGroup getBeaconGroup(String code) throws RemoteException {
			return new MajorGroup("test group");
		}

		@Override
		public Bundle getTopology(String code) throws RemoteException {
			Bundle bundle = new Bundle();
			bundle.putParcelable(IPC.BUNDLE_DATA1,
					mWiseObjects.getTopology(code));
			return bundle;
		}

		@Override
		public Bundle getBeaconGroupsInAuthority() throws RemoteException {
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList(IPC.BUNDLE_DATA1,
					mWiseObjects.getBeaconGroupsInAuthority());
			return bundle;
		}

		@Override
		public void startReceiving() throws RemoteException {
			mBeaconReceiver.activate();
		}

		@Override
		public void stopReceiving() throws RemoteException {
			mBeaconReceiver.deactivate();
		}

		@Override
		public List<Beacon> getAllNearbyBeacons() throws RemoteException {
			return mBeaconTracker.getAllNearbyBeacons();
		}
	};
}
