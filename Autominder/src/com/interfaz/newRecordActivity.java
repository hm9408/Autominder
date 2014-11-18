package com.interfaz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.autominder.Maintenance;
import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Record;

@SuppressLint("SimpleDateFormat")
public class newRecordActivity extends Activity implements OnDateSetListener{

	private Principal instancia;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;

    private Spinner spinner;
    private EditText kmPassedSince;
    private EditText newRecordDate;
    private EditText newNombreTaller;
    private EditText newCost;
	
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
	    
	    //NFC magic starts here
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {//are thre nfc capabilities in the device
        	
        	if (mNfcAdapter.isEnabled()) {
        		Toast.makeText(getApplicationContext(), "Acerque Tag del centro de mantenimento para obtener datos (si lo tiene)", Toast.LENGTH_LONG).show();
            } else {
            	Toast.makeText(getApplicationContext(), "Active NFC para acceso automatico a los datos de mantenimiento", Toast.LENGTH_LONG).show();
            }
        }
		
     // create an intent with tag data and deliver to this activity
        mPendingIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
 
        // set an intent filter for all MIME data
        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
            mIntentFilters = new IntentFilter[] { ndefIntent };
        } catch (Exception e) {
        	System.out.println(e.toString());
        }
 
        mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };
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
			}
			
			Record r = new Record(vCost, vNombreTaller, vKmPassedSince, vManten, vFecha);
			instancia.getSelected().addNewRecord(r);
			instancia.saveState();
			setResult(RESULT_OK);
			Toast.makeText(getApplicationContext(), "Mantenimiento registrado exitosamente", Toast.LENGTH_SHORT).show();
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
	
	@Override
	public void onNewIntent(Intent intent) {        
		String action = intent.getAction();
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		// parse through all NDEF messages and their records and pick text type only
		Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		
		if (data != null) {
			try {
				NdefRecord [] recs = ((NdefMessage)data[0]).getRecords();
				if (recs[0].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
						Arrays.equals(recs[0].getType(), NdefRecord.RTD_TEXT)) {

					byte[] payload = recs[0].getPayload();
					String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
					int langCodeLen = payload[0] & 0077;

					String msgFromNfc = new String(payload, langCodeLen + 1,
							payload.length - langCodeLen - 1, textEncoding);
					
					String[] dingen = msgFromNfc.split(";");
					
					 newNombreTaller.setText(dingen[0]);
					 newNombreTaller.setEnabled(false);
					 spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition(dingen[1]));
					 spinner.setEnabled(false);
					 newCost.setText(dingen[2]);
					 newCost.setEnabled(false);
					 
					 kmPassedSince.setText("0");
					 
					 trySaveRecord(null);
					
				}		
			} catch (Exception e) {
				Log.e("TagDispatch", e.toString());
			}

		}
	}

	@Override
    public void onResume() {
        super.onResume();
 
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
    }
 
    @Override
    public void onPause() {
        super.onPause();
 
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }
}
