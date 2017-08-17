package com.haski.swifto.service;

import com.haski.swifto.SwiftoApplication;

import android.app.IntentService;
import android.content.Intent;

public class AfterWalkIntentService extends IntentService {

	public AfterWalkIntentService() {
		super("SwiftoAfterWalkIntentService");
	}
	
	public AfterWalkIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		((SwiftoApplication) getApplication()).reactOnAfterWalk();
	}
}
