package com.autominder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.autominder.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class NotificationService extends Service {

	private ArrayList<Reminder> absolutelyAllReminders;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		if(intent != null){
			Bundle extras = intent.getExtras();
			if(extras != null){
				absolutelyAllReminders = (ArrayList<Reminder>)extras.getSerializable("allReminders");
				if (absolutelyAllReminders!=null) {
					SimpleDateFormat df = new SimpleDateFormat("dd-mmm-YYYY");
					String hoy = df.format(new Date());
					ArrayList<Reminder> remindersForToday= new ArrayList<Reminder>();
					for (int i = 0; i < absolutelyAllReminders.size(); i++) {
						Reminder r = absolutelyAllReminders.get(i);
						if(df.format(r.getFecha()).equals(hoy))remindersForToday.add(r);
					}
					if (!remindersForToday.isEmpty()) {
						String remindersForTodayString = "";
						for (int i = 0; i < remindersForToday.size(); i++) {
							Reminder r = remindersForToday.get(i);
							remindersForTodayString=remindersForTodayString+r.getNombreManten()+" - "+r.getNombreCarro()+"\n";
						}
						Notification noti = new Notification.Builder(this)
						.setContentTitle(getApplicationContext().getText(R.string.you_have)+" "+remindersForToday.size()+" "+getApplicationContext().getText(R.string.reminders_due))
						.setContentText(remindersForTodayString+getApplicationContext().getText(R.string.maintenance_reminder_sub))
						.setSmallIcon(R.drawable.ic_launcher)
						.build();
						NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						notificationManager.notify(0, noti);
					}
				}
			}
		}else{
			Toast.makeText(getApplicationContext(), "null INDENT", Toast.LENGTH_SHORT).show();
		}
		
		return super.onStartCommand(intent, flags, startId);

	}

	public void onStart(Context context,Intent intent, int startId)
	{

	}


	public class MyLocalBinder extends Binder {
		NotificationService getService() {
			return NotificationService.this;
		}
	}

}
