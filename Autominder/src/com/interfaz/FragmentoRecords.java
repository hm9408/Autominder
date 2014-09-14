package com.interfaz;

import com.autominder.R;
import com.autominder.R.id;
import com.autominder.R.layout;
import com.autominder.Vehicle;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentoRecords extends Fragment {

	public FragmentoRecords(Vehicle selected) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragmento_home, container, false);

		TextView messageTextView = (TextView)v.findViewById(R.id.textViewHome);
		messageTextView.setText("La puta vista de HOME");

		return v;

	}
}
