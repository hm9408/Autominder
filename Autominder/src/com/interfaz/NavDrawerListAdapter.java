package com.interfaz;

import java.util.ArrayList;

import com.autominder.R;
import com.autominder.Vehicle;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
    private ArrayList<Vehicle> vehicles;
     
    public NavDrawerListAdapter(Context context, ArrayList<Vehicle> vehicles){
        this.context = context;
        this.vehicles = vehicles;
    }
 
    @Override
    public int getCount() {
        return vehicles.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return vehicles.get(position);
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
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
          
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
          
        txtTitle.setText(vehicles.get(position).getName());
         
         
        return convertView;
    }

}
