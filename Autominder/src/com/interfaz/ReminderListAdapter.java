package com.interfaz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.autominder.R;
import com.autominder.Record;
import com.autominder.Reminder;
import com.autominder.Vehicle;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReminderListAdapter extends BaseAdapter {

	private Context context;
    private ArrayList<Reminder> reminders;
     
    public ReminderListAdapter(Context context, ArrayList<Reminder> reminders){
        this.context = context;
        this.reminders = reminders;
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
        
        return convertView;
    }

}
