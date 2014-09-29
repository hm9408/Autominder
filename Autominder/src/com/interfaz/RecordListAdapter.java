package com.interfaz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.autominder.R;
import com.autominder.Record;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class RecordListAdapter extends BaseAdapter {

	private Context context;
    private ArrayList<Record> records;
     
    public RecordListAdapter(Context context, ArrayList<Record> records){
        this.context = context;
        this.records = records;
    }
 
    @Override
    public int getCount() {
        return records.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return records.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.historial_list_item, null);
        }
          
        TextView txtTitle = (TextView) convertView.findViewById(R.id.manten_name);
        txtTitle.setText(records.get(position).getMaintenanceName());
        
        TextView fecha = (TextView) convertView.findViewById(R.id.fecha_record);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        fecha.setText(df.format(records.get(position).getFecha()));
        
        TextView kmPassed = (TextView) convertView.findViewById(R.id.km_passed_record);
        kmPassed.setText("Han pasado: "+records.get(position).getKmPassedSince()+"km");
        
        TextView nTaller = (TextView) convertView.findViewById(R.id.nom_taller_record);
        nTaller.setText(records.get(position).getNombreTaller());
        
        TextView cost = (TextView) convertView.findViewById(R.id.cost_record);
        cost.setText("$"+records.get(position).getCost());
         
        return convertView;
    }

}
