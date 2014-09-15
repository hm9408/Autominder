package com.interfaz;

import com.autominder.Principal;
import com.autominder.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentoRecords extends Fragment {

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

		return v;

	}
	
	@Override
	public void onResume() {
		super.onResume();
		list.setAdapter(new RecordListAdapter(getActivity(),p.getSelected().getRecords()));

	}
}
