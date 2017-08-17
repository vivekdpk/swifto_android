package com.haski.swifto.service;

import android.app.IntentService;
import android.content.Intent;

import com.haski.swifto.SwiftoApplication;

public class WalkMustBeStoppedIntentService extends IntentService {

	public WalkMustBeStoppedIntentService() {
		super("WalkMustBeStoppedIntentService");
	}

	public WalkMustBeStoppedIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		((SwiftoApplication)getApplication()).walkMustBeStopped();
	}
}
