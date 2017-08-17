package com.haski.swifto.service;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.haski.swifto.interfaces.IOneTimeGPSSearchNotifiable;

public class OneTimeGPSSearcher {

	IOneTimeGPSSearchNotifiable mResultNotifiable;
	
	LocationManager mLocationManager;
	
	Timer mTimer;
	
	/**
	 * 
	 * @param context
	 * @param resultNotifiable
	 * @param maxWaitTime - in milliseconds
	 * @return
	 */
	public boolean getLocation(Context context, IOneTimeGPSSearchNotifiable resultNotifiable, long maxWaitTime) {
		mResultNotifiable = resultNotifiable;
		
		if(mLocationManager == null) {
			mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}
		
		if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			resultNotifiable.locationReceiveFail("GPS is disabled!");
			return false;
		}
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		
		mTimer = new Timer();
		mTimer.schedule(new MaxSearchingTimerTask(), maxWaitTime);
		
		return true;
	}
	
	
	LocationListener mLocationListener = new LocationListener() {
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		public void onProviderEnabled(String provider) {
			
		}
		
		public void onProviderDisabled(String provider) {
			mTimer.cancel();
			mLocationManager.removeUpdates(this);
			mResultNotifiable.locationReceiveFail("GPS was turn off! Please, try again");
		}
		
		public void onLocationChanged(Location location) {
			mTimer.cancel();
			mLocationManager.removeUpdates(this);
			mResultNotifiable.locationReceiveSuccess(location);
		}
	};
	
	class MaxSearchingTimerTask extends TimerTask {
		@Override
		public void run() {
			mLocationManager.removeUpdates(mLocationListener);
			
			mResultNotifiable.locationReceiveFail("GPS searching timeout passed! Please, try again");
		}
	}
}
