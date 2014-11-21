package com.interfaz;

import java.util.ArrayList;

import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Vehicle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private MainActivity ma;
    private ArrayList<Vehicle> vehicles;
     
    public NavDrawerListAdapter(Context context, MainActivity ma, ArrayList<Vehicle> vehicles){
        this.context = context;
        this.ma = ma;
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
        
        ImageButton borrar = (ImageButton)convertView.findViewById(R.id.btn_eliminar_vehiculo);
		borrar.setVisibility(vehicles.size() != 1?View.VISIBLE:View.GONE);
		borrar.setOnClickListener(new DeleteOnClickListener(context, ma, vehicles.get(position)));
         
         
        return convertView;
    }
    
    private class DeleteOnClickListener implements OnClickListener{

    	Context context;
    	MainActivity ma;
    	Vehicle vehicle;
    	
    	public DeleteOnClickListener(Context context, MainActivity ma, Vehicle vehicle) {
			this.context = context;
			this.ma = ma;
			this.vehicle = vehicle;
		}
    	
		@Override
		public void onClick(View v) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
					case DialogInterface.BUTTON_POSITIVE:

						Principal instancia = Principal.darInstancia(context);

						instancia.deleteVehicle(vehicle);
						instancia.saveState();

						new AsyncTask<Void, Void, Void>(){
							@Override
							protected Void doInBackground(Void... p)
							{
								ma.pushCambios();
								return null;
							}
							@Override
							protected void onPostExecute(Void result)
							{
								ma.forzarRefresh(1);
								ma.refreshDrawer();
								ma.crearNotificationService();
							}
						}.execute();
						
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("¿Estás seguro?\nSe eliminará el vehículo por completo").setPositiveButton("Si", dialogClickListener)
			.setNegativeButton("No", dialogClickListener).show();
			
		}
    	
    }

}
