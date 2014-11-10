package com.interfaz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.autominder.ConexionCliente;
import com.autominder.Principal;
import com.autominder.R;

public class LoginActivity extends Activity{

	Principal instancia;

	EditText login;
	EditText contrasena;

	ConexionCliente c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_activity);

		setResult(RESULT_CANCELED);

		instancia = Principal.darInstancia(getApplicationContext());
		getActionBar().setTitle("Ingreso");

		login=(EditText)findViewById(R.id.login);
		contrasena = (EditText)findViewById(R.id.contrasena);

		c = new ConexionCliente(this);

	}

	public void login(View view){

		new AsyncTask<Void, Void, Boolean>()
		{

			@Override
			protected Boolean doInBackground(Void... p)
			{
				return c.verificarLogin( login.getText().toString(), contrasena.getText().toString());
			}

			@Override
			protected void onPostExecute(Boolean result)
			{
				//this is code for the UI thread, now that it knows what is the result.
				if(result.booleanValue()){
					System.out.println("login successful BIATCH");
					instancia.setUsername(login.getText().toString());
					instancia.setPassword(contrasena.getText().toString());

					//procede a pedir todos los datos que haya del usuario identificado
					new AsyncTask<Void, Void, Void>(){
						@Override
						protected Void doInBackground(Void... p)
						{
							System.out.println("se pretende hacer pull de los datos");
							c.datosPull(login.getText().toString(), contrasena.getText().toString());
							return null;
						}
						@Override
						protected void onPostExecute(Void result)
						{
							System.out.println("DEBERIA HACER FINISH");
							finish();
						}
					}.execute();
				}else{
					if (c.isOnline()) {
						showDialog("Login incorrecto", "Intente nuevamente, o ingrese como nuevo usuario");
					}else{
						showDialog("Sin conexión", "Revise sus configuraciones de red e intenete nuevamente");
					}
					
				}
			}
		}.execute();

	}

	public void newUser(View view){
		new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				return c.RegistrarLogin(login.getText().toString(), contrasena.getText().toString());
			}
			protected void onPostExecute(Boolean result) {
				if(result.booleanValue()){
					System.out.println("registro exitoso, aparentemente");
					instancia.setUsername(login.getText().toString());
					instancia.setPassword(contrasena.getText().toString());
					finish();
				}else{
					showDialog("Sin conexión", "Revise sus configuraciones de red e intenete nuevamente");
				}
				
			};
		}.execute();
		
	}
	
	public void goOffline(View view){
		System.out.println("Opcion avanzar offline");
		instancia.setUsername(null);
		instancia.setPassword(null);
		finish();
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
