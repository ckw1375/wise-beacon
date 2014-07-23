package com.wisewells.wisebeacon.topology;

import com.wisewells.sdk.datas.Topology;
import com.wisewells.wisebeacon.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TopologyListRow extends FrameLayout {

	private TextView mTopologyNameView;
	
	public TopologyListRow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.topology_list_row, this);
		mTopologyNameView = (TextView) findViewById(R.id.txt_topology_name);
	}
	
	public void setData(Topology topology) {
		mTopologyNameView.setText(topology.getName());
	}
}
