package com.interfaz;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.autominder.Maintenance;
import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Record;

public class AddMaintenanceActivity extends Activity {

	

	private Principal instancia;
	
	private ArrayList<Maintenance> a;
	private RadioGroup butGroup;
	private RadioButton por_km;
	private RadioButton por_tiempo;
	private Spinner spinNames;
	private EditText editCustomName;
	private EditText editKM;
	private Button butAdd;

	private Spinner spinPeriod;

	private EditText editLastTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_maintenance_activity);
		instancia = Principal.darInstancia(getApplication());
		a = instancia.getMantenimientos();
		ArrayList<String> maintenanceNames = new ArrayList<String>();
		for (int i = 0; i < a.size(); i++) {
			maintenanceNames.add(a.get(i).getNombre());
		}
		maintenanceNames.add("Personalizado...");
		setResult(RESULT_CANCELED);
		
		getActionBar().setTitle("Agregar Mantenimiento");

		instancia = Principal.darInstancia(getApplicationContext());
		butGroup = (RadioGroup)findViewById(R.id.radioGroup1);
		por_km = (RadioButton)findViewById(R.id.radio0);
		por_tiempo = (RadioButton)findViewById(R.id.radio1);
		editCustomName=(EditText)findViewById(R.id.editCustomName);
		editCustomName.setEnabled(false);
		editKM = (EditText)findViewById(R.id.editKM);
		editLastTime = (EditText)findViewById(R.id.editLastTime);
		butAdd = (Button)findViewById(R.id.butAddMaint);
		
		spinNames = (Spinner)findViewById(R.id.spinMaintNames);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, maintenanceNames); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinNames.setAdapter(spinnerArrayAdapter);
		spinNames.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (spinNames.getItemAtPosition(position).toString().equals("Personalizado...")) {
					editCustomName.setEnabled(true);
				}
				else if(!spinNames.getItemAtPosition(position).toString().equals("Personalizado..."))
				{
					if (a.get(position).getType()==1) {
						por_km.setChecked(true);
						editKM.setText(""+a.get(position).getKm());
						editKM.setEnabled(false);
					}
					else if(a.get(position).getType()==2){
						por_tiempo.setChecked(true);
						editKM.setText("");
					}
					editCustomName.setEnabled(false);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		
		spinPeriod = (Spinner) findViewById(R.id.spinPeriod);
		ArrayList<String> periods = new ArrayList<String>();
		periods.add("Cada año");
		periods.add("Cada seis meses");
		ArrayAdapter<String> spinnerArrayAdapterPeriod = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, periods); //selected item will look like a spinner set from XML
		spinnerArrayAdapterPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinPeriod.setAdapter(spinnerArrayAdapterPeriod);
		spinPeriod.setEnabled(false);
		
		
		butGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio0:
                        spinPeriod.setEnabled(false);
                        editKM.setEnabled(true);
                    break;

                    case R.id.radio1:
                    	editKM.setEnabled(false);
                    	spinPeriod.setEnabled(true);
                    break;
                }
            }
        });
		butAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tryAddMaintenance();
			}
		});
	}

	public void tryAddMaintenance() {
		System.out.println("Entró a guardar el nuevo mantenimiento");
		if (butGroup.getCheckedRadioButtonId()==R.id.radio0) { //por_km
			if (editLastTime.getText().toString().trim().equals("")||editKM.getText().toString().trim().equals("")||
					(editCustomName.isEnabled() && editCustomName.getText().toString().trim().equals(""))) {
				showDialog("Campos inválidos", "Algún campo se encuentra vacío, por favor ingresa valores.");
			}
			else if(editCustomName.isEnabled() && !editCustomName.getText().toString().trim().equals(""))
			{
				System.out.println("Entró, es por km, tiene custom name y no es vacío");
				instancia.agregarMantenimiento(editCustomName.getText().toString(), 
						getSelectedRadioButton(), 
						Integer.parseInt(editKM.getText().toString()), 
						getTime());
				Maintenance a = instancia.getMantenimientos().get(instancia.getMantenimientos().size()-1); //el recien agregado
				instancia.addMaintenanceSelected(a, 
						new Record(-1, "Taller desconocido", 
								Integer.parseInt(editLastTime.getText().toString()), 
								a.getNombre(), 
								new Date()));
				setResult(RESULT_OK);
				finish();
			}
			else if(!editCustomName.isEnabled())
			{
				System.out.println("Entró, es por km, tiene name cargado y no es vacío");
				instancia.agregarMantenimiento(spinNames.getSelectedItem().toString(), 
						getSelectedRadioButton(), 
						Integer.parseInt(editKM.getText().toString()), 
						getTime());
				Maintenance a = instancia.getMantenimientos().get(instancia.getMantenimientos().size()-1); //el recien agregado
				boolean added= instancia.addMaintenanceSelected(a, 
						new Record(-1, "Taller desconocido", 
								Integer.parseInt(editLastTime.getText().toString()), 
								a.getNombre(), 
								new Date()));
				setResult(RESULT_OK);
				finish();
			}				
		}
		else if (butGroup.getCheckedRadioButtonId()==R.id.radio1) { //por_tiempo
			if (editLastTime.getText().toString().trim().equals("")||editCustomName.isEnabled() && editCustomName.getText().toString().trim().equals("")) {
				showDialog("Campos inválidos", "Algún campo se encuentra vacío, por favor ingresa valores.");
			}
			else if(editCustomName.isEnabled() && !editCustomName.getText().toString().trim().equals(""))
			{
				System.out.println("Entró, es por tiempo, tiene custom name y no es vacío");
				instancia.agregarMantenimiento(editCustomName.getText().toString(), 
						getSelectedRadioButton(), 
						Integer.parseInt(editKM.getText().toString()), 
						getTime());
				Maintenance a = instancia.getMantenimientos().get(instancia.getMantenimientos().size()-1); //el recien agregado
				instancia.addMaintenanceSelected(a, 
						new Record(-1, "Taller desconocido", 
								Integer.parseInt(editLastTime.getText().toString()), 
								a.getNombre(), 
								new Date()));
				setResult(RESULT_OK);
				finish();
			}
			else if(!editCustomName.isEnabled())
			{
				System.out.println("Entró, es por tiempo, tiene name cargado y no es vacío");
				instancia.agregarMantenimiento(spinNames.getSelectedItem().toString(), 
						getSelectedRadioButton(), 
						Integer.parseInt(editKM.getText().toString()), 
						getTime());
				Maintenance a = instancia.getMantenimientos().get(instancia.getMantenimientos().size()-1); //el recien agregado
				instancia.addMaintenanceSelected(a, 
						new Record(-1, "Taller desconocido", 
								Integer.parseInt(editLastTime.getText().toString()), 
								a.getNombre(), 
								new Date()));
				setResult(RESULT_OK);
				finish();
			}
		}
		
	}

	public long getTime() {
		String a = spinPeriod.getSelectedItem().toString();
		long b=0;
		if (a.equals("Cada año")) {
			b= 31536000000L;
		} else if(a.equals("Cada seis meses")) {
			b= 15768000000L;
		}
		return b;
	}

	private int getSelectedRadioButton() {
		int a = butGroup.getCheckedRadioButtonId();
		switch (a) {
		case R.id.radio0:
			return 1;

		case R.id.radio1:
			return 2;
		}
		return 0;
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
