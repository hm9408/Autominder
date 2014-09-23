package com.interfaz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Record;
import com.autominder.Reminder;
import com.autominder.Vehicle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
     
    public ReminderListAdapter(Context context, ArrayList<Reminder> reminders, MainActivity ma){
        this.context = context;
        this.reminders = reminders;
        mainActivity = ma;
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
 
    @Override
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
        borrar.setEnabled(reminders.size() != 1);
        borrar.setOnClickListener(new EliminarOnClickListener(context, mainActivity, reminders.get(position).getNombreManten()));
        
        return convertView;
    }

    private class EliminarOnClickListener implements OnClickListener{

    	Context context;
    	MainActivity ma;
    	String maintenanceName;
    	
		public EliminarOnClickListener(Context context, MainActivity ma, String string) {
			this.context = context;
			this.ma = ma;
			maintenanceName = string;
		}

		@Override
		public void onClick(View v) {
			
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        switch (which){
			        case DialogInterface.BUTTON_POSITIVE:
			        	
						Principal instancia = Principal.darInstancia(context);
						
			        	instancia.getSelected().removeMaintenance(maintenanceName);
						instancia.saveState();
						
						if (ma != null)ma.forzarRefresh(2);
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
			
		}
    	
    }
}
