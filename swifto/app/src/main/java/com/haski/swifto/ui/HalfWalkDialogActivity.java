package com.haski.swifto.ui;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.WindowManager.LayoutParams;

import com.haski.swifto.R;

public class HalfWalkDialogActivity extends Activity {

	WakeLock mWakeLock;
	
	AlertDialog mDialog;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON | LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "SwiftoHalfWayWakeLock");
		mWakeLock.acquire();
		
		//prepare dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Swifto");
		builder.setMessage("Don't forget to take a photo!");
		builder.setIcon(R.drawable.dog_default);
		builder.setCancelable(false);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				mWakeLock.release();
				
				mDialog.dismiss();
				finish();
			}
		});
		
		mDialog = builder.create();
		
		
		//play sound and vibrate
		Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		MediaPlayer mediaPlayer = new MediaPlayer();
		
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{500, 1000, 500}, -1);
		
		try {
			mediaPlayer.setDataSource(getApplicationContext(), defaultRingtoneUri);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
			
			mediaPlayer.start();
			vibrator.vibrate(new long[]{500, 1000, 500, 1000, 500, 1000}, -1);
		} catch(IllegalArgumentException e) {
			
		} catch(SecurityException e) {
			
		} catch(IllegalStateException e) {
			
		} catch(IOException e) {
			
		} catch(Exception e) {
			
		}
		
		mDialog.show();
	}
}
