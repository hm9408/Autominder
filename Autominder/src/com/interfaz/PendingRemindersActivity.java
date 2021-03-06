package com.interfaz;

import java.util.ArrayList;
import java.util.Date;

import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Reminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PendingRemindersActivity extends Activity implements OnClickListener {

	ListView list;
	Principal p;
	
	ArrayList<Reminder> remindersForToday;
	int selection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pending_reminders);
		
		getActionBar().setTitle("Autominder - Pendientes");
		
		selection = -1;
		
		list = (ListView)findViewById(R.id.pending_reminders_list);
		TextView empty = new TextView(this);
		empty.setText("No hay mantenimientos pendientes");
		list.setEmptyView(empty);

		// setting the nav drawer list adapter
		p = Principal.darInstancia(this);
		refresh();
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		
		Button b = (Button)findViewById(R.id.reg_mant_2);
		b.setEnabled(!remindersForToday.isEmpty());
		b.setOnClickListener(this);
	}
	
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent openMainActivity= new Intent(this, MainActivity.class);
		startActivity(openMainActivity);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reg_mant_2:
			if (selection != -1) {
				System.out.println("------------------SELECTION:"+selection);
				p.setSelectedName(remindersForToday.get(selection).getNombreCarro());
				Intent i = new Intent(this, newRecordActivity.class);
				startActivityForResult(i, 888);
			}else{
				if (!remindersForToday.isEmpty()) {
					p.setSelectedName(remindersForToday.get(0).getNombreCarro());
					Intent i = new Intent(this, newRecordActivity.class);
					startActivityForResult(i, 888);
				}
			}
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 888){//vuelve de newRecordActivity
			if(resultCode == RESULT_OK){
				setResult(RESULT_OK);
				finish();
			}
		}
	}



	public void refresh() {
		System.out.println("///////////////////////////HACE REFRESH");
		ArrayList<Reminder>absolutelyAllReminders = p.obtenerReminders();
		remindersForToday= new ArrayList<Reminder>();
		for (int i = 0; i < absolutelyAllReminders.size(); i++) {
			Reminder r = absolutelyAllReminders.get(i);
			if(r.getFecha().getTime()<new Date().getTime())remindersForToday.add(r);
		}
		
		ReminderListAdapter adapter = new ReminderListAdapter(this,remindersForToday, null, null);
		adapter.SetPendingRemindersAct(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
		        view.setSelected(true);
		        System.out.println("CLICK:"+position);
		        list.setSelection(position);
		        selection = position;
		    }
		    
		});
	}
}
