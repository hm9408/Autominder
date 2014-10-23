package com.interfaz;

import android.webkit.JavascriptInterface;

public class JSInterface {

	JSInterfaceActivity ava;
	
	public JSInterface(JSInterfaceActivity jsia) {
		ava = jsia;
	}

	@JavascriptInterface
	public void recieveDistance(String distance){
		ava.setRouteDistance(Double.parseDouble(distance));
	}

}
