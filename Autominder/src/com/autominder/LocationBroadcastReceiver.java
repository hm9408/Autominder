package com.autominder;

import com.interfaz.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationBroadcastReceiver implements LocationListener{

	private double contKms;
	private static LocationManager mLocationManager;
	private Location current=null;	
	private String provider;
	
	public LocationBroadcastReceiver(String provider){
		contKms=0;
		this.provider = provider;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		System.out.println("ON_LOCATION_CHANGED locationBroadcastReceiver");
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
		    System.out.println("nueva distancia encontrada:"+dist);
		    contKms+=dist; //se suma la distancia calculada al conteo general
		    MainActivity.actualizarKmCount(contKms);
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

	public void stahrt(Context context){
		System.out.println("ON_RECEIVE LocationBroadcastReceiver (llamado por am en MainActivity)");
		
		mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(provider,MainActivity.frecuenciaModoCarro, 0, this, null);
	}
	
	public void stahp(){
		mLocationManager.removeUpdates(this);
	}

}
