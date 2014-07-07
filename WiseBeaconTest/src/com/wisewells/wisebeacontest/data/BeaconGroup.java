package com.wisewells.wisebeacontest.data;

import java.util.ArrayList;

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
		
	}
	
	public BeaconGroup(Parcel p) {
		code = p.readString();
		parent = p.readParcelable(BeaconGroup.class.getClassLoader());
		
		children = new ArrayList<BeaconGroup>();
		p.readTypedList(children, BeaconGroup.CREATOR);
		
		topologies = new ArrayList<Topology>();
		p.readTypedList(topologies, Topology.CREATOR);
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
