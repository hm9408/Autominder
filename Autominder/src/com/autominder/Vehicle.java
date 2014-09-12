package com.autominder;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable{

	private static final long serialVersionUID = -7614135801653779184L;
	
	private String name;
	private boolean favorite;
	/**
	 * es un numero que indica el numero de kilometros semanales
	 */
	private int frequency;
	private ArrayList<Maintenance> maintenances;
	private ArrayList<Record> records;

	
	public Vehicle(String name, int frequency,
			ArrayList<Maintenance> maintenances, ArrayList<Record> records) {
		this.name = name;
		this.frequency = frequency;
		this.maintenances = maintenances;
		this.records = records;
	}
	
	public ArrayList<Maintenance> getMaintenances() {
		return maintenances;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isFavorite() {
		return favorite;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public ArrayList<Record> getRecords() {
		return records;
	}
	
	public void setFrequency(int frequency) {
		this.frequency=frequency;
		recalcularMantenimientos();
	}

	private void recalcularMantenimientos() {
		// TODO Auto-generated method stub
		
	}
	
}
