package com.wisewells.agent.connector;

import java.util.ArrayList;
import java.util.List;

import com.wisewells.sdk.aidl.TopologyStateChangeListener;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.L;

/**
 * @file	ApplicationConnector.java
 * @author 	Mingook
 * @date	2014. 8. 23.
 * @description
 */
public class ApplicationConnector {
	private final String mAppPackageName;
	private TopologyStateChecker mTopologyChecker;
	
	public ApplicationConnector(String packageName) {
		mAppPackageName = packageName;
	}
	
	public void startTopologyChecker(List<Topology> topologies, TopologyStateChangeListener listener) {
		mTopologyChecker = new TopologyStateChecker(listener, topologies);
		mTopologyChecker.startCheck();
	}
	
	public void startTopologyChecker(Topology topology, TopologyStateChangeListener listener) {
		ArrayList<Topology> topologies = new ArrayList<Topology>();
		topologies.add(topology);
		mTopologyChecker = new TopologyStateChecker(listener, topologies);
		mTopologyChecker.startCheck();
	}
	
	public void stopTopologyChecker() {
		if(mTopologyChecker == null) {
			L.w("TopologyStateChecker was not started. But no probmle.");
			return;
		}
		
		mTopologyChecker.stopCheck();
	}
}
