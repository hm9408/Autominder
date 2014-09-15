package com.interfaz;

import java.util.ArrayList;
import java.util.Date;

import com.autominder.Maintenance;
import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Record;
import com.autominder.Vehicle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class AddVehicleActivity extends Activity {

	private EditText name;
	private EditText weeklyKM;
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

	private Button addVehicleButton;

	private Principal instancia;
	
	ArrayList<Maintenance> a;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_vehicle_activity);
		
		setResult(RESULT_CANCELED);
		
		getActionBar().setTitle("Agregar vehículo");

		instancia = Principal.darInstancia(getApplicationContext());

		name = (EditText)findViewById(R.id.nombre_vehiculo);
		weeklyKM =(EditText)findViewById(R.id.km_semanales_vehiculo);
		currentKmCount = (EditText)findViewById(R.id.km_actual_vehiculo);

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

		addVehicleButton = (Button)findViewById(R.id.addVehicleButton);
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

	public void tryAddVehicle(View view){
		String vName = name.getText().toString();
		if(vName == null || vName.trim().equals("")){
			showDialog("Nombre inválido", "El nombre de vehiculo ingresado es inválido");
		}else{
			try{
				int vCurrentKmCount = Integer.parseInt(currentKmCount.getText().toString());
				try{
					int vWeeklyKM = Integer.parseInt(weeklyKM.getText().toString());
					
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
						a.remove(0);
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
						a.remove(1);
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
						a.remove(2);
					}
					
					System.out.println("NUmero de mantenimientos:"+a.size());
					System.out.println("NUmero de records:"+r.size());
					Vehicle v = new Vehicle(vName, vWeeklyKM, vCurrentKmCount, a, r);
					if(!instancia.addVehicle(v)){
						showDialog("Vehiculo existente", "Ya existe un vehiculo con el nombre ingresado, prueba con otro");
					}else{
						setResult(RESULT_OK);
						finish();
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
}
