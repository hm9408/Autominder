package com.autominder;

import java.io.Serializable;
import java.util.Date;

public class Reminder implements Serializable{

	private static final long serialVersionUID = -2038456429396491478L;
	
	private String nombreManten;
	
	private Date fecha;
	
	private String nombreCarro;

	public Reminder(String nombreManten, Date fecha, String nombreCarro) {
		this.nombreManten = nombreManten;
		this.fecha = fecha;
		this.nombreCarro = nombreCarro;
	}

	public String getNombreManten() {
		return nombreManten;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getNombreCarro() {
		return nombreCarro;
	}
	
}
