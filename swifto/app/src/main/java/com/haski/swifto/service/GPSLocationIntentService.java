package com.haski.swifto.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.android.gms.maps.model.CameraPosition;
import com.haski.swifto.SwiftoApplication;

public class GPSLocationIntentService extends IntentService {

	public GPSLocationIntentService() {
		super("SwiftoGpsLocationIntentService");
	}
	
	public GPSLocationIntentService(String name) {
		super(name);
	}

	public void onCameraChange(CameraPosition arg0) {

	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		//extract data
		Bundle extras = intent.getExtras();

		if(extras != null) {
			Location location = (Location) extras.get(LocationManager.KEY_LOCATION_CHANGED);
			
			if(location != null) {
				SwiftoApplication mApplication = (SwiftoApplication) getApplication();
				
				if(mApplication.locationChanged(location)) {
					PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
		
					LocationManager mgr = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
					//LocationManager mgr = (LocationManager) getApplicationContext().getSystemService(LocationManager.NETWORK_PROVIDER);
					
					mgr.removeUpdates(pi);
					
					//start alarm for next GPS searching
					AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		
					Intent i = new Intent(getApplicationContext(), GPSAlarmBroadcastReceiver.class);
		
					PendingIntent pii = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
					
					//TODO: gpsSleepTimeout is here
					//alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + SharedPreferencesHelper.getGpsSleepTime(getApplicationContext()) * 1000, pii);
					alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10, pii);
				}
			}
		}
	}
}
