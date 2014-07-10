package com.wisewells.sdk.datas.topology;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.datas.RssiVector;

public class SectorTopology extends Topology implements Parcelable {
	private ArrayList<Sector> sectors;
	
	public static Parcelable.Creator<SectorTopology> CREATOR = new Creator<SectorTopology>() {

		@Override
		public SectorTopology[] newArray(int size) {
			return new SectorTopology[size];
		}

		@Override
		public SectorTopology createFromParcel(Parcel source) {
			return new SectorTopology(source);
		}
	};
	
	public SectorTopology(String code, String name) {
		super(code, name);
		init();
	}
	
	
	public SectorTopology(Parcel p) {
		super(p);
		init();
		p.readTypedList(sectors, Sector.CREATOR);
	}
	
	private void init() {
		sectors = new ArrayList<Sector>();
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeTypedList(sectors);
	};
	
	public Sector getCurrnetSector(RssiVector vector) {
		/*
		 * 固备泅
		 */
		return null;
	}
	
	public void addSector(String name) {
		sectors.add(new Sector(name));
	}
	
	public void addSectorSamples(String name, List<RssiVector> vectors) {
		Sector sector = findSector(name);
		
		if(sector == null) {
			// 抗寇贸府
			return;
		}
		
		sector.addSectorSamples(vectors);
	}
	
	public void addSectorSample(String name, RssiVector vector) {
		Sector sector = findSector(name);

		if(sector == null) {
			// 抗寇贸府
			return;
		}

		sector.addSectorSample(vector);
	}
	
	public void clearSectorSamples(String name) {
		Sector sector = findSector(name);

		if(sector == null) {
			// 抗寇贸府
			return;
		}
		
		sector.clearSectorSamples();
	}
	
	public void removeSecotr(String name) {
		Sector sector = findSector(name);

		if(sector == null) {
			// 抗寇贸府
			return;
		}
		sectors.remove(sector);
	}

	private Sector findSector(String name) { 
		Sector sector = null;
	
		for(Sector s : sectors) {
			if(!s.getName().equals(name))
				continue;
			
			sector = s;
			break;
		}
		
		return sector;
	}	
}

class Sector implements Parcelable {
	private String name;
	private ArrayList<RssiVector> sectorSamples;
	
	public static Parcelable.Creator<Sector> CREATOR = new Creator<Sector>() {
		
		@Override
		public Sector[] newArray(int size) {
			return new Sector[size];
		}
		
		@Override
		public Sector createFromParcel(Parcel source) {
			return new Sector(source);
		}
	};
	
	public Sector(String name) {
		init();
		this.name = name;
	}
	
	private Sector(Parcel p) {
		name = p.readString();
		p.readTypedList(sectorSamples, RssiVector.CREATOR);
	}

	private void init() {
		sectorSamples = new ArrayList<RssiVector>();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeTypedList(sectorSamples);
	}

	public void addSectorSample(RssiVector vector) {
		sectorSamples.add(vector);
	}

	public void addSectorSamples(List<RssiVector> vectors) {		
		sectorSamples.addAll(vectors);
	}

	public void clearSectorSamples() {
		sectorSamples.clear();
	}

	public String getName() {
		return this.name;
	}
}
