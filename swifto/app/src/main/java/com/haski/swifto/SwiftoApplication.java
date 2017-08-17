package com.haski.swifto;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.haski.swifto.database.SwiftoDatabaseAdapter;
import com.haski.swifto.database.TableColumns;
import com.haski.swifto.interfaces.IDialogResultWaitable;
import com.haski.swifto.interfaces.ILoadWalksContainingStartedWaitable;
import com.haski.swifto.interfaces.IStopWalkRequestNotifiable;
import com.haski.swifto.interfaces.WalksLoadedCallback;
import com.haski.swifto.model.EnumAsyncTasksErrors;
import com.haski.swifto.model.EnumPortionState;
import com.haski.swifto.model.EnumUploadPhotoStatus;
import com.haski.swifto.model.EnumWalkGPSPointSendStatus;
import com.haski.swifto.model.EnumWalkGPSPointType;
import com.haski.swifto.model.EnumWalkStatuses;
import com.haski.swifto.model.EnumWeekLoadStatus;
import com.haski.swifto.model.PerformedRequests;
import com.haski.swifto.model.StartedWalk;
import com.haski.swifto.model.UploadPhotoStatus;
import com.haski.swifto.model.vo.FeedMedicine;
import com.haski.swifto.model.vo.GeoPointExt;
import com.haski.swifto.model.vo.ImageFileHolder;
import com.haski.swifto.model.vo.Portion;
import com.haski.swifto.model.vo.StartedWalkExt;
import com.haski.swifto.model.vo.WalkGPSPoint;
import com.haski.swifto.model.vo.Walker;
import com.haski.swifto.model.vo.dog.Dog;
import com.haski.swifto.model.vo.dog.Services;
import com.haski.swifto.model.vo.json.GetWalkStateParser;
import com.haski.swifto.model.vo.json.GetWalksParser;
import com.haski.swifto.model.vo.walk.EnumWalkTypes;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.requestQueue.SwiftoTaskService;
import com.haski.swifto.requestQueue.tasks.BaseTask;
import com.haski.swifto.requestQueue.tasks.SendDataTask;
import com.haski.swifto.server.ServerRequestAsynkTask;
import com.haski.swifto.server.SwiftoRequestBuilder;
import com.haski.swifto.service.AfterWalkBroadcastReceiver;
import com.haski.swifto.service.GPSTracker;
import com.haski.swifto.service.GpsTrackerNew;
import com.haski.swifto.service.HalfWalkBroadcastReceiver;
import com.haski.swifto.service.IWalkGPSLocationNotifiable;
import com.haski.swifto.service.NetworkChangeReceiver;
import com.haski.swifto.service.WalkCompleteBroadcastReceiver;
import com.haski.swifto.ui.AfterWalkDialogActivity;
import com.haski.swifto.ui.HalfWalkDialogActivity;
import com.haski.swifto.ui.ScheduleActivity;
import com.haski.swifto.ui.WalkMapActivity;
import com.haski.swifto.util.AlertUtils;
import com.haski.swifto.util.CalendarUtils;
import com.haski.swifto.util.MD5Utils;
import com.haski.swifto.util.MemoryUtil;
import com.haski.swifto.util.ServiceUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.TaskErrorChecker;
import com.haski.swifto.util.ToastUtils;
import com.haski.swifto.util.WalkUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;
import com.haski.swifto.util.photoUploadService.UploadPhotoService;
import com.haski.swifto.util.photoUploadService.UploadPhotoTask;
import com.haski.swifto.util.photoUploadService.UploadVideoTask;


import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.CopyOnWriteArrayList;


/*import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;*/
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.annotation.GuardedBy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

//@ReportsCrashes(formKey = "dGY1QUxrdldHNnlGV0JkQ2NHT1JxdVE6MQ")
//@ReportsCrashes(formKey = "dGctdXVEdHg5b0lmaFRNaTBtX0N6V2c6MQ",
//@ReportsCrashes(formKey = "dE1tMk8yMWxuQ0lXUzdqOVJsMU5IbkE6MQ",
//formKey = "dGV4NTVkVklUY2J2ZjRwWVRYc1VxNGc6MQ",
/*@ReportsCrashes(formKey = "439c2c195bf7438f8f5e3f978d360063", formUri = "http://collector.tracepot.com/e15dcad9",// 1.2
mode = ReportingInteractionMode.SILENT*/
/*
 * resDialogCommentPrompt = R.string.acra_dialog_comment_prompt,
 * resDialogOkToast = R.string.acra_dialog_ok_toast, resDialogTitle =
 * R.string.acra_dialog_title, resDialogText = R.string.acra_dialog_text,
 * resToastText = R.string.acra_toast_text
 */
//)

@ReportsCrashes(formKey = "439c2c195bf7438f8f5e3f978d360063",
		 formUri = "http://collector.tracepot.com/e15dcad9",//1.2
		mode = ReportingInteractionMode.SILENT)
public class SwiftoApplication extends Application implements
		IWalkGPSLocationNotifiable {

	//GPSTracker gps;
	//GpsTrackerNew gps;

	private NetworkChangeReceiver receiver;

	public boolean isDetailLog = false;

	public class PointsGetter {
		public ArrayList<WalkGPSPoint> getAllRoutePointsForWalk(String walkId) {
			ArrayList<WalkGPSPoint> toRet = getDbAdapter()
					.selectWalkGpsPointsForWalkByType(walkId,
							EnumWalkGPSPointType.ROUTE);

			return toRet;
		}

		public WalkGPSPoint getLastRoutePoint(String walkId) {
			WalkGPSPoint toRet = getDbAdapter().selectLastRouteWalkGpsPoint(
					walkId);

			return toRet;
		}

		public ArrayList<WalkGPSPoint> getAllFailedRoutePointsForWalk(
				String walkId) {
			ArrayList<WalkGPSPoint> toRet = getDbAdapter()
					.selectWalkGpsPointsForWalkByTypeAndStatus(walkId,
							EnumWalkGPSPointType.ROUTE,
							EnumWalkGPSPointSendStatus.NOT_SENT);

			return toRet;
		}

		public void updateAllRoutePointsToSent(String walkId) {
			getDbAdapter().updateWalkGpsPointsStatusByWalkId(
					EnumWalkGPSPointSendStatus.SENT, walkId);
		}

		public ArrayList<WalkGPSPoint> getPoopPointsForWalk(String walkId) {
			ArrayList<WalkGPSPoint> toRet = getDbAdapter()
					.selectWalkGpsPointsForWalkByType(walkId,
							EnumWalkGPSPointType.POOP);

			return toRet;
		}

		public ArrayList<WalkGPSPoint> getMeetPointsForWalk(String walkId) {
			ArrayList<WalkGPSPoint> toRet = getDbAdapter()
					.selectWalkGpsPointsForWalkByType(walkId,
							EnumWalkGPSPointType.MEET);

			return toRet;
		}

		public ArrayList<WalkGPSPoint> getUnsentMeetAndPooPointsForWalk(
				String walkId) {
			ArrayList<WalkGPSPoint> toRet = getDbAdapter()
					.selectUnsentMeetAndPoopPointsOrderByTimestamp(walkId);

			return toRet;
		}

		public void updateMeetOrPooPointToSentByMd5(String md5) {
			getDbAdapter().updateWalkGpsPointStateByMd5(
					EnumWalkGPSPointSendStatus.SENT, md5);
		}
	}

	/** Supply walk "portions" - from database or from server */
	public class PortionsLoader {

		public void reloadAllPortions() {
			getDbAdapter().deleteWalksOwnersDogs();
			getDbAdapter().deleteAllPortions();
		}

		private final ConcurrentHashMap<String, WalksLoadedCallback> mapCallbacks = new ConcurrentHashMap<String, WalksLoadedCallback>();

		public void clearCallbacks() {
			mapCallbacks.clear();
		}

		/***/
		public Portion loadWalksForPortion(String portionId,
				WalksLoadedCallback callback, boolean showProgress,
				Context contextForProgress) {
			Portion portion = getDbAdapter().getPortion(portionId);

			// ...there is no portion in database - need to load walks for it
			// from server
			if (portion == null) {
				if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
					WalksLoadedCallback callbackForThisKey = mapCallbacks
							.get(portionId);

					if (callbackForThisKey == null) {
						mapCallbacks.putIfAbsent(portionId, callback);
						loadWalks(portionId, showProgress, contextForProgress);
					} else {
						mapCallbacks.replace(portionId, callback);
					}
				} else {
					SyslogUtils.logEvent(getApplicationContext(),
							"No internet connection on walks loading",
							EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
					ToastUtils.showShort(getApplicationContext(),
							"No internet connection");

					callback.interrupt();
				}
			}

			// ...portion signals that there is no walks for it
			else if (portion.State.equals(EnumPortionState.EMPTY)) {
				// signal that portion has no walks
				// do nothing, portion will be returned later
			}

			// ...portion has walks
			else if (portion.State.equals(EnumPortionState.HAS_WALKS)) {
				// select walks by uniqueId
				ArrayList<Walk> walks = getDbAdapter()
						.getWalksByIdentifierStartEnd(portionId);

				if (walks != null && walks.size() != 0) {
					// return walks to callback
					callback.walksLoaded(walks, portionId);
				} else {
					// some error!
					SyslogUtils.logEvent(getApplicationContext(),
							"Portion has walks but they not in database",
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
				}
			}

			return portion;
		}

		public Portion getPortionById(String portionId) {
			Portion toRet = getDbAdapter().getPortion(portionId);

			return toRet;
		}

		public ArrayList<Walk> getWalksFromDb(Context context,
				SwiftoDatabaseAdapter dbAdapter, String portionId) {
			ArrayList<Walk> toRet = getDbAdapter()
					.getWalksByIdentifierStartEnd(portionId);
			return toRet;
		}

		public void loadWalks(String portionId, boolean showProgress,
				Context contextForProgress) {
			String[] times = portionId.split("-");

			if (times.length == 2) {
				long timeStart = Long.parseLong(times[0]);
				long timeEnd = Long.parseLong(times[1]);

				String request = SwiftoRequestBuilder.buildGetWalks(
						SharedPreferencesHelper
								.getWalkerId(getApplicationContext()),
						timeStart, timeEnd);
				saveFile(request, "walk_request.txt");
				if (showProgress) {
					new GetWalksAsynkTask(portionId, contextForProgress,
							"Loading walks...", true).execute(request);
				} else {
					new GetWalksAsynkTask(portionId, getApplicationContext(),
							"", false).execute(request);
				}
			} else {
				SyslogUtils.logEvent(getApplicationContext(),
						"Error parsing portionId on loadWalks: " + portionId,
						EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			}
		}

		class GetWalksAsynkTask extends ServerRequestAsynkTask {
			private String mPortionId;

			public GetWalksAsynkTask(String portionId, Context ctx,
					String message, boolean showProgress) {
				super(ctx, message, showProgress);

				mPortionId = portionId;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				parseServerResponse(result, mPortionId, mContext);
			}
		}

		public void saveFile(String responce, String filename) {

			// String filename = "filename.txt";
			//return;
			File file = new File(Environment.getExternalStorageDirectory(),
					filename);
			FileOutputStream fos;
			byte[] data = new String(responce).getBytes();
			try {
				fos = new FileOutputStream(file);
				fos.write(data);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			}
		}

		private void parseServerResponse(String response, String portionId,
				Context context) {
			Log.i("walk response = ", response);
			saveFile(response, portionId+"walk_response.txt");
			String error = TaskErrorChecker.getErrorInString(response);

			if (error != null) {
				try {
					ToastUtils.showShort(context, error);
					SyslogUtils.logEvent(context, "Walks load for portion "
							+ portionId + " error: " + error,
							EnumLogSeverity.WARNING, EnumLogType.SERVER_TYPE);

					mapCallbacks.get(portionId).interrupt();
					mapCallbacks.remove(portionId);
				} catch (Exception e) {
					// context may be lost
					SyslogUtils
							.logEvent(
									context,
									String.format(
											Locale.getDefault(),
											"Error when informing about walk loading error: %s",
											e.toString()),
									EnumLogSeverity.ERROR,
									EnumLogType.INTERNAL_TYPE);
				}
			} else {
				SharedPreferencesHelper.saveTimeLastLoading(
						getApplicationContext(), System.currentTimeMillis());
				// saveFile(response);
				ArrayList<Walk> loadedWalks = new GetWalksParser()
						.parseGetWalks(response);
				//Log.d("nkp comm", loadedWalks.get(0).Dogs.get(0).Comments.toString());

				try {
					if (loadedWalks.size() == 0) {
						SyslogUtils.logEvent(context, String.format(
								Locale.getDefault(),
								"Loaded zero walks for %s, response: %s",
								datesFromPortionId(portionId), response),
								EnumLogSeverity.INFORMATIONAL,
								EnumLogType.SERVER_TYPE);
						// add portion with empty walks
						getDbAdapter().addPortion(portionId,
								EnumPortionState.EMPTY, false);
					} else {
						SyslogUtils.logEvent(context, String.format(
								Locale.getDefault(), "Loaded %d walks for %s",
								loadedWalks.size(),
								datesFromPortionId(portionId)),
								EnumLogSeverity.INFORMATIONAL,
								EnumLogType.SERVER_TYPE);
						getDbAdapter().addPortion(portionId,
								EnumPortionState.HAS_WALKS, false);

						for (Walk walk : loadedWalks) {
							walk.setIdentifierStartEndMillis(portionId);
							getDbAdapter().insertWalk(walk , context);
						}
					}

					mapCallbacks.get(portionId).walksLoaded(loadedWalks,
							portionId);
					mapCallbacks.remove(portionId);
				} catch (Exception e) {
					// context may be lost
					SyslogUtils.logEvent(context, String.format(
							Locale.getDefault(),
							"Error inserting portion of walks to database: %s",
							e.toString()), EnumLogSeverity.ERROR,
							EnumLogType.INTERNAL_TYPE);
				}
			}
		}
	}

	private static String datesFromPortionId(String portionId) {
		String[] splitted = portionId.split("-");

		if (splitted.length != 2) {
			return "";
		} else {
			long ms = Long.parseLong(splitted[0]) * 1000;
			SimpleDateFormat format = new SimpleDateFormat("dd MMM",
					Locale.getDefault());
			String toRet = format.format(new Date(ms));

			return toRet;
		}
	}

	public class WalkGetter {
		public Walk getWalkById(Context context, String walkId) {
			Walk toRet = getDbAdapter().getWalkByID(walkId);

			return toRet;
		}

		public Walk getCurrentWalk() {
			String currWalkId = SharedPreferencesHelper
					.getCurrentWalkId(getApplicationContext());
			Log.i("currWalkId 2", "" + currWalkId);
			Walk toRet = getDbAdapter().getWalkByID(currWalkId);
			Log.i("toRet 3", "" + toRet.Dogs.toString());
			return toRet;
		}

		public Walk getCurrentWalkWithoutOwnerAndDogs() {
			String currWalkId = SharedPreferencesHelper
					.getCurrentWalkId(getApplicationContext());
			Walk toRet = getDbAdapter().getWalkByIDWithoutOwnerAndDogs(
					currWalkId);

			return toRet;
		}

		public Walk getStartedWalk() {
			String startedWalkId = SharedPreferencesHelper
					.getStartedWalkId(getApplicationContext());
			Walk toRet = getDbAdapter().getWalkByID(startedWalkId);

			return toRet;
		}

		public Walk getStartedWalkWithoutOwnerAndDogs() {
			String startedWalkId = SharedPreferencesHelper
					.getStartedWalkId(getApplicationContext());
			Walk toRet = getDbAdapter().getWalkByIDWithoutOwnerAndDogs(
					startedWalkId);

			return toRet;
		}
	}

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
	private static Context sContext;
	@Override
	public void onCreate() {
		// TODO: ACRA
		ACRA.init(this);

		System.setProperty("org.joda.time.DateTimeZone.Provider",
				"org.joda.time.tz.UTCProvider");

        MultiDex.install(this);
		sContext = getApplicationContext();
		super.onCreate();

		Intent serviceIntent = new Intent(this, SwiftoTaskService.class);
		bindService(serviceIntent, taskServiceConnection,
				Context.BIND_AUTO_CREATE);

		Intent uploadPhotoServiceIntent = new Intent(this,
				UploadPhotoService.class);
		bindService(uploadPhotoServiceIntent, uploadPhotoServiceConnection,
				Context.BIND_AUTO_CREATE);

		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new NetworkChangeReceiver(SwiftoApplication.this);
		registerReceiver(receiver, filter);

		// init database if it hasn't been inited after installation
		getDbAdapter();

		FileInputStream fos = null;

		// create file for saving log entries, if it hasn't been created before
		// installation
		try {
			fos = openFileInput("log.json");
		} catch (FileNotFoundException e) {
			new File(getFilesDir(), "log.json");
		}

		if (fos != null) {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Context getContext() {
		return sContext;
	}
	@Override
	public void onLowMemory() {
		super.onLowMemory();

		SyslogUtils.logEvent(getContext(),
				"SwiftoApp.onLowMemory()", EnumLogSeverity.WARNING,
				EnumLogType.HARDWARE_TYPE);

		AlertUtils.showYes(getContext(), "Low Memory", "please free some memory to perform batter this app on your mobile", "OK",
				new IDialogResultWaitable() {
					public void reactOnYes() {


					}

					public void reactOnNo() {

					}
				});



	}

	public void startupCleaning() {
		getDbAdapter().deleteWalks();
		getDbAdapter().deleteDogs();
		getDbAdapter().deleteComments();
		getDbAdapter().deleteWalks();
		getDbAdapter().deleteAllPortions();
		getDbAdapter().deleteAllMonthsInfo();

		SharedPreferencesHelper.saveWeekLoadStatus(getApplicationContext(),
				EnumWeekLoadStatus.NOT_LOADED);
		SharedPreferencesHelper.deleteSelectedDay(getApplicationContext());
		SharedPreferencesHelper.deleteSelectedMonth(getApplicationContext());
		SharedPreferencesHelper.deleteSelectedYear(getApplicationContext());
		SharedPreferencesHelper
				.deleteSelectedFragmentIndexOnSchedule(getApplicationContext());
	}

	// ------------------------------------------------------
	//
	// Helpers
	//
	// ----------------------------

	@GuardedBy("this")
	private volatile SwiftoDatabaseAdapter mDbAdapter;

	public synchronized SwiftoDatabaseAdapter getDbAdapter() {
		if (mDbAdapter == null) {
			mDbAdapter = new SwiftoDatabaseAdapter(getApplicationContext());
		}

		if (!mDbAdapter.isOpen()) {
			mDbAdapter.open(getApplicationContext());
		}

		return mDbAdapter;
	}

	private final WalkGetter mWalkGetter = new WalkGetter();

	public WalkGetter getWalkGetter() {
		return mWalkGetter;
	}

	private final PortionsLoader mPortionsLoader = new PortionsLoader();

	public PortionsLoader getPortionsLoader() {
		return mPortionsLoader;
	}

	private final PointsGetter mPointsGetter = new PointsGetter();

	public PointsGetter getPointsGetter() {
		return mPointsGetter;
	}

	private NotificationManager mNotificationManager;

	private NotificationManager getNotificationManager() {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}

		return mNotificationManager;
	}

	// ---------------------------------------------------------------
	//
	// State variables
	//
	// -------------------------

	private Walker mWalker;

	public Walker getWalker() {
		if (mWalker == null) {
			mWalker = getDbAdapter().selectWalker();
		}

		return mWalker;
	}

	public void updateWalker(Walker freshWalker) {
		mWalker = freshWalker;

		getDbAdapter().updateWalker(mWalker);
	}

	public void deleteWalker() {
		getDbAdapter().deleteWalkers();
		mWalker = null;
	}

	private Uri mImageUri;

	public void cleanImageUri()
	{
		mImageUri = null;
	}

	public void setImageUri(Uri value) {

		mImageUri = value;
	}

	public Uri getImageUri() {
		return mImageUri;
	}

	private Uri mVedioUri;

	public void setVedioUri(Uri value) {
		mVedioUri = value;
	}

	public Uri getVedioUri() {
		return mVedioUri;
	}

	public void clearWalksForMonths() {
		getDbAdapter().deleteAllMonthsInfo();
	}

	// -------------------------------------------------------------------------
	//
	// Walks
	//
	// --------------------------

	ILoadWalksContainingStartedWaitable mLoadWalksContainStartedWaitable = null;

	public void loadWalksForDayContainingStartedWalk(Context context,
			ILoadWalksContainingStartedWaitable waitable) {
		mLoadWalksContainStartedWaitable = waitable;

		String walkUniqueId = SharedPreferencesHelper
				.getStartedWalkUniqueId(getApplicationContext());
		String[] times = walkUniqueId.split("-");
		double midnight = Double.parseDouble(times[0]);
		double twentyThree = Double.parseDouble(times[1]);
		String walksRequest = SwiftoRequestBuilder.buildGetWalks(
				SharedPreferencesHelper.getWalkerId(getApplicationContext()),
				midnight, twentyThree);

		new GetWalksAsynkTask(context, "Loading started walk...", true)
				.execute(walksRequest);
	}

	/** Use ONLY for started walk, please */
	class GetWalksAsynkTask extends ServerRequestAsynkTask {
		public GetWalksAsynkTask(Context ctx, String message, boolean showResult) {
			super(ctx, message, showResult);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			parseServerResponse(result);
		}
	}

	public void saveFile(String txt, String filename) {

		try {
			File myFile = new File("/sdcard/" + filename + ".txt");
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(txt);
			myOutWriter.close();
			fOut.close();
			// Toast.makeText(getApplicationContext(),
			// "Done writing SD 'mysdfile.txt'",
			// Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT)
			// .show();
		}
	}

	private void parseServerResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);

		if (error != null) {
			if (mLoadWalksContainStartedWaitable != null) {
				mLoadWalksContainStartedWaitable
						.onLoadWalksContainingStartedFailure(error);
			}
		} else {
			// saveFile(response);
			ArrayList<Walk> loadedWalks = new GetWalksParser()
					.parseGetWalks(response);

			if (loadedWalks.size() == 0) {
				// CANNOT GET HERE, because we have been loading walks for day,
				// that CONTAINS started walk - he cannot be walk-less
				SyslogUtils
						.logEvent(
								getApplicationContext(),
								"TERRIBLE! Load walks for day which contains started walk - no walks in response! Started Walk ID: "
										+ SharedPreferencesHelper
												.getStartedWalkId(getApplicationContext()),
								EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
			} else {
				String walkUniqueId = SharedPreferencesHelper
						.getStartedWalkUniqueId(getApplicationContext());

				clearWalksForMonths();
				getDbAdapter().deleteWalksOwnersDogs();
				getDbAdapter().deleteAllPortions();
				getDbAdapter().addPortion(walkUniqueId,
						EnumPortionState.HAS_WALKS, false);

				for (Walk walk : loadedWalks) {
					walk.setIdentifierStartEndMillis(walkUniqueId);
					getDbAdapter().insertWalk(walk , getApplicationContext());
				}

				try {
					mLoadWalksContainStartedWaitable
							.onLoadWalksContainingStartedSuccess();
				} catch (Exception e) {
				}
			}
		}
	}

	public void continueWalk(StartedWalk startedWalk, Walk walk) {
		SharedPreferencesHelper.setStartedWalkId(walk._id,
				getApplicationContext());
		SharedPreferencesHelper.setStartedWalkUniqueId(
				walk.getIdentifierStartEndMillis(), getApplicationContext());

		StartedWalkExt startedWalkExt = getDbAdapter().getStartedWalkForId(
				walk._id);

		if (startedWalkExt == null) {
			startedWalkExt = new StartedWalkExt();
			startedWalkExt.makeDefault();
			startedWalkExt.StartedTime = startedWalk.getStartTime();
			startedWalkExt.WalkId = walk._id;

			if (walk.WalkType.equals(EnumWalkTypes.FREE)) {
				startedWalkExt.PhotoSkipped = true;
				startedWalkExt.PhotoUploaded = true;
				startedWalkExt.PhotoUploadTriedOnce = true;
			}

			getDbAdapter().addStartedWalk(startedWalkExt);
		}
	}

	public PowerManager.WakeLock wl;
	private static String WAKE_LOCK_TAG = "SwiftoWakeLockTag";

	/**
	 * 
	 * @param createStartedWalk

	 */
	public void startCurrentWalk(boolean createStartedWalk) {
		// create and aquire a wakelock
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
		wl.acquire();

		// write current walk data to shred preferences
		final Walk currentWalk = getWalkGetter()
				.getCurrentWalkWithoutOwnerAndDogs();

		SyslogUtils.logEvent(getApplicationContext(),
				"Starting current walk, id: " + currentWalk._id,
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		SharedPreferencesHelper.setStartedWalkId(currentWalk._id, this);
		SharedPreferencesHelper.setStartedWalkUniqueId(
				currentWalk.getIdentifierStartEndMillis(), this);
		SharedPreferencesHelper.setWalkInProcess(getApplicationContext(), true);

		// change walk statur to STARTED in database
		updateWalkStatusInDatabase(currentWalk._id, WalkUtils.STATUS_STARTED);

		if (createStartedWalk) {
			// TODO: check, are there startedWalk in database (it can be
			// received in get_walker_active_routes)
			StartedWalkExt startedWalkExt = getDbAdapter().getStartedWalkForId(
					currentWalk._id);

			if (startedWalkExt == null) {
				startedWalkExt = new StartedWalkExt();
				startedWalkExt.makeDefault();
				startedWalkExt.StartedTime = System.currentTimeMillis();
				startedWalkExt.WalkId = currentWalk._id;

				if (currentWalk.WalkType.equals(EnumWalkTypes.FREE)) {
					startedWalkExt.PhotoSkipped = true;
					startedWalkExt.PhotoUploaded = true;
					startedWalkExt.PhotoUploadTriedOnce = true;
				}
			} else {
				// do nothing
			}

			getDbAdapter().addStartedWalk(startedWalkExt);
		}

		//nkp
		if(!currentWalk.WalkType.equals("overnight")){
			AlarmManager al = (AlarmManager) getSystemService(ALARM_SERVICE);
			Intent halfWalkIntent = new Intent(getApplicationContext(),
					HalfWalkBroadcastReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
					0, halfWalkIntent, 0);
			al.cancel(pi);
			al.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + currentWalk.Duration * 1000 / 2,
					pi);
		}


		//debugAfterWalk(true);
		startWalkProgressServices();
		initValuesOfWalkDependentOnTimers();
	}

	public void debugAfterWalk(boolean isFirstTime) {
		Walk currentWalk = getWalkGetter().getCurrentWalkWithoutOwnerAndDogs();
		AlarmManager al1 = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent afterWalkIntent = new Intent(getApplicationContext(),
				AfterWalkBroadcastReceiver.class);
		PendingIntent pi1 = PendingIntent.getBroadcast(getApplicationContext(),
				0, afterWalkIntent, 0);
		al1.cancel(pi1);
		if (isFirstTime) {
			al1.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime()
							+ (currentWalk.Duration * 1000) + (1000 * 60 * 5),
					pi1);
		} else {
			al1.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + (1000 * 60 * 5), pi1);
		}
	}

	public void cancelHalfWalkAlarm() {

		AlarmManager al = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent halfWalkIntent = new Intent(getApplicationContext(),
				HalfWalkBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
				0, halfWalkIntent, 0);

		al.cancel(pi);
	}

	public void cancelAfterWalkAlarm() {
		AlarmManager al = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent halfWalkIntent = new Intent(getApplicationContext(),
				AfterWalkBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
				0, halfWalkIntent, 0);

		al.cancel(pi);
	}

	// TODO: change in release
	// private static final long PERIOD_WALK_MUST_BE_STOPPED = 1000 * 1 *
	// 15;//FIFTEEN SECONDS
	// private static final long PERIOD_WALK_MUST_BE_STOPPED = 1000 * 1 * 60 *
	// 20;//TWENTY MINUTES
	private static final long PERIOD_WALK_MUST_BE_STOPPED = 1000 * 1 * 60 * 60;// ONE
																				// HOUR

	// TODO: refactor
	public boolean initValuesOfWalkDependentOnTimers() {
		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);
		final Walk startedWalk = getWalkGetter()
				.getStartedWalkWithoutOwnerAndDogs();
		StartedWalkExt startedWalkExt = getDbAdapter().getStartedWalkForId(
				startedWalkId);

		if (startedWalk == null) {
			SyslogUtils.logEvent(this,
					"Critical error: there is no related walk in database, walkId: "
							+ startedWalkId, EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);
			Toast.makeText(this,
					"Error! (no walk info in database). Please, send the log!",
					Toast.LENGTH_SHORT).show();
			stopWalkProgressServices();

			Intent scheduleIntent = new Intent(getApplicationContext(),
					ScheduleActivity.class);
			scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(scheduleIntent);

			return false;
		}

		if (startedWalkExt == null) {
			SyslogUtils.logEvent(this,
					"Critical error: startedWalkExt is null, walkId: "
							+ startedWalkId, EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);
			Toast.makeText(
					this,
					"Error! (no started walk info in database). Please, send the log!",
					Toast.LENGTH_SHORT).show();
			stopWalkProgressServices();

			Intent scheduleIntent = new Intent(getApplicationContext(),
					ScheduleActivity.class);
			scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(scheduleIntent);

			return false;
		}

		long currentTimeMillis = System.currentTimeMillis();

		// TODO: change in release
		// long walkDurationInMillis = 1 * 1000 * 10;
		// long walkDurationInMillis = 1 * 1000 * 60 * 60;
		long walkDurationInMillis = startedWalk.Duration * 1000;
		long timeWhenWalkWillBeCompleted;

		//nkp
		Calendar cal = CalendarUtils._6AM_AFTER_DAY_FROM_SECOND(startedWalk.StartTime);
		long day_6am_time = cal.getTimeInMillis();

		//long timeWhenWalkWillBeCompleted = startedWalkExt.StartedTime
				//+ walkDurationInMillis;
		long timeLeft;
		if(mResumedWalkMapActivity!=null && mResumedWalkMapActivity.minPassed>0)
		{
			// need to open this comment when gps off and device off effect on
//			timeWhenWalkWillBeCompleted = walkDurationInMillis - (mResumedWalkMapActivity.minPassed*60*1000);
//			timeLeft = timeWhenWalkWillBeCompleted;
			//Log.d("resumapactivity check", "is calling run");

			// need to close this code when gps off and device off effect on
			timeWhenWalkWillBeCompleted = startedWalkExt.StartedTime
					+ walkDurationInMillis;
			if(startedWalk.WalkType.equals("overnight"))
				timeLeft = day_6am_time - System.currentTimeMillis();
			else
				timeLeft = timeWhenWalkWillBeCompleted - currentTimeMillis;

//			SyslogUtils
//					.logEvent(
//							getApplicationContext(),
//							String.format(
//									"timeLeft  in old  in If = " + (timeLeft/60000) + " minPassed = "+ mResumedWalkMapActivity.minPassed),
//							EnumLogSeverity.INFORMATIONAL,
//							EnumLogType.INTERNAL_TYPE);


			SyslogUtils
					.logEvent(
							getApplicationContext(),
							String.format(
									"timeLeft  in old  in if = " + (timeLeft/60000) + " timeWhenWalkWillBeCompleted = "+ timeWhenWalkWillBeCompleted),
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);

			SyslogUtils
					.logEvent(
							getApplicationContext(),
							String.format(
									"timeLeft  in old  in if = " + (timeLeft/60000) + " startedWalkExt.StartedTime = "+ startedWalkExt.StartedTime
											+ "   walkDurationInMillis = "+ walkDurationInMillis),
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);
		}
		else
		{

			//Log.d("mcompleted check", "is calling");
			 timeWhenWalkWillBeCompleted = startedWalkExt.StartedTime
			+ walkDurationInMillis;

			if(startedWalk.WalkType.equals("overnight"))
				timeLeft = day_6am_time - System.currentTimeMillis();
			else
				timeLeft = timeWhenWalkWillBeCompleted - currentTimeMillis;


			SyslogUtils
					.logEvent(
							getApplicationContext(),
							String.format(
									"timeLeft  in old  in else = " + (timeLeft/60000) + " timeWhenWalkWillBeCompleted = "+ timeWhenWalkWillBeCompleted),
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);

			SyslogUtils
					.logEvent(
							getApplicationContext(),
							String.format(
									"timeLeft  in old  in else = " + (timeLeft/60000) + " startedWalkExt.StartedTime = "+ startedWalkExt.StartedTime
											+ "   walkDurationInMillis = "+ walkDurationInMillis),
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);


		}


		long timeWhenWalkMustBeStopped = timeWhenWalkWillBeCompleted
				+ PERIOD_WALK_MUST_BE_STOPPED;

		SyslogUtils
				.logEvent(
						getApplicationContext(),
						String.format(
								"CurrTime: %d, will be completed in: %d, will be stopped in: %d",
								currentTimeMillis, timeWhenWalkWillBeCompleted
										- currentTimeMillis,
								timeWhenWalkMustBeStopped - currentTimeMillis),
						EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);

		// ���� �������� �� ���������


		//ToastUtils.showLong(getApplicationContext(), "timeWhenWalkWillBeCompleted  in old = " + timeWhenWalkWillBeCompleted);
		//ToastUtils.showLong(getApplicationContext(), "currentTimeMillis  in old = " + currentTimeMillis);
		//nkp start
		//Log.d("nkp check", " is calling "+startedWalk.WalkType );
		if (startedWalk.WalkType.equals("overnight")){

			//Log.d("6 Am date", cal.getTime() + " nkp day_6am_time "+day_6am_time);

			if (day_6am_time > System.currentTimeMillis()){
				Log.d("completed walk", "nkp completed day_6am_time"+ day_6am_time + " current "+System.currentTimeMillis());

				AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
				Intent walkComplIntent = new Intent(getApplicationContext(),
						WalkCompleteBroadcastReceiver.class);

				PendingIntent pi = PendingIntent.getBroadcast(
						getApplicationContext(), 0, walkComplIntent, 0);
				alarmMgr.cancel(pi);
				alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + timeLeft, pi);


			}else {
				getDbAdapter().updateSomeFlagOfStartedWalk(startedWalkExt.WalkId,
						TableColumns.StartedWalksColumns.COMPLETED, true);
				//Log.d("completed overnight", "nkp completed "+ day_6am_time + " current "+currentTimeMillis);
				long  overNightStayMustBeStopped = day_6am_time + PERIOD_WALK_MUST_BE_STOPPED;
				// ... � �� ������ 20 �����
				if ( currentTimeMillis < overNightStayMustBeStopped ) {
					reactOnWalkComplete(overNightStayMustBeStopped - currentTimeMillis);
					Log.d("reactOn overnight", "nkp is calling");
				}

				// ... � ������ 20 �����
				else {
					stopWalkProgressServices();
					removeWalkCompletitionAlarms();
					removeFinishedWalkNotification();
					removeUploadPhotoNotification();
				}
			}

		}else {
			//old start
			if (timeWhenWalkWillBeCompleted > currentTimeMillis) {
				//long timeLeft = timeWhenWalkWillBeCompleted - currentTimeMillis;
				//Log.d("running walk", "nkp completed "+ timeWhenWalkMustBeStopped + " current "+currentTimeMillis);

				// ��������� "������" ��� ���������� �� ��������� �����������
				// ������������ �������
				AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

				Intent walkComplIntent = new Intent(getApplicationContext(),
						WalkCompleteBroadcastReceiver.class);

				PendingIntent pi = PendingIntent.getBroadcast(
						getApplicationContext(), 0, walkComplIntent, 0);

				alarmMgr.cancel(pi);
				alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + timeLeft, pi);

				//ToastUtils.showLong(getApplicationContext(), "timeLeft  in old  in mint= " + timeLeft/60000);

				//ToastUtils.showLong(getApplicationContext(), "timeLeft  in old in mint  = " + (timeLeft / 60000));


				SyslogUtils
						.logEvent(
								getApplicationContext(),
								String.format(
										"timeLeft  in old  in mint = " + timeLeft/60000),
								EnumLogSeverity.INFORMATIONAL,
								EnumLogType.INTERNAL_TYPE);

			}
			// ���� �������� ���������...
			else {
				getDbAdapter().updateSomeFlagOfStartedWalk(startedWalkExt.WalkId,
						TableColumns.StartedWalksColumns.COMPLETED, true);
				//Log.d("completed walk", "completed "+ timeWhenWalkMustBeStopped + " current "+currentTimeMillis);

				// ... � �� ������ 20 �����
				if (currentTimeMillis < timeWhenWalkMustBeStopped) {
					reactOnWalkComplete(timeWhenWalkMustBeStopped - currentTimeMillis);

					Log.d("reactOn withtime", "nkp is calling");
				}

				// ... � ������ 20 �����
				else {
					stopWalkProgressServices();

					removeWalkCompletitionAlarms();

					removeFinishedWalkNotification();
					removeUploadPhotoNotification();
				}
			}
			//old end
		}

		/*//old start
		if (timeWhenWalkWillBeCompleted > currentTimeMillis) {
			//long timeLeft = timeWhenWalkWillBeCompleted - currentTimeMillis;
			Log.d("running walk", "completed "+ timeWhenWalkMustBeStopped + " current "+currentTimeMillis);

			// ��������� "������" ��� ���������� �� ��������� �����������
			// ������������ �������
			AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

			Intent walkComplIntent = new Intent(getApplicationContext(),
					WalkCompleteBroadcastReceiver.class);

			PendingIntent pi = PendingIntent.getBroadcast(
					getApplicationContext(), 0, walkComplIntent, 0);

			alarmMgr.cancel(pi);
			alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + timeLeft, pi);

			//ToastUtils.showLong(getApplicationContext(), "timeLeft  in old  in mint= " + timeLeft/60000);

			//ToastUtils.showLong(getApplicationContext(), "timeLeft  in old in mint  = " + (timeLeft / 60000));


			SyslogUtils
					.logEvent(
							getApplicationContext(),
							String.format(
									"timeLeft  in old  in mint = " + timeLeft/60000),
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);

		}
		// ���� �������� ���������...
		else {
			getDbAdapter().updateSomeFlagOfStartedWalk(startedWalkExt.WalkId,
					TableColumns.StartedWalksColumns.COMPLETED, true);
			//Log.d("completed walk", "completed "+ timeWhenWalkMustBeStopped + " current "+currentTimeMillis);

			// ... � �� ������ 20 �����
			if (currentTimeMillis < timeWhenWalkMustBeStopped) {
				reactOnWalkComplete(timeWhenWalkMustBeStopped
						- currentTimeMillis);

				Log.d("reactOn withtime", "is calling");
			}

			// ... � ������ 20 �����
			else {
				stopWalkProgressServices();

				removeWalkCompletitionAlarms();

				removeFinishedWalkNotification();
				removeUploadPhotoNotification();
			}
		}
		//old end*/
		//nkp end

		return true;
	}


	public void reactOnAfterWalk() {

//		Intent dialogIntent = new Intent(getApplicationContext(),
//				AfterWalkDialogActivity.class);
//		dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		getApplicationContext().startActivity(dialogIntent);


		Intent dialogIntent = new Intent(getApplicationContext(),WalkMapActivity.class);
		WalkMapActivity.isAfterwalk = "yes";
		dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(dialogIntent);



	}

	public void cancleWalkTimer()
	{
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

		Intent walkComplIntent = new Intent(getApplicationContext(),
				WalkCompleteBroadcastReceiver.class);

		PendingIntent pi = PendingIntent.getBroadcast(
				getApplicationContext(), 0, walkComplIntent, 0);

		alarmMgr.cancel(pi);

		cancelHalfWalkAlarm(); // if gps or device off then it need to be stopped
		cancelAfterWalkAlarm(); // if gps or device off then it need to be stopped
		//alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				///SystemClock.elapsedRealtime() + timeLeft, pi);
	}
	public void setWalkCompleteTimer(long remainlTime)
	{


		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);
		if(startedWalkId ==null)
		{
			return;
		}
		final Walk startedWalk = getWalkGetter()
				.getStartedWalkWithoutOwnerAndDogs();

		StartedWalkExt startedWalkExt = getDbAdapter().getStartedWalkForId(
				startedWalkId);


		long currentTimeMillis = System.currentTimeMillis();

		// TODO: change in release
		// long walkDurationInMillis = 1 * 1000 * 10;
		// long walkDurationInMillis = 1 * 1000 * 60 * 60;
		long walkDurationInMillis = startedWalk.Duration * 1000;

		//long timeWhenWalkWillBeCompleted = startedWalkExt.StartedTime
				//+walkDurationInMillis;

		//long timeWhenWalkWillBeCompleted = startedWalkExt.StartedTime - walkDurationInMillis;

		long timeWhenWalkWillBeCompleted = walkDurationInMillis - remainlTime;

		long timeWhenHalfWalkWillBeCompleted = (walkDurationInMillis/2) - remainlTime;

		//long timeWhenWalkMustBeStopped = timeWhenWalkWillBeCompleted
		//		+ PERIOD_WALK_MUST_BE_STOPPED;
		//long timeLeft = timeWhenWalkWillBeCompleted - currentTimeMillis + remainlTime;

		//ToastUtils.showLong(getApplicationContext(),"remainlTime   = "+remainlTime);
		//ToastUtils.showLong(getApplicationContext(),"timeWhenWalkWillBeCompleted = "+timeWhenWalkWillBeCompleted);
		//ToastUtils.showLong(getApplicationContext(),"timeWhenWalkWillBeCompleted in min  = "+(timeWhenWalkWillBeCompleted/60000));

		// ��������� "������" ��� ���������� �� ��������� �����������
		// ������������ �������

		if(timeWhenWalkWillBeCompleted>0) {
			AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

			Intent walkComplIntent = new Intent(getApplicationContext(),
					WalkCompleteBroadcastReceiver.class);

			PendingIntent pi = PendingIntent.getBroadcast(
					getApplicationContext(), 0, walkComplIntent, 0);


			alarmMgr.cancel(pi);
			alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + timeWhenWalkWillBeCompleted, pi);
		}

		if(timeWhenHalfWalkWillBeCompleted>0) {
			AlarmManager al = (AlarmManager) getSystemService(ALARM_SERVICE);
			Intent halfWalkIntent = new Intent(getApplicationContext(),
					HalfWalkBroadcastReceiver.class);
			PendingIntent pi1 = PendingIntent.getBroadcast(getApplicationContext(),
					0, halfWalkIntent, 0);
			al.cancel(pi1);
			al.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + timeWhenHalfWalkWillBeCompleted,
					pi1);
		}

		//ToastUtils.showLong(getApplicationContext(),"timeLeft in min  = "+(timeLeft/60000));
	}

	public void reactOnHalfWalk() {
		Walk startedWalk = getWalkGetter().getStartedWalkWithoutOwnerAndDogs();

		if (startedWalk == null) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"Half walk is reached, startedWalk is NULL!!! Neither beeping nor vibrating",
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			return;
		}

		StartedWalkExt startedWalkExt = getDbAdapter().getStartedWalkForId(
				startedWalk._id);

		if (startedWalkExt == null) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"Half walk is reached, startedWalkExt is NULL!!! Neither beeping nor vibrating",
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			return;
		}

		if (startedWalkExt != null && !startedWalkExt.PhotoUploaded) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"Half walk reached, walk is in progress: beeping, vibrating",
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);
//			Intent dialogIntent = new Intent(getApplicationContext(),
//					HalfWalkDialogActivity.class);
//			dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			getApplicationContext().startActivity(dialogIntent);


			Intent dialogIntent = new Intent(getApplicationContext(),WalkMapActivity.class);
			dialogIntent.putExtra("isHalfWalk","yes");
			WalkMapActivity.isHalfwalk = "yes";
			dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(dialogIntent);

//			if(mResumedWalkMapActivity != null)
//				mResumedWalkMapActivity.showHalfWalkAlert();
//			else
//				SyslogUtils
//						.logEvent(
//								getApplicationContext(),
//								"Half walk reached, but mResumedWalkMapActivity is null",
//								EnumLogSeverity.INFORMATIONAL,
//								EnumLogType.INTERNAL_TYPE);
		}
	}


	public void onWalkComplete() {

		if (mResumedWalkMapActivity == null) {
			Intent dialogIntent = new Intent(getApplicationContext(),
					AfterWalkDialogActivity.class);
			dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(dialogIntent);
		} else {
			// ToastUtils.showLong(getApplicationContext(),
			// "alram set but user do something on walk map");
			debugAfterWalk(false);
		}

		// Walk startedWalk =
		// getWalkGetter().getStartedWalkWithoutOwnerAndDogs();
		//
		// if (startedWalk == null) {
		// SyslogUtils
		// .logEvent(
		// getApplicationContext(),
		// "After walk is reached, startedWalk is NULL!!! Neither beeping nor vibrating",
		// EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
		// return;
		// }
		//
		// StartedWalkExt startedWalkExt = getDbAdapter().getStartedWalkForId(
		// startedWalk._id);
		//
		// if (startedWalkExt == null) {
		// SyslogUtils
		// .logEvent(
		// getApplicationContext(),
		// "After walk is reached, startedWalkExt is NULL!!! Neither beeping nor vibrating",
		// EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
		// return;
		// }
		//
		// if (startedWalkExt != null && !startedWalkExt.Completed) {
		// SyslogUtils
		// .logEvent(
		// getApplicationContext(),
		// "After walk reached, walk is in progress: beeping, vibrating",
		// EnumLogSeverity.INFORMATIONAL,
		// EnumLogType.INTERNAL_TYPE);
		// Intent dialogIntent = new Intent(getApplicationContext(),
		// AfterWalkDialogActivity.class);
		// dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// getApplicationContext().startActivity(dialogIntent);
		// }
	}

	private void removeWalkCompletitionAlarms() {
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent walkComplIntent = new Intent(getApplicationContext(),
				WalkCompleteBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
				0, walkComplIntent, 0);
		alarmMgr.cancel(pi);

		// vivek hack
		// Intent walkMustStopIntent = new Intent(getApplicationContext(),
		// WalkMustBeStoppedBroadcastReceiver.class);
		// PendingIntent pi2 =
		// PendingIntent.getBroadcast(getApplicationContext(),
		// 0, walkMustStopIntent, 0);
		// alarmMgr.cancel(pi2);
	}

	private WalkMapActivity mResumedWalkMapActivity;

	public void resumeWalkMapActivity(WalkMapActivity walkMapActivity) {
		mResumedWalkMapActivity = walkMapActivity;
	}

	public void pauseWalkMapActivity() {
		mResumedWalkMapActivity = null;
	}

	IStopWalkRequestNotifiable mStopWalkNotifiable;

	public void subscribeToStopWalk(
			IStopWalkRequestNotifiable paramSropWalkNotifiable) {
		mStopWalkNotifiable = paramSropWalkNotifiable;
	}

	public void unsubscribeFromStopWalk() {
		mStopWalkNotifiable = null;
	}

	public void stopCurrentWalk(Context mContext,
			IStopWalkRequestNotifiable stopWalkNotifiable) {
		mStopWalkNotifiable = stopWalkNotifiable;

		// ������������� 20-�������� ������ �������� StopWalk
		removeWalkCompletitionAlarms();
		cancelAfterWalkAlarm();

		mContextForStopWalk = mContext;

		if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			SyslogUtils.logEvent(getApplicationContext(), "stopCurrentWalk()",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			getOneWalkState();
		} else {
			SyslogUtils.logEvent(getApplicationContext(),
					"No internet connection on stopCurrentWalk()",
					EnumLogSeverity.ERROR, EnumLogType.HARDWARE_TYPE);
			taskMap.put("stopCurrentWalk", "stopCurrentWalk");
		}

	}

	private Context mContextForStopWalk;

	private void getOneWalkState() {
		String walkIdToStop = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());
		String request = SwiftoRequestBuilder.buildGetWalkState(walkIdToStop);

		SyslogUtils.logEvent(getApplicationContext(),
				"end walk request ------      "+request,
				EnumLogSeverity.INFORMATIONAL, EnumLogType.HARDWARE_TYPE);

		if (mContextForStopWalk != null) {
			new GetWalkStateTask(walkIdToStop, mContextForStopWalk,
					"Getting walk state...", true).execute(request);
		} else {
			new GetWalkStateTask(walkIdToStop, getApplicationContext(), "",
					false).execute(request);

		}
	}

	class GetWalkStateTask extends ServerRequestAsynkTask {
		private String mWalkId;

		public GetWalkStateTask(String walkId, Context ctx, String message,
				boolean showProgress) {
			super(ctx, message, showProgress);

			mWalkId = walkId;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			parseGetWalkStateResponse(result, mWalkId);
		}
	}

	private void parseGetWalkStateResponse(String response, String walkId) {
		String error = TaskErrorChecker.getErrorInString(response);

		if (error != null) {
			SyslogUtils.logEvent(getApplicationContext(),
					"Error getting walk state: " + error,
					EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
			Toast.makeText(getApplicationContext(),
					"Error getting walk state: " + error, Toast.LENGTH_SHORT)
					.show();
		} else {
			String walkState = new GetWalkStateParser()
					.parseGetWalkState(response);

			// ...walk is still "STARTED"
			if (walkState.equals(EnumWalkStatuses.STARTED)) {
				SyslogUtils.logEvent(this, "Walk has not completed",
						EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
				stopCurrWalk();

				SyslogUtils.logEventWithResponce(this, "Walk has not completed",
						EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE,response) ;
				stopCurrWalk();
			}

			// ...walk has been stopped
			else if (walkState.equals(EnumWalkStatuses.COMPLETED)) {
				SyslogUtils.logEvent(this, "Walk already completed",
						EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);

				stopWalkSuccess();
			}
		}
	}

	public void stopCurrWalk() {
		Walk currWalk = getWalkGetter().getStartedWalkWithoutOwnerAndDogs();


		long walkofftime = SharedPreferencesHelper.getWalkOffTimeExact(currWalk._id, getApplicationContext());

		Timestamp stamp = new Timestamp(walkofftime);
		Date date = new Date(stamp.getTime());

		if(walkofftime < 100)
		{
			walkofftime = System.currentTimeMillis();

			 stamp = new Timestamp(walkofftime);
			 date = new Date(stamp.getTime());

			SyslogUtils.logEvent(getApplicationContext(),
					"walkofftime is not seted new value is     ------  "+date,
					EnumLogSeverity.ERROR, EnumLogType.HARDWARE_TYPE);
		}
		else
		{
			SyslogUtils.logEvent(getApplicationContext(),
					"walkofftime is already set and value is      ------  "+date,
					EnumLogSeverity.ERROR, EnumLogType.HARDWARE_TYPE);
		}

		String stopWalkRequest = SwiftoRequestBuilder.buildStopWalk(
				currWalk.walkerID, currWalk._id, walkofftime);

		if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {

			SyslogUtils.logEvent(getApplicationContext(),
					"stopWalkRequest    ------  "+stopWalkRequest,
					EnumLogSeverity.ERROR, EnumLogType.HARDWARE_TYPE);

			if (mContextForStopWalk != null) {
				new StopWalkAsyncTask(mContextForStopWalk,
						"Sending end walk...", true).execute(stopWalkRequest);
			} else {
				new StopWalkAsyncTask(getApplicationContext(), "", false)
						.execute(stopWalkRequest);
			}
		} else {
			SyslogUtils.logEvent(getApplicationContext(),
					"No internet connection on stopCurrWalk()",
					EnumLogSeverity.ERROR, EnumLogType.HARDWARE_TYPE);

			if (mStopWalkNotifiable != null) {
				mStopWalkNotifiable.onStopWalkFailure();
			}
		}
	}

	public void walkComplete() {
		SyslogUtils.logEvent(getApplicationContext(),
				"SwiftoApplication.walkComplete()",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		Log.d("reactOn withservice", "nkp is calling");
		reactOnWalkComplete(PERIOD_WALK_MUST_BE_STOPPED);
	}

	public void walkMustBeStopped() {
		SyslogUtils.logEvent(getApplicationContext(),
				"Stopping walk after 1 hour past...",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		removeFinishedWalkNotification();
		removeUploadPhotoNotification();
		stopWalkProgressServices();
		stopCurrentWalk(null, mStopWalkNotifiable);
	}

	private void reactOnWalkComplete(long timeToWait) {
		String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		getDbAdapter().updateSomeFlagOfStartedWalk(startedWalkId,
				TableColumns.StartedWalksColumns.COMPLETED, true);

		SyslogUtils.logEvent(getApplicationContext(),
				"Walk reaches its end, walkId: " + startedWalkId,
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		// change notification to "Walk Completed"
		addFinishedWalkNotification();
		debugAfterWalk(false);

		// ���� �����-�� �������� ������� ��������� "isComplete" �������� -
		// �������� ��� ��������
		if (mResumedWalkMapActivity != null) {
			mResumedWalkMapActivity.onWalkComplete();
		}

		// ������ ������ �� timeToWait, �� ��������� �������� ����������
		// stop_walk
		// vivek hack
		// AlarmManager alarmMgr = (AlarmManager)
		// getSystemService(ALARM_SERVICE);
		// Intent walkMustBeStopIntent = new Intent(getApplicationContext(),
		// WalkMustBeStoppedBroadcastReceiver.class);
		// PendingIntent pi =
		// PendingIntent.getBroadcast(getApplicationContext(),
		// 0, walkMustBeStopIntent, 0);
		// alarmMgr.cancel(pi);
		// alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		// SystemClock.elapsedRealtime() + timeToWait, pi);
	}

	class StopWalkAsyncTask extends ServerRequestAsynkTask {
		public StopWalkAsyncTask(Context ctx, String message,
				boolean showProgress) {
			super(ctx, message, showProgress);
		}

		@Override
		protected String doInBackground(String... params) {
			return super.doInBackground(params);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			parseStopWalkResponse(result);
		}
	}

	private void parseStopWalkResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);

		if (error != null) {
			Toast.makeText(getApplicationContext(),
					"Stop walk error: " + error, Toast.LENGTH_SHORT).show();
			SyslogUtils.logEvent(getApplicationContext(), "Stop walk error: "
					+ error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);

			if (mStopWalkNotifiable != null) {
				mStopWalkNotifiable.onStopWalkFailure();
			}
		} else {
			stopWalkSuccess();
		}
	}

	private void stopWalkSuccess() {
		if (taskMap.containsKey("stopCurrentWalk")) {
			taskMap.remove("stopCurrentWalk");
		}

		if (mResumedWalkMapActivity != null) {

			mResumedWalkMapActivity.minPassed = 0;
			mResumedWalkMapActivity.resultFile = null;

			imageList.clear();
			imageProcessList.clear();
			imageSuccessList.clear();

			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"DEBUG - mResumedWalkMapActivity.minPassed set to zero , Now value is = " + mResumedWalkMapActivity.minPassed,
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);
		}
		else
		{
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"DEBUG - mResumedWalkMapActivity.minPassed is not set zero because " +
										" mResumedWalkMapActivity is null, Now value is = "+mResumedWalkMapActivity.minPassed ,
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);
		}



		removeFinishedWalkNotification();
		removeUploadPhotoNotification();

		SharedPreferencesHelper
				.setWalkInProcess(getApplicationContext(), false);
		SharedPreferencesHelper.deleteLastMessage(getApplicationContext());
		stopWalkProgressServices();


		String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		SyslogUtils.logEvent(getApplicationContext(), "Stop walk success, id: "
				+ startedWalkId, EnumLogSeverity.INFORMATIONAL,
				EnumLogType.SERVER_TYPE);
		getDbAdapter().updateSomeFlagOfStartedWalk(startedWalkId,
				TableColumns.StartedWalksColumns.STOP_WALK_SENT, true);
		updateWalkStatusInDatabase(
				SharedPreferencesHelper
						.getStartedWalkId(getApplicationContext()),
				WalkUtils.STATUS_COMPLETED);

		SharedPreferencesHelper.deleteStartedWalkId(getApplicationContext());
		SharedPreferencesHelper
				.deleteStartedWalkUniqueId(getApplicationContext());

		SwiftoApplication.uploadPhotoService.shutdownNow();

		if (mStopWalkNotifiable != null) {
			mStopWalkNotifiable.onStopWalkSuccess();
		}

		try {
			if (wl != null && wl.isHeld()) {
				wl.release();
			}

		} catch (Exception e) {

		}
	}

	public void updateWalkStatusInDatabase(String walkId, String status) {
		getDbAdapter().upladteWalkState(walkId, status);
	}

	// -------------------------------------------------
	//
	// Send message
	//
	// --------------

	public void sendMessage(Context mContext, String message) {

		if (message==null)
			return;

		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		String msg = SharedPreferencesHelper.getWalkMSG(startedWalkId, getApplicationContext());
		if(msg.equals("yes"))
			return;

		try {
			final String utfEncodedMessage = URLEncoder
					.encode(message, "UTF-8");
			final String request = SwiftoRequestBuilder.buildSendMessage(
					SharedPreferencesHelper.getWalkerId(mContext),
					SharedPreferencesHelper
							.getStartedWalkId(getApplicationContext()),
					"walkerNote", utfEncodedMessage);
			SyslogUtils.logEvent(getApplicationContext(), "Sending message: "
					+ utfEncodedMessage, EnumLogSeverity.INFORMATIONAL,
					EnumLogType.INTERNAL_TYPE);
			new SendMessageAsyncTask(mContext, "Sending message...", true)
					.execute(request);

		} catch (UnsupportedEncodingException e) {
			// should not be here
			e.printStackTrace();
			SyslogUtils.logEvent(getApplicationContext(),
					"Sending message failed: unsupported encoding exception",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		}
	}

	class SendMessageAsyncTask extends ServerRequestAsynkTask {
		public SendMessageAsyncTask(Context ctx, String message,
				boolean showProgress) {
			super(ctx, message, showProgress);
		}

		@Override
		protected String doInBackground(String... params) {
			return super.doInBackground(params);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			parseSendMessageResponse(result);
		}
	}

	private void parseSendMessageResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);

		if (error != null) {
			ToastUtils.showShort(getApplicationContext(),
					"Error sending message: " + error);
			SyslogUtils.logEvent(getApplicationContext(),
					"Send Message error: " + error, EnumLogSeverity.ERROR,
					EnumLogType.SERVER_TYPE);

			if (mResumedWalkMapActivity != null) {
				mResumedWalkMapActivity.onSendMessageFailure();
			}
		} else {
			if (taskMap.containsKey("sendMessage")) {
				taskMap.remove("sendMessage");

			}
			SyslogUtils.logEvent(getApplicationContext(),
					"Send message success", EnumLogSeverity.INFORMATIONAL,
					EnumLogType.SERVER_TYPE);
			getDbAdapter()
					.updateSomeFlagOfStartedWalk(
							SharedPreferencesHelper
									.getStartedWalkId(getApplicationContext()),
							TableColumns.StartedWalksColumns.MESSAGE_SENT, true);

			if (mResumedWalkMapActivity != null) {
				mResumedWalkMapActivity.onSendMessageSuccess();
			}
		}
	}

	public static boolean boundToTaskService = false;
	public static SwiftoTaskService taskService;

	private final ServiceConnection taskServiceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			boundToTaskService = false;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			SwiftoTaskService.SwiftoTaskBinder binder = (SwiftoTaskService.SwiftoTaskBinder) service;
			taskService = binder.getService();

			boundToTaskService = true;
		}
	};

	public static boolean boundToUploadPhotoService = false;
	public static UploadPhotoService uploadPhotoService;

	private final ServiceConnection uploadPhotoServiceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			boundToUploadPhotoService = false;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			UploadPhotoService.UploadPhotoServiceBinder binder = (UploadPhotoService.UploadPhotoServiceBinder) service;
			uploadPhotoService = binder.getService();
			boundToUploadPhotoService = true;
		}
	};

	// -----------------------------------------------------------
	//
	// Sending add_points, add_meet, add_poop
	//
	// ------------------------------------------------

	public void addPointAndSendIfNeeded(Location location) {
		// save point to database with status SENDING
		String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		// get all meet/poop points
		ArrayList<PerformedRequests> requs = getDbAdapter()
				.selectPreformedRequests(startedWalkId);

		// try to send unsent meet and poop
		for (PerformedRequests req : requs) {
			sendRequestMeetOrPoo(req.getText(), startedWalkId, false);
		}

		WalkGPSPoint walkGpsPoint = new WalkGPSPoint(
				(int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		walkGpsPoint.WalkId = startedWalkId;
		walkGpsPoint.Type = EnumWalkGPSPointType.ROUTE;
		walkGpsPoint.Status = EnumWalkGPSPointSendStatus.NOT_SENT;
		walkGpsPoint.Accuracy = location.getAccuracy();

		String accur = String.format(Locale.getDefault(), "Accuracy: %f",
				walkGpsPoint.Accuracy);
		String walkerId = SharedPreferencesHelper
				.getWalkerId(getApplicationContext());

		// save point to database
		getDbAdapter().addWalkGpsPoint(walkGpsPoint);



		// comment this code and make a funtion with this code and call here

		sendUnsetPoints(true);


		/*

		// get all NOT_SENT points from database and send
		ArrayList<WalkGPSPoint> notSentPoints = getDbAdapter()
				.selectWalkGpsPointsForWalkByTypeAndStatus(startedWalkId,
						EnumWalkGPSPointType.ROUTE,
						EnumWalkGPSPointSendStatus.NOT_SENT);

		String request = SwiftoRequestBuilder.buildAddPointsWalkGps(walkerId,
				startedWalkId, notSentPoints);

		// try to send point(s) to server
		try {
			if(isDetailLog) {
				SyslogUtils.logEvent(
						getApplicationContext(),
						"Sending points for walk id: "
								+ startedWalkId
								+ ", request: ..."
								+ request.substring(
								Math.max(0, request.length() - 100),
								request.length()) + " " + accur,
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			}
			sendRequestPoints(request, startedWalkId, false);
		} catch (Exception e) {
			SyslogUtils.logEvent(
					getApplicationContext(),
					"Error sending points for walk id: "
							+ startedWalkId
							+ ", request: ..."
							+ request.substring(
									Math.max(0, request.length() - 100),
									request.length()), EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);
		}*/
	}

	public void addPointAndSendIfNeededExt(WalkGPSPoint point) {
		// save point to database with status SENDING
		String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		// get all meet/poop points
		ArrayList<PerformedRequests> requs = getDbAdapter()
				.selectPreformedRequests(startedWalkId);

		// try to send unsent meet and poop
		for (PerformedRequests req : requs) {
			sendRequestMeetOrPoo(req.getText(), startedWalkId, false);
		}

		WalkGPSPoint walkGpsPoint = new WalkGPSPoint(point.getLatitudeE6(),
				point.getLongitudeE6(), point.getTimestamp());
		walkGpsPoint.WalkId = startedWalkId;
		walkGpsPoint.Type = EnumWalkGPSPointType.ROUTE;
		walkGpsPoint.Status = EnumWalkGPSPointSendStatus.NOT_SENT;


		walkGpsPoint.Accuracy = point.Accuracy;// getAccuracy();

		String accur = String.format(Locale.getDefault(), "Accuracy: %f",
				walkGpsPoint.Accuracy);
		String walkerId = SharedPreferencesHelper
				.getWalkerId(getApplicationContext());

		// save point to database
		getDbAdapter().addWalkGpsPoint(walkGpsPoint);

		// comment this code and make a funtion with this code and call here

		sendUnsetPoints(true);
		/*// get all NOT_SENT points from database and send
		ArrayList<WalkGPSPoint> notSentPoints = getDbAdapter()
				.selectWalkGpsPointsForWalkByTypeAndStatus(startedWalkId,
						EnumWalkGPSPointType.ROUTE,
						EnumWalkGPSPointSendStatus.NOT_SENT);

		String request = SwiftoRequestBuilder.buildAddPointsWalkGps(walkerId,
				startedWalkId, notSentPoints);

		// try to send point(s) to server
		try {
			if(isDetailLog) {
				SyslogUtils.logEvent(
						getApplicationContext(),
						"Sending points for walk id: "
								+ startedWalkId
								+ ", request: ..."
								+ request.substring(
								Math.max(0, request.length() - 100),
								request.length()) + " " + accur,
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			}
			sendRequestPoints(request, startedWalkId, false);
		} catch (Exception e) {
			SyslogUtils.logEvent(
					getApplicationContext(),
					"Error sending points for walk id: "
							+ startedWalkId
							+ ", request: ..."
							+ request.substring(
									Math.max(0, request.length() - 100),
									request.length()), EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);
		}*/
	}

	public  ArrayList<WalkGPSPoint> notSentPoints = new ArrayList<WalkGPSPoint>();
	//public  ArrayList<WalkGPSPoint> notSentPoints = new CopyOnWriteArrayList<WalkGPSPoint>();
	// CopyOnWriteArrayList
	public  ArrayList<WalkGPSPoint> tempNotSentPoints = new ArrayList<WalkGPSPoint>();

	public boolean isFirstTimeCall = false;
	public void sendUnsetPoints(boolean isFirstTime)
	{
		if(!isFirstTimeCall && isFirstTime )
		{
			isFirstTimeCall = true;
		}
		String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		String walkerId = SharedPreferencesHelper
				.getWalkerId(getApplicationContext());
		if(isFirstTimeCall && isFirstTime) {


			notSentPoints = getDbAdapter()
					.selectWalkGpsPointsForWalkByTypeAndStatus(startedWalkId,
							EnumWalkGPSPointType.ROUTE,
							EnumWalkGPSPointSendStatus.NOT_SENT);
		}

		for(int i=0;i<notSentPoints.size();i++)
		{
			if(i < 10) {
				tempNotSentPoints.add(notSentPoints.get(i));
			}
			else {
				break;
			}
		}





		String request = SwiftoRequestBuilder.buildAddPointsWalkGps(walkerId,
				startedWalkId, tempNotSentPoints);

		// try to send point(s) to server
		try {

			sendRequestPoints(request, startedWalkId, false);
		} catch (Exception e) {
			SyslogUtils.logEvent(
					getApplicationContext(),
					"Error sending points for walk id: "
							+ startedWalkId
							+ ", request: ..."
							+ request.substring(
							Math.max(0, request.length() - 100),
							request.length()), EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);
		}
	}


	public void addPooInfoAndSend(GeoPointExt pooInfo, int dogIndex) {
		Walk startedWalk = getWalkGetter().getStartedWalk();
		WalkGPSPoint wgp = getPointsGetter().getLastRoutePoint(startedWalk._id);

		if (wgp == null) {
			SyslogUtils.logEvent(getApplicationContext(),
					"Try to add_poop - there are no last point in database. WalkId: "
							+ startedWalk._id, EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);
			return;
		}

		wgp.Type = EnumWalkGPSPointType.POOP;
		wgp.Status = EnumWalkGPSPointSendStatus.NOT_SENT;
		wgp.setTimestamp(System.currentTimeMillis());

		String pooRequest = SwiftoRequestBuilder.buildAddPoopWithTimestamp(
				startedWalk._id, wgp, startedWalk.Dogs.get(dogIndex)._id,
				wgp.getTimestamp());
		wgp.MD5 = MD5Utils.getMD5(pooRequest);
		getDbAdapter().addWalkGpsPoint(wgp);
		SyslogUtils.logEvent(getApplicationContext(), "Sending add_poop : "
				+ pooRequest, EnumLogSeverity.INFORMATIONAL,
				EnumLogType.INTERNAL_TYPE);
		sendRequestMeetOrPoo(pooRequest, startedWalk._id, false);
	}

	public void addMeetInfoAndSend(GeoPointExt meetInfo, int dogIndex) {
		Walk startedWalk = getWalkGetter().getStartedWalk();
		WalkGPSPoint wgp = getPointsGetter().getLastRoutePoint(startedWalk._id);

		if (wgp == null) {
			SyslogUtils.logEvent(getApplicationContext(),
					"Try to add_meet - there are no last point in database. WalkId: "
							+ startedWalk._id, EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);
			return;
		}

		wgp.Type = EnumWalkGPSPointType.MEET;
		wgp.Status = EnumWalkGPSPointSendStatus.NOT_SENT;
		wgp.setTimestamp(System.currentTimeMillis());

		String meetRequest = SwiftoRequestBuilder.buildAddMeetWithTimestamp(
				startedWalk._id, wgp, startedWalk.Dogs.get(dogIndex)._id,
				wgp.getTimestamp());

		wgp.MD5 = MD5Utils.getMD5(meetRequest);
		getDbAdapter().addWalkGpsPoint(wgp);

		// Log.d("SwiftoApp", meetRequest);
		SyslogUtils.logEvent(getApplicationContext(), "Sending add_meet: "
				+ meetRequest, EnumLogSeverity.INFORMATIONAL,
				EnumLogType.INTERNAL_TYPE);

		sendRequestMeetOrPoo(meetRequest, startedWalk._id, false);
	}

	private void sendRequestPoints(final String request, final String walkId,
			final boolean sendingUnsent) {
		if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			BaseTask task;
			ListenableFuture<String> futureTask;
			task = new SendDataTask(request);
			futureTask = SwiftoApplication.taskService.enqueueTask(task);

			Futures.addCallback(futureTask, new FutureCallback<String>() {
				public void onFailure(Throwable e) {
					SyslogUtils.logEvent(getApplicationContext(),
							"Data NOT sent to server, walkId:" + walkId
									+ ", error: " + e.toString(),
							EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);

					/*if (sendingUnsent) {
						onSendUnsentPointsFailure();
					}*/

						if(notSentPoints.size() > 0)
						{
							SyslogUtils.logEvent(getApplicationContext(),
									"Data packate sent to server - actual size is- " + notSentPoints.size(),
									EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);

							for(int i = 0; i < tempNotSentPoints.size(); i++)
							{
								try {
									notSentPoints.remove(tempNotSentPoints.get(i));
								}
								catch (Exception ex)
								{
									SyslogUtils.logEvent(getApplicationContext(),
											"Data packate sent Error is- " + ex.toString(),
											EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
								}
							}

							tempNotSentPoints.clear();

							if(notSentPoints.size() > 0) {
								SyslogUtils.logEvent(getApplicationContext(),
										"now size is -   " + notSentPoints.size() + " And sendUnsetPoints called",
										EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);


								sendUnsetPoints(false);


							}
							else
							{
								SyslogUtils.logEvent(getApplicationContext(),
										"now size is -   " + notSentPoints.size() + " And next process run ",
										EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);

								getPointsGetter().updateAllRoutePointsToSent(walkId);
								if (isDetailLog) {
									SyslogUtils.logEvent(getApplicationContext(),
											"Data sent to server, walkId: " + walkId,
											EnumLogSeverity.INFORMATIONAL,
											EnumLogType.SERVER_TYPE);
								}

								if (sendingUnsent) {
									onSendUnsentPointsSuccess();
								}

								isFirstTimeCall = false;
							}
						}
						else {

							getPointsGetter().updateAllRoutePointsToSent(walkId);
							if (isDetailLog) {
								SyslogUtils.logEvent(getApplicationContext(),
										"Data sent to server, walkId: " + walkId,
										EnumLogSeverity.INFORMATIONAL,
										EnumLogType.SERVER_TYPE);
							}

							if (sendingUnsent) {
								onSendUnsentPointsSuccess();
							}

							isFirstTimeCall = false;
						}

				}

				public void onSuccess(String result) {

					if(notSentPoints.size() > 0)
					{
						SyslogUtils.logEvent(getApplicationContext(),
								"Data packate sent to server - actual size is- " + notSentPoints.size(),
								EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);

						for(int i = 0; i < tempNotSentPoints.size(); i++)
						{
							try {
								notSentPoints.remove(tempNotSentPoints.get(i));
							}
							catch (Exception e)
							{
								SyslogUtils.logEvent(getApplicationContext(),
										"Data packate sent Error is- " + e.toString(),
										EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
							}
						}

						tempNotSentPoints.clear();

						if(notSentPoints.size() > 0) {
							SyslogUtils.logEvent(getApplicationContext(),
									"now size is -   " + notSentPoints.size() + " And sendUnsetPoints called",
									EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);


							sendUnsetPoints(false);


						}
						else
						{
							SyslogUtils.logEvent(getApplicationContext(),
									"now size is -   " + notSentPoints.size() + " And next process run ",
									EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);

							getPointsGetter().updateAllRoutePointsToSent(walkId);
							if (isDetailLog) {
								SyslogUtils.logEvent(getApplicationContext(),
										"Data sent to server, walkId: " + walkId,
										EnumLogSeverity.INFORMATIONAL,
										EnumLogType.SERVER_TYPE);
							}

							if (sendingUnsent) {
								onSendUnsentPointsSuccess();
							}

							isFirstTimeCall = false;
						}
					}
					else {

						getPointsGetter().updateAllRoutePointsToSent(walkId);
						if (isDetailLog) {
							SyslogUtils.logEvent(getApplicationContext(),
									"Data sent to server, walkId: " + walkId,
									EnumLogSeverity.INFORMATIONAL,
									EnumLogType.SERVER_TYPE);
						}

						if (sendingUnsent) {
							onSendUnsentPointsSuccess();
						}

						isFirstTimeCall = false;
					}
				}
			});
		} else {
			SyslogUtils.logEvent(getApplicationContext(),
					"No internet connection on " + " send data",
					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
		}
	}

	private void onSendUnsentPointsSuccess() {
		if (taskMap.containsKey("sendUnsentPointsAndMeetPoos")) {
			taskMap.remove("sendUnsentPointsAndMeetPoos");
		}

		if (SwiftoApplication.taskService.tasksInQueue() == 0) {
			mUnsentRequestsAreSending = false;

			if (mResumedWalkMapActivity != null) {
				mResumedWalkMapActivity.onSendUnsentRequestsSuccess();
			}
		}
	}

	private void onSendUnsentPointsFailure() {
		if (SwiftoApplication.taskService.tasksInQueue() == 0) {
			mUnsentRequestsAreSending = false;

			if (mResumedWalkMapActivity != null) {
				mResumedWalkMapActivity.onSendUnsentRequestsFailure();
			}
		}
	}

	/**
	 * <ul>
	 * <li>On success meet/poo POINT will be updated in WALK_GPS_POINTS table as
	 * SENT. Also, related REQUEST will be deleted from PERFORMED_REQUESTS</li>
	 * <li>On failure meet/poo REQUEST will be added to PERFORMED_REQUESTS table
	 * </li>
	 * </ul>
	 * */
	private void sendRequestMeetOrPoo(final String request,
			final String walkId, final boolean sendingUnsent) {
		if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			BaseTask task;
			ListenableFuture<String> futureTask;
			task = new SendDataTask(request);
			futureTask = SwiftoApplication.taskService.enqueueTask(task);

			Futures.addCallback(futureTask, new FutureCallback<String>() {
				public void onFailure(Throwable arg0) {
					SyslogUtils.logEvent(getApplicationContext(),
							"Meet or Poo NOT sent to server, walkId:" + walkId,
							EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
					cacheFailedRequestToDatabase(request, walkId);

					if (sendingUnsent) {
						onSendUnsentMeetPoopFailure();
					}
				}

				public void onSuccess(String result) {
					getPointsGetter().updateMeetOrPooPointToSentByMd5(
							MD5Utils.getMD5(request));
					SyslogUtils.logEvent(getApplicationContext(),
							"Meet or poo sent to server, walkId: " + walkId,
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.SERVER_TYPE);
					deleteFailedRequestFromDb(request, walkId);

					if (sendingUnsent) {
						onSendUnsentMeetPoopSuccess();
					}
				}
			});
		} else {
			SyslogUtils.logEvent(getApplicationContext(),
					"No internet connection on " + " send meet or poo",
					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
			cacheFailedRequestToDatabase(request, walkId);
		}
	}

	private void onSendUnsentMeetPoopSuccess() {
		if (SwiftoApplication.taskService.tasksInQueue() == 0) {
			mUnsentRequestsAreSending = false;

			if (mResumedWalkMapActivity != null) {
				mResumedWalkMapActivity.onSendUnsentRequestsSuccess();
			}
		}
	}

	private void onSendUnsentMeetPoopFailure() {
		if (SwiftoApplication.taskService.tasksInQueue() == 0) {
			mUnsentRequestsAreSending = false;

			if (mResumedWalkMapActivity != null) {
				mResumedWalkMapActivity.onSendUnsentRequestsFailure();
			}
		}
	}

	private void cacheFailedRequestToDatabase(String request, String walkId) {
		PerformedRequests perfRequest = new PerformedRequests();
		perfRequest.setErrorDescription("NO_INET");
		perfRequest.setIsSuccess(false);
		perfRequest.setText(request);
		perfRequest.setMD5(MD5Utils.getMD5(request));
		perfRequest.setWalkId(walkId);

		getDbAdapter().insertPerformedRequest(perfRequest);
	}

	private void deleteFailedRequestFromDb(String request, String walkId) {
		getDbAdapter().deletePerformedRequerstWithBody(request);
	}

	private boolean mUnsentRequestsAreSending = false;

	public boolean getUnsentRequestsAreSending() {
		return mUnsentRequestsAreSending;
	}

	public void sendUnsentPointsAndMeetPoos() {
		mUnsentRequestsAreSending = true;

		String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());
		ArrayList<PerformedRequests> requs = getDbAdapter()
				.selectPreformedRequests(startedWalkId);

		for (PerformedRequests req : requs) {
			sendRequestMeetOrPoo(req.getText(), startedWalkId, true);
		}

		String walkerId = SharedPreferencesHelper
				.getWalkerId(getApplicationContext());

		// get all NOT_SENT points from database and send
		ArrayList<WalkGPSPoint> notSentPoints = getDbAdapter()
				.selectWalkGpsPointsForWalkByTypeAndStatus(startedWalkId,
						EnumWalkGPSPointType.ROUTE,
						EnumWalkGPSPointSendStatus.NOT_SENT);
		String requestPoints = SwiftoRequestBuilder.buildAddPointsWalkGps(
				walkerId, startedWalkId, notSentPoints);
		SyslogUtils.logEvent(
				getApplicationContext(),
				"Sending unsent points for walk id: "
						+ startedWalkId
						+ ", request: ..."
						+ requestPoints.substring(
								Math.max(0, requestPoints.length() - 100),
								requestPoints.length()),
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		// try to send point to server
		sendRequestPoints(requestPoints, startedWalkId, true);
	}

	public boolean hasUnsentDataAndSaveStatusToDatabase() {
		String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());
		ArrayList<PerformedRequests> requs = getDbAdapter()
				.selectPreformedRequests(startedWalkId);

		// get all NOT_SENT points from database and send
		ArrayList<WalkGPSPoint> notSentPoints = getDbAdapter()
				.selectWalkGpsPointsForWalkByTypeAndStatus(startedWalkId,
						EnumWalkGPSPointType.ROUTE,
						EnumWalkGPSPointSendStatus.NOT_SENT);

		if (requs != null && requs.size() > 0) {
			return true;
		}

		if (notSentPoints != null && notSentPoints.size() > 0) {
			return true;
		}

		getDbAdapter()
				.updateSomeFlagOfStartedWalk(
						SharedPreferencesHelper
								.getStartedWalkId(getApplicationContext()),
						TableColumns.StartedWalksColumns.UNSENT_DATA_SENT, true);
		return false;
	}

	// --------------------------
	//
	// Notifications
	//
	// -----------------------

	public static int UPLOAD_PHOTO_NOTIFICATION_ID = 239560;
	public static int WALK_PROGRESS_NOTIFICATION_ID = 712985419;

	@SuppressLint("NewApi")
	public void addStartedWalkNotification() {
		NotificationManager nm = getNotificationManager();

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.dog_default)
                .setTicker("Walk is in progress")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Swifto")
                .setContentText("Walk is in progress");

/*		Notification startWalkNotification = new Notification(
				R.drawable.dog_default, "Walk is in progress",
				System.currentTimeMillis());*/
		Intent walkIntent = new Intent(getApplicationContext(),
				WalkMapActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, walkIntent, 0);

/*		startWalkNotification.setLatestEventInfo(getApplicationContext(),
				"Swifto", "Walk is in progress", pendingIntent);*/
        builder.setContentIntent(pendingIntent);

		//nm.notify(WALK_PROGRESS_NOTIFICATION_ID, startWalkNotification);
        nm.notify(WALK_PROGRESS_NOTIFICATION_ID, builder.build());
	}

	private void addFinishedWalkNotification() {
		NotificationManager nm = getNotificationManager();
	/*	Notification startWalkNotification = new Notification(
				R.drawable.dog_default, "Walk complete!",
				System.currentTimeMillis());*/
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.dog_default)
                .setTicker("Walk complete!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Swifto")
                .setContentText("Walk completed!");

		Intent walkIntent = new Intent(getApplicationContext(),
				WalkMapActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, walkIntent, 0);

		/*startWalkNotification.setLatestEventInfo(getApplicationContext(),
				"Swifto", "Walk completed!", pendingIntent);*/
        builder.setContentIntent(pendingIntent);

//		nm.notify(WALK_PROGRESS_NOTIFICATION_ID, startWalkNotification);
        nm.notify(WALK_PROGRESS_NOTIFICATION_ID, builder.build());
	}

	private void removeFinishedWalkNotification() {
		Log.d("nkp" , " removeFinishedWalkNotification is calling");
		NotificationManager nm = getNotificationManager();
		nm.cancel(WALK_PROGRESS_NOTIFICATION_ID);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void addUploadPhotoSuccessNotification() {
		NotificationManager nm = getNotificationManager();

	/*	Notification uploadPhotoSuccessNotification = new Notification(
				R.drawable.green_check, "Photo uploaded succesfully!",
				System.currentTimeMillis());*/
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.green_check)
                .setTicker("Photo uploaded succesfully!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Swifto")
                .setContentText("Photo uploaded succesfully!")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS);

		/*uploadPhotoSuccessNotification.flags = Notification.DEFAULT_LIGHTS
				| Notification.FLAG_AUTO_CANCEL;
*/
		Intent walkIntent = new Intent(getApplicationContext(),
				WalkMapActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, walkIntent, 0);

/*		uploadPhotoSuccessNotification.setLatestEventInfo(
				getApplicationContext(), "Swifto",
				"Photo uploaded succesfully", pendingIntent);*/
        builder.setContentIntent(pendingIntent);

//		nm.notify(UPLOAD_PHOTO_NOTIFICATION_ID, uploadPhotoSuccessNotification);
        nm.notify(UPLOAD_PHOTO_NOTIFICATION_ID, builder.build());
	}

	public void addUploadPhotoFailNotification(String cause) {
		NotificationManager nm = getNotificationManager();
		/*Notification uploadPhotoFailNotification = new Notification(
				R.drawable.red_cross,
				"Photo uploading failed. Please, send the log and try again",
				System.currentTimeMillis());
		uploadPhotoFailNotification.flags = Notification.DEFAULT_LIGHTS
				| Notification.FLAG_AUTO_CANCEL;*/
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.red_cross)
                .setTicker("Photo uploading failed. Please, send the log and try again")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Swifto")
                .setContentText("Photo uploading failed. Please, send the log and try again")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS);

		Intent walkIntent = new Intent(getApplicationContext(),
				WalkMapActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, walkIntent, 0);

		/*uploadPhotoFailNotification.setLatestEventInfo(getApplicationContext(),
				"Swifto",
				"Photo uploading failed. Please, send the log and try again",
				pendingIntent);*/
        builder.setContentIntent(pendingIntent);

//		nm.notify(UPLOAD_PHOTO_NOTIFICATION_ID, uploadPhotoFailNotification);
        nm.notify(UPLOAD_PHOTO_NOTIFICATION_ID, builder.build());
	}

	public void removeUploadPhotoNotification() {
		NotificationManager nm = getNotificationManager();

		nm.cancel(UPLOAD_PHOTO_NOTIFICATION_ID);
	}

	// ------------------------------------------------------
	//
	// GPS service
	//
	// ---------------------

	/*
	 * public ArrayList<FeedMedicine> getFeedMedicineText() {
	 * ArrayList<FeedMedicine> feedMedicineAL = new ArrayList<FeedMedicine>();
	 * Walk currentWalk = getWalkGetter().getCurrentWalk();
	 *
	 * if (currentWalk.DogsToFeedIDs.equals("")) { return feedMedicineAL; }
	 * String[] splitted = currentWalk.DogsToFeedIDs.split(","); List<String>
	 * ids = Arrays.asList(splitted);
	 *
	 * for (Dog d : currentWalk.Dogs) { if (ids.indexOf(d._id) != -1) {
	 * feedMedicineAL.add(new FeedMedicine("Did you remember to feed " + d.Name
	 * + "?",false)); } }
	 *
	 * for (Dog d : currentWalk.Dogs) { if (d.Features.Medication) {
	 * feedMedicineAL.add(new FeedMedicine("Have you given medication to " +
	 * d.Name + " ? (" + d.FeaturesDetails.Medication + ")",true)); } }
	 *
	 * return feedMedicineAL; }
	 */

	public ArrayList<FeedMedicine> getFeedMedicineText() {
		ArrayList<FeedMedicine> feedMedicineAL = new ArrayList<FeedMedicine>();
		Walk currentWalk = getWalkGetter().getCurrentWalk();
		if (currentWalk.Servicess == null) {
			return feedMedicineAL;
		}
		// ToastUtils.showLong(getApplicationContext(),
		// "size of serivices = "+currentWalk.Servicess.size());

		for (int i = 0; i < currentWalk.Servicess.size(); i++) {
			Services srvc = currentWalk.Servicess.get(i);
			try {
				Dog d = currentWalk.Dogs.get(i);
				if (srvc.Feed != null && srvc.Feed) {
					if (srvc.FeedInstructions != null) {
						feedMedicineAL.add(new FeedMedicine(
								"Did you remember to feed " + d.Name + "? "
										+ "" + srvc.FeedInstructions + "",
								false));
					} else {
						feedMedicineAL.add(new FeedMedicine(
								"Did you remember to feed " + d.Name + "?",
								false));
					}

				}
			} catch (Exception e) {
				// if (srvc.Feed != null && srvc.Feed) {
				// feedMedicineAL
				// .add(new FeedMedicine("Did you remember to feed "
				// + srvc.Dog_Id + "? " + "("
				// + srvc.FeedInstructions + ")", false));
				// }
			}
		}
		for (int i = 0; i < currentWalk.Servicess.size(); i++) {
			Services srvc = currentWalk.Servicess.get(i);
			try {
				Dog d = currentWalk.Dogs.get(i);
				if (srvc.Medicate != null && srvc.Medicate) {
					if (srvc.MedicateInstructions != null) {
						feedMedicineAL.add(new FeedMedicine(
								"Have you given medication to " + d.Name
										+ " ? " + srvc.MedicateInstructions
										+ "", true));
					} else {
						feedMedicineAL
								.add(new FeedMedicine(
										"Have you given medication to "
												+ d.Name + " ?", true));
					}

				}
			} catch (Exception e) {
				// feedMedicineAL.add(new FeedMedicine(
				// "Have you given medication to " + srvc.Dog_Id + " ? ("
				// + srvc.MedicateInstructions + ")", true));
			}

		}
		return feedMedicineAL;
	}

	public String getFeedText() {
		Walk currentWalk = getWalkGetter().getCurrentWalk();

		if (currentWalk.DogsToFeedIDs.equals("")) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		String[] splitted = currentWalk.DogsToFeedIDs.split(",");
		List<String> ids = Arrays.asList(splitted);

		List<String> dogsToFeed = new ArrayList<String>();

		for (Dog d : currentWalk.Dogs) {
			if (ids.indexOf(d._id) != -1) {
				dogsToFeed.add(d.Name);

			}
		}

		if (dogsToFeed.size() > 0) {
			for (int i = 0; i < dogsToFeed.size(); i++) {
				if (i == 0) {
					builder.append(String.format(Locale.getDefault(),
							"Don't forget to feed %s", dogsToFeed.get(i)));
				} else {
					if (i != dogsToFeed.size() - 1) {
						builder.append(String.format(Locale.getDefault(),
								", %s", dogsToFeed.get(i)));
					} else {
						builder.append(String.format(Locale.getDefault(),
								" and %s", dogsToFeed.get(i)));
					}
				}
			}

			builder.append(".");

			return builder.toString();
		} else {
			return "";
		}
	}

	public String getMedicationText() {
		Walk currentWalk = getWalkGetter().getCurrentWalk();

		StringBuilder builder = new StringBuilder();

		List<String> dogsToMedicate = new ArrayList<String>();
		int i = 0;
		for (Dog d : currentWalk.Dogs) {
			if (d.Features.Medication) {
				dogsToMedicate.add(d.Name);

				if (i == 0) {
					builder.append(String.format(Locale.getDefault(),
							"Have you given medication to %s ? (%s)", d.Name,
							d.FeaturesDetails.Medication));
				} else {
					if (i != currentWalk.Dogs.size() - 1) {
						builder.append(String.format(Locale.getDefault(),
								", %s ? (%s)", d.Name,
								d.FeaturesDetails.Medication));
					} else {
						builder.append(String.format(Locale.getDefault(),
								" and %s ? (%s)", d.Name,
								d.FeaturesDetails.Medication));
					}
				}
				i++;

				// dog.MedicateInstructions
			}
		}

		// builder.append("?");

		return builder.toString();

		// if(dogsToMedicate.size() > 0) {
		// for(int i = 0; i < dogsToMedicate.size(); i++) {
		// if(i == 0) {
		// builder.append(String.format(Locale.getDefault(),
		// "Have you given medication to %s", dogsToMedicate.get(i)));
		// } else {
		// if(i != dogsToMedicate.size()-1) {
		// builder.append(String.format(Locale.getDefault(), ", %s",
		// dogsToMedicate.get(i)));
		// } else {
		// builder.append(String.format(Locale.getDefault(), " and %s",
		// dogsToMedicate.get(i)));
		// }
		// }
		// }
		//
		// builder.append("?");
		//
		// return builder.toString();
		// } else {
		// return "";
		// }
	}

	public void startWalkProgressServices() {

		String walkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());
		Boolean gpsEnabled = gpsTurnedOn();

		SyslogUtils.logEvent(getApplicationContext(), String.format(
				Locale.getDefault(),
				"Start collecting GPS updates, walkId: %s, gps enabled: %b",
				walkId, gpsEnabled), EnumLogSeverity.INFORMATIONAL,
				EnumLogType.INTERNAL_TYPE);
		SharedPreferencesHelper.setWalkInProcess(getApplicationContext(), true);
		addStartedWalkNotification();


		Walk currentWalk = getWalkGetter().getCurrentWalk();
		//gps = new GPSTracker(getApplicationContext(), SwiftoApplication.this);
		//gps = new GpsTrackerNew(getApplicationContext(), SwiftoApplication.this);

		// Intent intent = new Intent(getApplicationContext(),
		// GPSTracker.class);
		// intent.putExtra(Service.KEY_TEST, "123456789");
		// startService(intent);

		// check if GPS enabled
//		Location lc = new Location("Ower Address");
//		lc.setLatitude(currentWalk.Owner.StaticLocation.Location
//				.getLatitudeE6());
//		lc.setLongitude(currentWalk.Owner.StaticLocation.Location
//				.getLongitudeE6());


		//try
		//{
		//if (gps.canGetLocation()) {

			//locationChanged(gps.getLocation());

		//if (mResumedWalkMapActivity != null) {
			//Toast.makeText(getApplicationContext()," startLocationUpdates started ",Toast.LENGTH_LONG).show();
			//mResumedWalkMapActivity.startLocationUpdates();
		//}

		//else
		//{
			//Toast.makeText(getApplicationContext()," mResumedWalkMapActivity = Null ",Toast.LENGTH_LONG).show();
		//}

			//double latitude = gps.getLatitude();
			//double longitude = gps.getLongitude();

			// \n is for new line
			/*
			 * Toast.makeText( getApplicationContext(),
			 * "Your Location is - \nLat: " + latitude + "\nLong: " + longitude,
			 * Toast.LENGTH_LONG).show();
			 */
		//} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			//gps.showSettingsAlert();
		//}

		//}
		//catch (Exception e)
		//{
			//ToastUtils.showLong(getApplicationContext(),"error = "+e.toString());
		//}
		/*
		 * // wait some time...to start getting GPS coordinates AlarmManager
		 * alarmManager = (AlarmManager) getApplicationContext()
		 * .getSystemService(Context.ALARM_SERVICE); Intent i = new
		 * Intent(getApplicationContext(), GPSAlarmBroadcastReceiver.class);
		 * PendingIntent pii =
		 * PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
		 * 
		 * //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, //
		 * SystemClock.elapsedRealtime() + 3000, pii);
		 * 
		 * alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		 * SystemClock.elapsedRealtime() + 100, pii);
		 */

	}

	public Boolean gpsTurnedOn() {
		LocationManager mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return mgr == null ? false : mgr
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public void stopWalkProgressServices() {
		String walkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		SyslogUtils.logEvent(getApplicationContext(),
				"Stop collecting GPS updates, walkID: " + walkId,
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		//Log.d("stopWalkProgressService" , "nkp is calling walk id "+ walkId);

		/*
		 * // stop getting GPS coordinates... Intent gpsIntent = new
		 * Intent(getApplicationContext(), GPSLocationIntentService.class);
		 * LocationManager mgr = (LocationManager) getApplicationContext()
		 * .getSystemService(Context.LOCATION_SERVICE); PendingIntent pi =
		 * PendingIntent.getService(getApplicationContext(), 0, gpsIntent, 0);
		 * 
		 * mgr.removeUpdates(pi);
		 * 
		 * // ..and stop timer, which "wakes" getting GPS coordinates
		 * AlarmManager alarmManager = (AlarmManager)
		 * getSystemService(Context.ALARM_SERVICE); Intent i = new
		 * Intent(getApplicationContext(), GPSAlarmBroadcastReceiver.class);
		 * PendingIntent pii =
		 * PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
		 * alarmManager.cancel(pii);
		 */
		if (mResumedWalkMapActivity != null) {
		//Toast.makeText(getApplicationContext()," startLocationUpdates started ",Toast.LENGTH_LONG).show();
			mResumedWalkMapActivity.stopLocationUpdates();
			//Log.d("stopWalkProgressService" , "nkp mResumedWalkMapActivity is calling");
			SyslogUtils.logEvent(getApplicationContext(),
					"Stop stopLocationUpdates GPS updates, walkID: " + walkId,
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		}
		else
		{
			SyslogUtils.logEvent(getApplicationContext(),
					"Error on Stop stopLocationUpdates GPS updates, walkID: " + walkId,
					EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
		}


		removeFinishedWalkNotification();

		try {
			if (wl != null && wl.isHeld()) {
				wl.release();
			}
		} catch (Exception e) {

		}
	}

	public boolean locationChanged(Location location) {

		// check accuracy
		// if good:
		// --------clear gps points for one session in database
		// --------send this point
		// if bad:
		// --------check - do we reach the limit of points
		// --------if no:
		// ----------------insert received point to db
		// ----------------increment attempts counter
		// --------if yes:
		// ----------------insert received point to db
		// ----------------select best point from session
		// ----------------clear gps points for one session in database
		// ----------------send best point
		// ----------------reset attempts counter

		if (location.getAccuracy() > SharedPreferencesHelper
				.getThreshold(getApplicationContext())) {
			int currentNumberOfPoints = getDbAdapter()
					.selectCountOfGpsPointsForOneSession();
			currentNumberOfPoints++;

				SyslogUtils.logEvent(getApplicationContext(), String.format(
								Locale.getDefault(), "%d point, accuracy = %f",
								currentNumberOfPoints, location.getAccuracy()),
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);


			if (currentNumberOfPoints < SharedPreferencesHelper
					.getMaxAttemptsToFindPoint(getApplicationContext())) {
				// insert the new point
				getDbAdapter().insertGpsPointForOneSession(
						(int) (location.getLatitude() * 1E6),
						(int) (location.getLongitude() * 1E6),
						location.getTime(), location.getAccuracy());

				return false;
			} else {
				// insert the new point
				getDbAdapter().insertGpsPointForOneSession(
						(int) (location.getLatitude() * 1E6),
						(int) (location.getLongitude() * 1E6),
						location.getTime(), location.getAccuracy());

				// get the "best" point in session
				WalkGPSPoint bestPoint = getDbAdapter()
						.selectBestGpsPointFromSession();

				// clean session's points
				getDbAdapter().deleteAllGpsPointsForOneSession();
				correctLocationReceivedExt(bestPoint);
				return true;
			}
		} else {
			if(isDetailLog)
				SyslogUtils.logEvent(getApplicationContext(), String.format(
						Locale.getDefault(), "Accuracy = %f is fine",
						location.getAccuracy()), EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);

			WalkGPSPoint bestPoint = new WalkGPSPoint(
					(int) (location.getLatitude() * 1E6),
					(int) (location.getLongitude() * 1E6), location.getTime());

			bestPoint.Accuracy = location.getAccuracy();
			getDbAdapter().deleteAllGpsPointsForOneSession();
			correctLocationReceivedExt(bestPoint);
			return true;
		}
	}

	private void correctLocationReceivedExt(WalkGPSPoint point) {
		addPointAndSendIfNeededExt(point);

		if (mResumedWalkMapActivity != null) {
			mResumedWalkMapActivity.locationChangedExt(point);
		}
	}

	// --------------------------------------------------------------------
	//
	// Upload photo
	//
	// ----------------------

	private final AtomicBoolean mIsUploadingToAmazon = new AtomicBoolean(false);

	public boolean getIsUploadingToAmazon() {
		return mIsUploadingToAmazon.get();
	}

	private String mDogIdForPhoto;
	private File mfile;

	public void setDogIdForPhoto(String dogId) {
		mDogIdForPhoto = dogId;
	}

	public void uploadToAmazon(File file) {

		mfile = file;
		// ToastUtils.showLong(getApplicationContext(),
		// "uploadToAmazon started");

		if(file==null)
		{
			return;
		}



//		final String startedWalkId = SharedPreferencesHelper
//				.getStartedWalkId(getApplicationContext());
//
		String path = SharedPreferencesHelper.getWalkImage(getApplicationContext());
		String processPath = SharedPreferencesHelper.getWalkImageProcess(getApplicationContext());
//


//
		for(int i =0 ; i <imageProcessList.size();i++)
		{

			if(imageProcessList.get(i).getFile().getAbsolutePath().equals(file.getAbsolutePath()))
			{
				SyslogUtils.logEvent(getApplicationContext(),
						"Reject due to same file already uploaded on server , old  path  is  " + imageProcessList.get(i).getFile().getAbsolutePath() + " new path is " + file.getAbsolutePath()+ " checkActiveImage = "+checkActiveImage(),
						EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);

				if(!checkActiveImage())
				{
					offlineMsgNWalkChange();
				}

				//return;
			}
		}


		for(int i =0 ; i <imageSuccessList.size();i++)
		{

			if(imageSuccessList.get(i).getFile().getAbsolutePath().equals(file.getAbsolutePath()))
			{
				SyslogUtils.logEvent(getApplicationContext(),
						"Reject due to same file trying to upload twice , old  path  is  " + imageSuccessList.get(i).getFile().getAbsolutePath() + " new path is " + file.getAbsolutePath()+ " checkActiveImage = "+checkActiveImage(),
						EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);

				if(!checkActiveImage())
				{
					offlineMsgNWalkChange();
				}

				return;
			}
		}

//		if(path.equals(file.getAbsolutePath()))
//		{
//			SyslogUtils.logEvent(getApplicationContext(),
//					"Reject due to same file already uploaded on server, old  path  is  " + path + " new path is " + file.getAbsolutePath() + " checkActiveImage = "+checkActiveImage(),
//					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
//
//			if(!checkActiveImage())
//			{
//				offlineMsgNWalkChange();
//			}
//
//			return;
//		}

//		if(processPath.equals(file.getAbsolutePath()))
//		{
//			SyslogUtils.logEvent(getApplicationContext(),
//					"Reject due to same file trying to upload twice , old  path  is  " + processPath + " new path is " + file.getAbsolutePath()+ " checkActiveImage = "+checkActiveImage(),
//					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
//
//			if(mResumedWalkMapActivity!=null)
//			{
//
//			}
//
//			if(!checkActiveImage())
//			{
//				offlineMsgNWalkChange();
//			}
//
//			return;
//		}


		Walk currentWalk = getWalkGetter().getCurrentWalk();

		SharedPreferencesHelper.saveWalkImageByDog(getApplicationContext(), currentWalk._id, mDogIdForPhoto);


		if (!ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), "No internet connection",
					Toast.LENGTH_SHORT).show();
			SyslogUtils.logEvent(getApplicationContext(),
					"No internet connection on " + "trying to upload photo and filepath is " + file.getAbsolutePath(),
					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
			taskMap.put("photo upload", file);

			if(!checkFile(file)) {
				imageList.add(new ImageFileHolder("Active", file));
			}

			SyslogUtils.logEvent(getApplicationContext(),
					"Debug log   size of imageList is " + imageList.size() + "  when  net connection is off " +
							"  and image list is " + imageList.toString(),
					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);

			//mResumedWalkMapActivity.checkPhoto(currentWalk._id);

			return;
		}

		imageProcessList.add(new ImageFileHolder("Process", file));

		//SharedPreferencesHelper.saveWalkImageProcess(getApplicationContext(), file.getAbsolutePath());

		try {

			String memoryInfo = MemoryUtil.getMemoryInfo(getApplicationContext());
			SyslogUtils.logEvent(getApplicationContext(), String.format(
							Locale.getDefault(), "uploadToAmazon, file is %s, memory: %s",
							file.getAbsolutePath(), memoryInfo),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			removeUploadPhotoNotification();

			mIsUploadingToAmazon.set(true);

			BaseTask task = new UploadPhotoTask(file, mDogIdForPhoto,
					SwiftoApplication.this);

			ListenableFuture<String> futureTask = SwiftoApplication.uploadPhotoService
					.enqueueTask(task);
			Futures.addCallback(futureTask, new FutureCallback<String>() {
				public void onFailure(Throwable exception) {
					mIsUploadingToAmazon.set(false);
					String memoryInfo = MemoryUtil
							.getMemoryInfo(getApplicationContext());
					SyslogUtils.logEvent(
							getApplicationContext(),
							String.format(
									Locale.getDefault(),
									"uploadToAmazon failure, exception is %s, memory: %s , mDogIdForPhoto is %s , file is % ",
									exception.toString(), memoryInfo,mDogIdForPhoto , mfile.getAbsolutePath()),
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);

					saveFile(
							String.format(
									Locale.getDefault(),
									"uploadToAmazon failure, exception is %s, memory: %s",
									exception.toString(), memoryInfo),
							"uploadToAmazon_failure");

					parseUploadPhotoResponse(EnumAsyncTasksErrors.UPLOAD_PHOTO_FAILURE_EXCEPTION);
				}

				public void onSuccess(String response) {
					mIsUploadingToAmazon.set(false);
					parseUploadPhotoResponse(response);
				}
			});
		} catch (RejectedExecutionException exception) {
			mIsUploadingToAmazon.set(false);
			String memoryInfo1 = MemoryUtil
					.getMemoryInfo(getApplicationContext());
			SyslogUtils.logEvent(getApplicationContext(), String.format(
					Locale.getDefault(),
					"uploadToAmazon failure, exception is %s, memory: %s , mDogIdForPhoto is %s , file is %",
					exception.toString(), memoryInfo1,mDogIdForPhoto , mfile.getAbsolutePath()),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			saveFile(String.format(Locale.getDefault(),
					"uploadToAmazon failure, exception is %s, memory: %s",
					exception.toString(), memoryInfo1),
					"uploadToAmazon_failure_1");

			parseUploadPhotoResponse(EnumAsyncTasksErrors.UPLOAD_PHOTO_FAILURE_EXCEPTION);
		}

		// if(mResumedWalkMapActivity!=null)
		// {
		// }
		//
		// else
		// {
		// ToastUtils.showLong(getApplicationContext(),
		// "mResumedWalkMapActivity is null");
		// }

	}

	public void uploadVideoToAmazon(File file) {
		String memoryInfo = MemoryUtil.getMemoryInfo(getApplicationContext());
		SyslogUtils.logEvent(getApplicationContext(), String.format(
				Locale.getDefault(), "uploadToAmazon, file is %s, memory: %s",
				file.getAbsolutePath(), memoryInfo),
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		removeUploadPhotoNotification();

		mIsUploadingToAmazon.set(true);

		BaseTask task = new UploadVideoTask(file, mDogIdForPhoto, this);
		try {
			ListenableFuture<String> futureTask = SwiftoApplication.uploadPhotoService
					.enqueueTask(task);
			Futures.addCallback(futureTask, new FutureCallback<String>() {
				public void onFailure(Throwable exception) {
					mIsUploadingToAmazon.set(false);
					String memoryInfo = MemoryUtil
							.getMemoryInfo(getApplicationContext());
					SyslogUtils.logEvent(
							getApplicationContext(),
							String.format(
									Locale.getDefault(),
									"uploadToAmazon failure, exception is %s, memory: %s",
									exception.toString(), memoryInfo),
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);

					saveFile(
							String.format(
									Locale.getDefault(),
									"uploadToAmazon failure, exception is %s, memory: %s",
									exception.toString(), memoryInfo),
							"uploadToAmazon_failure");

					parseUploadPhotoResponse(EnumAsyncTasksErrors.UPLOAD_PHOTO_FAILURE_EXCEPTION);
				}

				public void onSuccess(String response) {
					mIsUploadingToAmazon.set(false);
					parseUploadPhotoResponse(response);
				}
			});
		} catch (RejectedExecutionException exception) {
			mIsUploadingToAmazon.set(false);
			String memoryInfo1 = MemoryUtil
					.getMemoryInfo(getApplicationContext());
			SyslogUtils.logEvent(getApplicationContext(), String.format(
					Locale.getDefault(),
					"uploadToAmazon failure, exception is %s, memory: %s",
					exception.toString(), memoryInfo1),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			saveFile(String.format(Locale.getDefault(),
					"uploadToAmazon failure, exception is %s, memory: %s",
					exception.toString(), memoryInfo1),
					"uploadToAmazon_failure_1");

			parseUploadPhotoResponse(EnumAsyncTasksErrors.UPLOAD_PHOTO_FAILURE_EXCEPTION);
		}
	}

	private void parseUploadPhotoResponse(String response) {
		// TODO: do we need to delete photo files?

		saveFile(response, "PhotoResponce_" + response.substring(0, 5));
		saveFile(response, "PhotoResponce");
		mIsUploadingToAmazon.set(false);

		// need to cancel and reset state if we already stopped a walk
		if (SharedPreferencesHelper.getWalkIsInProcess(getApplicationContext()) == false) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"Cancelling handling upload photo because there is no walk in progress",
							EnumLogSeverity.WARNING, EnumLogType.INTERNAL_TYPE);

			Log.i("vivek hack1",
					"Cancelling handling upload photo because there is no walk in progress");

			UploadPhotoStatus.getInstance().setStatusNotUploading();
			return;
		}

		String error = TaskErrorChecker.getErrorInString(response);

		if (error != null) {

			//addUploadPhotoFailNotification(error);
			SyslogUtils.logEvent(getApplicationContext(),
					"Photo was not uploaded: " + error, EnumLogSeverity.ERROR,
					EnumLogType.SERVER_TYPE);

			Log.i("vivek hack2", "Photo was not uploaded: " + error);

			if (UploadPhotoStatus.getInstance().getStatus()
					.equals(EnumUploadPhotoStatus.UPLOADING_POPUP)) {
				getDbAdapter()
						.updateSomeFlagOfStartedWalk(
								SharedPreferencesHelper
										.getStartedWalkId(getApplicationContext()),
								TableColumns.StartedWalksColumns.PHOTO_UPLOAD_TRIED_ONCE,
								true);
			}

			if (mResumedWalkMapActivity != null) {
				Log.i("vivek hack3", "mResumedWalkMapActivity is null ");
				mResumedWalkMapActivity.onUploadPhotoFailure(error);
			} else {
				Log.i("vivek hack4", "mResumedWalkMapActivity is NOT null ");
				UploadPhotoStatus.getInstance().setStatusNotUploading();
			}
		} else {

			if (taskMap.containsKey("photo upload")) {
				taskMap.remove("photo upload");
			}

			//ToastUtils.showLong(getApplicationContext()," response = " + response);
			addUploadPhotoSuccessNotification();

			Log.i("vivek hack5", "Photo was uploaded. Response: " + response);
			cancelHalfWalkAlarm();
			SyslogUtils.logEvent(getApplicationContext(),
					"Photo was uploaded. Response: " + response,
					EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);

			// ToastUtils.showLong(getApplicationContext(),
			// "Photo was not uploaded: " + error);

			getDbAdapter()
					.updateSomeFlagOfStartedWalk(
							SharedPreferencesHelper
									.getStartedWalkId(getApplicationContext()),
							TableColumns.StartedWalksColumns.PHOTO_UPLOADED,
							true);

			if (mResumedWalkMapActivity != null) {
				mResumedWalkMapActivity.onUploadPhotoSuccess();
			} else {
				UploadPhotoStatus.getInstance().setStatusNotUploading();
			}
		}
	}

	public void isGpsShow(boolean isShow) {
		return;  // vivek just comment code for gps code
//		if (mResumedWalkMapActivity != null) {
//			mResumedWalkMapActivity.showGpsError(isShow, true);
//		}
	}

	public void isDeviceOff(boolean isOff) {
		return; // vivek just comment code for gps code
//		if (mResumedWalkMapActivity != null) {
//			mResumedWalkMapActivity.setDeviceOff(isOff);
//		}
	}

	public int picincr = 0;
	public static HashMap<String, Object>  taskMap = new HashMap<String, Object>();

	public static ArrayList<ImageFileHolder> imageList = new ArrayList<ImageFileHolder>();
	public static ArrayList<ImageFileHolder> imageProcessList = new ArrayList<ImageFileHolder>();
	public static ArrayList<ImageFileHolder> imageSuccessList = new ArrayList<ImageFileHolder>();
//	public boolean isImage = false;
//	public boolean isPointsAndMeet = false;
//	public boolean isSendMessage = false;
//	public boolean isCurrentWalk = false;
	public int Delay = 0;

	public int imagedelay = 0 ;
	public void networkChanged(boolean isNetWork) {
		if (isNetWork) {
			// taskMap.put("photo upload", file);

			if (taskMap.containsKey("poop_notification")) {

				ToastUtils.showLong(getApplicationContext(), "Poop Notification text sent to Client");
				taskMap.remove("poop_notification");
			}

			for (int i = 0; i < taskMap.size(); i++) {
				if (taskMap.containsKey("photo upload")) {
					try {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {

								if(imageList.size()>0) {
									uploadToAmazon(imageList.get(0).getFile());
									imagedelay = imageList.size() * 2000;

								}
								else
								{
									uploadToAmazon((File) taskMap.get("photo upload"));
								}
							}
						}, Delay);

						Delay = 7000 + imagedelay;

					} catch (Exception exception) {
						saveFile(exception.toString(),
								"imageUploadAfterNetchangedFail");
					}

				}
			}
		}
	}

	public void offlineMsgNWalkChange() {

		SyslogUtils.logEvent(getApplicationContext(),
				"offlineMsgNWalkChange function called",
				EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
		Delay = 0;
		for (int i = 0; i < taskMap.size(); i++) {
//			if (taskMap.containsKey("sendUnsentPointsAndMeetPoos")) {
//				try {
////					new Handler().postDelayed(new Runnable() {
////						@Override
////						public void run() {
////							sendUnsentPointsAndMeetPoos();
////						}
////					}, Delay);
//
//
//					mResumedWalkMapActivity.runOnUiThread(new Runnable() {
//						public void run() {
//
//							sendUnsentPointsAndMeetPoos();
//						}
//					});
//
//					//Delay = 4000;
//
//				} catch (Exception exception) {
//					saveFile(exception.toString(),
//							"sendUnsentPointsAndMeetPoosFail");
//
//					SyslogUtils.logEvent(getApplicationContext(),
//							"sendUnsentPointsAndMeetPoosFail   error - "+ exception.toString(),
//							EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
//				}
//
//			}

			if (taskMap.containsKey("sendMessage")) {
				try {
//					new Handler().postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							sendMessage(getApplicationContext(),
//									(String) taskMap.get("sendMessage"));
//						}
//					}, Delay);
//
//					Delay = 4000;

					mResumedWalkMapActivity.runOnUiThread(new Runnable() {
						public void run() {

							sendMessage(getApplicationContext(),
									(String) taskMap.get("sendMessage"));
						}
					});

				} catch (Exception exception) {
					saveFile(exception.toString(), "sendMessageFail");
					SyslogUtils.logEvent(getApplicationContext(),
							"sendMessageFail   error - " + exception.toString(),
							EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
				}

			}

//			if (mResumedWalkMapActivity == null && taskMap.containsKey("stopCurrentWalk")) {
//				try {
//
////					new Handler().postDelayed(new Runnable() {
////						@Override
////						public void run() {
////							getOneWalkState();
////						}
////					}, Delay);
//
//
//					mResumedWalkMapActivity.runOnUiThread(new Runnable() {
//						public void run() {
//
//							getOneWalkState();
//						}
//					});
//
//				} catch (Exception exception) {
//
//					saveFile(exception.toString(), "stopCurrentWalkFail");
//
//					SyslogUtils.logEvent(getApplicationContext(),
//							"stopCurrentWalkFail   error - " + exception.toString(),
//							EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
//				}
//
//			}
		}
	}


	public boolean checkActiveImage()
	{
		if(imageList.size()==0)
		{
			return true;
		}
		for(int i = 0; i < imageList.size(); i++) {
			if (imageList.get(i).getStatus().equals("Active"))
			{
				return true;
			}
		}
		return false;
	}

	public boolean checkFile(File file)
	{
		for(int i = 0; i < imageList.size(); i++) {
			if (imageList.get(i).getFileWithoutStatusChanged().getAbsolutePath().equals(file.getAbsolutePath())) {
				return true;
			}
		}
		return false;
	}

}
