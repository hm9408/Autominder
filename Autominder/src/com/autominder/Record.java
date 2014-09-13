package com.autominder;

import java.io.Serializable;

public class Record implements Serializable{

	private static final long serialVersionUID = -8228566643725427154L;
	
	private double cost;
	private String nombreTaller;
	private int kmPassedSince;
	private String maintenanceName;
	
	public Record(double cost, String nombreTaller, int kmPassedSince,
			String maintenanceName) {
		this.cost = cost;
		this.nombreTaller = nombreTaller;
		this.kmPassedSince = kmPassedSince;
		this.maintenanceName = maintenanceName;
	}

	public double getCost() {
		return cost;
	}

	public String getNombreTaller() {
		return nombreTaller;
	}

	public int getKmPassedSince() {
		return kmPassedSince;
	}

	public void setKmPassedSince(int kmPassedSince) {
		this.kmPassedSince = kmPassedSince;
	}

	public String getMaintenanceName() {
		return maintenanceName;
	}
		
}
