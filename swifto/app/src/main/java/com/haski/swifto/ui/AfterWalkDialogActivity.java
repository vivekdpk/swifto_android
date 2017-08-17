package com.haski.swifto.ui;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.interfaces.IDialogResultWaitable;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.util.AlertUtils;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

public class AfterWalkDialogActivity extends Activity {

	WakeLock mWakeLock;
	public static String isokpressed = "no";

	// AlertDialog mDialog;

	@SuppressWarnings("deprecation")
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(
				LayoutParams.FLAG_TURN_SCREEN_ON
						| LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP,
				"SwiftoAfterWalkIntentService");
		mWakeLock.acquire();
		Walk currentWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getStartedWalk();
		if(currentWalk==null)
		 return;
		String name = "";
		for (int i = 0; i < currentWalk.Dogs.size(); i++) {
			if (i == 0) {
				name = currentWalk.Dogs.get(i).Name;
			} else {
				name = name + " & " + currentWalk.Dogs.get(i).Name;
			}
		}

		AlertUtils.showYesNo(this, "Swifto", "Are you still walking " + name
				+ "?", "Yes", "No", new IDialogResultWaitable() {
			public void reactOnYes() {
				isokpressed = "no";
				((SwiftoApplication) getApplication()).debugAfterWalk(false);
				finish();
			}

			public void reactOnNo() {

				mWakeLock.release();

				isokpressed = "yes";
				finish();
				Intent walkMapIntent = new Intent(getApplicationContext(),
						WalkMapActivity.class);
				startActivity(walkMapIntent);
			}
		});

		// play sound and vibrate
		Uri defaultRingtoneUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		MediaPlayer mediaPlayer = new MediaPlayer();

		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(new long[] { 500, 1000, 500 }, -1);

		try {
			mediaPlayer.setDataSource(getApplicationContext(),
					defaultRingtoneUri);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});

			mediaPlayer.start();
			vibrator.vibrate(new long[] { 500, 1000, 500, 1000, 500, 1000 }, -1);
		} catch (IllegalArgumentException e) {

		} catch (SecurityException e) {

		} catch (IllegalStateException e) {

		} catch (IOException e) {

		} catch (Exception e) {

		}

		// mDialog.show();
	}
}
