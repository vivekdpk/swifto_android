package com.haski.swifto.service;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

public class GPSAlarmBroadcastReceiver extends BroadcastReceiver {

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent gpsIntent = new Intent(context, GPSLocationIntentService.class);
		
		
		LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//LocationManager mgr = (LocationManager) context.getSystemService(Context.NETWORK_PROVIDER);
		
		PendingIntent pi = PendingIntent.getService(context, 0, gpsIntent, 0);
		
		mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, pi);
		//mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, pi);
		
		//TODO: wakelock?
	}
	
	
}
