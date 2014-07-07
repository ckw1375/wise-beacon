package com.wisewells.sdk.datas.group;

import java.util.ArrayList;
import java.util.List;

import com.wisewells.sdk.TreeHelper;
import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.topology.Topology;
import com.wisewells.sdk.utils.L;

import android.os.Parcel;
import android.os.Parcelable;

public class BeaconGroup implements Parcelable{
		
	protected String code;
	protected BeaconGroup parent;	
	protected ArrayList<String> children;
	protected ArrayList<String> topologies;
	
	public static final Parcelable.Creator<BeaconGroup> CREATOR = new Creator<BeaconGroup>() {
		
		@Override
		public BeaconGroup[] newArray(int size) {
			return new BeaconGroup[size];
		}
		
		@Override
		public BeaconGroup createFromParcel(Parcel source) {
			return new BeaconGroup(source);
		}
	};
	
	public BeaconGroup() {
		init();
		L.i("BeaconGroup »ý¼º");
	}
	
	public BeaconGroup(String code, BeaconGroup parent, ArrayList<String> children, ArrayList<String> topologies) {
		this.code = code;
		this.parent = parent;
		this.children = children;
		this.topologies = topologies;
	}

	protected BeaconGroup(Parcel p) {
		init();
		
		code = p.readString();
		parent = p.readParcelable(BeaconGroup.class.getClassLoader());
		p.readStringList(children);
		p.readStringList(topologies);
	}
	
	private void init() {
		children = new ArrayList<String>();
		topologies = new ArrayList<String>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeParcelable(parent, 0);
		dest.writeStringList(children);
		dest.writeStringList(topologies);
	}
	
	public List<Beacon> getBeaconsInGroup() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		
		if(this instanceof MinorGroup) { 
			ArrayList<Beacon> temp = ((MinorGroup) this).getBeacons();
			for(Beacon b : temp)
				beacons.add(b);
		}
		
		TreeHelper manager = TreeHelper.getInstance();
		for(String child : children) {
			BeaconGroup bg = manager.getBeaconGroup(child);
			bg.getBeaconsInGroup();
		}
		
		return beacons;
	}

	/*
	 * Getter, Setter
	 */
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BeaconGroup getParent() {
		return parent;
	}

	public void setParent(BeaconGroup parent) {
		this.parent = parent;
	}
}
