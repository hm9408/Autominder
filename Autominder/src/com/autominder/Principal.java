package com.autominder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;

public class Principal {
	
	public static final String file = "Autominder.dat";
	
	private static Principal instancia;

	private ArrayList<Vehicle> vehiculos;
	private ArrayList<Maintenance> mantenimientos;
	
	public Principal(Context context) {
		
		boolean e;
		try {
			e = existenDatos();
			if(!e){
				if(vehiculos == null)vehiculos = new ArrayList<Vehicle>();
				if(mantenimientos == null)mantenimientos = new ArrayList<Maintenance>();
			}
		} catch (StreamCorruptedException e1) {
			System.out.println("StreamCorruptedException");
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.out.println("ClassNotFoundException");
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("IOException");
			e1.printStackTrace();
		}
		
	}

	public ArrayList<Vehicle> getVehiculos() {
		return vehiculos;
	}

	@SuppressWarnings("unchecked")
	private boolean existenDatos() throws StreamCorruptedException, IOException, ClassNotFoundException {

		FileInputStream fis = new FileInputStream(new File(file));
		ObjectInputStream ois = new ObjectInputStream(fis);
		vehiculos = (ArrayList<Vehicle>) ois.readObject();
		mantenimientos = (ArrayList<Maintenance>) ois.readObject();
		
		if (vehiculos == null && mantenimientos == null) {
			return false;
		}
		return true;		
	}

	public ArrayList<Maintenance> getMantenimientos() {
		return mantenimientos;
	}

	public static Principal darInstancia(Context context)
	{
		if(instancia == null)
		{
			instancia = new Principal(context);
		}
		return instancia;
	}
	
	public void agregarMantenimiento(String nombre, int tipo, int km, int tiempo){
		Maintenance m = new Maintenance(tipo, nombre, km, tiempo);
		mantenimientos.add(m);
	}
}
