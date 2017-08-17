package com.haski.swifto.service;

import com.haski.swifto.SwiftoApplication;

import android.app.IntentService;
import android.content.Intent;

public class HalfWalkIntentService extends IntentService {

	public HalfWalkIntentService() {
		super("SwiftoHalfWalkIntentService");
	}
	
	public HalfWalkIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		((SwiftoApplication) getApplication()).reactOnHalfWalk();
	}
}
