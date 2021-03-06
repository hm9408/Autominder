package com.interfaz;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.autominder.Principal;
import com.autominder.R;

public class FragmentoReminders extends Fragment implements OnClickListener {

	ListView list;
	Principal p;
	private Button butAddMaint;

	public FragmentoReminders() {
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
		ReminderListAdapter adapter = new ReminderListAdapter(getActivity(),p.getSelected().getReminders(), (MainActivity)getActivity(), this);
		list.setAdapter(adapter);

		return v;

	}

	@Override
	public void onResume() {
		super.onResume();
		list.setAdapter(new ReminderListAdapter(getActivity(),p.getSelected().getReminders(), (MainActivity)getActivity(), this));

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
	
	@SuppressWarnings("static-access")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 555){//volvio de AddMaintenanceActivity
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
						((MainActivity)getActivity()).crearNotificationService();
					}
				}.execute();
			}
		}else if (requestCode == 222) {//volvio de editMaintenanceActivity
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
					((MainActivity)getActivity()).forzarRefresh(2);
					((MainActivity)getActivity()).crearNotificationService();
				}
			}.execute();
		}
	}
}
