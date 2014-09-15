package com.autominder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.autominder.R;
import com.interfaz.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class NotificationService extends Service {

	private Principal instancia;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		Toast.makeText(getApplicationContext(), "checking is there are reminders for today", Toast.LENGTH_SHORT).show();
		instancia = Principal.darInstancia(getApplicationContext());
		ArrayList<Reminder>absolutelyAllReminders = instancia.obtenerReminders();
		if (absolutelyAllReminders!=null) {
			ArrayList<Reminder> remindersForToday= new ArrayList<Reminder>();
			for (int i = 0; i < absolutelyAllReminders.size(); i++) {
				Reminder r = absolutelyAllReminders.get(i);
				if(r.getFecha().getTime()<new Date().getTime())remindersForToday.add(r);
			}
			if (!remindersForToday.isEmpty()) {
				String remindersForTodayString = "";
				for (int i = 0; i < remindersForToday.size(); i++) {
					Reminder r = remindersForToday.get(i);
					remindersForTodayString=remindersForTodayString+r.getNombreManten()+" - "+r.getNombreCarro()+"\n";
				}
				NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
					.setContentTitle(getApplicationContext().getText(R.string.you_have)+" "+remindersForToday.size()+" "+getApplicationContext().getText(R.string.reminders_due))
					.setContentText(remindersForTodayString+"\n"+getApplicationContext().getText(R.string.maintenance_reminder_sub))
					.setSmallIcon(R.drawable.ic_launcher);
				
				Intent notificationIntent = new Intent(this, MainActivity.class);
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
