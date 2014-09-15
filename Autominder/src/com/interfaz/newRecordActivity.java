package com.interfaz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.autominder.Maintenance;
import com.autominder.Principal;
import com.autominder.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.DatePickerDialog.OnDateSetListener;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

public class newRecordActivity extends Activity implements OnDateSetListener{

	Principal instancia;

	Spinner spinner;
	EditText kmPassedSince;
	EditText newRecordDate;
	EditText newNombreTaller;
	EditText newCost;
	
	DatePickerFragment dpf;

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
		newRecordDate.setText(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
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
		newRecordDate.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
		
	}
}
