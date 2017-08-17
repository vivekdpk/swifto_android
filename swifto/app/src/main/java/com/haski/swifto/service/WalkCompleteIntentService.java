package com.haski.swifto.service;

import android.app.IntentService;
import android.content.Intent;

import com.haski.swifto.SwiftoApplication;

public class WalkCompleteIntentService extends IntentService {

	public WalkCompleteIntentService() {
		super("WalkCompleteIntentService");
	}

	public WalkCompleteIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		((SwiftoApplication)getApplication()).walkComplete();
	}
}
