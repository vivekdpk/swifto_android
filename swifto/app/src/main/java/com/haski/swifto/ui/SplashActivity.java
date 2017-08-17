package com.haski.swifto.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.model.EnumWeekLoadStatus;
import com.haski.swifto.model.UploadPhotoStatus;
import com.haski.swifto.model.vo.GPSConfig;
import com.haski.swifto.model.vo.Walker;
import com.haski.swifto.model.vo.WalkerActiveRouteInfo;
import com.haski.swifto.model.vo.json.GetGpsConfigParser;
import com.haski.swifto.model.vo.json.GetWalkerActiveRoutesParser;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.server.ServerRequestAsynkTask;
import com.haski.swifto.server.SwiftoRequestBuilder;
import com.haski.swifto.service.NetworkChangeReceiver;
import com.haski.swifto.util.GSONUtils;
import com.haski.swifto.util.ServiceUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.TaskErrorChecker;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

public class SplashActivity extends Activity {

	private SwiftoApplication mApplication;
	private Walker mWalker;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mApplication = (SwiftoApplication) getApplication();

		setContentView(R.layout.activity_splash);



		//storeImage(".noMedia");
		deleteImage(".noMedia");
		
		mApplication.getPortionsLoader().clearCallbacks();

		if (SharedPreferencesHelper.getWalkIsInProcess(getApplicationContext())) {
			// TODO: maybe just open the map?
			Walk currStartedWalk = ((SwiftoApplication) getApplication()).getWalkGetter()
					.getStartedWalkWithoutOwnerAndDogs();

			if (currStartedWalk == null) {
				Toast.makeText(getApplicationContext(), "Need reloading", Toast.LENGTH_SHORT).show();

				String startedWalkUniqueId = SharedPreferencesHelper.getStartedWalkUniqueId(getApplicationContext());

				if (startedWalkUniqueId.equals(SharedPreferencesHelper.DEFAULT_STARTED_WALK_UNIQUE_ID)) {
					SyslogUtils.logEvent(getApplicationContext(),
							"Start splash when WALK_IN_PROCESS and no startedWalk in database and no UNIQUE_WALK_ID in SharedPreferences",
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);

					mApplication.stopWalkProgressServices();

					startLoginActivity();
				} else {
					// TODO: we can download walks for day using UNIQUE_WALK_ID
					SyslogUtils.logEvent(getApplicationContext(),
							"Start splash when WALK_IN_PROCESS and no startedWalk in database but we have UNIQUE_WALK_ID in SharedPreferences: "
									+ startedWalkUniqueId,
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);

					mApplication.stopWalkProgressServices();

					startLoginActivity();
				}
			} else {
				SharedPreferencesHelper.saveCurrentWalkId(getApplicationContext(), currStartedWalk._id);
				SyslogUtils.logEvent(getApplicationContext(),
						"Start splash when WALK_IN_PROCESS and there is startedWalk in application",
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

				mApplication.startWalkProgressServices();

				// TODO: what about stopping progress service if false??
				if (mApplication.initValuesOfWalkDependentOnTimers()) {
					startWalkMapActivity();
				}
			}

			return;
		}

		SyslogUtils.logEvent(getApplicationContext(), "Splash created - no walks in process",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		SharedPreferencesHelper.saveWeekLoadStatus(getApplicationContext(), EnumWeekLoadStatus.NOT_LOADED);
		SharedPreferencesHelper.saveTimeLastLoading(getApplicationContext(), System.currentTimeMillis());

		mWalker = mApplication.getWalker();

		UploadPhotoStatus.getInstance().setStatusNotUploading();

		// if walker isn't in database or there is no internet connection -
		// start LoginActivity
		if (mWalker == null || !ServiceUtils.isNetworkAvailable(this)) {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					startLoginActivity();
				}
			}, TIME_TO_SHOW_SPLASH);
		}
		// login using walker from database
		else {
			new LoginWalkerTask(getApplicationContext(), "", false).execute();
		}
	}

	private Context mContext;

	@Override
	protected void onStart() {
		super.onStart();
		mContext = this;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private static final long TIME_TO_SHOW_SPLASH = 1000;

	private class LoginWalkerTask extends ServerRequestAsynkTask {
		public LoginWalkerTask(Context ctx, String message, boolean showProgress) {
			super(ctx, message, showProgress);
		}

		@Override
		protected String doInBackground(String... params) {

			String request = SwiftoRequestBuilder.buildLoginRequest(mWalker.username, mWalker.password);

			return super.doInBackground(request);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			parseLoginResponse(result);
		}
	}

	private void parseLoginResponse(String result) {
		String error = TaskErrorChecker.getErrorInString(result);

		if (error != null) {
			reactOnError("Login from splash error: " + error , result);
		} else {
			SyslogUtils.logEvent(getApplicationContext(), "Logged from splash as " + mWalker.username,
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			Walker preparedWalker = GSONUtils.parseWalker(result);
			preparedWalker.username = mWalker.username;
			preparedWalker.password = mWalker.password;

			((SwiftoApplication) getApplication()).updateWalker(preparedWalker);

			SharedPreferencesHelper.saveWalkerId(getApplicationContext(), preparedWalker.id);

			getAndSaveGpsConfig();
		}
	}

	private void getAndSaveGpsConfig() {
		if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			String model = Build.MODEL;
			String osVersion = Build.VERSION.RELEASE;

			String request = SwiftoRequestBuilder.buildGetGpsConfigRequest(model, osVersion);

			new GetGpsConfigTask(mContext, "", false).execute(request);
		} else {
			SyslogUtils.logEvent(getApplicationContext(), "No internet connection on getting GPS configuration",
					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
			Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
			startLoginActivity();
		}
	}

	class GetGpsConfigTask extends ServerRequestAsynkTask {

		public GetGpsConfigTask(Context ctx, String message, boolean showProgress) {
			super(ctx, message, showProgress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			parseGetGpsConfigResponse(result);
		}
	}

	private void parseGetGpsConfigResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);

		if (error != null) {
			reactOnError(error , response);
		} else {
			GPSConfig config = new GetGpsConfigParser().parseGpsConfig(response);

			String toLog = String.format(Locale.getDefault(),
					"Received GPS Config: gpsSleepTime=%d, maxAttemptsToFindPoints=%d, threshold=%f",
					config.GpsSleepTime, config.MaxAttemptsToFindPoint, config.Threshold);

			SyslogUtils.logEvent(getApplicationContext(), toLog, EnumLogSeverity.INFORMATIONAL,
					EnumLogType.SERVER_TYPE);

			SharedPreferencesHelper.saveGpsSleepTime(getApplicationContext(), config.GpsSleepTime);
			SharedPreferencesHelper.saveMaxAttemptsToFindPoint(getApplicationContext(), config.MaxAttemptsToFindPoint);
			SharedPreferencesHelper.saveThreshold(getApplicationContext(), config.Threshold);
			SharedPreferencesHelper.saveS3Key(getApplicationContext(), config.s3Key);
			SharedPreferencesHelper.saveS3Secret(getApplicationContext(), config.s3Secret);

			checkWalkerActiveRoutes();
		}
	}

	private void checkWalkerActiveRoutes() {
		if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			String request = SwiftoRequestBuilder
					.buildGetWalkerActiveRoutes(SharedPreferencesHelper.getWalkerId(getApplicationContext()));

			new GetWalkerActiveRoutesTask(this, "Loading your active walks info...", false).execute(request);
		} else {
			SyslogUtils.logEvent(getApplicationContext(), "No internet connection on check walker active routes",
					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
			Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
			startLoginActivity();
		}
	}

	class GetWalkerActiveRoutesTask extends ServerRequestAsynkTask {
		public GetWalkerActiveRoutesTask(Context ctx, String message, boolean showProgress) {
			super(ctx, message, showProgress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			parseWalkerActiveRoutesResponse(result);
		}
	}

	private void parseWalkerActiveRoutesResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);

		if (error != null) {
			reactOnError("Loading info about active routes error:" + error , response);
		} else {
			parseRoutes(response);
		}
	}

	private void startLoginActivity() {
		finish();

		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(loginIntent);
	}

	/**
	 * Alert is showing here. It will not be shown if Activity.isFinishing() ==
	 * true
	 */
	private void parseRoutes(String source) {
		if (isFinishing()) {
			SyslogUtils.logEvent(getApplicationContext(),
					"Parsing routes after Splash activity had finished. Silently returning...", EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);
			return;
		}

		final ArrayList<WalkerActiveRouteInfo> routeInfos = new GetWalkerActiveRoutesParser()
				.parseGetWalkerActiveRoutes(source);

		// there are no active walks
		if (routeInfos.size() == 0) {
			SyslogUtils.logEvent(getApplicationContext(), "Loaded info about routes - there is no started walks",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
			((SwiftoApplication) getApplication()).startupCleaning();

			startScheduleActivity();
		} else {
			SyslogUtils.logEvent(getApplicationContext(),
					"Loaded info about routes - walker has more than one started walk", EnumLogSeverity.ERROR,
					EnumLogType.SERVER_TYPE);

			
			startScheduleActivity();
			
//			AlertUtils.showYes(mContext, "Information", "You have started walks!", "Go to schedule1",
//					new IDialogResultWailtableExt() {
//
//						public void reactOnYes() {
//							startScheduleActivity();
//						}
//
//						public void reactOnNo() {
//							// nothing
//						}
//
//						public void reactOnDismiss() {
//							startLoginActivity();
//						}
//					});
		}
	}

	private void reactOnError(String error, String result) {
		Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
		SyslogUtils.logEventWithResponce(getApplicationContext(), "Loading info about active routes error: " + error,
				EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE, result);
		startLoginActivity();
	}

	private void startScheduleActivity() {
		finish();

		Intent scheduleIntent = new Intent(getApplicationContext(), ScheduleActivity.class);
		scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(scheduleIntent);
	}

	private void startWalkMapActivity() {
		finish();

		Intent walkMapIntent = new Intent(getApplicationContext(), WalkMapActivity.class);
		startActivity(walkMapIntent);
	}



	private boolean storeImage( String filename) {
		// get path to external storage (SD card)

		String iconsStoragePath = Environment.getExternalStorageDirectory()
				+ "/swiftoImages/";

		File sdIconStorageDir = new File(iconsStoragePath);

		// create storage directories, if they don't exist
		if (!sdIconStorageDir.exists()) {
			sdIconStorageDir.mkdirs();
		}

		String filePath = sdIconStorageDir.toString() + "/" + filename;


		File sdIconStorageDir1 = new File(filePath);

		try
		{
			sdIconStorageDir1.createNewFile();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("Create File", "File exists?" + sdIconStorageDir1.exists());

		SyslogUtils.logEvent(getApplicationContext(),
				"File exists?" + sdIconStorageDir1.exists(), EnumLogSeverity.INFORMATIONAL,
				EnumLogType.INTERNAL_TYPE);

		return true;
	}



	private boolean deleteImage( String filename) {
		// get path to external storage (SD card)

		String iconsStoragePath = Environment.getExternalStorageDirectory()
				+ "/swiftoImages/";

		File sdIconStorageDir = new File(iconsStoragePath);

		// create storage directories, if they don't exist
		if (!sdIconStorageDir.exists()) {
			sdIconStorageDir.mkdirs();
		}

		String filePath = sdIconStorageDir.toString() + "/" + filename;


		File file = new File(filePath);

		try
		{
			if(!file.delete())

				SyslogUtils.logEvent(getApplicationContext(), "Failed to delete " + file,
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {

				@Override
				public void onScanCompleted(String path, Uri uri) {
					// TODO Auto-generated method stub

				}
			});
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return true;
	}
}
