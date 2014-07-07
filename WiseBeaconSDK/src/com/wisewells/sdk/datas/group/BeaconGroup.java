package com.wisewells.sdk.datas.group;

import java.util.ArrayList;
import java.util.List;

import com.wisewells.sdk.datas.Beacon;
import com.wisewells.sdk.datas.topology.Topology;
import com.wisewells.sdk.utils.L;

import android.os.Parcel;
import android.os.Parcelable;

public class BeaconGroup implements Parcelable{
	
	protected String code;
	protected BeaconGroup parent;	
	protected ArrayList<BeaconGroup> children;
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
	
	protected BeaconGroup(Parcel p) {
		init();
		
		code = p.readString();
		parent = p.readParcelable(BeaconGroup.class.getClassLoader());
		p.readTypedList(children, BeaconGroup.CREATOR);
		p.readTypedList(topologies, Topology.CREATOR);
	}
	
	private void init() {
		children = new ArrayList<BeaconGroup>();
		topologies = new ArrayList<Topology>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeParcelable(parent, 0);
		dest.writeTypedList(children);
		dest.writeTypedList(topologies);
	}
	
	public List<Beacon> getBeaconsInGroup() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		
		if(this instanceof MinorGroup) { 
			ArrayList<Beacon> temp = ((MinorGroup) this).getBeacons();
			for(Beacon b : temp)
				beacons.add(b);
		}
		
		for(BeaconGroup bg : children) 
			return bg.getBeaconsInGroup();
		
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

	public ArrayList<BeaconGroup> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<BeaconGroup> children) {
		this.children = children;
	}

	public ArrayList<Topology> getTopologies() {
		return topologies;
	}

	public void setTopologies(ArrayList<Topology> topologies) {
		this.topologies = topologies;
	}
}
