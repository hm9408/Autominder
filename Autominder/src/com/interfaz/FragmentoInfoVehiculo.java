package com.interfaz;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Vehicle;

public class FragmentoInfoVehiculo extends Fragment implements OnClickListener {

	private Vehicle selected;
	private TextView kmCount;
	private TextView weeklyKm;
	private Button update;

	Principal instancia;

	public FragmentoInfoVehiculo() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragmento_info_vehiculo, container, false);

		instancia = Principal.darInstancia(getActivity());
		selected = instancia.getSelected();
		if (selected != null) {
			Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/exo2italic.ttf");
			kmCount = (TextView) v.findViewById(R.id.txtKmActualVal);
			kmCount.setTypeface(myTypeface);
			kmCount.setText(""+selected.getCurrentKmCount());
			weeklyKm = (TextView) v.findViewById(R.id.TextKmSemanales);
			weeklyKm.setTypeface(myTypeface);
			weeklyKm.setText(""+selected.getWeeklyKM());
			update = (Button) v.findViewById(R.id.butUpdateWeeklyKm);
			update.setOnClickListener(this);
		}
		return v;

	}

	@Override
	public void onResume() {
		super.onResume();
		selected = instancia.getSelected();
		if (selected != null) {
			kmCount.setText(""+selected.getCurrentKmCount());
			weeklyKm.setText(""+selected.getWeeklyKM());
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.butUpdateWeeklyKm:
			Intent i = new Intent(getActivity(), EditVehicleActivity.class);
			startActivityForResult(i, 777);
			break;
		}

	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 777){//viene de edit
			if(resultCode == getActivity().RESULT_OK){
				new AsyncTask<Void, Void, Void>(){
					@Override
					protected Void doInBackground(Void... p)
					{
						((MainActivity)getActivity()).pushCambios();
						return null;
					}
					@Override
					protected void onPostExecute(Void result)
					{
						getActivity().getActionBar().setTitle(instancia.getSelected().getName());
						//la siguiente linea funciona
						Toast.makeText(getActivity(), "editVehicle Volvio al FragmentoInfoVehiculo!", Toast.LENGTH_SHORT).show();
						((MainActivity)getActivity()).crearNotificationService();
					}
				}.execute();
			}
		}
	}
}
