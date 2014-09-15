package com.autominder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;

public class Principal implements Serializable{

	private static final long serialVersionUID = -6075841863258107180L;

	public static final String file = "Autominder.dat";

	private static Principal instancia;

	private Context c;

	private ArrayList<Vehicle> vehiculos;
	private ArrayList<Maintenance> mantenimientos;
	
	private Vehicle selected;

	public Principal(Context context) {

		c = context;
		loadState();
		if (vehiculos==null) {
			vehiculos = new ArrayList<Vehicle>();
		}
		if (mantenimientos==null) {
			mantenimientos = cargarMantenimientosIniciales();
			
		}
		
	}

	public ArrayList<Maintenance> cargarMantenimientosIniciales() {
		try {
			ArrayList<Maintenance> a = new ArrayList<Maintenance>();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(getClass().getResourceAsStream("/maintenance_types.xml"));
			NodeList nodeList = document.getDocumentElement().getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					// Get the value of all sub-elements.
					int type = Integer.parseInt(elem.getElementsByTagName("type")
							.item(0).getChildNodes().item(0).getNodeValue());
					String nombre = elem.getElementsByTagName("name").item(0)
							.getChildNodes().item(0).getNodeValue();
					int km = Integer.parseInt(elem.getElementsByTagName("km")
							.item(0).getChildNodes().item(0).getNodeValue());
					long tiempo = Long.parseLong(elem.getElementsByTagName("time")
							.item(0).getChildNodes().item(0).getNodeValue());
					Maintenance m = new Maintenance(type, nombre, km, tiempo);
					a.add(m);
				}
			}
			return a;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Vehicle> getVehiculos() {
		return vehiculos;
	}

	@SuppressWarnings("unchecked")
	public void loadState(){
		try {
			FileInputStream fis = c.openFileInput(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			vehiculos = (ArrayList<Vehicle>) ois.readObject();
			System.out.println("cargados "+vehiculos.size()+" vehiculos");
			mantenimientos = (ArrayList<Maintenance>) ois.readObject();
			System.out.println("cargados "+mantenimientos.size()+" mantenimientos");
			selected = vehiculos.get(0);
			System.out.println("cargado selected: "+selected.getName());
			ois.close();
			fis.close();
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

	public void agregarMantenimiento(String nombre, int tipo, int km, long tiempo){
		Maintenance m = new Maintenance(tipo, nombre, km, tiempo);
		mantenimientos.add(m);
		saveState();
	}


	public ArrayList<Reminder> obtenerReminders() {
		ArrayList<Reminder> allReminders = new ArrayList<Reminder>();
		for (int i = 0; i < vehiculos.size(); i++) {
			Vehicle act = vehiculos.get(i);
			allReminders.addAll(act.getReminders());
		}
		return allReminders;
	}

	public void saveState(){
		try {
			FileOutputStream fos = c.openFileOutput(file, c.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(vehiculos);
			oos.writeObject(mantenimientos);
			oos.close();
			fos.close();	
			System.out.println("Saved "+vehiculos.size()+" vehicles.");	
			System.out.println("Saved "+mantenimientos.size()+" maintenances.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @param v
	 * @return true si se logró agregar el vehiculo, false si ya existe un vehiculo con ese nombre
	 */
	public boolean addVehicle(Vehicle v) {
		boolean existe = false;
		for (int i = 0; i < vehiculos.size(); i++) {
			Vehicle act = vehiculos.get(i);
			if(act.getName().equals(v.getName())) return false;
		}
		vehiculos.add(v);
		saveState();
		return true;
		
	}

	public Vehicle getSelected() {
		return selected;
	}

	public void setSelected(Vehicle selected) {
		this.selected = selected;
	}
	
	public boolean addMaintenanceSelected(Maintenance m, Record r){
		boolean a = selected.addNewMaintenance(m, r);
		saveState();
		return a;
	}
}
