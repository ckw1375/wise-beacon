package com.wisewells.sdk.service;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.wisewells.sdk.beacon.RssiVector;

public class Sector implements Parcelable {
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
		init();
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