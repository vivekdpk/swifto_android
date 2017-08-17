package com.haski.swifto.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	
	public static void showShort(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showLong(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
