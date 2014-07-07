package com.wisewells.wisebeacontest.data;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Content implements Parcelable{
	
	private String name;
	private String code;
	private Topology topology;
	private Content parent;
	private ArrayList<Content> children;
	
	public static final Parcelable.Creator<Content> CREATOR = new Creator<Content>() {
		
		@Override
		public Content[] newArray(int size) {
			return new Content[size];
		}
		
		@Override
		public Content createFromParcel(Parcel source) {
			return new Content(source);
		}
	};
	
	public Content() {
		
	}
	
	public Content(Parcel p) {
		name = p.readString();
		code = p.readString();
		topology = p.readParcelable(Topology.class.getClassLoader());
		parent = p.readParcelable(Content.class.getClassLoader());
		
		children = new ArrayList<Content>();
		p.readTypedList(children, Content.CREATOR);
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

	public Content getParent() {
		return parent;
	}

	public void setParent(Content parent) {
		this.parent = parent;
	}

	public ArrayList<Content> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Content> children) {
		this.children = children;
	}
	
	
}
