package com.haski.swifto.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AfterWalkBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent halfWalkIntent = new Intent(context, AfterWalkIntentService.class);
		//PendingIntent pi = PendingIntent.getService(context, 0, halfWalkIntent, 0);
		
		context.startService(halfWalkIntent);
	}
}
