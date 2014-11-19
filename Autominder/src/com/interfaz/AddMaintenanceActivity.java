package com.interfaz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Spinner;

import com.autominder.Maintenance;
import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Record;

public class AddMaintenanceActivity extends Activity implements OnItemSelectedListener, OnDateSetListener {

	

	private Principal instancia;
	
	private ArrayList<Maintenance> a;
	private Spinner spinNames;
	private EditText editCustomName;
	private Switch criteria;
	private LinearLayout layoutKm;
		private EditText editKM;
		private EditText editKmLastTime;
	private LinearLayout layoutTime;	
		private EditText editTime;
		private EditText editTimeLastTime;
		private Spinner spinPeriod;
	
	String timeChoice;

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
		criteria = (Switch)findViewById(R.id.switch1);
		criteria.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        onToggleClicked(buttonView);
		    }
		});
		editCustomName=(EditText)findViewById(R.id.editCustomName);
		
		layoutKm =(LinearLayout)findViewById(R.id.kmMaintenanceLayout);
			editKM = (EditText)findViewById(R.id.editKM);
			editKmLastTime = (EditText)findViewById(R.id.editLastTime);
		
		layoutTime = (LinearLayout)findViewById(R.id.timeMaintenanceLayout);
			editTime = (EditText)findViewById(R.id.new_mainten_time_txt);
			editTimeLastTime=(EditText)findViewById(R.id.new_mainten_date);
		
		/* NI PUTA IDEA DE POR QUÉ TOCA CREAR myView
		 * PERO SI NO, sp QUEDA NULO
		 * http://stackoverflow.com/a/18690657/2109083*/
		//View myView = getLayoutInflater().inflate(R.layout.add_maintenance_activity, null);
		//Spinner sp = (Spinner)myView.findViewById(R.id.new_mainten_spinner_time);
		spinPeriod = (Spinner)findViewById(R.id.new_mainten_spinner_time);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_choices, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinPeriod.setAdapter(adapter);
		spinPeriod.setOnItemSelectedListener(this);
		
		spinNames = (Spinner)findViewById(R.id.spinMaintNames);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, maintenanceNames); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinNames.setAdapter(spinnerArrayAdapter);
		spinNames.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (spinNames.getItemAtPosition(position).toString().equals("Personalizado...")) {
					
					//Muestra el EditText del custom name
					editCustomName.setVisibility(View.VISIBLE);
					editCustomName.setEnabled(true);
					
					//borrar vista de T
					layoutTime.setVisibility(View.GONE);
					
					//cargar vista de KM
					layoutKm.setVisibility(View.VISIBLE);
					
					//poner el toggle en km y habilitarlo
					criteria.setChecked(false);//km
					criteria.setEnabled(true);
					
					//dejar vacio el textEdit de cuantos KM
					editKM.setText("");
					editKmLastTime.setText("");
				}
				else if(!spinNames.getItemAtPosition(position).toString().equals("Personalizado..."))
				{
					//Esconder el EditText del custom name
					editCustomName.setVisibility(View.GONE);
					
					if (a.get(position).getType()==Maintenance.SEGUN_KM) {
						//Bloquea el toggle en km
						criteria.setChecked(false);//toggle a km
						criteria.setEnabled(false);
						
						//borra la vista de tiempo
						layoutTime.setVisibility(View.GONE);
						
						//carga la vista de km
						layoutKm.setVisibility(View.VISIBLE);
						
						//carga km predet
						editKM.setText(""+a.get(position).getKm());
						editKM.setEnabled(true);
						editKmLastTime.setText("");
					}
					else if(a.get(position).getType()==Maintenance.SEGUN_TIEMPO){
						//Bloquea el toggle en T
						criteria.setChecked(true);//toggle a tiempo
						criteria.setEnabled(false);
						
						//borra vista de km
						layoutKm.setVisibility(View.GONE);

						//carga vista de t
						layoutTime.setVisibility(View.VISIBLE);

						//carga tiempo predet en meses
						long i = a.get(position).getTiempo();
						long j = (long)1000*60*60*24*30;
						long d = i/j;
						System.out.println(d);
						editTime.setText(""+d);
						spinPeriod.setSelection(1);//poner el spinner en meses
						
					}
					editCustomName.setEnabled(false);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		
	}

	@SuppressLint("SimpleDateFormat") public void tryAddMaintenance(View view) {
		System.out.println("Entró a guardar el nuevo mantenimiento");
		
		if (spinNames.getSelectedItem().toString().equals("Personalizado...")) {
			if(!editCustomName.getText().toString().trim().equals("")){
				if (criteria.isChecked()) {//tiempo
					try{
						long x =Long.parseLong(editTime.getText().toString());
						long y = darFactorMultiplicativo();
						long z = x*y;
						instancia.agregarMantenimiento(editCustomName.getText().toString(), 
								getSelectedMaintenanceType(), 
								-1, 
								z);
						Maintenance a = instancia.getMantenimientos().get(instancia.getMantenimientos().size()-1); //el recien agregado
						int estimatedKmPassed =instancia.getSelected().getKmPassedSince((new SimpleDateFormat("dd-MM-yyyy")).parse(editTimeLastTime.getText().toString()));
						instancia.addMaintenanceSelected(a, 
								new Record(-1, "Taller desconocido", 
										estimatedKmPassed,
										a.getNombre(), 
										(new SimpleDateFormat("dd-MM-yyyy")).parse(editTimeLastTime.getText().toString())
										));
						setResult(RESULT_OK);
						finish();
					}catch(NumberFormatException e){
						showDialog("Datos inválidos", "El intervalo de tiempo ingresado no es válido");
					} catch (ParseException e) {
						// No deberia pasar
						e.printStackTrace();
					}
				}else{//km
					try{
						instancia.agregarMantenimiento(editCustomName.getText().toString(), 
								getSelectedMaintenanceType(), 
								Integer.parseInt(editKM.getText().toString()), 
								-1);
						Maintenance a = instancia.getMantenimientos().get(instancia.getMantenimientos().size()-1); //el recien agregado
						instancia.addMaintenanceSelected(a, 
								new Record(-1, "Taller desconocido", 
										Integer.parseInt(editKmLastTime.getText().toString()), 
										a.getNombre(), 
										instancia.getSelected().getEstimatedDate(Integer.parseInt(editKmLastTime.getText().toString()))
										));
						setResult(RESULT_OK);
						finish();
					}catch(NumberFormatException e){
						showDialog("Datos inválidos", "El número de km ingresados no es válido");
						e.printStackTrace();
					}
				}
			}else{
				showDialog("Nombre inválido", "Por favor ingresa un nombre para el nuevo mantenimiento");
			}
				
		}else{//no es custon maintenance
			Maintenance m = null;
			for (int i = 0; i < a.size(); i++) {
				Maintenance act = a.get(i);
				if (act.getNombre().equals(spinNames.getSelectedItem().toString())) {
					m = act;
				}
			}
			
			if (m.getType()==Maintenance.SEGUN_KM) {
				try{
					m.setKm(Integer.parseInt(editKM.getText().toString()));
					instancia.addMaintenanceSelected(m, 
							new Record(-1, "Taller desconocido", 
									Integer.parseInt(editKmLastTime.getText().toString()), 
									m.getNombre(), 
									instancia.getSelected().getEstimatedDate(Integer.parseInt(editKmLastTime.getText().toString()))
									));
					setResult(RESULT_OK);
					finish();
				}catch(NumberFormatException e){
					showDialog("Datos inválidos", "El número de km ingresados no es válido");
				}
			}else{
				try{
					long x =Long.parseLong(editTime.getText().toString());
					long y = darFactorMultiplicativo();
					long z = x*y;
					m.setTiempo(z);
					int estimatedKmPassed =instancia.getSelected().getKmPassedSince((new SimpleDateFormat("dd-MM-yyyy")).parse(editTimeLastTime.getText().toString()));
					instancia.addMaintenanceSelected(m, 
							new Record(-1, "Taller desconocido", 
									estimatedKmPassed,
									m.getNombre(), 
									(new SimpleDateFormat("dd-MM-yyyy")).parse(editTimeLastTime.getText().toString())
									));
					setResult(RESULT_OK);
					finish();
				}catch(NumberFormatException e){
					showDialog("Datos inválidos", "El intervalo de tiempo ingresado no es válido");
				} catch (ParseException e) {
					// No deberia pasar
					e.printStackTrace();
				}
			}
		}
	}

	private long darFactorMultiplicativo() {
		if(timeChoice.equals("días")){
			return (long)24*60*60*1000;
		}else if(timeChoice.equals("meses")){
			return (long)30*24*60*60*1000;
		}else if(timeChoice.equals("años")){
			return (long)365*24*60*60*1000;
		}else{
			System.out.println("------------------------------------------ERROR TIMECHOICE");
			return -1;
		}
	}


	private int getSelectedMaintenanceType() {
		return criteria.isChecked()?Maintenance.SEGUN_TIEMPO:Maintenance.SEGUN_KM;
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
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		timeChoice = (String)parent.getItemAtPosition(position);
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	public void onToggleClicked(View view){
		boolean time = ((Switch) view).isChecked();
		if(time){
			//borra vista de km
			layoutKm.setVisibility(View.GONE);

			//carga vista de t
			layoutTime.setVisibility(View.VISIBLE);
			
			//dejar vacion los textEdit de tiempo
			editTime.setText("");
			editTimeLastTime.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		}else{
			//borra la vista de tiempo
			layoutTime.setVisibility(View.GONE);
			
			//carga la vista de km
			layoutKm.setVisibility(View.VISIBLE);
	
			//dejar vacio el textEdit de cuantos KM
			editKM.setText("");
			editKmLastTime.setText("");
		}
	}
	
	public void showDatePickerDialog(View view){
		Calendar c = Calendar.getInstance();	
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dpd = new DatePickerDialog(this, this, year, month, day);
		dpd.show();

	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		monthOfYear++;
		String sMonth = ""+monthOfYear;
		sMonth=sMonth.length()==1?"0"+sMonth:sMonth;
		editTimeLastTime.setText(dayOfMonth+"-"+sMonth+"-"+year);
		
	}
}
