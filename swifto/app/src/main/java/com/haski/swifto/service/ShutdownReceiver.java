package com.haski.swifto.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

/**
 * Created by viku on 1/28/2016.
 */
public class ShutdownReceiver extends BroadcastReceiver
{
    SwiftoApplication mApplication;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("In", "Switched Off");

        SyslogUtils.logEvent(context,
                "Switched Off event found ",
                EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);

        mApplication= ((SwiftoApplication) context.getApplicationContext());

        mApplication.isDeviceOff(true);
       // mApplication.isGpsShow(true);
    }
}