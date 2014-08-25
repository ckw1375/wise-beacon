package com.wisewells.sdk;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.estimote.sdk.internal.Preconditions;
import com.wisewells.sdk.aidl.IWiseAgent;
import com.wisewells.sdk.aidl.RPCListener;
import com.wisewells.sdk.aidl.TopologyStateChangeListener;
import com.wisewells.sdk.beacon.Beacon;
import com.wisewells.sdk.beacon.BeaconGroup;
import com.wisewells.sdk.beacon.DistanceVector;
import com.wisewells.sdk.beacon.Region;
import com.wisewells.sdk.beacon.RssiVector;
import com.wisewells.sdk.service.LocationTopology;
import com.wisewells.sdk.service.LocationTopology.Coordinate;
import com.wisewells.sdk.service.Sector;
import com.wisewells.sdk.service.Service;
import com.wisewells.sdk.service.Topology;
import com.wisewells.sdk.utils.IpcUtils;
import com.wisewells.sdk.utils.L;

public class WiseManager {
	
	private static final String ACTION_NAME_WISE_AGENT = "com.wisewells.agent.WiseAgent";
	private static final String EXCEPTION_MSG = "AIDL ERROR : ";
	private final Context mContext;
	private final InternalServiceConnection mServiceConnection;
	
	/**
	 *	Agent와 RPC를 통해 함수가 호출 되면, Bind Thread가 실행 된다.
	 *	이 때 UI를 업데이트하는 행동을 CalledFromWrongThreadException 이 발생한다.
	 *	그러므로 Callback으로 결과를 알려줄 때에는 메인 Handler를 통해 알려주자.
	 *	(당연히 Callback이 아닌 그냥 Return을 해줄때에는 함수를 부르는 쪽의 쓰레드에서 동작하므로 신경 쓸 필요 없다.)
	 */
	private final Handler mHandler;
	
	private IWiseAgent mAgent;
	private ServiceReadyCallback mReadyCallback;
	private EditBeaconGroupListener mEditGroupListener;
	private EditBeaconListener mEditBeaconListener;
	private EditServiceListener mEditServiceListener;
	private EditTopologyListener mEditTopologyListener;
	private TopologyStateListener mTopologyStateListener;
	
	private static WiseManager sInstance;
	
	public static WiseManager getInstance(Context context) {
		if(sInstance == null) sInstance = new WiseManager(context);
		return sInstance;
	}

	private WiseManager(Context context) {
		mContext = ((Context)Preconditions.checkNotNull(context));
		mServiceConnection = new InternalServiceConnection();
		mHandler = new Handler(mContext.getMainLooper());
	}

	public void connect(ServiceReadyCallback callback) {
		mReadyCallback = ((ServiceReadyCallback)Preconditions.checkNotNull(callback, "callback cannot be null"));
		if (isConnectedToService()) {
			callback.onServiceReady();
		}

		Intent intent = new Intent(ACTION_NAME_WISE_AGENT);
		intent.putExtra("package name", mContext.getPackageName());
		boolean bound = mContext.bindService(intent, mServiceConnection, 1);

		if (!bound)
			L.w("Fail to bind service");
	}


	public void disconnect() {
		if (!isConnectedToService()) {
			L.i("Not disconnecting because was not connected to service");
			return;
		}

		mContext.unbindService(mServiceConnection);
	}

	private boolean isConnectedToService() {
		return mAgent != null;
	}
	
	@SuppressWarnings("unused")
	private int _______________Beacon_______________;

	public List<Beacon> getBeaconsInGroup(String groupCode) {
		try {
			return mAgent.getBeaconsInGroup(groupCode);
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "getBeaconsInGroup");
			return null;
		}
	}

	@SuppressWarnings("unused")
	private int _______________BeaconGroup_______________;
	
	public void addBeaconGroup(int depth, String name, String parentCode, EditBeaconGroupListener listener) {
		mEditGroupListener = Preconditions.checkNotNull(listener, "Listener must be not null");
		try {
			mAgent.addBeaconGroup(depth, name, parentCode, new RPCListener.Stub() {
				@Override
				public void onSuccess(final Bundle data) throws RemoteException {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mEditGroupListener.onSuccess(IpcUtils.getParcelableFromBundle(BeaconGroup.class, data));		
						}
					});
				}
				@Override
				public void onFail(String err) throws RemoteException {
				}
			});
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "addBeaconGroup");
		}
	}

	public void addBeaconToBeaconGroup(String groupCode, Beacon beacon, EditBeaconListener listener) throws RemoteException {
		mEditBeaconListener = listener;
		try {
			mAgent.addBeaconToBeaconGroup(groupCode, beacon, new RPCListener.Stub() {
				@Override
				public void onSuccess(Bundle data) throws RemoteException {
					mEditBeaconListener.onSuccess(IpcUtils.getParcelableFromBundle(Beacon.class, data));
				}
				
				@Override
				public void onFail(String err) throws RemoteException {
				}
			});
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "addBeaconToBeaconGroup");
		}
		
	}
	
	public List<BeaconGroup> getBeaconGroups(String parentCode) {
		try {
			return mAgent.getBeaconGroups(parentCode);
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "getBeaconGroups");
			return null;
		}
	}

	public List<BeaconGroup> getBeaconGroupsInAuthority() {
		try {
			return mAgent.getBeaconGroupsInAuthority();
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "getBeaconGroupsInAuthority");
			return null;
		}
	}

	public BeaconGroup getBeaconGroup(String code) {
		try {
			return mAgent.getBeaconGroup(code);
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "getBeaconGroup");
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private int _______________Topology_______________;
	
	public void modifyTopology(Topology topology) {

	}

	public void deleteTopology(String code) {

	}

	public Topology getTopology(String code) {
		try {
			Bundle bundle = mAgent.getTopology(code);
			bundle.setClassLoader(Topology.class.getClassLoader());
			return bundle.getParcelable(IpcUtils.BUNDLE_KEY);
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "getTopology");
			return null;
		}
	}

	public void addLocationTopology(String serviceCode, String groupCode)
			throws RemoteException {

	}

	public void addProximityTopology(String serviceCode, String groupCode, 
			List<String> beaconCodes, double[] ranges, RPCListener listener) {
		try {
		mAgent.addProximityTopology(serviceCode, groupCode, beaconCodes, ranges, listener);
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "addProximityTopology");
		}
	}

	public void addSectorTopology(String serviceCode, String groupCode, 
			List<String> beaconCodes, List<Sector> sectors) {
		
		try {
			mAgent.addSectorTopology(serviceCode, groupCode, beaconCodes, sectors);
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "addSectorTopology");
		}
	}
	
	public void addSectorSample(String topologyCode, String sectorName) {
		try {
			mAgent.addSectorSample(topologyCode, sectorName);
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "addSectorSample");
		}
	}
	
	public void addSector(String topologyCode, String sectorName) {
		try {
			mAgent.addSector(topologyCode, sectorName);
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "addSector");
		}
	}

	@SuppressWarnings("unused")
	private int _______________Service_______________;
	
	public void addService(int depth, String name, String parentCode, EditServiceListener listener) {
		mEditServiceListener = Preconditions.checkNotNull(listener, "Listener must be not null");

		try {
			mAgent.addService(depth, name, parentCode, new RPCListener.Stub() {
				@Override
				public void onSuccess(Bundle data) throws RemoteException {
					final Service s = IpcUtils.getParcelableFromBundle(Service.class, data);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mEditServiceListener.onSuccess(s);
						}
					});
				}

				@Override
				public void onFail(String err) throws RemoteException {
				}
			});
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "addService");
		}
	}

	public void modifyService(Service service) {

	}

	public void deleteService(String code) {

	}

	public List<Service> getRootServices() {
		try {
			return mAgent.getRootServices();
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "getRootServices");
			return null;
		}
	}

	public List<Service> getChildServices(String parentCode) {
		try {
			return mAgent.getChildServices(parentCode);
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "getChildServices");
			return null;
		}
	}

	@SuppressWarnings("unused")
	private int _______________Use_Agent_Function_______________;
	
	public void startTrackingTopologyState(String packageName, String serviceCode, 
			TopologyStateListener listener) { 
		mTopologyStateListener = Preconditions.checkNotNull(listener, "Listener must be not null.");
		try {
			mAgent.startTrackingTopologyState(packageName, serviceCode, new TopologyStateChangeListener.Stub() {
				@Override
				public void onSectorChanged(final String sectorName) throws RemoteException {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mTopologyStateListener.onSectorChanged(sectorName);
						}
					});
				}
				@Override
				public void onProximityChanged(final Region region) throws RemoteException {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mTopologyStateListener.onProximityChanged(region);
						}
					});
				}
				@Override
				public void onLocationChanged(final Coordinate coordinate) throws RemoteException {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mTopologyStateListener.onLocationChanged(coordinate);
						}
					});
				}
			});
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "tracking topology state");
		}
	}

	public void stopTrackingTopologyState(String packageName) {
		try {
			mAgent.stopTrackingTopologyState(packageName);
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "stop tracking topology state");
		}
	}

	public void startReceiving() {
		try {
			mAgent.startReceiving();
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "startReceiving");
			e.printStackTrace();
		}
	}

	public void stopReceiving() {
		try {
			mAgent.stopReceiving();
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "stopReceiving");
			e.printStackTrace();
		}
	}

	public List<Beacon> getAllNearbyBeacons() {
		try {
			return mAgent.getAllNearbyBeacons();
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "getAllNearByBeacons");
			return null;
		}
	}

	public DistanceVector getBeaconDistance(List<String> codes) {
		try {
			DistanceVector dv = mAgent.getBeaconDistance(codes);
			return dv;
		} catch (RemoteException e) {
			L.e(EXCEPTION_MSG + "getBeaconDistance");
			return null;
		}
	}
	
	public RssiVector getAverageRssiVector(List<String> beaconCodes) {
		try {
			return mAgent.getAverageRssiVector(beaconCodes);
		} catch(RemoteException e) {
			L.e(EXCEPTION_MSG + "getAverageRssiVector");
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private int _______________Interface_______________;
	
	private class InternalServiceConnection implements ServiceConnection {
		private InternalServiceConnection() {
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			mAgent = IWiseAgent.Stub.asInterface(service);
			WiseManager.this.mReadyCallback.onServiceReady();
		}

		public void onServiceDisconnected(ComponentName name) {
			L.e("Service disconnected, crashed? " + name);
			WiseManager.this.mAgent = null;
		}
	}
	
	public interface ServiceReadyCallback {
		public void onServiceReady();
	}
	
	public interface EditServiceListener {
		public void onSuccess(Service service); 
		public void onFail();
	}
	
	public interface EditBeaconGroupListener {
		public void onSuccess(BeaconGroup beaconGroup); 
		public void onFail();
	}
	
	public interface EditBeaconListener {
		public void onSuccess(Beacon beacon); 
		public void onFail();
	}

	public interface EditTopologyListener {
		public void onSuccess(Topology topology); 
		public void onFail();
	}
	
	public interface TopologyStateListener {
		void onSectorChanged(String sectorName);
		void onProximityChanged(Region region);
		void onLocationChanged(LocationTopology.Coordinate coordinate);
	}
}
