package com.haski.swifto.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

/**
 * Created by viku on 1/26/2016.
 */

public class GpsLocationReceiver extends BroadcastReceiver {
    SwiftoApplication mApplication;
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "intent.getAction() = "+ intent.getAction(),
//                Toast.LENGTH_SHORT).show();
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {


            mApplication= ((SwiftoApplication) context.getApplicationContext());

            SyslogUtils.logEvent(context,
                    "android.location.PROVIDERS_CHANGED now gps is  "+checkGPS(context),
                    EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);

            mApplication.isGpsShow(checkGPS(context));

        }
    }

    public boolean checkGPS(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;

        }
        return  false;

    }
}