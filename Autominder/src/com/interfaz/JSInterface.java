package com.interfaz;

import android.webkit.JavascriptInterface;

public class JSInterface {

	AddVehicleActivity ava;
	
	public JSInterface(AddVehicleActivity addVehicleActivity) {
		ava = addVehicleActivity;
	}
	
	@JavascriptInterface
	public void recieveDistance(String distance){
		ava.setRouteDistance(Double.parseDouble(distance));
	}

}
