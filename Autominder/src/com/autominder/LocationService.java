package com.autominder;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class LocationService extends Service implements LocationListener{

	private final IBinder mBinder = new LocationServiceBinder();

	private Principal instancia;
	private double contKms=0;
	protected LocationManager mLocationManager;
	private Location current;	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		System.out.println("HOLA PUTAS DESDE EL SERVICE");
		Toast.makeText(getApplicationContext(), "STARTING LOCATION SERVICE", Toast.LENGTH_SHORT).show();
		
		instancia = Principal.darInstancia(getApplicationContext());
		ScheduledThreadPoolExecutor stps = new ScheduledThreadPoolExecutor(1);
		Runnable updateTask = new Runnable() {
			
			@Override
			public void run() {

				mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L,
			            0f, LocationService.this); //llama el método onLocationChanged
			}
		};
		stps.schedule(updateTask, 3L, TimeUnit.MINUTES); //cada 3 minutos se debería ejecutar la acción
		return super.onStartCommand(intent, flags, startId);

	}

	public class LocationServiceBinder extends Binder {
		
		public LocationServiceBinder(){}
		
		public LocationService getLocationService() {
			return LocationService.this;
		}
		
	}


	@Override
	public void onLocationChanged(Location location) {
		if(current==null)
		{
			current=location;
		}			
		else
		{
			//no es el primer muestreo
			double earthRadius = 6371; //kilometers
			double dLat = Math.toRadians(location.getLatitude()-current.getLatitude());
		    double dLng = Math.toRadians(location.getLongitude()-current.getLongitude());
		    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		               Math.cos(Math.toRadians(current.getLatitude())) * Math.cos(Math.toRadians(location.getLatitude())) *
		               Math.sin(dLng/2) * Math.sin(dLng/2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		    float dist = (float) (earthRadius * c);
		    contKms+=dist; //se suma la distancia calculada al conteo general
			current=location; //se cambia la posición vieja por la nueva para repetir el proceso
		}
		
	}

	public double getContKms() {
		return contKms;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
}
