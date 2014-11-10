package com.autominder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class ConexionCliente {

	private Socket socket;

	private PrintWriter out;

	private BufferedReader in;

	public static final String IP = "157.253.222.246";
	public static final int PUERTO = 9999;
	private Context context;

	public ConexionCliente(Context context) {
		this.context = context;
	}

	private boolean conectar()
	{
		if(isOnline()){
			try {
				System.out.println("CREANDO SOCKET");
				socket = new Socket(IP, PUERTO);
				System.out.println("Is the Socket null? " + socket==null);
				out = new PrintWriter(socket.getOutputStream(), true);
				System.out.println("Is the OutputStream null? " + out==null);			
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				System.out.println("Is the InputStreamReader null? " + in==null);
				System.out.println("Conexión establecida con el servidor de manera exitosa");
				return true;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}else{
			return false;
		}
		
	
	}
	private void desconectar(){

		try {
			out.println("DES");
			out.close();
			out = null;
			in.close();
			in = null;

			socket.close();
			socket = null;
			System.out.println("Se cerró la conexión con el servidor.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return false si el login es incorrecto, o si no hay conexion; true de lo contrario
	 */
	public boolean verificarLogin(String username, String password) {
		
		try {
			if (username != null && password != null && conectar()) {
				String res = "AUT##" + username + "##" + password;
				System.out.println("Mensaje: " + res);
				System.out.println("PrintWriter es nulo?" + out == null);
				out.println(res);
				String comando = in.readLine();
				System.out.println(comando);
				desconectar();
				if (comando.contains("OK")) {
					return true;
				} else {
					return false;
				}
			}else{
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("fuera del trycatch");
		return false;
	}
	
	/**
	 * @return false si no hay conexion, true de lo contrario
	 */
	public boolean RegistrarLogin(String username, String password) {
		
		if(username != null && password != null && conectar()){
			String res ="REGISTRO##"+username+"##"+password;
			System.out.println("Mensaje: "+res);
			System.out.println("PrintWriter es nulo?" + out==null);
			out.println(res);
			
			desconectar();
			return true;
		}else{
			return false;
		}
		
		
	}

	/**
	 * Hace pull de los datos, y los agrega a la instancia, asi como caragr los mantenimientos iniciales.<br>
	 * Sólo funciona cuando hay conexion de red y el nombre de usuario y contraseña son diferentes de null
	 * @param username
	 * @param password
	 */
	@SuppressLint("SimpleDateFormat")
	public void datosPull(String username, String password) {
		try {
			
			if (username != null && password != null && conectar()) {
				String res = "DATOSPULL##" + username + "##" + password;
				System.out.println("Mensaje: " + res);
				System.out.println("PrintWriter es nulo?" + out == null);
				out.println(res);
				String comando = in.readLine();
				System.out.println(comando);
				while (!comando.equals("FIN")) {

					while (comando.startsWith("VEH")) {//recibe un vehiculo
						String[] veh = comando.split("##");

						ArrayList<Maintenance> maintenances = new ArrayList<Maintenance>();
						comando = in.readLine();
						System.out.println(comando);
						while (comando.startsWith("MAN")) {
							String[] man = comando.split("##");
							Maintenance mAct = new Maintenance(
									Integer.parseInt(man[1]), man[2],
									Integer.parseInt(man[3]),
									Long.parseLong(man[4]));
							maintenances.add(mAct);
							comando = in.readLine();
							System.out.println(comando);
						}

						ArrayList<Record> records = new ArrayList<Record>();
						while (comando.startsWith("REC")) {
							String[] rec = comando.split("##");
							Record rAct = new Record(
									Double.parseDouble(rec[1]), rec[2],
									Double.parseDouble(rec[3]), rec[4],
									new SimpleDateFormat("dd-MM-yyyy")
											.parse(rec[5]));
							records.add(rAct);
							comando = in.readLine();
							System.out.println(comando);
						}

						Vehicle vAct = new Vehicle(veh[1],
								Double.parseDouble(veh[2]),
								Integer.parseInt(veh[3]), maintenances, records);
						System.out.println("se hizo pull del vehiculo '"
								+ vAct.getName() + "'");
						boolean a = Principal.darInstancia(context).addVehicle(
								vAct);
						System.out.println(a ? "se guardo el vehiculo"
								: "no se guardo el vehiculo; nombre repetido");

					}

				}
				desconectar();
				//carga los mantenimientos del XML tambien
				Principal.darInstancia(context).cargarMantenimientosIniciales();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Hace push de los datos al servidor.<br>
	 * solo funciona cuando hay conexion de red y el username y password son diferentes de null.
	 * @param username
	 * @param password
	 */
	@SuppressLint("SimpleDateFormat")
	public void datosPush(String username, String password){

		if (username != null && password != null && conectar()) {
			String res = "DATOSPUSH##" + username + "##" + password;
			System.out.println("Mensaje: " + res);
			System.out.println("PrintWriter es nulo?" + out == null);
			out.println(res);
			Principal p = Principal.darInstancia(context);
			String respuesta = null;
			for (int i = 0; i < p.getVehiculos().size(); i++) {
				Vehicle vAct = p.getVehiculos().get(i);
				respuesta = "VEH##" + vAct.getName() + "##"
						+ vAct.getWeeklyKM() + "##" + vAct.getCurrentKmCount();
				System.out.println(respuesta);
				out.println(respuesta);
				for (int j = 0; j < vAct.getMaintenances().size(); j++) {
					Maintenance mAct = vAct.getMaintenances().get(j);
					respuesta = "MAN##" + mAct.getType() + "##"
							+ mAct.getNombre() + "##" + mAct.getKm() + "##"
							+ mAct.getTiempo();
					System.out.println(respuesta);
					out.println(respuesta);
				}
				for (int j = 0; j < vAct.getRecords().size(); j++) {
					Record rAct = vAct.getRecords().get(j);
					respuesta = "REC##"
							+ rAct.getCost()
							+ "##"
							+ rAct.getNombreTaller()
							+ "##"
							+ rAct.getKmPassedSince()
							+ "##"
							+ rAct.getMaintenanceName()
							+ "##"
							+ new SimpleDateFormat("dd-MM-yyyy").format(rAct
									.getFecha());
					System.out.println(respuesta);
					out.println(respuesta);
				}
			}
			respuesta = "FIN";
			System.out.println(respuesta);
			out.println(respuesta);
			desconectar();
		}
		
	}
	
	@SuppressWarnings("static-access")
	public boolean isOnline() {
	    ConnectivityManager cm =(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}
//	protected void reportar(String... a) {
//		String res="";
//		//		try {
//		String lati1 = a[0];
//		String long1 = a[1];
//		String lati2 = a[2];
//		String long2 = a[3];
//		String tipo = a[4];
//		String rating = a[5];
//		String user = a[6];
//		//MENSAJE PARA EL SERVER
//
//		res="REP##"+lati1+"##"+long1+"##"+lati2+"##"+long2+"##"+tipo+"##"+rating+"##"+user+"##";
//		//			socket = new Socket(IP, PUERTO);
//		//			System.out.println("Is the Socket null? " + socket==null);
//		//			out = new PrintWriter(socket.getOutputStream(), true);
//		//			System.out.println("Is the OutputStream null? " + out==null);			
//		//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		//			System.out.println("Is the InputStreamReader null? " + in==null);
//		//		} catch (Exception e) {
//		//			res="EXCEPTION##"+e.getMessage();
//		//		}
//		System.out.println("Mensaje del reporte: "+res);
//		System.out.println("PrintWriter es nulo?" + out==null);
//		out.println(res);
//	}
//
	
	
	
//	Thread t = new Thread(new Runnable() {
//
//		@Override
//		public void run() {
//			try {
//				socket = new Socket(IP, PUERTO);
//				System.out.println("Is the Socket null? " + socket==null);
//				out = new PrintWriter(socket.getOutputStream(), true);
//				System.out.println("Is the OutputStream null? " + out==null);			
//				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				System.out.println("Is the InputStreamReader null? " + in==null);
//				System.out.println("Conexión establecida con el servidor de manera exitosa");
//			} catch (Exception e) {
//				System.out.println(e.getMessage()+ "ERROR");
//				e.printStackTrace();
//			}				
//		}
//	});
//	t.start();


///**
// * Método que modela la recepción y envío de los mensajes de comunicación con el cliente asociado a esta conexión
// * @throws Exception 
// */
//private void recibirMensajes() throws Exception{
//	String respuesta = null;
//	while( !conexionTerminada ){
//		String comando = in.readLine();
//		System.out.println(comando);
//		if(comando.startsWith("INF##")){
//			//Al entrar acá significa que esta en formato AUT##Username##Password
//			String[] datos = comando.split("##");
//			String lati1 = datos[1];
//			String long1 = datos[2];
//			String lati2 = datos[3];
//			String long2 = datos[4];
//			double lat1 = Double.parseDouble(lati1);
//			double lng1 = Double.parseDouble(long1);
//			double lat2 = Double.parseDouble(lati2);
//			double lng2 = Double.parseDouble(long2);
//			
//			String color = datos[5];
//			int col=0;
//			if (color.equals("YELLOW")) {
//				col = Color.YELLOW;
//			}
//			else if (color.equals("RED")) {
//				col = Color.RED;
//			}
//			else if(color.equals("WHITE"))
//			{
//				col = Color.WHITE;
//			}
//			System.out.println("Polyline: "+lat1+", "+lng1+", "+lat2+", "+lng2+", "+col);
//			main.createPolylineFromLatLngs(lat1, lng1, lat2, lng2, col,main.darMapa());
//		}
//	}
//}

//	public void enviarUbicacion(LatLng latLng1calleMarcada,
//			LatLng latLng2calleMarcada) {
//		double lati1 = latLng1calleMarcada.latitude;
//		double long1 = latLng1calleMarcada.longitude;
//		double lati2 = latLng2calleMarcada.latitude;
//		double long2 = latLng2calleMarcada.longitude;
//		String res="POS##"+lati1+"##"+long1+"##"+lati2+"##"+long2;
//		System.out.println("Entró a enviar su ubicación: " + res);
//		out.println(res);
//	}

