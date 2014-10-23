package com.interfaz;

import java.util.ArrayList;
import java.util.Date;

import com.autominder.Maintenance;
import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Record;
import com.autominder.Vehicle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AddVehicleActivity extends Activity implements OnEditorActionListener, JSInterfaceActivity {

	private EditText name;
	private EditText weeklyKM;
	private WebView webView; 
	private EditText veces;
	private EditText currentKmCount;

	private CheckBox cb1;
	private CheckBox cb2;
	private CheckBox cb3;

	private TextView maintenance1;
	private TextView maintenance2;
	private TextView maintenance3;

	private EditText km1;
	private EditText km2;
	private EditText km3;

	private Principal instancia;
	
	ArrayList<Maintenance> a;
	
	private double d;

	@SuppressLint("SetJavaScriptEnabled") @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_vehicle_activity);
		
		setResult(RESULT_CANCELED);
		
		getActionBar().setTitle("Agregar vehículo");

		instancia = Principal.darInstancia(getApplicationContext());
		d=-1;
		
		name = (EditText)findViewById(R.id.nombre_vehiculo);
		currentKmCount = (EditText)findViewById(R.id.km_actual_vehiculo);
		
		webView = (WebView)findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JSInterface(this), "Android");
		webView.loadUrl("file:///android_asset/mapBogota.html");
		webView.setOnTouchListener(new View.OnTouchListener() {

		    @Override
		    public boolean onTouch(View v, MotionEvent event) {                     

		        if(event.getAction()==MotionEvent.ACTION_UP){
		            webView.getParent().requestDisallowInterceptTouchEvent(false);

		        }else                                   
		            webView.getParent().requestDisallowInterceptTouchEvent(true);

		        return false;
		    }

		});
		veces = (EditText)findViewById(R.id.veces_por_semana);
		veces.setText(""+1);
		veces.setOnEditorActionListener(this);
		
		weeklyKM =(EditText)findViewById(R.id.km_semanales_vehiculo);
		
		cb1 = (CheckBox)findViewById(R.id.checkBox1);
		cb2 = (CheckBox)findViewById(R.id.checkBox2);
		cb3 = (CheckBox)findViewById(R.id.checkBox3);

		a = instancia.cargarMantenimientosIniciales();
		System.out.println("Tamaño inicial de a:"+a.size());
		while(a.size()>=4) {
			a.remove(3);
			System.out.println("se remueve uno" );
		}
		System.out.println("Tamaño final de a: "+a.size());
		
		maintenance1 = (TextView)findViewById(R.id.default_maintenance1);
		maintenance1.setText(a.get(0).getNombre());
		maintenance2 = (TextView)findViewById(R.id.default_maintenance2);
		maintenance2.setText(a.get(1).getNombre());
		maintenance3 = (TextView)findViewById(R.id.default_maintenance3);
		maintenance3.setText(a.get(2).getNombre());

		km1 = (EditText)findViewById(R.id.km1);
		km2 = (EditText)findViewById(R.id.km2);
		km3 = (EditText)findViewById(R.id.km3);
	}

	public void onCheckbox1Clicked(View checkBox){
		km1.setEnabled(((CheckBox) checkBox).isChecked());
	}
	public void onCheckbox2Clicked(View checkBox){
		km2.setEnabled(((CheckBox) checkBox).isChecked());
	}
	public void onCheckbox3Clicked(View checkBox){
		km3.setEnabled(((CheckBox) checkBox).isChecked());
	}

	public void setRouteDistance(final double distance){
		d=distance;
		runOnUiThread(new Runnable() {
		
			@Override
			public void run() {
				System.out.println("Entra a setRouteDistance");
				weeklyKM.setText(""+(Double.parseDouble(veces.getText().toString())*distance/1000));
				weeklyKM.setEnabled(false);
				System.out.println("distancia ingresada: "+distance);
			}
		});
		
	}
	
	public void tryAddVehicle(View view){
		String vName = name.getText().toString();
		if(vName == null || vName.trim().equals("")){
			showDialog("Nombre inválido", "El nombre de vehiculo ingresado es inválido");
		}else{
			try{
				int vCurrentKmCount = Integer.parseInt(currentKmCount.getText().toString());
				try{
					double vWeeklyKM = Double.parseDouble(weeklyKM.getText().toString());
					
					ArrayList<Record> r = new ArrayList<Record>();
					if(cb1.isChecked()){
						try {
							int vKm1 = Integer.parseInt(km1.getText().toString());
							Record rec = new Record(-1, "Taller desconocido", vKm1, a.get(0).getNombre(), new Date());
							r.add(rec);
						} catch (NumberFormatException e) {
							showDialog("Kilometraje de registro inválido", "Por favor, ingresa hace cuántos kilometros realizaste el mantenimiento '"+a.get(0).getNombre()+"'");
						}
					}else{
						for (int i = 0; i < a.size(); i++) {
							Maintenance m = a.get(i);
							if(m.getNombre().equalsIgnoreCase("aceite")){
								a.remove(i);
								break;
							}
							
						}
					}
					
					if(cb2.isChecked()){
						try {
							int vKm2 = Integer.parseInt(km2.getText().toString());
							Record rec = new Record(-1, "Taller desconocido", vKm2, a.get(1).getNombre(), new Date());
							r.add(rec);
						} catch (NumberFormatException e) {
							showDialog("Kilometraje de registro inválido", "Por favor, ingresa hace cuántos kilometros realizaste el mantenimiento '"+a.get(1).getNombre()+"'");
						}
					}else{
						for (int i = 0; i < a.size(); i++) {
							Maintenance m = a.get(i);
							if(m.getNombre().equalsIgnoreCase("llantas")){
								a.remove(i);
								break;
							}
							
						}
					}
					
					if(cb3.isChecked()){
						try {
							int vKm3 = Integer.parseInt(km3.getText().toString());
							Record rec = new Record(-1, "Taller desconocido", vKm3, a.get(2).getNombre(), new Date());
							r.add(rec);
						} catch (NumberFormatException e) {
							showDialog("Kilometraje de registro inválido", "Por favor, ingresa hace cuántos kilometros realizaste el mantenimiento '"+a.get(2).getNombre()+"'");
						}
					}else{
						for (int i = 0; i < a.size(); i++) {
							Maintenance m = a.get(i);
							if(m.getNombre().equalsIgnoreCase("frenos")){
								a.remove(i);
								break;
							}
							
						}
					}
					
					System.out.println("NUmero de mantenimientos:"+a.size());
					System.out.println("NUmero de records:"+r.size());
					
					if(a.isEmpty()){
						showDialog("Sin recordatorios", "Debes seleccionar al menos un recordatorios para tu vehiculo");
					}else{
						Vehicle v = new Vehicle(vName, vWeeklyKM, vCurrentKmCount, a, r);
						if(!instancia.addVehicle(v)){
							showDialog("Vehiculo existente", "Ya existe un vehiculo con el nombre ingresado, prueba con otro");
						}else{
							setResult(RESULT_OK);
							finish();
						}
					}
				}catch(NumberFormatException e){
					showDialog("Kilometraje semanal inválido", "El kilometraje semanal ingresado es inválido");
				}
			}catch(NumberFormatException e){
				showDialog("Kilometraje actual inválido", "El kilometraje actual ingresado es inválido");
			}
		}
	}

	private void showDialog(String title, String message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(title);
		alertDialog.setCancelable(false);
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {

			}
		});
		AlertDialog dialog= alertDialog.create();
		dialog.show();

	}

	@Override
	public boolean onEditorAction(TextView v, int arg1, KeyEvent arg2) {
		if(v.getId() == R.id.veces_por_semana){
			
			if(weeklyKM.getText().toString() != null && d!=-1){
				setRouteDistance(d);
			}
			
			return true;
		}else{
			return false;
		}
	}
}
