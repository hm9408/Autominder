package com.interfaz;

import com.autominder.Maintenance;
import com.autominder.Principal;
import com.autominder.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EditMaintenanceActivity extends Activity implements OnItemSelectedListener {

	public final static String MAINTENANCE_NAME= "mainteannce name";

	Principal instancia;
	String maintenName;

	EditText txtKm;
	EditText txtTime;
	String timeChoice;

	Maintenance x;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_maintenance_activity);

		setResult(RESULT_CANCELED);

		instancia = Principal.darInstancia(getApplicationContext());
		getActionBar().setTitle("Mantenimiento - "+instancia.getSelected().getName());

		maintenName = (String) getIntent().getExtras().get(MAINTENANCE_NAME);
		((TextView)findViewById(R.id.edit_maintenName)).setText("Editar '"+maintenName+"'");

		x = instancia.getSelected().getMaintenance(maintenName);

		if(x.getType() == Maintenance.SEGUN_KM){
			LinearLayout ll_tiempo = (LinearLayout)findViewById(R.id.edit_time_layout);
			((ViewManager)ll_tiempo.getParent()).removeView(ll_tiempo);

			txtKm = (EditText)findViewById(R.id.mainten_km_txt);
			txtKm.setText(""+x.getKm());

			/*NI PUTA IDEA DE PORQUE TOCA CREAR myView
			 * PERO SI NO, sp QUEDA NULO
			 * http://stackoverflow.com/a/18690657/2109083*/
			View myView = getLayoutInflater().inflate(R.layout.edit_maintenance_activity, null);
			Spinner sp = (Spinner)myView.findViewById(R.id.spinner_time);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_choices, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(adapter);
			sp.setOnItemSelectedListener(this);
		}else if(x.getType() == Maintenance.SEGUN_TIEMPO){
			LinearLayout ll_km = (LinearLayout)findViewById(R.id.edit_km_layout);
			((ViewManager)ll_km.getParent()).removeView(ll_km);

			txtTime = (EditText)findViewById(R.id.mainten_time_txt);
			txtTime.setText(""+x.getTiempo());
		}

	}

	public void trySaveMaintenance(View view){
		try {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
					case DialogInterface.BUTTON_POSITIVE:

						if(x.getType() == Maintenance.SEGUN_KM){

							int newKmInterval = Integer.parseInt(txtKm.getText().toString());
							instancia.getSelected().modifyMaintenance(maintenName, newKmInterval, -1, null);

						}else if(x.getType() == Maintenance.SEGUN_TIEMPO){

							int newTimeInterval = Integer.parseInt(txtTime.getText().toString());
							instancia.getSelected().modifyMaintenance(maintenName, -1, newTimeInterval, timeChoice);

						}
						
						instancia.saveState();

						setResult(RESULT_OK);
						finish();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("¿Estás seguro?\nEl recordatorio asociado cambiará").setPositiveButton("Si", dialogClickListener)
			.setNegativeButton("No", dialogClickListener).show();
		} catch (NumberFormatException e) {
			if(x.getType() == Maintenance.SEGUN_KM){

				showDialog("Intervalo de kilometros inválido", "El intervalo ingresado es inválido");

			}else if(x.getType() == Maintenance.SEGUN_TIEMPO){

				showDialog("Intervalo de tiempo inválido", "El intervalo ingresado es inválido");

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
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		timeChoice = (String)parent.getItemAtPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}
