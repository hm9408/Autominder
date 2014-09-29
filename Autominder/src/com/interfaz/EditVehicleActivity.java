package com.interfaz;

import com.autominder.Principal;
import com.autominder.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class EditVehicleActivity extends Activity {

	Principal instancia;
	
	NumberPicker np;
	EditText et;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.edit_vehicle_activity);
		
		setResult(RESULT_CANCELED);
		
		instancia = Principal.darInstancia(getApplicationContext());
		getActionBar().setTitle("Editar datos - "+instancia.getSelected().getName());
		
		np=(NumberPicker)findViewById(R.id.NumberPicker01);
		et = (EditText)findViewById(R.id.new_km);
		
		int currentKmCount = instancia.getSelected().getCurrentKmCount();
		int weeklyKM = instancia.getSelected().getWeeklyKM();
		
		et.setText(""+currentKmCount);
		np.setMaxValue(weeklyKM+15);
		np.setMinValue(weeklyKM>15?weeklyKM-15:0);
		np.setValue(weeklyKM);
		np.setWrapSelectorWheel(false);
		
		
	}
	
	public void trySaveChanges(View view){
		try{
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        switch (which){
			        case DialogInterface.BUTTON_POSITIVE:
			        	int vCurrentKmCount = Integer.parseInt(et.getText().toString());
						int vWeeklyKM = np.getValue();
			        	
			        	instancia.getSelected().modifyCurrentKmCount(vCurrentKmCount);
						instancia.getSelected().setWeeklyKM(vWeeklyKM);
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
			builder.setMessage("¿Estás seguro?\nNuevos recordatorios serán generados").setPositiveButton("Si", dialogClickListener)
			    .setNegativeButton("No", dialogClickListener).show();
			
			
		}catch(NumberFormatException e){
			showDialog("Kilometraje actual inválido", "El kilometraje actual ingresado es inválido");
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
