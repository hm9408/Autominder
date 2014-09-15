package com.interfaz;

import com.autominder.R;
import com.autominder.R.id;
import com.autominder.R.layout;
import com.autominder.Vehicle;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentoInfoVehiculo extends Fragment implements OnClickListener {

	private Vehicle selected;
	private TextView kmCount;
	private EditText weeklyKm;
	private Button update;
	public FragmentoInfoVehiculo(Vehicle selected) {
		this.selected = selected;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragmento_info_vehiculo, container, false);
		if (selected != null) {
			kmCount = (TextView) v.findViewById(R.id.txtKmActualVal);
			kmCount.setText(selected.getCurrentKmCount());
			weeklyKm = (EditText) v.findViewById(R.id.editKmSemanales);
			weeklyKm.setText(selected.getWeeklyKM());
			update = (Button) v.findViewById(R.id.butUpdateWeeklyKm);
			update.setOnClickListener(this);
		}
		return v;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.butUpdateWeeklyKm:
			int newVal = Integer.parseInt(weeklyKm.getText().toString()); 
			selected.setWeeklyKM(newVal);
			Toast.makeText(getActivity(), "Valor semanal de KMs actualizado.", Toast.LENGTH_SHORT).show();
			break;
		}
		
	}
}
