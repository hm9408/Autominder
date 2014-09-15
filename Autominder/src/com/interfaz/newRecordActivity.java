package com.interfaz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.autominder.Maintenance;
import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Record;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class newRecordActivity extends Activity implements OnDateSetListener{

	Principal instancia;

	Spinner spinner;
	EditText kmPassedSince;
	EditText newRecordDate;
	EditText newNombreTaller;
	EditText newCost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.new_record_activity);

		setResult(RESULT_CANCELED);

		instancia = Principal.darInstancia(getApplicationContext());
		getActionBar().setTitle("Nuevo registro - "+instancia.getSelected().getName());

		spinner=(Spinner)findViewById(R.id.spinner1);
		kmPassedSince=(EditText)findViewById(R.id.new_km_passed_since);
		newRecordDate = (EditText)findViewById(R.id.new_record_date);
		newRecordDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		newNombreTaller = (EditText)findViewById(R.id.new_nombre_taller);
		newCost = (EditText)findViewById(R.id.new_cost);
		
		List<String> spinnerArray =  new ArrayList<String>();
		for (int i = 0; i < instancia.getSelected().getMaintenances().size(); i++) {
			Maintenance m = instancia.getSelected().getMaintenances().get(i);
			spinnerArray.add(m.getNombre());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
		
	}
	
	public void trySaveRecord(View view){
		String vManten = spinner.getSelectedItem().toString();
		try{
			int vKmPassedSince = Integer.parseInt(kmPassedSince.getText().toString());
			Date vFecha = new SimpleDateFormat("dd-MM-yyyy").parse(newRecordDate.getText().toString());
			String vNombreTaller = newNombreTaller.getText().toString().trim().isEmpty()?"Taller desconocido":newNombreTaller.getText().toString().trim();
			int vCost;
			try{
				vCost=Integer.parseInt(newCost.getText().toString());
			}catch (NumberFormatException e) {
				vCost=-1;
				Toast.makeText(getApplicationContext(), "Error procesando el campo de costo, se asume -1", Toast.LENGTH_SHORT).show();
			}
			
			Record r = new Record(vCost, vNombreTaller, vKmPassedSince, vManten, vFecha);
			instancia.getSelected().addNewRecord(r);
			instancia.saveState();
			setResult(RESULT_OK);
			finish();
			
		} catch (NumberFormatException e) {
			showDialog("Error", "Por favor, ingresa hace cuántos kilometros realizaste el mantenimiento '"+vManten+"'");
		} catch (ParseException e1) {
			System.out.println("EEEEEEEEEERRROOORRRR");
			System.out.println("wtff");
			e1.printStackTrace();
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
		newRecordDate.setText(dayOfMonth+"-"+sMonth+"-"+year);
		
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
