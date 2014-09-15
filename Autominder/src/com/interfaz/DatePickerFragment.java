package com.interfaz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {

	Date d;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		d = new Date();
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public Date getDate(){
		return d;
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		try {
			d = new Date(new SimpleDateFormat("dd-MM-yyyy").parse(dayOfMonth+"-"+monthOfYear+"-"+year).getTime());
		} catch (ParseException e) {
			System.out.println("EEEEEEEEEEEEEEEEEERRRRRRRRRRRRRRROOOOOOOORRRRRRR");
			System.out.println("no hace bien el parse");
			e.printStackTrace();
		}
		
	}
}
