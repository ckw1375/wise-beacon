package com.wisewells.sdk.datas.group;

import java.util.ArrayList;
import java.util.List;

import com.wisewells.sdk.WiseObjects;
import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.topology.Topology;
import com.wisewells.sdk.utils.L;

import android.os.Parcel;
import android.os.Parcelable;

public class BeaconGroup implements Parcelable{
		
	protected String code;
	protected String parent;	
	protected ArrayList<String> children;
	protected ArrayList<Topology> topologies;
	
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
	
	public BeaconGroup(String code, String parent, ArrayList<String> children, ArrayList<Topology> topologies) {
		this.code = code;
		this.parent = parent;
		this.children = children;
		this.topologies = topologies;
	}

	protected BeaconGroup(Parcel p) {
		init();
		
		code = p.readString();
		parent = p.readString();
		p.readStringList(children);
		p.readTypedList(topologies, Topology.CREATOR);
	}
	
	private void init() {
		children = new ArrayList<String>();
		topologies = new ArrayList<Topology>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(parent);
		dest.writeStringList(children);
		dest.writeTypedList(topologies);
	}
	
	public List<Beacon> getBeaconsInGroup() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		
		if(this instanceof MinorGroup) { 
			ArrayList<Beacon> temp = ((MinorGroup) this).getBeacons();
			for(Beacon b : temp)
				beacons.add(b);
		}
		
		WiseObjects manager = WiseObjects.getInstance();
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

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public void setTopologies(ArrayList<Topology> t) {
		topologies = t;
	}
}
