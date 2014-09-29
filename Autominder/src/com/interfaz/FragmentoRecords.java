package com.interfaz;

import com.autominder.Principal;
import com.autominder.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentoRecords extends Fragment implements OnClickListener{

	ListView list;
	Principal p;
	
	public FragmentoRecords() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragmento_home, container, false);

		list = (ListView)v.findViewById(R.id.record_list);
		TextView empty = new TextView(getActivity());
		empty.setText("No hay records");
		list.setEmptyView(empty);

		// setting the nav drawer list adapter
		p = Principal.darInstancia(getActivity());
		RecordListAdapter adapter = new RecordListAdapter(getActivity(),p.getSelected().getRecords());
		list.setAdapter(adapter);
		
		Button b = (Button)v.findViewById(R.id.reg_mant);
		b.setOnClickListener(this);
		
		return v;

	}
	
	@Override
	public void onResume() {
		super.onResume();
		list.setAdapter(new RecordListAdapter(getActivity(),p.getSelected().getRecords()));

	}
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.reg_mant:
			Intent i = new Intent(getActivity(), newRecordActivity.class);
			startActivityForResult(i, 888);
			break;
		}
		
	}

	
	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 888){
			if(resultCode == getActivity().RESULT_OK){
				getActivity().getActionBar().setTitle(p.getSelected().getName());
				//la siguiente linea funciona
				//Toast.makeText(getActivity(), "Volvio al FragmentoRecords!", Toast.LENGTH_SHORT).show();
				((MainActivity)getActivity()).crearNotificationService();
			}
		}
	}
}
