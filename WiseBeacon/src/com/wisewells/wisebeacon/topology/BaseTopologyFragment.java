package com.wisewells.wisebeacon.topology;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.wisewells.sdk.beacon.Beacon;

public abstract class BaseTopologyFragment extends Fragment {
	public abstract void replaceListViewData(List<Beacon> beacons);
	public abstract void saveTopology();
}
