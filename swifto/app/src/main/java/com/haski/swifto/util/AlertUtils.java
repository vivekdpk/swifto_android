package com.haski.swifto.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.haski.swifto.interfaces.IDialogResultWailtableExt;
import com.haski.swifto.interfaces.IDialogResultWaitable;

public class AlertUtils {
	
	public static void showYesNo(Context context, String title, String message, String buttonTextYes, String buttonTextNo, final IDialogResultWaitable waiter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(buttonTextYes, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which)  {
				if(waiter != null) {
					waiter.reactOnYes();
				}
			}
		});
		
		builder.setNegativeButton(buttonTextNo, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(waiter != null) {
					waiter.reactOnNo();
				}
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public static void showYes(Context context, String title, String message, String buttonText, final IDialogResultWaitable waiter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(buttonText, new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if(waiter != null) {
					waiter.reactOnYes();
				}
			}
		});
		
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				if(waiter instanceof IDialogResultWailtableExt) {
					((IDialogResultWailtableExt) waiter).reactOnDismiss();
				}
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
