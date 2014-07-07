package com.wisewells.sdk.datas;

import java.util.ArrayList;

import com.wisewells.sdk.datas.topology.Topology;

import android.os.Parcel;
import android.os.Parcelable;

public class ServiceContent implements Parcelable{
	
	private String name;
	private String code;
	private Topology topology;
	private ServiceContent parent;
	private ArrayList<ServiceContent> children;
	
	public static final Parcelable.Creator<ServiceContent> CREATOR = new Creator<ServiceContent>() {
		
		@Override
		public ServiceContent[] newArray(int size) {
			return new ServiceContent[size];
		}
		
		@Override
		public ServiceContent createFromParcel(Parcel source) {
			return new ServiceContent(source);
		}
	};
	
	public ServiceContent() {
		
	}
	
	public ServiceContent(Parcel p) {
		name = p.readString();
		code = p.readString();
		topology = p.readParcelable(Topology.class.getClassLoader());
		parent = p.readParcelable(ServiceContent.class.getClassLoader());
		
		children = new ArrayList<ServiceContent>();
		p.readTypedList(children, ServiceContent.CREATOR);
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
		dest.writeTypedList(children);
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

	public ServiceContent getParent() {
		return parent;
	}

	public void setParent(ServiceContent parent) {
		this.parent = parent;
	}

	public ArrayList<ServiceContent> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<ServiceContent> children) {
		this.children = children;
	}
	
	
}
