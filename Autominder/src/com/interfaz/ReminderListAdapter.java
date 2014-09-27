package com.interfaz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Record;
import com.autominder.Reminder;
import com.autominder.Vehicle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ReminderListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Reminder> reminders;
	private MainActivity mainActivity;
	private FragmentoReminders fr;

	public ReminderListAdapter(Context context, ArrayList<Reminder> reminders, MainActivity ma, FragmentoReminders fr){
		this.context = context;
		this.reminders = reminders;
		mainActivity = ma;
		this.fr = fr;
	}

	@Override
	public int getCount() {
		return reminders.size();
	}

	@Override
	public Object getItem(int position) {       
		return reminders.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "SimpleDateFormat", "InflateParams" }) @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.recordatorios_list_item, null);
		}

		TextView txtTitle = (TextView) convertView.findViewById(R.id.manten_remin);
		txtTitle.setText(reminders.get(position).getNombreManten()+" ("+reminders.get(position).getNombreCarro()+")");

		TextView fecha = (TextView) convertView.findViewById(R.id.fecha_remin);
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		fecha.setText(df.format(reminders.get(position).getFecha()));

		ImageButton borrar = (ImageButton)convertView.findViewById(R.id.btn_eliminar);
		borrar.setVisibility(reminders.size() != 1?View.VISIBLE:View.GONE);
		borrar.setOnClickListener(new AccionOnClickListener(context, mainActivity, reminders.get(position).getNombreManten(), fr, AccionOnClickListener.ELIMINAR));

		ImageButton editar = (ImageButton)convertView.findViewById(R.id.btn_editar);
		editar.setOnClickListener(new AccionOnClickListener(context, mainActivity, reminders.get(position).getNombreManten(), fr, AccionOnClickListener.EDITAR));

		return convertView;
	}

	private class AccionOnClickListener implements OnClickListener{

		Context context;
		MainActivity ma;
		FragmentoReminders fr;
		String maintenanceName;
		int tipoRespuesta;

		public final static int ELIMINAR = 1;
		public final static int EDITAR = 2;

		public AccionOnClickListener(Context context, MainActivity ma, String string, FragmentoReminders fr, int tipoRespuesta) {
			this.context = context;
			this.ma = ma;
			this.fr = fr;
			maintenanceName = string;
			this.tipoRespuesta = tipoRespuesta;
		}

		@Override
		public void onClick(View v) {
			if(tipoRespuesta == ELIMINAR){
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
						case DialogInterface.BUTTON_POSITIVE:

							Principal instancia = Principal.darInstancia(context);

							instancia.getSelected().removeMaintenance(maintenanceName);
							instancia.saveState();

							if (ma != null){
								ma.forzarRefresh(1);
								ma.crearNotificationService();
							}
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							//No button clicked
							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("¿Estás seguro?\nSe eliminará el mantenimiento y sus records").setPositiveButton("Si", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();

			}else if(tipoRespuesta == EDITAR){
				Intent i = new Intent(context, EditMaintenanceActivity.class)
							.putExtra(EditMaintenanceActivity.MAINTENANCE_NAME, maintenanceName);	
				if(fr != null){//se llama editMaintenanceActivity desde el FragemntoReminders
					fr.startActivityForResult(i, 222);
				}else{//se llama editMaintenanceActivity desde PendingRemindersActivity
					context.startActivity(i);
				}
			}
		}

	}
}
