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
	protected String name;
	protected String parentCode;	
	protected ArrayList<String> childCodes;
	protected ArrayList<Topology> topologyCodes;
	
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

	public BeaconGroup(String code, String name, String parent, ArrayList<String> childCodes, 
			ArrayList<Topology> topologyCodes) {
		this.code = code;
		this.name = name;
		this.parentCode = parent;
		this.childCodes = childCodes;
		this.topologyCodes = topologyCodes;
	}

	protected BeaconGroup(Parcel p) {
		init();
		
		code = p.readString();
		name = p.readString();
		parentCode = p.readString();
		p.readStringList(childCodes);
		p.readTypedList(topologyCodes, Topology.CREATOR);
	}
	
	private void init() {
		childCodes = new ArrayList<String>();
		topologyCodes = new ArrayList<Topology>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(name);
		dest.writeString(parentCode);
		dest.writeStringList(childCodes);
		dest.writeTypedList(topologyCodes);
	}
	
	public List<Beacon> getBeaconsInGroup() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		
		if(this instanceof MinorGroup) { 
			ArrayList<Beacon> temp = ((MinorGroup) this).getBeacons();
			for(Beacon b : temp)
				beacons.add(b);
		}
		
		WiseObjects manager = WiseObjects.getInstance();
		for(String child : childCodes) {
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

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parent) {
		this.parentCode = parent;
	}
	
	public void setTopologyCodes(ArrayList<Topology> t) {
		topologyCodes = t;
	}
	
	public String getName() {
		return this.name;
	}
}
