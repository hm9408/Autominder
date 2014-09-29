package com.autominder;

import java.util.ArrayList;
import java.util.Date;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.interfaz.PendingRemindersActivity;

public class NotificationService extends Service {

	private Principal instancia;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		instancia = Principal.darInstancia(getApplicationContext());
		ArrayList<Reminder>absolutelyAllReminders = instancia.obtenerReminders();
		if (absolutelyAllReminders!=null) {
			ArrayList<Reminder> remindersForToday= new ArrayList<Reminder>();
			for (int i = 0; i < absolutelyAllReminders.size(); i++) {
				Reminder r = absolutelyAllReminders.get(i);
				if(r.getFecha().getTime()<new Date().getTime())remindersForToday.add(r);
			}
			if (!remindersForToday.isEmpty()) {
				
				NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
					.setContentTitle(getApplicationContext().getText(R.string.you_have)+" "+remindersForToday.size()+" "+getApplicationContext().getText(R.string.reminders_due))
					.setContentText(getApplicationContext().getText(R.string.maintenance_reminder_sub))
					.setSmallIcon(R.drawable.notif_icon)
					.setAutoCancel(true);
				
				Intent notificationIntent = new Intent(getApplicationContext(), PendingRemindersActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(contentIntent);
	
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.notify(0, builder.build());
			}
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
