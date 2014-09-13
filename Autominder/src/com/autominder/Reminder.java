package com.autominder;

import java.io.Serializable;
import java.util.Date;

public class Reminder implements Serializable{

	private static final long serialVersionUID = -2038456429396491478L;
	
	private String nombreManten;
	
	private Date fecha;

	public Reminder(String nombreManten, Date fecha) {
		this.nombreManten = nombreManten;
		this.fecha = fecha;
	}

	public String getNombreManten() {
		return nombreManten;
	}

	public Date getFecha() {
		return fecha;
	}
	
}
