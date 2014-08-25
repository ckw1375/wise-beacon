package com.wisewells.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;

import com.wisewells.agent.beaconreceiver.BeaconReceiver;
import com.wisewells.agent.connector.ApplicationConnector;
import com.wisewells.agent.model.BeaconGroupModel;
import com.wisewells.agent.model.BeaconModel;
import com.wisewells.agent.model.ServiceModel;
import com.wisewells.agent.model.TopologyModel;
import com.wisewells.sdk.BeaconTracker;
import com.wisewells.sdk.BeaconTracker.Filter;
import com.wisewells.sdk.aidl.IWiseAgent;
import com.wisewells.sdk.aidl.IWiseAgent.Stub;
import com.wisewells.sdk.aidl.RPCListener;
import com.wisewells.sdk.aidl.TopologyStateChangeListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.beacon.DistanceVector;
import com.wisewells.sdk.beacon.Region;
import com.wisewells.sdk.beacon.RssiVector;
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
	private final HandlerThread mHandlerThread;
	private final Handler mHandler;
	private final BeaconTracker mTracker;
	private BeaconReceiver mBeaconReceiver;
	
	private BeaconModel mBeaconModel;
	private BeaconGroupModel mGroupModel;
	private ServiceModel mServiceModel;
	private TopologyModel mTopologyModel;

	public WiseAgent() {
		mConnectorMap = new HashMap<String, ApplicationConnector>();
		mApplicationRequestingFindBeacon = new HashSet<String>();
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
		
		mBeaconModel = new BeaconModel(this);
		mGroupModel = new BeaconGroupModel(this);
		mServiceModel = new ServiceModel(this);
		mTopologyModel = new TopologyModel(this);
	}

	public void onDestroy() {
		L.i("Service destroyed");

		mHandlerThread.quit();
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
		String name = intent.getStringExtra("package name");
		ApplicationConnector connector = new ApplicationConnector(name);
		mConnectorMap.put(name, connector);
		
		return mBinder;
	}

	IWiseAgent.Stub mBinder = new Stub() {

		@Override
		public void addBeaconGroup(int depth, String name, String parentCode, RPCListener listener) throws RemoteException {
			mGroupModel.add(depth, name, parentCode, listener);
		}

		@Override
		public BeaconGroup getBeaconGroup(String code) throws RemoteException {
			return mGroupModel.get(code);
		}

		@Override
		public List<BeaconGroup> getBeaconGroups(String parentCode) throws RemoteException {
			return mGroupModel.getChildren(parentCode);
		}

		@Override
		public List<BeaconGroup> getBeaconGroupsInAuthority() throws RemoteException {
			return mGroupModel.getAll();
		}

		@Override
		public List<Beacon> getBeaconsInGroup(String groupCode) throws RemoteException {
			return mBeaconModel.getAllBeaconsInGroup(groupCode);
		};

		@Override
		public void addBeaconToBeaconGroup(String groupCode, Beacon beacon, RPCListener listener) 
				throws RemoteException {
			mBeaconModel.addTo(groupCode, beacon, listener);
		}

		@Override
		public void addService(int depth, String name, String parentCode, RPCListener listener)
				throws RemoteException {
			mServiceModel.add(depth, name, parentCode, listener);
		}

		@Override
		public List<Service> getRootServices() throws RemoteException {
			return mServiceModel.getRootServices();
		}
		
		@Override
		public List<Service> getChildServices(String parentCode) throws RemoteException {
			return mServiceModel.getChildren(parentCode);
		}

		@Override
		public Bundle getTopology(String code) throws RemoteException {
			Bundle bundle = new Bundle();
			bundle.putParcelable(IpcUtils.BUNDLE_KEY, mWiseObjects.getTopology(code));
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
		public void addLocationTopology() throws RemoteException {
			
		}

		@Override
		public void addProximityTopology(String serviceCode, String groupCode, 
				List<String> beaconCodes, double[] ranges, RPCListener listener) throws RemoteException {
			
			mTopologyModel.addProximityTopology(serviceCode, groupCode, beaconCodes, ranges, listener);
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
			
			L.d("Agent try addSectorTopology");
			SectorTopology t = new SectorTopology(makeBeaconVector(beaconCodes));
			t.setAllSectors(sectors);
			t.setCode(WiseServer.requestCode());
			
			/*
			 * Builder pattern 써보자.. 시간되면
			 */
			mWiseObjects.getService(serviceCode).attachTo(t);
			mWiseObjects.getBeaconGroup(groupCode).attachTo(t);
			mWiseObjects.putTopology(t);
		}
		
		@Override
		public void startTrackingTopologyState(String packageName, String serviceCode, 
				TopologyStateChangeListener listener) throws RemoteException {
			mBeaconReceiver.activate();
			ApplicationConnector connector = mConnectorMap.get(packageName);
			List<Topology> topologies = mWiseObjects.getAllTopologiesInService(serviceCode);
			for(Topology t : topologies) 
				t.setBeaconTracker(mTracker);
			
			L.i(topologies.size() + "");
			connector.startTopologyChecker(topologies, listener);
		}
		
		@Override
		public RssiVector getAverageRssiVector(List<String> beaconCodes) throws RemoteException {
			return mTracker.getAvgRssi(makeBeaconVector(beaconCodes));
		}
		
		@Override
		public void stopTrackingTopologyState(String packageName) throws RemoteException {
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
		}
	};
}
