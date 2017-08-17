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
public class RestartReceiver extends BroadcastReceiver
{
    SwiftoApplication mApplication;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //SecureMessagesActivity.ToDoOnMobileSwitchOn();
        Log.d("In", "Switched On");
        SyslogUtils.logEvent(context,
                "Switched On event found ",
                EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
        mApplication= ((SwiftoApplication) context.getApplicationContext());

       // mApplication.isGpsShow(false);
        mApplication.isDeviceOff(false);
    }
}