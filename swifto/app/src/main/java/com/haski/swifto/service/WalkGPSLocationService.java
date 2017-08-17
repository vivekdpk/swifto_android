package com.haski.swifto.service;
/*
import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.requestQueue.SwiftoTaskService.SwiftoTaskBinder;
import com.haski.swifto.ui.WalkMapActivity;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;
*/
public class WalkGPSLocationService/* extends Service/* implements LocationListener*/{
/*
	@Override
	public IBinder onBind(Intent arg0) {
		//return mBinder;
		return null;
	}
	
	private final IBinder mBinder = new WalkGpsLocationServiceBinder();
	
	public class WalkGpsLocationServiceBinder extends Binder
	{
		public WalkGPSLocationService getService()
		{
			return WalkGPSLocationService.this;
		}
	}
	*/
	
	
	
	//private LocationProvider provCoarse;
	//private LocationProvider provFine;
	/*
	public static final String EXTRA_NOTIFICATION_MESSAGE = "com.haski.swifto.ExtraNotificationMessage";
	public static final String EXTRA_WALKER_ID = "com.haski.swifto.ExtraWalkerId";
	public static final String EXTRA_WALK_ID = "com.haski.swifto.ExtraWalkId";
*/
	//@Override
	//public void onCreate() {
	//	super.onCreate();
		
		//Log.d("WalkGPSService", "GPS Service.onCreate()");
		//SyslogUtils.logEvent(getApplicationContext(), "GPS Service.onCreate()", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		
		//locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		//provCoarse = locationManager.getProvider(locationManager.getBestProvider(createCoarseCriteria(), false));

		//provFine = locationManager.getProvider(locationManager.getBestProvider(createFineCriteria(), false));
		
		//long period = IS_TEN_MINUTES ? (1000 * 60 * 10) : (1000 * 5);
		
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, period, 10.0f, this);
		
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this);
		/*locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1.0f, new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}
			
			public void onProviderEnabled(String provider) {
				
			}
			
			public void onProviderDisabled(String provider) {
				
			}
			
			public void onLocationChanged(Location location) {
				if(lastLocation == null)
				{
					lastLocation = location;
				}
				
				//��������� �������� � ����� ���������
				GPS_NOTIFIABLE.locationChanged(location);
				
				locationManager.removeUpdates(this);
				//switchToGpsProvider();
			}
		});*/
		
		
		//NotificationManager nm = getNotificationManager();
	//}
	/*
	private String mWalkId;
	private String mWalkerId;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.d("WalkGPSService", "GPS Service.onStartCommand()");
		SyslogUtils.logEvent(getApplicationContext(), "GPS Service.onStartCommand()", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		
		String notMessage = intent.getStringExtra(WalkGPSLocationService.EXTRA_NOTIFICATION_MESSAGE);
		mWalkId = intent.getStringExtra(WalkGPSLocationService.EXTRA_WALK_ID);
		mWalkerId = intent.getStringExtra(WalkGPSLocationService.EXTRA_WALKER_ID);
		
		Notification startWalkNotification = new Notification(R.drawable.dog_default, notMessage, System.currentTimeMillis());

		Intent walkIntent = new Intent(getApplicationContext(), WalkMapActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, walkIntent, 0);
		startWalkNotification.setLatestEventInfo(getApplicationContext(), "Swifto", notMessage, pendingIntent);
		
		startForeground(SwiftoApplication.WALK_PROGRESS_NOTIFICATION_ID, startWalkNotification);
		
		//performLocationRequestUpdate();
		
		//performSetAlarm();
		
		return START_STICKY;
	}
	*/
	/*
	private void performLocationRequestUpdate()
	{
		long period = IS_TEN_MINUTES ? (1000 * 60 * 10) : (1000 * 5);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this);
	}
	
	private void performLocationReceive()
	{
		
	}
	
	private void performSetAlarm()
	{
		AlarmManager mgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		Intent i = new Intent(getApplicationContext(), GPSAlarmBroadcastReceiver.class);
		//PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
		PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
		
		mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, pi);
		//mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 5000, pi);
	}
	*/
	/*
	private void switchToGpsProvider()
	{
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1.0f, this);
	}
	 */
	/*
	@Override
	public void onDestroy() {
		super.onDestroy();
		SyslogUtils.logEvent(getApplicationContext(), "GPS Service.onDestroy()", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		/*
		if(locationManager != null)
		{
			locationManager.removeUpdates(this);
		}*//*
		stopForeground(true);
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		SyslogUtils.logEvent(getApplicationContext(), "GPS Service.onLowMemory()", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
	}
	*/
	/*
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		
		SyslogUtils.logEvent(getApplicationContext(), String.format("GPS Service.onTrimMemory(), level: ", level), EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
	}
	*/
	/*
	private LocationManager locationManager;
	*/
	/*public static IWalkGPSLocationNotifiable GPS_NOTIFIABLE;
	public static boolean IS_TEN_MINUTES = false;*/
	
	/*
	public static void setNotifiable(IWalkGPSLocationNotifiable notifiable)
	{
		GPS_NOTIFIABLE = notifiable;
	}
	*/

	//private Location lastLocation;
	
	/*
	public void onLocationChanged(Location location) {
		
		if(lastLocation == null)
		{
			//����� ������� ��������� ��������� ������� Alarm �� 30 ������
			//...� ���������� ���������� ��� � ����
			lastLocation = location;
		}
		
		if(GPS_NOTIFIABLE == null)
		{
			//??
			Log.d("GPS service", "Application Destroyed");
			Context appCtx = getApplicationContext();
			
			if(appCtx != null)
			{
				SyslogUtils.logEvent(appCtx, "GPS service - application destroyed", EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			}
		}
		else
		{
			//��������� �������� � ����� ���������
			GPS_NOTIFIABLE.locationChanged(location);
		}
	}

	public void onProviderDisabled(String provider) {
		SyslogUtils.logEvent(getApplicationContext(), "GPS service - provider disabled", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
	}

	public void onProviderEnabled(String provider) {
		SyslogUtils.logEvent(getApplicationContext(), "GPS service - provider enabled", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	*//*
	private static Criteria createCoarseCriteria()
	{
		Criteria c = new Criteria();
		
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		c.setAltitudeRequired(false);
		c.setBearingRequired(false);
		c.setSpeedRequired(false);
		c.setCostAllowed(true);
		c.setPowerRequirement(Criteria.POWER_HIGH);
		
		return c;
	}

	private static Criteria createFineCriteria()
	{
		Criteria c = new Criteria();
		
		c.setAccuracy(Criteria.ACCURACY_FINE);
		c.setAltitudeRequired(false);
		c.setBearingRequired(false);
		c.setSpeedRequired(false);
		c.setCostAllowed(true);
		c.setPowerRequirement(Criteria.POWER_HIGH);
		
		return c;
	}*/
}
