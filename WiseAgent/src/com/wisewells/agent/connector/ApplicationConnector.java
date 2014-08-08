package com.wisewells.agent.connector;

import java.util.List;

import android.os.Handler;

import com.wisewells.sdk.aidl.TopologyStateChangeListener;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.L;

public class ApplicationConnector {
	private final String mAppPackageName;
	private final Handler mAgentHandler;
	private TopologyStateChecker mTopologyChecker;
	
	public ApplicationConnector(String packageName, Handler handler) {
		mAppPackageName = packageName;
		mAgentHandler = handler;		
	}
	
	public void startTopologyChecker(TopologyStateChangeListener listener, List<Topology> topologies) {
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
