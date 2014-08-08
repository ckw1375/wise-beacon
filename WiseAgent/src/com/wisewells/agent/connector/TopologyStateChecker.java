package com.wisewells.agent.connector;

import java.util.List;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;

import com.wisewells.sdk.aidl.TopologyStateChangeListener;
import com.wisewells.sdk.beacon.Region;
import com.wisewells.sdk.service.LocationTopology;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.L;

class TopologyStateChecker {
	private final TopologyStateChangeListener mListener;
	private final List<Topology> mTopologies;
	private final Thread mThread;
	
	private long mCheckIntervalInMillis;
	
	public TopologyStateChecker(TopologyStateChangeListener topologyListener, List<Topology> topologies) {
		mListener = topologyListener;
		mTopologies = topologies;
		mCheckIntervalInMillis = 100; // default
		mThread = new Thread(mCheckTopologyState, "Checker Thread");
	}
	
	public void registerCheckedTopology(Topology topology) {
		mTopologies.add(topology);
	}
	
	public void unregisterCheckedTopology(Topology topology) {
		mTopologies.remove(topology);
	}
	
	public void startCheck() {
		L.w("start check");
		mThread.start();
	}
	
	public void stopCheck() {
		L.w("stop check");
		mThread.interrupt();
	}
	
	private Runnable mCheckTopologyState = new Runnable() {
		@Override
		public void run() {
			L.w("Checker Thared Start");

			try {
				while(!Thread.currentThread().isInterrupted()){
					for(Topology topology : mTopologies) {
						switch(topology.getType()) {
						case Topology.TYPE_PROXIMITY:
							Region region = (Region) topology.getResult();				
							mListener.onProximityChanged(region);
							break;
						case Topology.TYPE_SECTOR:
							String sector = (String) topology.getResult();
							mListener.onSectorChanged(sector);						
							break;
						case Topology.TYPE_LOCATION:
							LocationTopology.Coordinate coord = (LocationTopology.Coordinate) topology.getResult();
							mListener.onLocationChanged(coord);
							break;
						}
					}
					Thread.sleep(mCheckIntervalInMillis);
				}
			} catch(RemoteException e1) {
				L.e("Topology Checker Error");
			} catch(InterruptedException e2) {
				L.w("Checker Thared Stop");
				return;
			}
			
		}
	};
}
