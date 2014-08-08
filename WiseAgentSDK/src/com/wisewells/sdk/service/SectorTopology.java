package com.wisewells.sdk.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import com.wisewells.sdk.BeaconTracker;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.beacon.RssiVector;

public class SectorTopology extends Topology implements Parcelable {
	
	private ArrayList<Sector> mSectors;
//	private HashMap<String,SampleList> sectors;
	private static final int K = 5; //Parameter for KNN algorithm
	
	public static final Creator<SectorTopology> CREATOR = new Creator<SectorTopology>() {
		@Override
		public SectorTopology[] newArray(int size) {
			return new SectorTopology[size];
		}
		@Override
		public SectorTopology createFromParcel(Parcel source) {
			return new SectorTopology(source);
		}
	};
	
	public SectorTopology(BeaconVector beaconVector) {
		super(TYPE_SECTOR, beaconVector);
		mSectors = new ArrayList<Sector>();
//		sectors = new HashMap<String,SampleList>();
	}
	
	private SectorTopology(Parcel in) {
		super(in);
		in.readTypedList(mSectors, Sector.CREATOR);
	}
	
	@Override
	public int describeContents() {
		return super.describeContents();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeTypedList(mSectors);
	}
	
	@Override
	public String getTypeName() {
		return "Sector";
	}
	
	//Add a new sector with the sector name 'sectName'. 
	//Successful (return true) only when there is no sector with the same name.
	public boolean addSector(String name) {
		if(!containsSectorName(name)) {
			mSectors.add(new Sector(name));
			return true;
		}
		
		return false;
	}

	private boolean containsSectorName(String name) {
		for(Sector sector : mSectors) {
			if(sector.getName().equals(name))
				return false;
		}
		return true;
	}
	
	public void deleteAllSectors() {
//		sectors.clear();
		mSectors.clear();
	}

	private Sector getSectorWith(String name) {
		for(Sector sector : mSectors) {
			if(sector.equals(name))
				return sector;
		}
		return null;
	}
	
	//Add a new sample to the sector with a given name. 
	public boolean addSample(String name) {
		Sector sector = getSectorWith(name);
		if(sector == null) return false;
		
		sector.addSectorSample(mTracker.getAvgRssi(mBeaconVector));
		return true;
//		SampleList sl = sectors.get(name);
//		if(sl == null) return false;
//		RssiVector rv = mTracker.getAvgRssi(mBeaconVector);
//		sl.addSample(rv);
//		return true;
	}
	
	//Clear all samples of the sector with a given name. 
	public boolean clearSample(String name) {
		Sector sector = getSectorWith(name);
		if(sector == null) return false;
		
		sector.clearSectorSamples();
		return true;
		
//		SampleList sl = sectors.get(sectName);
//		if(sl == null) return false;
//		sl.clearSample();
//		return true;
	}
	//Get the number of samples of the sector with a given name. 
	//In case of an error, return -1. 
	public int getSampleNumber(String name) {
		Sector sector = getSectorWith(name);
		if(sector == null) return -1;
		
		return sector.getSampleNumber();
		
//		SampleList sl = sectors.get(sectName);
//		if(sl == null) return -1;
//		return sl.getSampleNumber();
	}
	
	//Return the sector name of the sector that the user is currently in (by using the KNN algorithm)
	//In case of an error, return null.
	@Override
	public String getResult() {
		if(mSectors.size() == 0) return null;		
//		if(sectors.size() == 0) return null;
		
		RssiVector currentVector = mTracker.getAvgRssi(mBeaconVector);		
		ArrayList<Pair<Double,String>> list = new ArrayList<Pair<Double,String>>();
		
		for(Sector sector : mSectors) {
			String sectorName = sector.getName();
			ArrayList<RssiVector> samples = sector.getSamples();
			for(int i=0; i<samples.size(); i++) {
				Pair<Double, String> p = new Pair<Double, String>(currentVector.sqDist(samples.get(i)), sectorName);
				list.add(p);
			}
		}
			
//		for(Map.Entry<String,SampleList> entry : sectors.entrySet()) {
//			String sn = entry.getKey();
//			SampleList sl = entry.getValue();
//			for(int ind = 0; ind < sl.getSampleNumber(); ind ++) {
//				Pair<Double,String> p = new Pair<Double,String>(currentVector.sqDist(sl.getSample(ind)),sn);
//				l.add(p);
//			}			
//		}
		
		Collections.sort(list, new Comparator<Pair<Double,String>>() { 
			public int compare(Pair<Double,String> lhs, Pair<Double,String> rhs) {
				return Double.compare(lhs.first, rhs.first);
			}
		});
		
		if(list.get(0).first.isInfinite()) {
			return null;
		}
		
		String selected = null;
		int maxCount = 0;
		for(Sector sector : mSectors) {
			String sectorName = sector.getName();
			int count = 0;
			for(int k = 0; k < Math.min(K, list.size()); k++) {
				if(list.get(k).first.isInfinite()) break;
				if(list.get(k).second == sectorName) count ++;
			}
			if(count >= maxCount) {
				selected = sectorName;
				maxCount = count;
			}
		}
				
		/*for(Map.Entry<String,SampleList> entry : sectors.entrySet()) {
			String sn = entry.getKey();
			int count = 0;
			for(int k = 0; k < Math.min(K, list.size()); k++) {
				if(list.get(k).first.isInfinite()) break;
				if(list.get(k).second == sn) count ++;
			}
			if(count >= maxCount) {
				selected = sn;
				maxCount = count;
			}
		}*/
		return selected;
	}
	
	/*private class SampleList {
		private ArrayList<RssiVector> sp;
		public SampleList() {
			sp = new ArrayList<RssiVector>();
		}
		public void addSample(RssiVector s) {
			sp.add(s);
		}
		public RssiVector getSample(int ind) {
			if(ind < 0 || ind >= sp.size()) return null;
			return sp.get(ind);
		}
		public void clearSample() {
			sp.clear();
		}
		public int getSampleNumber() {
			return sp.size();
		}
	}*/
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
	
	public int getSampleNumber() {
		return sectorSamples.size();
	}
	
	public ArrayList<RssiVector> getSamples() {
		return sectorSamples;
	}
}
