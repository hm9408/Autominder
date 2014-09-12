package com.autominder;

import java.io.Serializable;

public class Maintenance implements Serializable{

	private static final long serialVersionUID = 2732812090880984675L;
	
	public static final int SEGUN_KM = 1;
	public static final int SEGUN_TIEMPO = 2;
	
	private int type;
	private String nombre;
	
	private int km;
	private int tiempo;
		
	public Maintenance(int type, String nombre, int km, int tiempo) {
		this.type = type;
		this.nombre = nombre;
		this.km = km;
		this.tiempo = tiempo;
	}
	
	public int getType() {
		return type;
	}
	public String getNombre() {
		return nombre;
	}
	public int getKm() {
		return km;
	}
	public int getTiempo() {
		return tiempo;
	}
	
}
