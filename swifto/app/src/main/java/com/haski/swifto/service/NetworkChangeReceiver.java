package com.haski.swifto.service;

import com.haski.swifto.SwiftoApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

	public  NetworkChangeReceiver(SwiftoApplication application)
	{
		this.application = application;
	}
	private boolean isConnected = false;
	private static final String LOG_TAG = "CheckNetworkStatus";

	private SwiftoApplication application;
	private boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						if (!isConnected) {
							Log.v(LOG_TAG, "Now you are connected to Internet!");

							isConnected = true;
							
							// do your processing here ---
							// if you need to post any data to the server or get
							// status
							// update from the server
						}
						return true;
					}
				}
			}
		}
		Log.v(LOG_TAG, "You are not connected to Internet!");
		isConnected = false;
		return false;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		Log.v(LOG_TAG, "Receieved notification about network status");
		isNetworkAvailable(context);
		application.networkChanged(isNetworkAvailable(context));
	}
}
