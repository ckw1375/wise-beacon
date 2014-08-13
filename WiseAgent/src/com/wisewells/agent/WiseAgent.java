package com.wisewells.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;

import com.wisewells.agent.beaconreceiver.BeaconReceiver;
import com.wisewells.agent.connector.ApplicationConnector;
import com.wisewells.sdk.BeaconTracker;
import com.wisewells.sdk.BeaconTracker.Filter;
import com.wisewells.sdk.aidl.EditObjectListener;
import com.wisewells.sdk.aidl.IWiseAgent;
import com.wisewells.sdk.aidl.IWiseAgent.Stub;
import com.wisewells.sdk.aidl.TopologyStateChangeListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.beacon.DistanceVector;
import com.wisewells.sdk.beacon.MajorGroup;
import com.wisewells.sdk.beacon.MinorGroup;
import com.wisewells.sdk.beacon.Region;
import com.wisewells.sdk.beacon.RssiVector;
import com.wisewells.sdk.beacon.UuidGroup;
import com.wisewells.sdk.service.ProximityTopology;
import com.wisewells.sdk.service.Sector;
import com.wisewells.sdk.service.SectorTopology;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.IpcUtils;
import com.wisewells.sdk.utils.L;

public class WiseAgent extends android.app.Service {

	static final boolean DEBUG_MODE = false;

	static final long EXPIRATION_MILLIS = TimeUnit.SECONDS.toMillis(10L);

	private static final String THREAD_NAME_AGENT = "WiseAgentThread";

	private final HashMap<String, ApplicationConnector> mConnectorMap;
	private final HashSet<String> mApplicationRequestingFindBeacon;
	private final WiseObjects mWiseObjects;
	private final HandlerThread mHandlerThread;
	private final Handler mHandler;
	private final BeaconTracker mTracker;
	private BeaconReceiver mBeaconReceiver;
	
	

	public WiseAgent() {
		mConnectorMap = new HashMap<String, ApplicationConnector>();
		mApplicationRequestingFindBeacon = new HashSet<String>();
		mWiseObjects = new WiseObjects();
		mHandlerThread = new HandlerThread(THREAD_NAME_AGENT, 10);
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		mTracker = BeaconTracker.getInstance();
		
		BeaconTracker.Filter filter = new Filter();
		filter.add(new Region(null, null, null));
		
		mTracker.setFilter(filter);
	}

	public void onCreate() {
		super.onCreate();
		if (DEBUG_MODE)
			android.os.Debug.waitForDebugger();
		L.i("Creating service");

		mBeaconReceiver = new BeaconReceiver(this, mHandler, mTracker);
	}

	public void onDestroy() {
		L.i("Service destroyed");

		mHandlerThread.quit();
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
		String name = intent.getStringExtra("package name");
		ApplicationConnector connector = new ApplicationConnector(name, mHandler);
		mConnectorMap.put(name, connector);
		
		return mBinder;
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
			if(!(group instanceof MajorGroup)) {
				throw new RuntimeException("Only Major Group can add beacon. Maybe this group code is uuid or minor group's code");
			}
			
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

		/*@Override
		public List<BeaconGroup> getBeaconGroups(List<String> codes)
				throws RemoteException {

			ArrayList<BeaconGroup> groups = new ArrayList<BeaconGroup>();
			for (String code : codes) {
				groups.add(mWiseObjects.getBeaconGroup(code));
			}

			return groups;
		}*/

		@Override
		public List<Beacon> getBeacons(String groupCode) throws RemoteException {
			ArrayList<Beacon> beacons = mWiseObjects.getAllBeaconsInGroup(groupCode);
			return beacons;
		}

		@Override
		public void addService(String name, String parentCode, EditObjectListener listener)
				throws RemoteException {

			Service service = new Service(name);
			service.setCode(WiseServer.requestCode());

			if(parentCode != null) mWiseObjects.getService(parentCode).addChild(service);
			mWiseObjects.putService(service);
			
			Bundle b = new Bundle();
			b.putParcelable(IpcUtils.BUNDLE_KEY, service);
			listener.onEditSuccess("Success Add Service", b);
		}

		@Override
		public List<Service> getRootServices() throws RemoteException {
			return mWiseObjects.getRootServices();
		}
		
		@Override
		public List<Service> getChildServices(String parentCode) throws RemoteException {
			Set<String> codes = mWiseObjects.getService(parentCode).getChildCodes();
			ArrayList<Service> willReturn = new ArrayList<Service>();
			for(String code : codes) {
				willReturn.add(mWiseObjects.getService(code));
			}
			
			return willReturn;
		}

		@Override
		public Bundle getBeaconGroup(String code) throws RemoteException {
			Bundle bundle = new Bundle();
			bundle.putParcelable(IpcUtils.BUNDLE_KEY, mWiseObjects.getBeaconGroup(code));
			return bundle;
		}

		@Override
		public Bundle getTopology(String code) throws RemoteException {
			Bundle bundle = new Bundle();
			bundle.putParcelable(IpcUtils.BUNDLE_KEY, mWiseObjects.getTopology(code));
			return bundle;
		}

		@Override
		public Bundle getBeaconGroupsInAuthority() throws RemoteException {
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList(IpcUtils.BUNDLE_KEY, mWiseObjects.getBeaconGroupsInAuthority());
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
			return mTracker.getAllNearbyBeacons();
		}

		@Override
		public BeaconVector getBeaconVector(String groupCode) throws RemoteException {
			List<Beacon> beacons = getBeacons(groupCode);
			ArrayList<Region> regions = new ArrayList<Region>();
			for(Beacon beacon : beacons) {
				regions.add(beacon.getRegion());
			}
			
			BeaconVector beaconVector = new BeaconVector(regions.size());
			beaconVector.setAll(regions);
			return beaconVector;
		}

		@Override
		public void addLocationTopology() throws RemoteException {
			
		}

		@Override
		public void addProximityTopology(String serviceCode, String groupCode, 
				String[] beaconCodes, double[] ranges) throws RemoteException {
			
			if(beaconCodes.length != ranges.length) 
				throw new RuntimeException("Beacon and range pair is not same");
			
			Double[] temp = new Double[ranges.length];
			for(int i=0; i<ranges.length; i++) {
				temp[i] = ranges[i];
			}
						
			ProximityTopology t = new ProximityTopology(makeBeaconVector(Arrays.asList(beaconCodes)), temp);
			t.setCode(WiseServer.requestCode());
			
			mWiseObjects.getService(serviceCode).attachTo(t);
			mWiseObjects.getBeaconGroup(groupCode).attachTo(t);
			mWiseObjects.putTopology(t);
		}
		
		private BeaconVector makeBeaconVector(List<String> beaconCodes) {
			int size = beaconCodes.size();
			BeaconVector beaconVector = new BeaconVector(size);
			for(int i=0; i<size; i++) {
				Beacon b = mWiseObjects.getBeacon(beaconCodes.get(i));
				Region r = new Region(b.getProximityUUID(), b.getMajor(), b.getMinor());
				beaconVector.set(i, r);
			}
			
			return beaconVector;
		}

		@Override
		public void addSectorTopology(String serviceCode, String groupCode, 
				List<String> beaconCodes, List<Sector> sectors) throws RemoteException {
			
			SectorTopology t = new SectorTopology(makeBeaconVector(beaconCodes));
			t.setCode(WiseServer.requestCode());
			
			/*
			 * Builder pattern 써보자.. 시간되면
			 */
			mWiseObjects.getService(serviceCode).attachTo(t);
			mWiseObjects.getBeaconGroup(groupCode).attachTo(t);
			mWiseObjects.putTopology(t);
		}
		
		@Override
		public void startTracking(String packageName, String serviceCode, TopologyStateChangeListener listener) 
				throws RemoteException {
			mBeaconReceiver.activate();
			ApplicationConnector connector = mConnectorMap.get(packageName);
			List<Topology> topologies = mWiseObjects.getAllTopologiesInService(serviceCode);
			for(Topology t : topologies) 
				t.setBeaconTracker(mTracker);
			
			L.i(topologies.size() + "");
			connector.startTopologyChecker(listener, topologies);
		}
		
		@Override
		public RssiVector getAverageRssiVector(List<String> beaconCodes) throws RemoteException {
			return mTracker.getAvgRssi(makeBeaconVector(beaconCodes));
		}
		
		@Override
		public void stopTracking(String packageName) throws RemoteException {
			ApplicationConnector connector = mConnectorMap.get(packageName);
			connector.stopTopologyChecker();
			mBeaconReceiver.deactivate();
		}
		
		public DistanceVector getBeaconDistance(List<String> beaconCodes) throws RemoteException {
			return mTracker.getAvgDist(makeBeaconVector(beaconCodes));
		}

		@Override
		public boolean addSector(String topologyCode, String sectorName) throws RemoteException {
			SectorTopology topology = null;
			try {
				topology = (SectorTopology) mWiseObjects.getTopology(topologyCode);
			} catch(ClassCastException e) {
				L.e("topologyCode is not sector topology's code.");
			}
			 
			if(topology == null)
				return false;
			
			return topology.addSector(sectorName);
		}

		@Override
		public void addSectorSample(String topologyCode, String sectorName) throws RemoteException {
			SectorTopology topology = null;
			try {
				topology = (SectorTopology) mWiseObjects.getTopology(topologyCode);
				topology.setBeaconTracker(mTracker);
			} catch(ClassCastException e) {
				L.e("topologyCode is not sector topology's code.");
			}
			
			if(topology == null)
				return;
			
			topology.addSample(sectorName);			
		};
	};
}
