package com.wisewells.sdk.datas.topology;

import java.util.ArrayList;
import java.util.List;

import com.wisewells.sdk.datas.RssiVector;

public class SectorTopology extends Topology {
	private ArrayList<Sector> sectors;
	
	public SectorTopology() {
		init();
	}
	
	private void init() {
		sectors = new ArrayList<Sector>();
	}
	
	public Sector getCuSector(RssiVector vector) {
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
