package com.haski.swifto.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WalkCompleteBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent walkCompleteIntent = new Intent(context, WalkCompleteIntentService.class);
		
		context.startService(walkCompleteIntent);
	}
}
