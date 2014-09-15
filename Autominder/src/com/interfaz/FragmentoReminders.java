package com.interfaz;

import com.autominder.Principal;
import com.autominder.R;
import com.autominder.R.id;
import com.autominder.R.layout;
import com.autominder.Vehicle;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentoReminders extends Fragment {

	
	
	public FragmentoReminders() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragmento_distancia, container, false);

		ListView list = (ListView)v.findViewById(R.id.reminders_list);
		TextView empty = new TextView(getActivity());
		empty.setText("No hay recordatorios");
		list.setEmptyView(empty);

		// setting the nav drawer list adapter
		Principal p = Principal.darInstancia(getActivity());
		ReminderListAdapter adapter = new ReminderListAdapter(getActivity(),p.getSelected().getReminders());
		list.setAdapter(adapter);
		
		return v;

	}
}
