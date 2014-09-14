package com.autominder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class Principal {

	public static final String file = "Autominder.dat";

	private static Principal instancia;
	
	private Context c;

	private ArrayList<Vehicle> vehiculos;
	private ArrayList<Maintenance> mantenimientos;

	public Principal(Context context) {
		
		c = context;
		boolean e;
		try {
			e = existenDatos();
			//cargarMantenimientosIniciales();
			//new Vehicle("Honda", 20, 100000, cargarMantenimientosIniciales(), records)
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

	private ArrayList<Maintenance> cargarMantenimientosIniciales() {
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
	private boolean existenDatos() throws StreamCorruptedException, IOException, ClassNotFoundException {

		FileInputStream fis = new FileInputStream(new File(file));
		ObjectInputStream ois = new ObjectInputStream(fis);
		vehiculos = (ArrayList<Vehicle>) ois.readObject();
		mantenimientos = (ArrayList<Maintenance>) ois.readObject();
		fis.close();
		ois.close();
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
			FileOutputStream fos = c.openFileOutput(file, Context.MODE_PRIVATE);
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
}
