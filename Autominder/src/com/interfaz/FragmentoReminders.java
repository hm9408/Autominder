package com.interfaz;

import com.autominder.Principal;
import com.autominder.R;
import com.autominder.R.id;
import com.autominder.R.layout;
import com.autominder.Vehicle;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentoReminders extends Fragment implements OnClickListener {

	ListView list;
	Principal p;
	private Button butAddMaint;

	public FragmentoReminders() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragmento_reminders, container, false);

		list = (ListView)v.findViewById(R.id.reminders_list);
		TextView empty = new TextView(getActivity());
		empty.setText("No hay recordatorios");
		list.setEmptyView(empty);
		butAddMaint = (Button) v.findViewById(R.id.butAddMaintenanceAct);
		butAddMaint.setOnClickListener(this);
		// setting the nav drawer list adapter
		p = Principal.darInstancia(getActivity());
		ReminderListAdapter adapter = new ReminderListAdapter(getActivity(),p.getSelected().getReminders());
		list.setAdapter(adapter);

		return v;

	}

	@Override
	public void onResume() {
		super.onResume();
		list.setAdapter(new ReminderListAdapter(getActivity(),p.getSelected().getReminders()));

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.butAddMaintenanceAct:
			Intent i = new Intent(getActivity(), AddMaintenanceActivity.class);
			startActivityForResult(i, 555);
			break;
		}
		
	}
}
