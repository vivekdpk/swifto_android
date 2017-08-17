package com.haski.swifto.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WalkMustBeStoppedBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent walkMustBeStoppedIntent = new Intent(context, WalkMustBeStoppedIntentService.class);
		context.startService(walkMustBeStoppedIntent);
	}
}
