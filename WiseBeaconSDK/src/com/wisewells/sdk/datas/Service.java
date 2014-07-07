package com.wisewells.sdk.datas;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.topology.Topology;

public class Service implements Parcelable{
	
	private String name;
	private String code;
	private Topology topology;
	private Service parent;
	private ArrayList<String> children;
	
	public static final Parcelable.Creator<Service> CREATOR = new Creator<Service>() {
		
		@Override
		public Service[] newArray(int size) {
			return new Service[size];
		}
		
		@Override
		public Service createFromParcel(Parcel source) {
			return new Service(source);
		}
	};
	
	public Service() {
		
	}
	
	public Service(Parcel p) {
		init();
		name = p.readString();
		code = p.readString();
		topology = p.readParcelable(Topology.class.getClassLoader());
		parent = p.readParcelable(Service.class.getClassLoader());		
		p.readStringList(children);
	}
	
	private void init() {
		children = new ArrayList<String>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(code);
		dest.writeParcelable(topology, 0);
		dest.writeParcelable(parent, 0);
		dest.writeStringList(children);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Topology getTopology() {
		return topology;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}

	public Service getParent() {
		return parent;
	}

	public void setParent(Service parent) {
		this.parent = parent;
	}

	public ArrayList<String> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<String> children) {
		this.children = children;
	}
	
	
}
