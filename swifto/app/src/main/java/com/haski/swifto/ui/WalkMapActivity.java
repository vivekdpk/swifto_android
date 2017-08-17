package com.haski.swifto.ui;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidquery.AQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.database.TableColumns;
import com.haski.swifto.interfaces.IDialogResultWaitable;
import com.haski.swifto.interfaces.ISendMessageResultNotifiable;
import com.haski.swifto.interfaces.ISendUnsentRequestsWaitable;
import com.haski.swifto.interfaces.IStopWalkRequestNotifiable;
import com.haski.swifto.interfaces.IUploadPhotoNotifiable;
import com.haski.swifto.interfaces.IWalkProgressNotifiable;
import com.haski.swifto.model.EnumUploadPhotoStatus;
import com.haski.swifto.model.GeoPointExtWithData;
import com.haski.swifto.model.UploadPhotoStatus;
import com.haski.swifto.model.UploadVideoStatus;
import com.haski.swifto.model.vo.CheckPhotoConfig;
import com.haski.swifto.model.vo.FeedMedicine;
import com.haski.swifto.model.vo.GPSConfig;
import com.haski.swifto.model.vo.ImageFileHolder;
import com.haski.swifto.model.vo.StartedWalkExt;
import com.haski.swifto.model.vo.WalkGPSPoint;
import com.haski.swifto.model.vo.dog.Dog;
import com.haski.swifto.model.vo.json.CheckPhotoParser;
import com.haski.swifto.model.vo.json.GetGpsConfigParser;
import com.haski.swifto.model.vo.walk.EnumWalkTypes;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.server.ServerRequestAsynkTask;
import com.haski.swifto.server.SwiftoRequestBuilder;
import com.haski.swifto.service.GPSTracker;
import com.haski.swifto.ui.mapOverlays.ActionsOverlay;
import com.haski.swifto.ui.mapOverlays.GeoPointedBitmap;
import com.haski.swifto.ui.mapOverlays.MarkerOverlay;
import com.haski.swifto.ui.mapOverlays.PathOverlay;
import com.haski.swifto.util.AlertUtils;
import com.haski.swifto.util.CalendarUtils;
import com.haski.swifto.util.IntentUtils;
import com.haski.swifto.util.ServiceUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.StorageUtils;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.TaskErrorChecker;
import com.haski.swifto.util.ToastUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

//import com.google.android.gms.maps.MapFragment;

public class  WalkMapActivity extends BaseActivity implements
		IWalkProgressNotifiable, ISendMessageResultNotifiable,
		IStopWalkRequestNotifiable, OnCameraChangeListener,
		IUploadPhotoNotifiable, ISendUnsentRequestsWaitable,
		com.google.android.gms.location.LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener{

	public float angle = 0;
	public String imageFileName = "";
	public String videoFileName = "";
	public static final int MEDIA_TYPE_VIDEO = 2;
	private Uri fileUri;
	public boolean isPhotoTaken = false;

	public static File resultFile;

	public boolean isimagepopupshowing = false;
	public boolean isPopupShowing = false;

	ActionBar actionbar;

	private static final String TAG = "LocationActivity";

	private static final long INTERVAL = 1000 * 15;
	private static final long INTERVAL_OVERNIGHT = 1000 * 5 * 60;
	private static final long FASTEST_INTERVAL = 1000 * 6;
	private static final long FASTEST_INTERVAL_OVERNIGHT = 1000 * 3 * 60;
	Button btnFusedLocation;
	TextView tvLocation;
	LocationRequest mLocationRequest;
	GoogleApiClient mGoogleApiClient;
	Location mCurrentLocation;
	String mLastUpdateTime;
	public static boolean isPhotoCancle = false;

	public Location lastRightLocation = null;

	public boolean isWalkPressed = false;

	public static String isokpressed = "no";

	//nkp
	private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;

	protected void createLocationRequest() {
		Walk currentStartedWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getStartedWalk();
		mLocationRequest = new LocationRequest();
		//nkp
		if(currentStartedWalk.WalkType.equals("overnight")){
			mLocationRequest.setInterval(INTERVAL_OVERNIGHT);
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL_OVERNIGHT);
		}else {
			mLocationRequest.setInterval(INTERVAL);
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		}
		mLocationRequest.setSmallestDisplacement(0);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	private boolean isGooglePlayServicesAvailable() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == status) {
			return true;
		} else {
			GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
			return false;
		}
	}



	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());

		//ToastUtils.showLong(getApplicationContext()," Map connected");

		//nkp
		// Here, thisActivity is the current activity
//		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//				ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//			// Should we show an explanation?
//			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//					Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//				// Show an expanation to the user *asynchronously* -- don't block
//				// this thread waiting for the user's response! After the user
//				// sees the explanation, try again to request the permission.
//
//			} else {
//
//				// No explanation needed, we can request the permission.
//
//				ActivityCompat.requestPermissions(this,
//						new String[]{Manifest.permission.READ_CONTACTS},
//						MY_PERMISSIONS_REQUEST_FINE_LOCATION);
//
//				// MY_PERMISSION_REQUEST_READ_FINE_LOCATION is an
//				// app-defined int constant. The callback method gets the
//				// result of the request.
//			}
//		}
		startLocationUpdates();
	}

	public void startLocationUpdates() {


		PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
		//Toast.makeText(getApplicationContext()," startLocationUpdates started in walkmapactivity",Toast.LENGTH_LONG).show();
		Log.d(TAG, "Location update started ..............: ");
	}


	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(TAG, "Connection failed: " + connectionResult.toString());
	}

	@Override
	public void onLocationChanged(Location location) {

		String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		if(startedWalkId.equals(SharedPreferencesHelper.DEFAULT_STARTED_WALK_ID))
		{
			stopLocationUpdates();
			minPassed = 0;
			resultFile = null;
			SyslogUtils.logEvent(getApplicationContext(),
					"onLocationChanged still calling for  walk id = "+startedWalkId+". we have to stop it manually .......................",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			return;
		}
		else {


			if(lastRightLocation == null) {

				if (location.getAccuracy() < 25.0 && location.getSpeed() < 6.95) {
					//Do something
					//ToastUtils.showLong(mContext, "accurate location = " + location.getAccuracy());

					//lastRightLocation = location;
					mTxtGpsLoading.setVisibility(View.GONE);
					Log.d(TAG, "Firing onLocationChanged..............................................");
					//Toast.makeText(getApplicationContext()," onLocationChanged "+location,Toast.LENGTH_LONG).show();
					mCurrentLocation = location;
					mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
					//updateUI();

					if (mApplication != null) {
						mApplication.locationChanged(location);
					} else {
						ToastUtils.showLong(mContext, "mApplication = null");
					}

				} else {

					//ToastUtils.showLong(mContext, "accurate location = " + location.getAccuracy());

					SyslogUtils.logEvent(getApplicationContext(),
							"This time gps not shown because lower accuracy  = " + location.getAccuracy(),
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
				}
			}

			else
			{

				if(isBetterLocation(location ,lastRightLocation )) {
					if (location.getAccuracy() < 19.0 && location.getSpeed() < 6.95) {
						//Do something
						//ToastUtils.showLong(mContext, "accurate location = " + location.getAccuracy());

						//lastRightLocation = location;
						mTxtGpsLoading.setVisibility(View.GONE);
						Log.d(TAG, "Firing onLocationChanged..............................................");
						//Toast.makeText(getApplicationContext()," onLocationChanged "+location,Toast.LENGTH_LONG).show();
						mCurrentLocation = location;
						mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
						//updateUI();

						if (mApplication != null) {
							mApplication.locationChanged(location);
						} else {
							ToastUtils.showLong(mContext, "mApplication = null");
						}

					} else {

						//ToastUtils.showLong(mContext, "accurate location = " + location.getAccuracy());

						SyslogUtils.logEvent(getApplicationContext(),
								"This time gps not shown because lower accuracy  = " + location.getAccuracy(),
								EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
					}
				}

			}
		}
	}



	public void stopLocationUpdates() {
		try {
			if(mGoogleApiClient.isConnected())

			{
				LocationServices.FusedLocationApi.removeLocationUpdates(
						mGoogleApiClient, this);
				Log.d(TAG, "Location update stopped .......................");

				SyslogUtils.logEvent(getApplicationContext(),
						"Location update stopped .......................",
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			}
		}
		catch(Exception e)
		{
			SyslogUtils.logEvent(getApplicationContext(),
					"error when stopLocationUpdates "+e.toString(),
					EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
		}
	}


//	@Override
//	public void onProviderDisabled(String provider) {
//		ToastUtils.showLong(mContext, "Swifto onProviderDisabled "+provider);
//
//		if(mApplication!=null)
//		{
//
//			showGpsError(true);
//		}
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//		ToastUtils.showLong(mContext, "Swifto onProviderEnabled " + provider);
//
//		if(mApplication!=null)
//		{
//			showGpsError(false);
//		}
//	}

	@Override
	public void onBackPressed() {
		if (!this.isFinishing()) {
			if (popupWindowTakePhoto != null
					&& popupWindowTakePhoto.isShowing()) {
				popupWindowTakePhoto.dismiss();
			} else {
				Toast.makeText(getApplicationContext(),
						"Use top buttons for navigation", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public void onCameraChange(CameraPosition arg0) {

	}

	private Activity mContext;

	/*
	 * @Override protected boolean isRouteDisplayed() { return false; }
	 */

	private MapController mapController;
	private SwiftoApplication mApplication;

	protected Button btNavLeft;
	protected Button btNavRight;
	protected Button btMenuList;
	protected TextView tvTitle;

	// private MapView mapView;
	private GoogleMap googleMap;
	private PolylineOptions polylineOptions;
	private ImageButton btPlay;
	private ImageButton btPoo;
	private Button btTakePicture, btText;
	private Button btTakeVideo;
	private ProgressBar mProgressWalk;
	// private ProgressBar mProgressPhotoUploading;
	private TextView mTxtCountdown;

	private LinearLayout mLayoutSearching;
	private RelativeLayout mLayoutButtons;

	private TextView mTxtDogNames;
	private TextView mTxtWalkStatus;

	private TextView mTxtGpsOff;
	private TextView mTxtGpsLoading;
	private Button btStopWalk;

	private Bitmap pinPoo;
	private Bitmap pinMeet;
	GPSTracker gps;

	Marker myPositionMarker;

	private PathOverlay mPathOverlay;
	private ActionsOverlay mActionsOverlay;
	private MarkerOverlay mMarkerOverlay;

	private TranslateAnimation mAnimMoveDown;
	private TranslateAnimation mAnimMoveUp;

	private int PROGRESS_INCREMENT_VALUE_IN_SECONDS = 1;
	// private int REQUEST_CODE_BUTTON_TAKE_PHOTO = 712985419;
	// private int REQUEST_CODE_POPUP_PHOTO = 914589127;
	//
	// private int REQUEST_CODE_BUTTON_TAKE_VIDEO = 712985420;
	// private int REQUEST_CODE_POPUP_VIDEO = 914589128;

	private int REQUEST_CODE_BUTTON_TAKE_PHOTO = 19;
	private int REQUEST_CODE_POPUP_PHOTO = 27;

	private int REQUEST_CODE_BUTTON_TAKE_VIDEO = 20;
	private int REQUEST_CODE_POPUP_VIDEO = 28;

	public long actualTimeRemaining;

	public static String isHalfwalk  = "no";
	public static String isAfterwalk  = "no";

	public ArrayList swiftoImageArray = new ArrayList();


	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_walk_map);

		mApplication = (SwiftoApplication) getApplication();
		mApplication.resumeWalkMapActivity(this);


		if (!isGooglePlayServicesAvailable()) {
			finish();
		}
		createLocationRequest();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.window_title);

		actionbar = getActionBar();

		if (actionbar == null) {

		} else {
			setSlidingActionBarEnabled(false);
			// Toast.makeText(getApplicationContext(), "Actionbar ok",
			// 1).show();
			final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
					.inflate(R.layout.window_title, null);

			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayHomeAsUpEnabled(false);
			actionbar.setDisplayShowTitleEnabled(false);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setIcon(android.R.color.transparent);
			actionbar.setCustomView(actionBarLayout);
		}
		try {
			String startwalk = getIntent().getStringExtra("newwalk");
			if (startwalk != null && startwalk.equals("yes")) {
				minPassed = 0;
				resultFile = null;
			}
		} catch(Exception e)
		{

		}
		//walkMapIntent.putExtra("newwalk", "yes");


		mContext = this;
		//mApplication = (SwiftoApplication) getApplication();

		if (!SharedPreferencesHelper.getWalkIsInProcess(this)) {
			SyslogUtils.logEvent(getApplicationContext(),
					"WalkMap created when there are no walks in process",
					EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			finish();
			return;
		} else {
			Walk currentStartedWalk = ((SwiftoApplication) getApplication())
					.getWalkGetter().getStartedWalkWithoutOwnerAndDogs();

			if (currentStartedWalk == null) {
				SyslogUtils
						.logEvent(
								getApplicationContext(),
								"WalkMap created, there is walk in process, but there is no related walk in database. Walk id: "
										+ SharedPreferencesHelper
												.getStartedWalkId(this),
								EnumLogSeverity.ERROR,
								EnumLogType.INTERNAL_TYPE);
				finish();
				return;
			} else {
				initViews();
				initValues();
				initAnimations();
			}
		}

		//String startedWalkId = SharedPreferencesHelper.getStartedWalkId(this);

		//ToastUtils.showLong(getApplicationContext()," on create = " );

		// we with Tamara are always went like para
		//StartedWalkExt startedWalkExt = mApplication.getDbAdapter()
		//		.getStartedWalkForId(startedWalkId);
		//refreshViews(startedWalkExt, currentStartedWalk,
		//		false);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	private static final String AFTER_PHOTO_TOKEN_STATUS = "AFTER_PHOTO_TOKEN_STATUS";



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(AFTER_PHOTO_TOKEN_STATUS, mAfterPhotoTokenStatus.get());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			int afterPhotoTokenStatus = savedInstanceState
					.getInt(AFTER_PHOTO_TOKEN_STATUS);
			mAfterPhotoTokenStatus.set(afterPhotoTokenStatus);
		}
	}

	@Override
	public void onStart() {

		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();

		// TODO: move to onStop() ?
		//((SwiftoApplication) getApplication()).pauseWalkMapActivity();
	}

	@Override
	public void onResume() {
		super.onResume();

//		if(gps==null) {
//			gps = new GPSTracker(getApplicationContext(), (SwiftoApplication) getApplication());
//		}

		if(!isWalkPressed) {
			if (mGoogleApiClient.isConnected()) {
				startLocationUpdates();
				Log.d(TAG, "Location update resumed .....................");
			} else {
				createLocationRequest();
			}
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			SyslogUtils
					.logEvent(
							this,
							"Error: orientation is landscape in WalkMapActivity. Changing to portrait",
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		mContext = this;

		mApplication = (SwiftoApplication) getApplication();

		String startedWalkId = SharedPreferencesHelper.getStartedWalkId(this);

		//ToastUtils.showLong(getApplicationContext()," startedWalkId = " + startedWalkId);

		// we with Tamara are always went like para
		StartedWalkExt startedWalkExt = mApplication.getDbAdapter()
				.getStartedWalkForId(startedWalkId);

		Walk currentStartedWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getStartedWalk();

		if (startedWalkExt == null) {
			SyslogUtils.logEvent(this,
					"Critical error: startedWalkExt is null, walkId: "
							+ startedWalkId, EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);

			Toast.makeText(
					this,
					"Error! (no started walk info in database). Please, send the log!",
					Toast.LENGTH_SHORT).show();

			finish();

			Intent scheduleIntent = new Intent(getApplicationContext(),
					ScheduleActivity.class);
			scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(scheduleIntent);

			return;
		}

		if (currentStartedWalk == null) {
			SyslogUtils.logEvent(this,
					"Critical error: there is no related walk in database, walkId: "
							+ startedWalkId, EnumLogSeverity.ERROR,
					EnumLogType.INTERNAL_TYPE);

			Toast.makeText(this,
					"Error! (no walk info in database). Please, send the log!",
					Toast.LENGTH_SHORT).show();

			finish();

			Intent scheduleIntent = new Intent(getApplicationContext(),
					ScheduleActivity.class);
			scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(scheduleIntent);

			return;
		}

		List<WalkGPSPoint> routePoints = ((SwiftoApplication) getApplication())
				.getPointsGetter().getAllRoutePointsForWalk(startedWalkId);

		ArrayList<WalkGPSPoint> poopPoints = ((SwiftoApplication) getApplication())
				.getPointsGetter().getPoopPointsForWalk(startedWalkId);
		ArrayList<WalkGPSPoint> meetPoints = ((SwiftoApplication) getApplication())
				.getPointsGetter().getMeetPointsForWalk(startedWalkId);

		initilizeMap();

//		if (routePoints != null)
//			refreshViews(startedWalkExt, currentStartedWalk,
//					routePoints.size() > 0);



		initProgressBar(startedWalkExt, currentStartedWalk);

		refreshViews(startedWalkExt, currentStartedWalk,
				false);

		//ToastUtils.showLong(getApplicationContext(), " on resume called = " + minPassed);
		initDueToUploadPhotoStatus();

		// draw to map previous route and play/poo points
		addPreviousRecordsToMap(routePoints, poopPoints, meetPoints);

		if (mAfterPhotoTokenStatus.get() != -1) {
			if (mAfterPhotoTokenStatus.get() == REQUEST_CODE_BUTTON_TAKE_PHOTO) {
				showUploadPhotoConfirm(true);
			} else if (mAfterPhotoTokenStatus.get() == REQUEST_CODE_POPUP_PHOTO) {
				showUploadPhotoConfirm(false);
			}

			else if (mAfterPhotoTokenStatus.get() == REQUEST_CODE_BUTTON_TAKE_VIDEO) {
				showUploadVideoConfirm(true);
			} else if (mAfterPhotoTokenStatus.get() == REQUEST_CODE_POPUP_VIDEO) {
				showUploadVideoConfirm(false);
			}

		} else {
			Boolean gpsTurnedOn = ((SwiftoApplication) getApplication())
					.gpsTurnedOn();

			if (!gpsTurnedOn) {
				SyslogUtils.logEvent(getApplicationContext(),
						"Map opened with GPS disabled",
						EnumLogSeverity.WARNING, EnumLogType.INTERNAL_TYPE);
//				ToastUtils.showLong(getApplicationContext(),
//						"GPS is disabled! Please, enable it in settings");
				
				buildAlertMessageNoGps();
			}
		}

		//String afterwalk = AfterWalkDialogActivity.isokpressed;
		if (isokpressed != null && isokpressed.equals("yes") && isWalkPressed) {
			continueStopWalk(false);
		}



		if(isHalfwalk.equals("yes")) {
			showHalfWalkAlert();
			isHalfwalk = "no";
		}


		if(isAfterwalk.equals("yes")) {
			showAfterWalk();
			isAfterwalk = "no";
		}



//
//		if (currentStartedWalk.Dogs.size()>1 && isWalkPressed) {
//			continueStopWalk();
//		}

		// TODO: move to onStart() ?
		mApplication.resumeWalkMapActivity(this);
	}
	
	
//	public void statusCheck() {
//		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//			buildAlertMessageNoGps();
//
//		}
//
//	}

//	public boolean checkGPS() {
//		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//			return true;
//
//		}
//		return  false;
//
//	}

	public boolean gpsshow = true;
	private void buildAlertMessageNoGps() {
		if(gpsshow) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							gpsshow = true;
							startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					showGpsError(true, false);
					gpsshow = true;
					dialog.cancel();

					// finish();
				}
			});
			final AlertDialog alert = builder.create();
			alert.show();
			gpsshow = false;
		}

	}

	public static int minPassed = 0;

	public void adjustWalkCompleteTime()
	{
		String startedWalkId = SharedPreferencesHelper.getStartedWalkId(this);

		// we with Tamara are always went like para
		StartedWalkExt startedWalkExt = mApplication.getDbAdapter()
				.getStartedWalkForId(startedWalkId);
		Walk currentStartedWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getStartedWalk();
		mApplication = (SwiftoApplication) getApplication();

		long gpsTime = getGpsOffTime();
		//ToastUtils.showLong(getApplicationContext(),"gpsTime = "+ gpsTime);

		long diff = System.currentTimeMillis() - startedWalkExt.StartedTime;

		long secPassed = (diff / 1000) - gpsTime;

		long actualTimeRemaining = secPassed*1000;
		//ToastUtils.showLong(getApplicationContext(), "secPassed = " + secPassed);


		mApplication.setWalkCompleteTimer(minPassed*60*1000);

	}


	public long getDeviceOffTime()
	{
		mApplication = (SwiftoApplication) getApplication();
		// nothing is performing...
		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);

		long gpsoffduration = SharedPreferencesHelper.getGpsOffTimeDuration(startedWalkId, mContext);

		long diff = System.currentTimeMillis() - gpsoffduration;
		long secPassed = diff / 1000;
		return secPassed;
	}


	public long getGpsOffTime()
	{
		mApplication = (SwiftoApplication) getApplication();
		// nothing is performing...
		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);
		
		boolean isGpsOff = SharedPreferencesHelper.getIsGpsOff(mContext);


		long gpsOffTime = SharedPreferencesHelper.getGpsOffTime(startedWalkId, mContext);
		long gpsOffTime_new = SharedPreferencesHelper.getGpsOffTime(startedWalkId, mContext);


		if(isGpsOff)
		{
			
			long gpsOffTimeExact = SharedPreferencesHelper.getGpsOffTimeExact(startedWalkId, mContext);
			long gpsoffduration = SharedPreferencesHelper.getGpsOffTimeDuration(startedWalkId, mContext);

			long diff = System.currentTimeMillis() - (gpsOffTimeExact+gpsoffduration);
			long secPassed = diff / 1000;
			//int gpsOff = (int) (secPassed / 60);
			gpsOffTime_new = gpsOffTime_new + secPassed;
			gpsOffTime = secPassed;

			//ToastUtils.showLong(getApplicationContext(),"gpsoffduration  = "
			//		+ gpsoffduration+" secPassed - " + secPassed);
		}


		//ToastUtils.showLong(getApplicationContext(),"gpsOffTime_new = "
		//		+ gpsOffTime_new+" gpsOffTime - " + gpsOffTime);

		
		SharedPreferencesHelper.saveGpsOffTime(startedWalkId, mContext, gpsOffTime);
		//SharedPreferencesHelper.saveGpsOffTimeExact(startedWalkId, mContext, gpsOffTime_new);
		return gpsOffTime;
		
	}

	private void initProgressBar(final StartedWalkExt startedWalkExt,
			Walk currentStartedWalk) {

		//ToastUtils.showLong(getApplicationContext(), " initProgressBar isWalkPressed "+ isWalkPressed);


		mApplication = (SwiftoApplication) getApplication();
		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);

		final Walk startedWalk = mApplication.getWalkGetter()
				.getStartedWalkWithoutOwnerAndDogs();
		
		long gpsTime = getGpsOffTime();
		long deviceOfftime = getDeviceOffTime();

		//long offduration = SharedPreferencesHelper.getGpsOffTimeDuration(startedWalkId,mContext)/1000;

		//gpsTime = gpsTime ;
		long secPassed = 0;


		long diff = System.currentTimeMillis() - startedWalkExt.StartedTime;

		//secPassed = (diff / 1000) - gpsTime - deviceOfftime;

		secPassed = (diff / 1000) ;




		long actualTimeRemaining = secPassed*1000;

		minPassed = (int) (secPassed / 60) ;

		if(isWalkPressed) {

			return;
		}

		int walkDurationInSeconds = currentStartedWalk.Duration;

		// number of progress bar incrementations
		int maximum = walkDurationInSeconds
				/ PROGRESS_INCREMENT_VALUE_IN_SECONDS;


		int offsetsPassed = (int) (secPassed / PROGRESS_INCREMENT_VALUE_IN_SECONDS);

		mProgressWalk.setMax(maximum);
		mProgressWalk.setProgress(offsetsPassed);

		mTxtCountdown.setText(String
				.format(getResources().getString(
						R.string.activity_walk_map_min_walked), minPassed));

		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}

		mHandler = new Handler();

		mRunnable = new Runnable() {
			public void run() {
				updateCountdown(startedWalkExt.StartedTime);
				mHandler.postDelayed(mRunnable, REPEAT_INTERVAL);
			}
		};

		mRunnable.run();
}

private int REPEAT_INTERVAL = 10000;
	private Handler mHandler;

	Runnable mRunnable;

	/**
	 * @param startTime
	 *            in milliseconds
	 */
	private void updateCountdown(long startTime) {


		//ToastUtils.showLong(getApplicationContext(), " updateCountdown  isWalkPressed "+ isWalkPressed);

		
		long gpsTime = getGpsOffTime();
		long diff = System.currentTimeMillis() - startTime ;

		long secPassed = (diff / 1000)- gpsTime;

		minPassed = (int) (secPassed / 60) ;

		//ToastUtils.showLong(getApplicationContext(),"minPassed  on updateCountdown = " + minPassed);

		int offsetsPassed = (int) (secPassed / PROGRESS_INCREMENT_VALUE_IN_SECONDS);

		if(isWalkPressed) {

			return;
		}

		mProgressWalk.setProgress(offsetsPassed);
		mTxtCountdown.setText(String
				.format(getResources().getString(
						R.string.activity_walk_map_min_walked), minPassed));
	}

	private void initViews() {
		btNavLeft = (Button) findViewById(R.id.window_title_bt_left);
		btNavLeft.setText(getResources().getText(
				R.string.window_title_bt_dog_info));
		btNavLeft.setOnClickListener(btNavigateLeftClickListener);

		btNavRight = (Button) findViewById(R.id.window_title_bt_right);
		btNavRight.setText(getResources().getString(
				R.string.window_title_bt_schedule));
		btNavRight.setOnClickListener(btNavigateRightClickListener);

		btMenuList = (Button) findViewById(R.id.window_title_bt_slidemenu);
		// btMenuList.setOnClickListener(btMenuListClickListener);

		btMenuList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				toggle();
			}
		});

		tvTitle = (TextView) findViewById(R.id.window_title_txt_title);
		tvTitle.setVisibility(View.GONE);

		/*
		 * mapView = (MapView) findViewById(R.id.activity_walk_map_map_view);
		 * mapView.setBuiltInZoomControls(true);
		 */

		initilizeMap();

		btPlay = (ImageButton) findViewById(R.id.activity_walk_map_bt_play);
		btPlay.setOnClickListener(btPlayClickListener);

		btPoo = (ImageButton) findViewById(R.id.activity_walk_map_bt_poo);
		btPoo.setOnClickListener(btPooClickListener);

		btTakePicture = (Button) findViewById(R.id.activity_walk_map_bt_take_pic);
		btTakePicture.setOnClickListener(btTakePictureClickListener);

		btText = (Button) findViewById(R.id.activity_walk_map_bt_text_client);
		btText.setOnClickListener(btTextClientClickListener);

		btTakeVideo = (Button) findViewById(R.id.activity_walk_map_bt_take_video);
		btTakeVideo.setOnClickListener(btTakeVideoClickListener);

		mLayoutButtons = (RelativeLayout) findViewById(R.id.activity_walk_map_layout_buttons);

		mLayoutSearching = (LinearLayout) findViewById(R.id.activity_walk_map_layout_searching);

		btStopWalk = (Button) findViewById(R.id.activity_walk_map_bt_stop_walk);
		btStopWalk.setOnClickListener(btStopWalkClickListener);
		// vivek
		// btStopWalk.setVisibility(View.VISIBLE);+

		//ToastUtils.showLong(getApplicationContext(), " initViews  called ");
		btStopWalk.setVisibility(View.GONE);

		mProgressWalk = (ProgressBar) findViewById(R.id.activity_walk_map_progress_walk);

		// mProgressWalk.getIndeterminateDrawable().setColorFilter(Color.GREEN,
		// android.graphics.PorterDuff.Mode.MULTIPLY);

		mProgressWalk.getProgressDrawable().setColorFilter(Color.GREEN,
				Mode.SRC_IN);

		// mProgressPhotoUploading = (ProgressBar)
		// findViewById(R.id.activity_walk_map_progress_upload_photo);

		mTxtCountdown = (TextView) findViewById(R.id.activity_walk_map_txt_countdown);

		mTxtDogNames = (TextView) findViewById(R.id.activity_walk_map_txt_dog_names);
		mTxtWalkStatus = (TextView) findViewById(R.id.activity_walk_map_txt_info);

		mTxtGpsOff = (TextView) findViewById(R.id.activity_walk_map_txt_gpsoff);

		mTxtGpsLoading = (TextView) findViewById(R.id.activity_walk_map_txt_gpsloading);
	}

	public void showGpsError(boolean isShow , boolean isTimeUpdate) {

		//Toast.makeText(getApplicationContext(),"showGpsError called - "+isShow,Toast.LENGTH_LONG).show();
		SharedPreferencesHelper.saveIsGpsOff(mContext, isShow);
		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);
		mApplication = (SwiftoApplication) getApplication();
		
		if (isShow) {
			//mTxtGpsOff.setVisibility(View.VISIBLE);

			if(isTimeUpdate) {
				SharedPreferencesHelper.saveGpsOffTimeExact(startedWalkId, mContext, System.currentTimeMillis());
				mApplication.cancleWalkTimer();
			}

		} else {
			mTxtGpsOff.setVisibility(View.GONE);
			adjustWalkCompleteTime();
			if(isTimeUpdate) {

				long gpsofftime = SharedPreferencesHelper.getGpsOffTimeExact(startedWalkId,mContext);
				if(gpsofftime<1)
				{
					//ToastUtils.showLong(getApplicationContext(), "Hack lag gaya");
					return;
				}

				SharedPreferencesHelper.saveGpsOffTimeExact(startedWalkId,mContext,0L);

				long offduration = gpsofftime - System.currentTimeMillis();
				//SharedPreferencesHelper.saveGpsOffTimeExact(startedWalkId, mContext, 0L);
				long oldoffduration = SharedPreferencesHelper.getGpsOffTimeDuration(startedWalkId, mContext);

				//ToastUtils.showLong(getApplicationContext(), "offduration = " + offduration
				//		+ "  oldoffduration = " + oldoffduration  + " total = " + (offduration));


				//ToastUtils.showLong(getApplicationContext(), "offduration = " + offduration / 60000
				//		+ "  oldoffduration = " + oldoffduration / 60000 + " total = " + (offduration) / 60000);


				SharedPreferencesHelper.saveGpsOffTimeDuration(startedWalkId, mContext, offduration + oldoffduration);

			}
		}
	}



	public void setDeviceOff(boolean isOFF) {

		//Toast.makeText(getApplicationContext(),"showGpsError called - "+isShow,Toast.LENGTH_LONG).show();

		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);
		mApplication = (SwiftoApplication) getApplication();

		if (isOFF) {
			SharedPreferencesHelper.saveDeviceOffTimeExact(startedWalkId, mContext, System.currentTimeMillis());
			mApplication.cancleWalkTimer();

		} else {

			adjustWalkCompleteTime();
			long gpsofftime = SharedPreferencesHelper.getDeviceOffTimeExact(startedWalkId, mContext);
				if(gpsofftime<1)
				{
					//ToastUtils.showLong(getApplicationContext(), "Hack lag gaya");
					return;
				}
				SharedPreferencesHelper.saveDeviceOffTimeExact(startedWalkId, mContext, 0L);
				long offduration = gpsofftime - System.currentTimeMillis();
				//SharedPreferencesHelper.saveGpsOffTimeExact(startedWalkId, mContext, 0L);
				long oldoffduration = SharedPreferencesHelper.getDeviceOffTimeDuration(startedWalkId, mContext);



/*
				ToastUtils.showLong(getApplicationContext(), "offduration = " + offduration / 60000
						+ "  oldoffduration = " + oldoffduration / 60000 + " total = " + (offduration) / 60000);
			*/


				SharedPreferencesHelper.saveDeviceOffTimeDuration(startedWalkId, mContext, offduration + oldoffduration);
		}
	}

	private void initilizeMap() {
		if (googleMap == null) {

//			 googleMap = ((MapFragment) getFragmentManager().findFragmentById(
//			  R.id.activity_walk_map_map_view)).getMap();

			googleMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.activity_walk_map_map_view))
					.getMap();


			// check if map is created successfully or not
			if (googleMap == null) {




				 Toast.makeText(getApplicationContext(),

						 "Sorry! unable to create maps", Toast.LENGTH_SHORT)
						 .show();
				 return;


			}
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
		}
	}

	private void refreshViews(StartedWalkExt startedWalkExt,
			Walk currentStartedWalk, boolean hasPoints) {
		((SwiftoApplication) getApplication()).subscribeToStopWalk(this);

		if(minPassed < 0)
		{

			initProgressBar(startedWalkExt, currentStartedWalk);

		}
		// TODO: uncomment // vivek
		if (hasPoints) {
			// before GPS found, hide buttons Poo, Meet, TakePicture
			mLayoutButtons.setVisibility(View.VISIBLE);

			// indication of GPS searching
			mLayoutSearching.setVisibility(View.GONE);
		} else {

			 mLayoutButtons.setVisibility(View.VISIBLE);

			// indication of GPS searching
			 mLayoutSearching.setVisibility(View.GONE);

			//mLayoutButtons.setVisibility(View.GONE);
			//mLayoutSearching.setVisibility(View.VISIBLE);
		}

		// TODO: comment
		// mLayoutButtons.setVisibility(View.VISIBLE);
		// mLayoutSearching.setVisibility(View.GONE);

		// TODO: can it be erroneous?
		AQuery aq = new AQuery(this);

		// for (int i = 0; i < currentStartedWalk.Dogs.size(); i++) {
		// String dogPict = currentStartedWalk.Dogs.get(i).Pic;
		// aq.id(R.id.activity_walk_map_img_dog).
		// image(dogPict, true, true, 0, R.drawable.dog_default_big)
		// .width(50).height(50);
		// }

		if (currentStartedWalk.Dogs.size() > 0) {
			String dogPict = currentStartedWalk.Dogs.get(0).Pic;
			aq.id(R.id.activity_walk_map_img_dog)
					.image(dogPict, true, true, 0, R.drawable.dog_default_big)
					.width(50).height(50);
		} else {
			findViewById(R.id.activity_walk_map_img_dog).setVisibility(
					View.GONE);
		}
		if (currentStartedWalk.Dogs.size() > 1) {
			String dogPict1 = currentStartedWalk.Dogs.get(1).Pic;

			aq.id(R.id.activity_walk_map_img_dog1)
					.image(dogPict1, true, true, 0, R.drawable.dog_default_big)
					.width(50).height(50);
		} else {
			findViewById(R.id.activity_walk_map_img_dog1).setVisibility(
					View.GONE);
		}

		if (currentStartedWalk.Dogs.size() > 2) {
			String dogPict2 = currentStartedWalk.Dogs.get(2).Pic;

			aq.id(R.id.activity_walk_map_img_dog2)
					.image(dogPict2, true, true, 0, R.drawable.dog_default_big)
					.width(50).height(50);
		} else {
			findViewById(R.id.activity_walk_map_img_dog2).setVisibility(
					View.GONE);
		}

		if (currentStartedWalk.Dogs.size() > 3) {
			String dogPict3 = currentStartedWalk.Dogs.get(3).Pic;

			aq.id(R.id.activity_walk_map_img_dog3)
					.image(dogPict3, true, true, 0, R.drawable.dog_default_big)
					.width(50).height(50);
		} else {
			findViewById(R.id.activity_walk_map_img_dog3).setVisibility(
					View.GONE);
		}

		if (currentStartedWalk.Dogs.size() > 4) {
			String dogPict4 = currentStartedWalk.Dogs.get(4).Pic;

			aq.id(R.id.activity_walk_map_img_dog4)
					.image(dogPict4, true, true, 0, R.drawable.dog_default_big)
					.width(50).height(50);
		} else {
			findViewById(R.id.activity_walk_map_img_dog4).setVisibility(
					View.GONE);
		}

		mTxtDogNames.setText(currentStartedWalk.getDogNames());

		//nkp
		if(currentStartedWalk.WalkType.equals("overnight")){
			mTxtWalkStatus.setText("Overnight stay");
			mProgressWalk.setVisibility(View.GONE);
			mTxtCountdown.setVisibility(View.GONE);
		}else{
			mTxtWalkStatus.setText(String.format(
					getResources().getString(R.string.activity_walk_map_min_walk),
					currentStartedWalk.Duration / 60));
			mProgressWalk.setVisibility(View.VISIBLE);
			mTxtCountdown.setVisibility(View.VISIBLE);
		}


		// for FREE (MEET AND GREET) walk immediately show "End Meet & Greet"
		// button
		if (currentStartedWalk.WalkType.equals(EnumWalkTypes.FREE)) {
			btStopWalk.setText(getResources().getString(
					R.string.activity_walk_map_bt_end_meet_greet));
			btStopWalk.setVisibility(View.VISIBLE);
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"WalkMap - refreshing views - startedWalk is FREE, [stop button] is visible",
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);
		} else {
			//if (startedWalkExt.Completed)
//			if(startedWalkExt.Completed)
//			{
//				ToastUtils.showLong(getApplicationContext()," startedWalkExt.Completed - ye galat hai id = " + startedWalkExt.WalkId);
//			}
			//if (minPassed>=currentStartedWalk.Duration / 60 && startedWalkExt.Completed || minPassed>=currentStartedWalk.Duration / 60) {
				// normally we won't get here at all

				//ToastUtils.showLong(getApplicationContext(),"minPassed in if on refresh view = " + minPassed);

			//nkp
			//Log.d("walk type ", currentStartedWalk.WalkType + "start time "+walkstarttime);
			if(currentStartedWalk.WalkType.equals("overnight")){
				Calendar cal = CalendarUtils._6AM_AFTER_DAY_FROM_SECOND(currentStartedWalk.StartTime);
				long day_6am_time = cal.getTimeInMillis();
				if (startedWalkExt.MessageSent && startedWalkExt.StopWalkSent) {
					SyslogUtils
							.logEvent(
									getApplicationContext(),
									"WalkMap - refreshing views - startedWalk is completed, message sent and stop walk sent",
									EnumLogSeverity.ERROR,
									EnumLogType.INTERNAL_TYPE);
				} else {


					//Log.d("6 Am date end button", cal.getTime() + " nkp walk map day_6am_time "+day_6am_time + "current time "+System.currentTimeMillis());
					if(day_6am_time <= System.currentTimeMillis()){
						btStopWalk.setVisibility(View.VISIBLE);
						btStopWalk.setText(getResources().getString(R.string.activity_walk_map_bt_overnight_check_out));
						Log.d("now > 6 oclock ", "nkp is calling, startedWalkExt.Completed : "+startedWalkExt.Completed);
						SyslogUtils
								.logEvent(
										getApplicationContext(),
										"DEBUG Value of overnight stay job is completed.",
										EnumLogSeverity.INFORMATIONAL,
										EnumLogType.INTERNAL_TYPE);  //  Oriflame Kiwi & Orange Boosting Shampoo:

						SyslogUtils
								.logEvent(
										getApplicationContext(),
										"WalkMap - refreshing views - overnight is completed, [check out button] is visible",
										EnumLogSeverity.INFORMATIONAL,
										EnumLogType.INTERNAL_TYPE);
					}else{
						btStopWalk.setVisibility(View.GONE);
						SyslogUtils
								.logEvent(
										getApplicationContext(),
										"WalkMap - refreshing views - overNight stay is NOT completed, [check out button] is gone" ,
										EnumLogSeverity.INFORMATIONAL,
										EnumLogType.INTERNAL_TYPE);
					}
				}
			}else {
				if ((startedWalkExt.Completed && minPassed>=currentStartedWalk.Duration / 60)|| (minPassed>=currentStartedWalk.Duration / 60)){
					if (startedWalkExt.MessageSent && startedWalkExt.StopWalkSent) {
						SyslogUtils
								.logEvent(
										getApplicationContext(),
										"WalkMap - refreshing views - startedWalk is completed, message sent and stop walk sent",
										EnumLogSeverity.ERROR,
										EnumLogType.INTERNAL_TYPE);
					} else {


						long walkstarttime = SharedPreferencesHelper.getWalkOnTimeExact(mContext);

						long diff =  System.currentTimeMillis() - walkstarttime;

						int tempmin = (int)(diff/(1000 * 60));


						if(tempmin>=currentStartedWalk.Duration / 60)
						{
							Log.d("endbutton", "nkp visible "+startedWalkExt.Completed);
							btStopWalk.setVisibility(View.VISIBLE);
							btStopWalk.setText(getResources().getString(R.string.activity_walk_map_bt_stop_walk));
							SyslogUtils
									.logEvent(
											getApplicationContext(),
											"DEBUG Value of startedWalkExt.Completed = "+startedWalkExt.Completed+
													" and value of minPassed = "+minPassed+" " +
													" and value of tempmin = "+tempmin+" " +
													"and value of currentStartedWalk.Duration / 60 = "+(currentStartedWalk.Duration / 60)+" " +
													"and value of   minPassed>=currentStartedWalk.Duration / 60 "+( minPassed>=currentStartedWalk.Duration / 60)+"",
											EnumLogSeverity.INFORMATIONAL,
											EnumLogType.INTERNAL_TYPE);  //  Oriflame Kiwi & Orange Boosting Shampoo:

							SyslogUtils
									.logEvent(
											getApplicationContext(),
											"WalkMap - refreshing views - startedWalk is completed, [stop button] is visible",
											EnumLogSeverity.INFORMATIONAL,
											EnumLogType.INTERNAL_TYPE);
						}
						else
						{
							SyslogUtils
									.logEvent(
											getApplicationContext(),
											"WalkMap - refreshing views - startedWalk is not completed, [stop button] is gone" +
													" and value of minPassed = "+minPassed+" " +
													" and value of tempmin = "+tempmin+" " +
													" here minpassed value is wrong",

											EnumLogSeverity.INFORMATIONAL,
											EnumLogType.INTERNAL_TYPE);
						}
					}
				} else {
					// vivek
					// btStopWalk.setVisibility(View.VISIBLE);




					btStopWalk.setVisibility(View.GONE);
					SyslogUtils
							.logEvent(
									getApplicationContext(),
									"WalkMap - refreshing views - startedWalk is NOT completed, [stop button] is gone" ,
									EnumLogSeverity.INFORMATIONAL,
									EnumLogType.INTERNAL_TYPE);
				}
			}
		}
	}


	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}


	private void initDueToUploadPhotoStatus() {
		if (UploadPhotoStatus.getInstance().getStatus()
				.equals(EnumUploadPhotoStatus.NOT_UPLOADING)) {
			// mProgressPhotoUploading.setVisibility(View.GONE);
		} else if (UploadPhotoStatus.getInstance().getStatus()
				.equals(EnumUploadPhotoStatus.UPLOADING_BUTTON_TAKE_PHOTO)) {
			// mProgressPhotoUploading.setVisibility(View.VISIBLE);
		} else if (UploadPhotoStatus.getInstance().getStatus()
				.equals(EnumUploadPhotoStatus.UPLOADING_POPUP)) {
			// mProgressPhotoUploading.setVisibility(View.VISIBLE);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initValues() {

		if (googleMap == null)
		{
			//ToastUtils.showLong(getApplicationContext(),"Sorry , Cant create map");
			return;
		}

		googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			public void onCameraChange(CameraPosition arg0) {
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
				googleMap.setOnCameraChangeListener(WalkMapActivity.this);
			}
		});

		pinMeet = BitmapFactory.decodeResource(getResources(),
				R.drawable.pin_meet);
		pinPoo = BitmapFactory.decodeResource(getResources(),
				R.drawable.pin_poo);

		// Poo/Play pins in the middle
		mActionsOverlay = new ActionsOverlay();
		// googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		//googleMap.setMyLocationEnabled(true);


		googleMap.setMyLocationEnabled(false);
		googleMap.getUiSettings().setMyLocationButtonEnabled(false);

		// overlays.add(mActionsOverlay);

		// current position marker on the top

		mMarkerOverlay = new MarkerOverlay(BitmapFactory.decodeResource(
				getResources(), R.drawable.circle_blue));
		// overlays.add(mMarkerOverlay);

	}

	ArrayList<LatLng> points = new ArrayList<LatLng>();

	private void addPreviousRecordsToMap(List<WalkGPSPoint> pointsRoute,
			ArrayList<WalkGPSPoint> pointsPoo,
			ArrayList<WalkGPSPoint> pointsMeet) {

		/*
		 * mPathOverlay.clear();
		 * 
		 * for (GeoPoint point : pointsRoute) { mPathOverlay.addItem(point); }
		 */

		mActionsOverlay.clear();

		for (GeoPoint pooPoint : pointsPoo) {
			mActionsOverlay.addItem(new GeoPointedBitmap(pooPoint
					.getLatitudeE6(), pooPoint.getLongitudeE6(), pinPoo));

			LatLng latLng = new LatLng(pooPoint.getLatitudeE6() / 1E6,
					pooPoint.getLongitudeE6() / 1E6);
			BitmapDescriptor btmpDesc = BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeResource(getResources(),
							R.drawable.pin_poo));
			googleMap.addMarker(new MarkerOptions().position(latLng).icon(
					btmpDesc));
		}

		for (GeoPoint meetPoint : pointsMeet) {
			mActionsOverlay.addItem(new GeoPointedBitmap(meetPoint
					.getLatitudeE6(), meetPoint.getLongitudeE6(), pinMeet));

			LatLng latLng = new LatLng(meetPoint.getLatitudeE6() / 1E6,
					meetPoint.getLongitudeE6() / 1E6);
			BitmapDescriptor btmpDesc = BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeResource(getResources(),
							R.drawable.pin_meet));
			googleMap.addMarker(new MarkerOptions().position(latLng).icon(
					btmpDesc));
		}

		/*
		 * for (GeoPoint meetPoint : pointsMeet) { mActionsOverlay.addItem(new
		 * GeoPointedBitmap(meetPoint .getLatitudeE6(),
		 * meetPoint.getLongitudeE6(), pinMeet)); }
		 * 
		 * mMarkerOverlay.clear();
		 */

		// animate map to last point
		/*
		 * if (pointsRoute.size() > 0) { GeoPoint gp =
		 * pointsRoute.get(pointsRoute.size() - 1);
		 * 
		 * lastPoint = gp;
		 * 
		 * mMarkerOverlay.moveTo(pointsRoute.get(pointsRoute.size() - 1));
		 * 
		 * mapController.animateTo(gp); mapView.postInvalidate(); }
		 */

		points.clear();
		// Instantiating the class PolylineOptions to plot polyline in the map
		polylineOptions = new PolylineOptions();
		// Setting the color of the polyline
		//polylineOptions.color(Color.BLUE);
		polylineOptions.color(Color.RED);
		// Setting the width of the polyline
		polylineOptions.width(3);

		for (GeoPoint point : pointsRoute) {
			// mPathOverlay.addItem(point);
			LatLng lt = new LatLng(point.getLatitudeE6() / 1E6,
					point.getLongitudeE6() / 1E6);
			points.add(lt);
			googleMap.animateCamera(CameraUpdateFactory
					.newLatLngZoom(lt, 18.0f));
			lastPoint = point;
		}

		try{
			myPositionMarker.remove();
		} catch (Exception e){
			//..
		}



		// Setting points of polyline
		polylineOptions.addAll(points);
		// Adding the polyline to the map
		googleMap.addPolyline(polylineOptions);

		if(lastPoint!=null)

		{
			myPositionMarker = googleMap.addMarker(new MarkerOptions()
					.flat(true)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.circle_blue))
					.anchor(0.5f, 0.5f)
					.position(new LatLng(lastPoint.getLatitudeE6() / 1E6,
							lastPoint.getLongitudeE6() / 1E6)));
		}
	}


	private void initAnimations() {
		mAnimMoveDown = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f);
		mAnimMoveDown.setDuration(500);

		mAnimMoveUp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mAnimMoveUp.setDuration(500);
	}

	AnimationListener hideSerchingListener = new AnimationListener() {
		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationRepeat(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mLayoutSearching.setVisibility(View.GONE);
			mLayoutButtons.setVisibility(View.VISIBLE);
			mLayoutButtons.startAnimation(mAnimMoveUp);
		}
	};

	AnimationListener hideButtonsListener = new AnimationListener() {
		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationRepeat(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mLayoutButtons.setVisibility(View.GONE);
			mLayoutSearching.setVisibility(View.VISIBLE);
			mLayoutSearching.startAnimation(mAnimMoveUp);
		}
	};

	OnClickListener btPlayClickListener = new OnClickListener() {
		public void onClick(View v) {
			Walk currentStartedWalk = ((SwiftoApplication) getApplication())
					.getWalkGetter().getStartedWalk();

			// some defense
			if (currentStartedWalk == null) {
				return;
			}

			if (currentStartedWalk.Dogs.size() > 1) {
				showQuickActionForMeet(v);
			} else {
				addMeetInfo(0);
			}
		}
	};

	private void showQuickActionForMeet(View anchor) {
		QuickAction quickAction = getQuickActionForDogs();
		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						addMeetInfo(actionId);
					}
				});

		quickAction.show(anchor);
	}

	private void addMeetInfo(int dogIndex) {

		if (lastPoint != null) {
			GeoPointExtWithData meetInfo = new GeoPointExtWithData(
					lastPoint.getLatitudeE6(), lastPoint.getLongitudeE6());

			mApplication.addMeetInfoAndSend(meetInfo, dogIndex);

			mActionsOverlay.addItem(new GeoPointedBitmap(lastPoint
					.getLatitudeE6(), lastPoint.getLongitudeE6(), pinMeet));

			LatLng latLng = new LatLng(lastPoint.getLatitudeE6() / 1E6,
					lastPoint.getLongitudeE6() / 1E6);

			BitmapDescriptor btmpDesc = BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeResource(getResources(),
							R.drawable.pin_meet));

			googleMap.addMarker(new MarkerOptions().position(latLng).icon(
					btmpDesc));
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
					18.0f));

		} else {
			//ToastUtils.showLong(getApplicationContext(), "lastPoint is null");
		}
		// mapView.postInvalidate();
	}

	OnClickListener btPooClickListener = new OnClickListener() {
		public void onClick(View v) {
			Walk currentStartedWalk = ((SwiftoApplication) getApplication())
					.getWalkGetter().getStartedWalk();

			
			// some defense
			if (currentStartedWalk == null) {
				return;
			}

			
			if (currentStartedWalk.Dogs.size() > 1) {
				showQuickActionForPoo(v);
			} else {

				if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
					// Log.w("vivek1",
					// "mApplication.sendUnsentPointsAndMeetPoos();");
					// ToastUtils.showLong(mContext,"mApplication.sendUnsentPointsAndMeetPoos();");
					ToastUtils.showLong(mContext, "Poop Notification text sent to Client");
				} else {
					// noInternetConnection();
					SwiftoApplication.taskMap.put("poop_notification", "Poop Notification text sent to Client");
				}


				addPooInfo(0);
			}
		}
	};

	private void showQuickActionForPoo(View anchor) {
		QuickAction quickAction = getQuickActionForDogs();
		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					public void onItemClick(QuickAction source, int pos,
											int actionId) {

						if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
							// Log.w("vivek1",
							// "mApplication.sendUnsentPointsAndMeetPoos();");
							// ToastUtils.showLong(mContext,"mApplication.sendUnsentPointsAndMeetPoos();");
							ToastUtils.showLong(mContext, "Poop Notification text sent to Client");
						} else {
							// noInternetConnection();
							//ToastUtils.showLong(mContext, "Poop Notification text sent to Client");

							SwiftoApplication.taskMap.put("poop_notification", "Poop Notification text sent to Client");
						}

						addPooInfo(actionId);
					}
				});

		quickAction.show(anchor);

	}

	private void addPooInfo(int dogIndex) {
		/*
		 * GeoPointExtWithData pooInfo = new GeoPointExtWithData(
		 * lastPoint.getLatitudeE6(), lastPoint.getLongitudeE6());
		 * 
		 * mApplication.addPooInfoAndSend(pooInfo, dogIndex);
		 * 
		 * mActionsOverlay.addItem(new
		 * GeoPointedBitmap(lastPoint.getLatitudeE6(),
		 * lastPoint.getLongitudeE6(), pinPoo)); mapView.postInvalidate();
		 */

		if (lastPoint != null) {
			GeoPointExtWithData pooInfo = new GeoPointExtWithData(
					lastPoint.getLatitudeE6(), lastPoint.getLongitudeE6());

			mApplication.addPooInfoAndSend(pooInfo, dogIndex);

			mActionsOverlay.addItem(new GeoPointedBitmap(lastPoint
					.getLatitudeE6(), lastPoint.getLongitudeE6(), pinPoo));

			LatLng latLng = new LatLng(lastPoint.getLatitudeE6() / 1E6,
					lastPoint.getLongitudeE6() / 1E6);

			BitmapDescriptor btmpDesc = BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeResource(getResources(),
							R.drawable.pin_poo));

			googleMap.addMarker(new MarkerOptions().position(latLng).icon(
					btmpDesc));
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
					18.0f));

		} else {
			//ToastUtils.showLong(getApplicationContext(), "lastPoint is null");
		}

	}

	private QuickAction getQuickActionForDogs() {
		QuickAction quickAction = new QuickAction(mContext,
				QuickAction.HORIZONTAL);
		Walk currentWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getStartedWalk();

		if (currentWalk != null) {
			ArrayList<Dog> dogs = currentWalk.Dogs;

			for (int i = 0; i < dogs.size(); i++) {
				Dog dog = dogs.get(i);

				ActionItem item = new ActionItem(i, dog.Name);
				quickAction.addActionItem(item, dog.Pic);
			}
		} else {
			SyslogUtils.logEvent(getApplicationContext(),
					"WalkMap.getQuickActionsForDog() - startedWalk is null",
					EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
		}

		return quickAction;
	}

	// -------------------------------------------------------------------
	//
	// Take Photo
	//
	// --------------------

	OnClickListener btTakePictureClickListener = new OnClickListener() {
		public void onClick(View v) {

			// if (!UploadPhotoStatus.getInstance().getStatus()
			// .equals(EnumUploadPhotoStatus.NOT_UPLOADING)) {
			// Toast.makeText(mContext, "Photo is uploading now. Please wait",
			// Toast.LENGTH_SHORT).show();
			// return;
			// }

			if (!ServiceUtils.isNetworkAvailable(mContext)) {
//				Toast.makeText(mContext, "No internet connection",
//						Toast.LENGTH_SHORT).show();
				SyslogUtils
						.logEvent(getApplicationContext(),
								"No internet connection on "
										+ "trying to upload photo",
								EnumLogSeverity.WARNING,
								EnumLogType.HARDWARE_TYPE);
				// return;
			}

			if (!IntentUtils.isIntentAvailable(mContext,
					MediaStore.ACTION_IMAGE_CAPTURE)) {
				AlertUtils.showYes(mContext, "Error",
						"Camera application not found", "OK", null);
				return;
			}

			Walk startedWalk = ((SwiftoApplication) getApplication())
					.getWalkGetter().getStartedWalk();

			// set boolean flag for photo taken
			SharedPreferencesHelper.savePhotoTaken(startedWalk._id,
					getApplicationContext(), true);

			if (startedWalk.Dogs.size() > 1) {
				showQuickActionForPhoto(v, startedWalk.Dogs, true);
			} else {
				((SwiftoApplication) getApplication())
						.setDogIdForPhoto(startedWalk.Dogs.get(0)._id);
				startCameraActivity(true);
			}
		}
	};

	OnClickListener btTextClientClickListener = new OnClickListener() {
		public void onClick(View v) {

			//((SwiftoApplication) getApplication()).debugAfterWalk(false);

			Walk startedWalk = ((SwiftoApplication) getApplication())
					.getWalkGetter().getStartedWalk();

//			long walkofftime = SharedPreferencesHelper.getWalkOffTimeExact(startedWalk._id, getApplicationContext());
//
//			if(walkofftime<100)
//			{
//				SyslogUtils.logEvent(getApplicationContext(),
//						"walkofftime     ------  " + System.currentTimeMillis(),
//						EnumLogSeverity.ERROR, EnumLogType.HARDWARE_TYPE);
//
//				//long time  = System.currentTimeMillis();
//				SharedPreferencesHelper.saveWalkOffTimeExact(startedWalk._id, mContext,System.currentTimeMillis());
//
//				ToastUtils.showLong(getApplicationContext(), "walkofftime = -" +System.currentTimeMillis() );
//
//			}
//			else
//			{
//
//				SyslogUtils.logEvent(getApplicationContext(),
//						"walkofftime already addedd  and value is      ------  "+walkofftime,
//						EnumLogSeverity.ERROR, EnumLogType.HARDWARE_TYPE);
//
//				ToastUtils.showLong(getApplicationContext(), "walkofftime already found = -" + walkofftime);
//			}


			sendSMS(startedWalk.Owner.PhonePrimary);



		}
	};

	public void addFeedAndMedicine() {
		final Dialog dialog = new Dialog(WalkMapActivity.this);
		dialog.setContentView(R.layout.dialod_feed_medicine);
		dialog.setTitle("Fed/Medicated");
		dialog.setCancelable(false);

		final ArrayList<FeedMedicine> feedMedicineList = ((SwiftoApplication) getApplication())
				.getFeedMedicineText();

		if (feedMedicineList.size() == 0) {
			showDialogSendMessage();
			return;
		}

		ListView fmlist = (ListView) dialog
				.findViewById(R.id.feed_medicine_list);
		// ArrayAdapter<String> ar = new
		// ArrayAdapter<String>(getApplicationContext(),
		// R.layout.list_item_feed_medicine,R.id.label, feedMedicineArray);
		FeedMedicineListAdapter ar = new FeedMedicineListAdapter(
				getApplicationContext(), feedMedicineList);
		fmlist.setAdapter(ar);
		Button ok = (Button) dialog
				.findViewById(R.id.dialog_feed_medicine_ButtonOK);

		Button cancle = (Button) dialog
				.findViewById(R.id.dialog_feed_medicine_ButtonCancle);

		dialog.show();
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean check = false;
				for (int i = 0; i < feedMedicineList.size(); i++) {
					if (!feedMedicineList.get(i).isCheck()) {
						check = true;
					}
				}
				if (check) {
					ToastUtils.showLong(getApplicationContext(),
							"Please check all");
				} else {
					// ToastUtils.showLong(getApplicationContext(),
					// "All check");
					dialog.cancel();
					showDialogSendMessage();
				}

				return;
			}
		});

		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}

	public void sendSMS(String no) {
		Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
				+ no));
		startActivity(smsIntent);
	}

	OnClickListener btTakeVideoClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (!UploadVideoStatus.getInstance().getStatus()
					.equals(EnumUploadPhotoStatus.NOT_UPLOADING)) {
				Toast.makeText(mContext, "Video is uploading now. Please wait",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (!ServiceUtils.isNetworkAvailable(mContext)) {
				Toast.makeText(mContext, "No internet connection",
						Toast.LENGTH_SHORT).show();
				SyslogUtils
						.logEvent(getApplicationContext(),
								"No internet connection on "
										+ "trying to upload photo",
								EnumLogSeverity.WARNING,
								EnumLogType.HARDWARE_TYPE);
				return;
			}

			if (!IntentUtils.isIntentAvailable(mContext,
					MediaStore.ACTION_VIDEO_CAPTURE)) {
				AlertUtils.showYes(mContext, "Error",
						"Camera application not found", "OK", null);
				return;
			}

			Walk startedWalk = ((SwiftoApplication) getApplication())
					.getWalkGetter().getStartedWalk();

			if (startedWalk.Dogs.size() > 1) {
				showQuickActionForPhoto(v, startedWalk.Dogs, false);
			} else {
				((SwiftoApplication) getApplication())
						.setDogIdForPhoto(startedWalk.Dogs.get(0)._id);
				startCameraVideoActivity(true);
			}
		}
	};

	private void showQuickActionForPhoto(View anchor,
			final ArrayList<Dog> dogs, final boolean isImage) {
		QuickAction quickAction = getQuickActionForDogs();
		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						((SwiftoApplication) getApplication())
								.setDogIdForPhoto(dogs.get(actionId)._id);
						if (isImage) {
							startCameraActivity(true);
						} else {
							startCameraVideoActivity(true);
						}
					}
				});

		quickAction.show(anchor);
	}

	//private Camera camera;
	private void startCameraActivity(boolean byButtonTakePicture) {
		if (!StorageUtils.hasStorage(true)) {
			AlertUtils.showYes(this, "Storage problems",
					"Please, send a photo manually, there is storage problems",
					"OK", null);
			return;
		}

		long time = System.currentTimeMillis();
		int sec = (int) (time / 1000);

//		camera = Camera.open();
//		PackageManager pm=getApplicationContext().getPackageManager();
//		final Camera.Parameters p = camera.getParameters();
//
//		p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//		camera.setParameters(p);
//		camera.startPreview();

		imageFileName = String.format("SWIFTO_"+Locale.getDefault(), "%d.png", sec);
		ContentResolver contentResolver = getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(MediaStore.Images.Media.TITLE, imageFileName);
		//cv.put(MediaStore.Images.Media.TITLE, imageFileName);
		mApplication.setImageUri(contentResolver.insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv));



		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				mApplication.getImageUri());
		cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

		if (byButtonTakePicture) {
			startActivityForResult(cameraIntent, REQUEST_CODE_BUTTON_TAKE_PHOTO);
		} else {
			startActivityForResult(cameraIntent, REQUEST_CODE_POPUP_PHOTO);
		}


	}


	private boolean isFlashSupported(PackageManager packageManager){
		// if device support camera flash?
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			return true;
		}
		return false;
	}

	/**
	 * @param packageManager
	 * @return true <b>if the device support camera</b><br/>
	 * false <b>if the device doesn't support camera</b>
	 */
	private boolean isCameraSupported(PackageManager packageManager){
		// if device support camera?
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		}
		return false;
	}

	private void startCameraVideoActivity(boolean byButtonTakePicture) {
		if (!StorageUtils.hasStorage(true)) {
			AlertUtils.showYes(this, "Storage problems",
					"Please, send a photo manually, there is storage problems",
					"OK", null);
			return;
		}

		long time = System.currentTimeMillis();
		int sec = (int) (time / 1000);
		videoFileName = String.format(Locale.getDefault(), "VID_%d.mp4", sec);
		ContentResolver contentResolver = getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(MediaStore.Video.Media.TITLE, videoFileName);
		mApplication.setVedioUri(contentResolver.insert(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cv));

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

		Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

		if (byButtonTakePicture) {
			startActivityForResult(cameraIntent, REQUEST_CODE_BUTTON_TAKE_VIDEO);
		} else {
			startActivityForResult(cameraIntent, REQUEST_CODE_POPUP_VIDEO);
		}
	}

	private AtomicInteger mAfterPhotoTokenStatus = new AtomicInteger(-1);

	// private AtomicInteger mAfterVedioTokenStatus1 = new AtomicInteger(-2);

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (SharedPreferencesHelper.getWalkIsInProcess(getApplicationContext()) == false) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"Returned from camera to map when there is no walk in progress. Finishing map activity",
							EnumLogSeverity.WARNING, EnumLogType.INTERNAL_TYPE);
			finish();
			return;
		}



		SyslogUtils
				.logEvent(
						getApplicationContext(),
						"Returned from camera with result code  --  " + resultCode+
						"Returned from camera with result code  --  " + resultCode,
						EnumLogSeverity.WARNING, EnumLogType.INTERNAL_TYPE);
		if(resultCode==RESULT_CANCELED)
		{
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"Returned from camera with result code RESULT_CANCELED so no photo process --  "+resultCode,
							EnumLogSeverity.WARNING, EnumLogType.INTERNAL_TYPE);
			mAfterPhotoTokenStatus.set(-1);

			mApplication.cleanImageUri();
			resultFile = null;

			deleteLastImageId();


		}
		else
		{
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							" requestCode == RESULT_CANCELED condition failed  --  " + resultCode,
							EnumLogSeverity.WARNING, EnumLogType.INTERNAL_TYPE);
		}

		if (resultCode != RESULT_CANCELED) {
			if (requestCode == REQUEST_CODE_BUTTON_TAKE_PHOTO) {
				if (resultCode == RESULT_OK) {
					mAfterPhotoTokenStatus.set(REQUEST_CODE_BUTTON_TAKE_PHOTO);
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else if (resultCode == RESULT_FIRST_USER) {
					mAfterPhotoTokenStatus.set(-1);
				} else {

					SyslogUtils
							.logEvent(
									getApplicationContext(),
									" mApplication.cleanImageUri(); called  ",
									EnumLogSeverity.WARNING, EnumLogType.INTERNAL_TYPE);
					mApplication.cleanImageUri();
					resultFile = null;
					//mAfterPhotoTokenStatus.set(-1);
				}
			} else if (requestCode == REQUEST_CODE_POPUP_PHOTO) {
				if (resultCode == RESULT_OK) {
					mAfterPhotoTokenStatus.set(REQUEST_CODE_POPUP_PHOTO);
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else if (resultCode == RESULT_FIRST_USER) {
					mAfterPhotoTokenStatus.set(-1);
				} else {

					SyslogUtils
							.logEvent(
									getApplicationContext(),
									" mApplication.cleanImageUri(); called  ",
									EnumLogSeverity.WARNING, EnumLogType.INTERNAL_TYPE);
					mApplication.cleanImageUri();
					resultFile = null;
					//mAfterPhotoTokenStatus.set(-1);
				}
			}

			else if (requestCode == REQUEST_CODE_BUTTON_TAKE_VIDEO) {
				if (resultCode == RESULT_OK) {
					mAfterPhotoTokenStatus.set(REQUEST_CODE_BUTTON_TAKE_VIDEO);
					videoFileName = "" + data.getData();
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else if (resultCode == RESULT_FIRST_USER) {
					mAfterPhotoTokenStatus.set(-1);
				} else {
					mAfterPhotoTokenStatus.set(-1);
				}
			} else if (requestCode == REQUEST_CODE_POPUP_VIDEO) {
				if (resultCode == RESULT_OK) {
					mAfterPhotoTokenStatus.set(REQUEST_CODE_POPUP_VIDEO);
					videoFileName = "" + data.getData();
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else if (resultCode == RESULT_FIRST_USER) {
					mAfterPhotoTokenStatus.set(-1);
				} else {
					mAfterPhotoTokenStatus.set(-1);
				}
			} else {
				mAfterPhotoTokenStatus.set(-1);
		}
		}
	}


	public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(
				bm, 0, 0, width, height, matrix, false);


//		if (bm != null && !bm.isRecycled())
//		{
//			bm.recycle();
//			bm = null;
//			System.gc();
//		}

		//bm.recycle();
		return resizedBitmap;
	}

	private void showUploadPhotoConfirm(final boolean byButtonTakePhoto) {

		SyslogUtils.logEvent(getApplicationContext(), "showUploadPhotoConfirm  isPopupShowing value - " + isPopupShowing,
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		if(isPopupShowing)
		{
			return;
		}

		isPopupShowing = true;

		SyslogUtils.logEvent(getApplicationContext(), "showUploadPhotoConfirm",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		final Dialog dialog = new Dialog(this);

		dialog.setCancelable(false);

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK &&
						event.getAction() == KeyEvent.ACTION_UP &&
						!event.isCanceled()) {

					deleteRotateFiles();

					SyslogUtils.logEvent(getApplicationContext(), "\"Back \" pressed from image dialog ",
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);
					mAfterPhotoTokenStatus.set(-1);

					mApplication.cleanImageUri();
					resultFile = null;

					isPopupShowing = false;
					isimagepopupshowing = false;
					dialog.dismiss();


					return true;
				}
				return false;
			}
		});
		dialog.setContentView(R.layout.dialod_image_confirm);
		dialog.setTitle("Take Photo");
		final ImageView image1 = (ImageView) dialog.findViewById(R.id.image);
		final String path = getPathFromImageUri(mApplication.getImageUri(),
				mContext);
		BitmapFactory.Options options = new BitmapFactory.Options();

		// down sizing image as it throws OutOfMemory Exception for larger
		// images

		getLastImageId()		;

		options.inSampleSize = 8;
		final Bitmap bitmap = BitmapFactory.decodeFile(path, options);

		if(bitmap == null)
		{
			ToastUtils.showShort(getApplicationContext()," Image if null please try again. ");
			return;
		}

		image1.setImageBitmap(bitmap);

		Button ok, cancle, rotate;
		ok = (Button) dialog.findViewById(R.id.dialog_ButtonOK);
		cancle = (Button) dialog.findViewById(R.id.dialog_ButtonCancle);
		rotate = (Button) dialog.findViewById(R.id.dialog_Rotate);

		rotate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if(angle > 270 && angle < 360)
				{
					angle = 0 ;
				}
				else {
					angle = angle + 90;
				}
				Matrix matrix = new Matrix();
				image1.setScaleType(ScaleType.MATRIX); // required
				//image1.setScaleType(ScaleType.FIT_CENTER);

				matrix.postRotate(angle, image1.getDrawable().getBounds()
						.width() / 2,
						image1.getDrawable().getBounds().height() / 2);


//				matrix.postRotate(angle, image1.getDrawable().getBounds()
//								.width() ,
//						image1.getDrawable().getBounds().height());


				// matrix.postRotate((float) angle, pivX, pivY);
				image1.setImageMatrix(matrix);


				BitmapDrawable drawable = (BitmapDrawable) image1.getDrawable();
				if(drawable == null)
				{
					ToastUtils.showShort(getApplicationContext()," Image if null please try again. ");
					return;
				}
				Bitmap bitmap1 = drawable.getBitmap();

				if(bitmap1 == null)
				{
					ToastUtils.showShort(getApplicationContext()," Image if null please try again. ");
					return;
				}

				SyslogUtils.logEvent(getApplicationContext(),"rorate image before resize  with anngle - " + angle +
								" And  bitmap1.getWidth() = " + bitmap1.getWidth()
								+" And bitmap1.getHeight() = "+  bitmap1.getHeight(),
						EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);

				bitmap1 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);

				SyslogUtils.logEvent(getApplicationContext(),"rorate image with anngle - " + angle +
						" And  bitmap1.getWidth() = " + bitmap1.getWidth()
						+" And bitmap1.getHeight() = "+  bitmap1.getHeight(),
						EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);

				//image1.setScaleType(ScaleType.FIT_CENTER);

				try {
					bitmap1 = getResizedBitmap(bitmap1, bitmap1.getWidth() * 2, bitmap1.getHeight() * 2);
				}
				catch (Exception e)
				{

				}

				Uri tempuri = getImageUri(getApplicationContext(),bitmap1);
				mApplication.setImageUri(tempuri);

				getLastImageId();

			}
		});

		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub


				getLastImageId();

				SyslogUtils.logEvent(getApplicationContext(),
						"\"Yes\" pressed", EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);
				// mProgressPhotoUploading.setVisibility(View.VISIBLE);
				//ToastUtils.showShort(getApplicationContext()," Photo started to upload");
				mAfterPhotoTokenStatus.set(-1);
				compressImage(byButtonTakePhoto);

				deleteRotateFiles();

				BitmapDrawable drawable = (BitmapDrawable) image1.getDrawable();
				Bitmap bitmap1 = drawable.getBitmap();
				storeImage(bitmap1, imageFileName);

				isimagepopupshowing = false;

				isPopupShowing = false;
				if (!ServiceUtils.isNetworkAvailable(getApplicationContext())) {
					mApplication.cancelHalfWalkAlarm();
					if(isWalkPressed) {
						continueStopWalk(false);
					}
				}


				else {
					mApplication.cancelHalfWalkAlarm();
					if(isWalkPressed) {
						continueStopWalk(true);
					}
				}



				dialog.dismiss();
			}
		});

		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				deleteRotateFiles();

				SyslogUtils.logEvent(getApplicationContext(), "\"No\" pressed",
						EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);
				mAfterPhotoTokenStatus.set(-1);

				isimagepopupshowing = false;

				mApplication.cleanImageUri();
				resultFile = null;
				isPopupShowing = false;
				dialog.dismiss();
			}
		});

		dialog.show();

		// AlertUtils.showYesNo(mContext, "Take Photo", "This photo will be sent
		// to the dog owner by email. Please be sure this is a good photo of the
		// dog. If not, please hit cancel", "Send photo", "Cancel", new
		// IDialogResultWaitable() {
		// public void reactOnYes() {
		// SyslogUtils.logEvent(getApplicationContext(), "\"Yes\" pressed",
		// EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		// mProgressPhotoUploading.setVisibility(View.VISIBLE);
		// mAfterPhotoTokenStatus.set(-1);
		// compressImage(byButtonTakePhoto);
		// }
		//
		// public void reactOnNo() {
		// SyslogUtils.logEvent(getApplicationContext(), "\"No\" pressed",
		// EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		// mAfterPhotoTokenStatus.set(-1);
		// }
		// });
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {

		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {

		// Check that the SDCard is mounted
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraVideo");

		// Create the storage directory(MyCameraVideo) if it does not exist
		if (!mediaStorageDir.exists()) {

			if (!mediaStorageDir.mkdirs()) {

				// output.setText("Failed to create directory MyCameraVideo.");

				// Toast.makeText(WalkMapActivity.this, "Failed to create
				// directory MyCameraVideo.", Toast.LENGTH_LONG).show();

				Log.d("MyCameraVideo",
						"Failed to create directory MyCameraVideo.");
				return null;
			}
		}

		// Create a media file name

		// For unique file name appending current timeStamp with file name
		Date date = new Date();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date
				.getTime());

		File mediaFile;

		if (type == MEDIA_TYPE_VIDEO) {

			// For unique video file name appending current timeStamp with file
			// name
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");

		} else {
			return null;
		}

		return mediaFile;
	}

	private void showUploadVideoConfirm(final boolean byButtonTakePhoto) {
		SyslogUtils.logEvent(getApplicationContext(), "showUploadVideoConfirm",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialod_video_confirm);
		dialog.setTitle("Take Video");
		final VideoView vedio = (VideoView) dialog
				.findViewById(R.id.videoPreview);
		final String path = getVideoPathFromImageUri(mApplication.getVedioUri(), mContext);

		// final TextView tv = (TextView)
		// dialog.findViewById(R.id.dialog_video_text);
		// tv.setText("path = " + path + " videoFileName = " + videoFileName);

		MediaController mediaController = new MediaController(
				dialog.getContext());
		mediaController.setAnchorView(vedio);
		// Set video link (mp4 format )
		// Uri video = Uri.parse("mp4 video link");
		vedio.setMediaController(mediaController);

		vedio.setVideoPath(videoFileName);
		// start playing
		vedio.start();

		Button ok, cancle, rotate;
		ok = (Button) dialog.findViewById(R.id.dialog_video_ButtonOK);
		cancle = (Button) dialog.findViewById(R.id.dialog_video_ButtonCancle);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub


				deleteRotateFiles();


				SyslogUtils.logEvent(getApplicationContext(),
						"\"Yes\" pressed", EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);
				// mProgressPhotoUploading.setVisibility(View.VISIBLE);
				mAfterPhotoTokenStatus.set(-1);
				// mAfterVedioTokenStatus.set(-2);
				compressVedio(byButtonTakePhoto);

				// BitmapDrawable drawable = (BitmapDrawable)
				// image1.getDrawable();
				// Bitmap bitmap1 = drawable.getBitmap();
				// storeImage(bitmap1, imageFileName);

				dialog.dismiss();
			}
		});

		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SyslogUtils.logEvent(getApplicationContext(), "\"No\" pressed",
						EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);

				//mAfterPhotoTokenStatus.set(-1);



				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private boolean storeImage(Bitmap imageData, String filename) {
		// get path to external storage (SD card)

		if(!filename.contains(".png")) {
			long time = System.currentTimeMillis();
			int sec = (int) (time / 1000);
			filename = String.format(Locale.getDefault(), "%d.png", sec);
		}

		String iconsStoragePath = Environment.getExternalStorageDirectory()
				+ "/swiftoImages/";

		String filePath = "";
		File sdIconStorageDir = new File(iconsStoragePath);

		// create storage directories, if they don't exist
		if (!sdIconStorageDir.exists()) {
			sdIconStorageDir.mkdirs();
		}
		try {
			filePath = sdIconStorageDir.toString() + "/" + filename;
			//filePath.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);

			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);

			if(bos!=null)
			{
				imageData.compress(CompressFormat.PNG, 100, bos);

				bos.flush();
				bos.close();
				Toast.makeText(mContext, "Photo successfully saved", Toast.LENGTH_LONG).show();
			}
			// choose another format if PNG doesn't suit you


		} catch (FileNotFoundException e) {
			SyslogUtils.logEvent(getApplicationContext(),
					"Error saving image file: " + e.getMessage() + "  filePath : " + filePath +
							" iconsStoragePath: " + iconsStoragePath,
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			Toast.makeText(mContext,
					"Error saving image file: " + e.getMessage()+ "  filePath : " + filePath+
							" iconsStoragePath: " + iconsStoragePath, Toast.LENGTH_LONG).show();
			return false;
		} catch (Exception e) {
			SyslogUtils.logEvent(getApplicationContext(),
					"Error saving image file: " + e.getMessage()+ "  filePath : " + filePath+
					" iconsStoragePath: " + iconsStoragePath,
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			Toast.makeText(mContext,
					"Error saving image file: " + e.getMessage()+ "  filePath : " + filePath+
							" iconsStoragePath: " + iconsStoragePath, Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}

	private void compressImage(boolean byButtonTakePhoto) {
		if (byButtonTakePhoto) {
			UploadPhotoStatus.getInstance().setStatusUploadingButtonTakePhoto();
		} else {
			UploadPhotoStatus.getInstance().setStatusUploadingPopup();
		}

		resultFile = convertImageUriToFile(mApplication.getImageUri(), mContext);

		if (resultFile != null) {
			SyslogUtils.logEvent(getApplicationContext(),
					"compressImage, file is " + resultFile.getAbsolutePath(),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			runOnUiThread(new Runnable() {
				public void run() {

					mApplication.uploadToAmazon(resultFile);
					// vivek need to change in future
					//mApplication.cancelHalfWalkAlarm();
				}
			});

		} else {
			SyslogUtils.logEvent(getApplicationContext(),
					"Problem getting file with image - result file is null",
					EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			UploadPhotoStatus.getInstance().setStatusNotUploading();
		}
	}

	private void compressVedio(boolean byButtonTakeVideo) {
		if (byButtonTakeVideo) {
			UploadVideoStatus.getInstance().setStatusUploadingButtonTakePhoto();
		} else {
			UploadVideoStatus.getInstance().setStatusUploadingPopup();
		}

		// final File resultFile =
		// convertVedioUriToFile(mApplication.getVedioUri(), mContext);
		final File resultVideoFile = new File(videoFileName);

		if (resultVideoFile != null) {
			SyslogUtils.logEvent(
					getApplicationContext(),
					"compressVedio, file is "
							+ resultVideoFile.getAbsolutePath(),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			runOnUiThread(new Runnable() {
				public void run() {
					mApplication.uploadVideoToAmazon(resultVideoFile);
				}
			});
		} else {
			SyslogUtils.logEvent(getApplicationContext(),
					"Problem getting file with Vedio - result file is null",
					EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			UploadPhotoStatus.getInstance().setStatusNotUploading();
		}
	}

	public static String getPathFromImageUri(Uri imgUri, Activity activity) {

		Cursor cursor = null;
		File toRet = null;
		String path = null;

		try {
			String[] proj = { MediaStore.Images.Media.DATA,
			// MediaStore.Images.Media._ID,
			// MediaStore.Images.ImageColumns.ORIENTATION
			};

			CursorLoader cLoader = new CursorLoader(activity, imgUri, proj,
					null, null, null);
			cursor = cLoader.loadInBackground();
			cursor.moveToFirst();

			int file_ColumnIndex = cursor
					.getColumnIndex(MediaStore.Images.Media.DATA);

			path = cursor.getString(file_ColumnIndex);
			toRet = new File(path);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return path;
	}

	public static String getVideoPathFromImageUri(Uri imgUri, Activity activity) {

		Cursor cursor = null;
		File toRet = null;
		String path = null;

		try {
			String[] proj = { MediaStore.Video.Media.DATA,
			// MediaStore.Images.Media._ID,
			// MediaStore.Images.ImageColumns.ORIENTATION
			};

			CursorLoader cLoader = new CursorLoader(activity, imgUri, proj,
					null, null, null);
			cursor = cLoader.loadInBackground();
			cursor.moveToFirst();

			int file_ColumnIndex = cursor
					.getColumnIndex(MediaStore.Video.Media.DATA);

			path = cursor.getString(file_ColumnIndex);
			toRet = new File(path);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return path;
	}

	public static File convertImageUriToFile(Uri imgUri, Activity activity) {
		Cursor cursor = null;
		File toRet = null;

		try {
			String[] proj = { MediaStore.Images.Media.DATA,
			// MediaStore.Images.Media._ID,
			// MediaStore.Images.ImageColumns.ORIENTATION
			};

			CursorLoader cLoader = new CursorLoader(activity, imgUri, proj,
					null, null, null);
			cursor = cLoader.loadInBackground();
			cursor.moveToFirst();

			int file_ColumnIndex = cursor
					.getColumnIndex(MediaStore.Images.Media.DATA);

			String path = cursor.getString(file_ColumnIndex);
			toRet = new File(path);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return toRet;
	}

	public static File convertVedioUriToFile(Uri videoUri, Activity activity) {
		Cursor cursor = null;
		File toRet = null;

		try {
			String[] proj = { MediaStore.Video.Media.DATA,
			// MediaStore.Images.Media._ID,
			// MediaStore.Images.ImageColumns.ORIENTATION
			};

			CursorLoader cLoader = new CursorLoader(activity, videoUri, proj,
					null, null, null);
			cursor = cLoader.loadInBackground();
			cursor.moveToFirst();

			int file_ColumnIndex = cursor
					.getColumnIndex(MediaStore.Video.Media.DATA);

			String path = cursor.getString(file_ColumnIndex);
			toRet = new File(path);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return toRet;
	}

	OnClickListener btStopWalkClickListener = new OnClickListener() {
		public void onClick(View v) {


			mApplication = (SwiftoApplication) getApplication();
			// nothing is performing...
			final String startedWalkId = SharedPreferencesHelper
					.getStartedWalkId(getApplicationContext());


			long walkofftime = SharedPreferencesHelper.getWalkOffTimeExact(startedWalkId, getApplicationContext());

			Timestamp stamp = new Timestamp(walkofftime);
			Date date = new Date(stamp.getTime());

			Log.d("button click", "nkp date "+ date);

			if(walkofftime<100)
			{
				//Log.d("less 100", "nkp is calling " +walkofftime);
				long time  = System.currentTimeMillis();
				 stamp = new Timestamp(time);
				 date = new Date(stamp.getTime());
				SyslogUtils.logEvent(getApplicationContext(),
						"walkofftime     ------  " + date,
						EnumLogSeverity.ERROR, EnumLogType.HARDWARE_TYPE);


				SharedPreferencesHelper.saveWalkOffTimeExact(startedWalkId, mContext,time);

			}
			else
			{
				//Log.d("greater 100", "nkp is calling " +walkofftime);
				SyslogUtils.logEvent(getApplicationContext(),
						"walkofftime already addedd  and value is      ------  "+date,
						EnumLogSeverity.ERROR, EnumLogType.HARDWARE_TYPE);
			}



			isWalkPressed = true;

			continueStopWalk(false);
			stopLocationUpdates();
			if(mApplication!=null) {
				//Log.d("cancelAfterWalkalarm", "nkp is calling");
				mApplication.cancelAfterWalkAlarm();
			}

		}
	};

	/** Are some end walk task running now? */
	private boolean stopWalkTasksArePerformingNow() {
		// ...is photo being uploaded
		if (mApplication.getIsUploadingToAmazon()) {
			Toast.makeText(mContext, "Photo is uploading, please wait",
					Toast.LENGTH_SHORT).show();
			SyslogUtils.logEvent(mContext, "Photo is uploading, please wait",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			return true;
		}

		// ...are non-sent requests being sent
		if (mApplication.getUnsentRequestsAreSending()) {
			Toast.makeText(mContext, "Unsent data is sending, please wait",
					Toast.LENGTH_SHORT).show();
			SyslogUtils.logEvent(getApplicationContext(),
					"Unsent data is sending, please wait",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			return true;
		}

		return false;
	}

	/** Hardcore */
	public void continueStopWalk(boolean afterpic) {
		mApplication = (SwiftoApplication) getApplication();
		// Log.w("vivek1", "continueStopWalk Called");
		// Log.d("nkp", "continueStopWalk Called");
		// ToastUtils.showLong(mContext, "continueStopWalk Called");

		// if (stopWalkTasksArePerformingNow()) {
		// // Log.w("vivek1", "return stopWalkTasksArePerformingNow()");
		// //
		// ToastUtils.showLong(mContext,"return stopWalkTasksArePerformingNow()");
		// return;
		// }

		mApplication.stopWalkProgressServices();
		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);
		checkPhoto(startedWalkId , afterpic);
		//checkPhotoNMsg();
		// stopWalk();

	}

	public void checkMsg()
	{

		mApplication = (SwiftoApplication) getApplication();
		// nothing is performing...
		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);
		StartedWalkExt startedWalkExt = mApplication.getDbAdapter()
				.getStartedWalkForId(startedWalkId);

		if (mApplication.hasUnsentDataAndSaveStatusToDatabase()) {

			if (startedWalkExt.UnsentDataSkipped == false) {

				if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
					// Log.w("vivek1",
					// "mApplication.sendUnsentPointsAndMeetPoos();");
					// ToastUtils.showLong(mContext,"mApplication.sendUnsentPointsAndMeetPoos();");
					mApplication.sendUnsentPointsAndMeetPoos();
				} else {
					// noInternetConnection();
					mApplication.taskMap.put("sendUnsentPointsAndMeetPoos", "sendUnsentPointsAndMeetPoos");
				}

			}
		}

		if (startedWalkExt.MessageSent) {
			Log.w("vivek1", "onSendMessageSuccess();");
			// ToastUtils.showLong(mContext, "onSendMessageSuccess();");
			onSendMessageSuccess();
		} else {

			showRemindersIfNeeded(startedWalkExt, startedWalkId);
		}

	}

	public void showPhoto()
	{
		mApplication = (SwiftoApplication) getApplication();
		// nothing is performing...
		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);
		StartedWalkExt startedWalkExt = mApplication.getDbAdapter()
				.getStartedWalkForId(startedWalkId);

		if (startedWalkExt.PhotoUploaded == false) {
			if (startedWalkExt.PhotoUploadTriedOnce == false && isWalkPressed) {
				SyslogUtils
						.logEvent(
								getApplicationContext(),
								"Stop walk - photo not uploaded. Starting to upload photo",
								EnumLogSeverity.INFORMATIONAL,
								EnumLogType.INTERNAL_TYPE);
				mApplication
						.getDbAdapter()
						.updateSomeFlagOfStartedWalk(
								startedWalkId,
								TableColumns.StartedWalksColumns.PHOTO_UPLOAD_TRIED_ONCE,
								true);

				//if (ServiceUtils.isNetworkAvailable(this)) {
				// Log.w("vivek1", "showPopupTakePhoto call");
				// ToastUtils.showLong(mContext, "showPopupTakePhoto call");
				boolean isphototake = SharedPreferencesHelper
						.gatPhotoTaken(startedWalkId, this);

				if (isphototake) {
					resultFile = convertImageUriToFile(
							mApplication.getImageUri(), mContext);
					// ToastUtils.showLong(mContext,
					// "resultFile is null"+resultFile);
					if (resultFile != null) {
						mApplication.uploadToAmazon(resultFile);
						// vivek need to change in future
					} else {
						showPopupTakePhoto();
						return;
					}
				} else {
					// ToastUtils.showLong(mContext,
					// "isphototake is false");
					showPopupTakePhoto();
					return;
				}

				//} else {
				//	noInternetConnection();
				//}

				// showRemindersIfNeeded(startedWalkExt, startedWalkId);
				// ToastUtils.showLong(mContext,"return startedWalkExt.PhotoUploadTriedOnce == false");
				// Log.w("vivek1",
				// "return startedWalkExt.PhotoUploadTriedOnce == false");
				// return;
			} else {
				if (startedWalkExt.PhotoSkipped == false) {
					// Log.w("vivek1", "startedWalkExt.PhotoSkipped == false");
					// ToastUtils.showLong(mContext,"startedWalkExt.PhotoSkipped == false");
					//if (ServiceUtils
					//		.isNetworkAvailable(getApplicationContext())) {
					// ToastUtils.showLong(mContext,"Photo uploading fails. ok pressed");
					boolean isphototake = SharedPreferencesHelper
							.gatPhotoTaken(startedWalkId, this);

					if (isphototake) {
						resultFile = convertImageUriToFile(
								mApplication.getImageUri(), mContext);
						// ToastUtils.showLong(mContext,
						// "resultFile is null"+resultFile);
						if (resultFile != null) {
							mApplication.uploadToAmazon(resultFile);
						} else {
							showPopupTakePhoto();
							return;
						}
					} else {
						// ToastUtils.showLong(mContext,
						// "isphototake is false");
						showPopupTakePhoto();
						return;
					}

					//} else {
					//	noInternetConnection();
					//}

					/*
					 * AlertUtils.showYes(mContext, "Failure",
					 * "Photo uploading fails.", "Retry", new
					 * IDialogResultWaitable() { public void reactOnYes() { if
					 * (ServiceUtils
					 * .isNetworkAvailable(getApplicationContext())) { //
					 * ToastUtils
					 * .showLong(mContext,"Photo uploading fails. ok pressed");
					 * if (resultFile != null) { mApplication
					 * .uploadToAmazon(resultFile); } else {
					 * showPopupTakePhoto(); }
					 *
					 * } else { noInternetConnection(); } }
					 *
					 * public void reactOnNo() { }
					 *
					 * });
					 */

					// showRemindersIfNeeded(startedWalkExt, startedWalkId);
					// ToastUtils.showLong(mContext,"return startedWalkExt.PhotoSkipped == false");
					// return;
				}
			}
		}
	}

	private void checkPhotoNMsg() {
		mApplication = (SwiftoApplication) getApplication();
		// nothing is performing...
		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(this);
		StartedWalkExt startedWalkExt = mApplication.getDbAdapter()
				.getStartedWalkForId(startedWalkId);

		if (mApplication.hasUnsentDataAndSaveStatusToDatabase()) {

			if (startedWalkExt.UnsentDataSkipped == false) {

				if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
					// Log.w("vivek1",
					// "mApplication.sendUnsentPointsAndMeetPoos();");
					// ToastUtils.showLong(mContext,"mApplication.sendUnsentPointsAndMeetPoos();");
					mApplication.sendUnsentPointsAndMeetPoos();
				} else {
					// noInternetConnection();
					mApplication.taskMap.put("sendUnsentPointsAndMeetPoos", "sendUnsentPointsAndMeetPoos");
				}

			}

			/*
			 * if (startedWalkExt.UnsentDataSkipped == false) {
			 * AlertUtils.showYes(mContext, "Information",
			 * "You have unsent data! Send it to server, please.", "Send", new
			 * IDialogResultWaitable() {
			 * 
			 * @Override public void reactOnYes() { if (ServiceUtils
			 * .isNetworkAvailable(getApplicationContext())) { //
			 * Log.w("vivek1", //
			 * "mApplication.sendUnsentPointsAndMeetPoos();"); //
			 * ToastUtils.showLong
			 * (mContext,"mApplication.sendUnsentPointsAndMeetPoos();");
			 * mApplication.sendUnsentPointsAndMeetPoos(); } else {
			 * noInternetConnection(); } }
			 * 
			 * @Override public void reactOnNo() { // do nothing } });
			 * 
			 * // showRemindersIfNeeded(startedWalkExt, startedWalkId);
			 * 
			 * // return; }
			 */
		}

		if (startedWalkExt.PhotoUploaded == false) {
			if (startedWalkExt.PhotoUploadTriedOnce == false) {
				SyslogUtils
						.logEvent(
								getApplicationContext(),
								"Stop walk - photo not uploaded. Starting to upload photo",
								EnumLogSeverity.INFORMATIONAL,
								EnumLogType.INTERNAL_TYPE);
				mApplication
						.getDbAdapter()
						.updateSomeFlagOfStartedWalk(
								startedWalkId,
								TableColumns.StartedWalksColumns.PHOTO_UPLOAD_TRIED_ONCE,
								true);

				//if (ServiceUtils.isNetworkAvailable(this)) {
					// Log.w("vivek1", "showPopupTakePhoto call");
					// ToastUtils.showLong(mContext, "showPopupTakePhoto call");
					boolean isphototake = SharedPreferencesHelper
							.gatPhotoTaken(startedWalkId, this);

					if (isphototake) {
						resultFile = convertImageUriToFile(
								mApplication.getImageUri(), mContext);
						// ToastUtils.showLong(mContext,
						// "resultFile is null"+resultFile);
						if (resultFile != null) {
							mApplication.uploadToAmazon(resultFile);
									// vivek need to change in future
						} else {
							showPopupTakePhoto();
							return;
						}
					} else {
						// ToastUtils.showLong(mContext,
						// "isphototake is false");
						showPopupTakePhoto();
						return;
					}

				//} else {
				//	noInternetConnection();
				//}

				// showRemindersIfNeeded(startedWalkExt, startedWalkId);
				// ToastUtils.showLong(mContext,"return startedWalkExt.PhotoUploadTriedOnce == false");
				// Log.w("vivek1",
				// "return startedWalkExt.PhotoUploadTriedOnce == false");
				// return;
			} else {
				if (startedWalkExt.PhotoSkipped == false) {
					// Log.w("vivek1", "startedWalkExt.PhotoSkipped == false");
					// ToastUtils.showLong(mContext,"startedWalkExt.PhotoSkipped == false");
					//if (ServiceUtils
					//		.isNetworkAvailable(getApplicationContext())) {
						// ToastUtils.showLong(mContext,"Photo uploading fails. ok pressed");
						boolean isphototake = SharedPreferencesHelper
								.gatPhotoTaken(startedWalkId, this);

						if (isphototake) {
							resultFile = convertImageUriToFile(
									mApplication.getImageUri(), mContext);
							// ToastUtils.showLong(mContext,
							// "resultFile is null"+resultFile);
							if (resultFile != null) {
								mApplication.uploadToAmazon(resultFile);
							} else {
								showPopupTakePhoto();
								return;
							}
						} else {
							// ToastUtils.showLong(mContext,
							// "isphototake is false");
							showPopupTakePhoto();
							return;
						}

					//} else {
					//	noInternetConnection();
					//}

					/*
					 * AlertUtils.showYes(mContext, "Failure",
					 * "Photo uploading fails.", "Retry", new
					 * IDialogResultWaitable() { public void reactOnYes() { if
					 * (ServiceUtils
					 * .isNetworkAvailable(getApplicationContext())) { //
					 * ToastUtils
					 * .showLong(mContext,"Photo uploading fails. ok pressed");
					 * if (resultFile != null) { mApplication
					 * .uploadToAmazon(resultFile); } else {
					 * showPopupTakePhoto(); }
					 * 
					 * } else { noInternetConnection(); } }
					 * 
					 * public void reactOnNo() { }
					 * 
					 * });
					 */

					// showRemindersIfNeeded(startedWalkExt, startedWalkId);
					// ToastUtils.showLong(mContext,"return startedWalkExt.PhotoSkipped == false");
					// return;
				}
			}
		}

		if (startedWalkExt.MessageSent) {
			Log.w("vivek1", "onSendMessageSuccess();");
			// ToastUtils.showLong(mContext, "onSendMessageSuccess();");..
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"onSendMessageSuccess ",
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);
			onSendMessageSuccess();
		} else {

			showRemindersIfNeeded(startedWalkExt, startedWalkId);
		}
	}

	private void showRemindersIfNeeded(StartedWalkExt startedWalkExt,
			String startedWalkId) {
		// vivek
		// if (startedWalkExt.RemindersShownOnce) {
		// return;
		// }

		if (isFinishing()) {
			SyslogUtils.logEvent(getApplicationContext(),
					"Reminder(s) didn't showed - activity is not running",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		} else {

			mApplication
					.getDbAdapter()
					.updateSomeFlagOfStartedWalk(
							startedWalkId,
							TableColumns.StartedWalksColumns.REMINDERS_SHOWN_ONCE,
							true);


			if(popupWindowTakePhoto!=null  )
			{
				if(popupWindowTakePhoto.isShowing())
					popupWindowTakePhoto.dismiss();
			}
			addFeedAndMedicine();
			// vivek

		}
	}

	// private void showRemindersIfNeeded(StartedWalkExt startedWalkExt,
	// String startedWalkId) {
	// // vivek
	// if (startedWalkExt.RemindersShownOnce) {
	// return;
	// }
	//
	// if (isFinishing()) {
	// SyslogUtils.logEvent(getApplicationContext(),
	// "Reminder(s) didn't showed - activity is not running",
	// EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
	// } else {
	// String feedText = ((SwiftoApplication) getApplication())
	// .getFeedText();
	//
	// final Activity context = this;
	//
	// if (feedText != null && feedText.length() > 0) {
	// SyslogUtils.logEvent(getApplicationContext(),
	// "Feed reminder is showing with text: " + feedText,
	// EnumLogSeverity.INFORMATIONAL,
	// EnumLogType.INTERNAL_TYPE);
	// AlertUtils.showYes(context, "Feed Reminder", feedText, "OK",
	// new IDialogResultWaitable() {
	// @Override
	// public void reactOnYes() {
	// SyslogUtils.logEvent(getApplicationContext(),
	// "Feed reminder dismissed",
	// EnumLogSeverity.INFORMATIONAL,
	// EnumLogType.INTERNAL_TYPE);
	// }
	//
	// @Override
	// public void reactOnNo() {
	// // don't get here
	// }
	// });
	// } else {
	// SyslogUtils
	// .logEvent(
	// getApplicationContext(),
	// "Feed reminder didn't showed - no one dog should be fed after the walk",
	// EnumLogSeverity.INFORMATIONAL,
	// EnumLogType.INTERNAL_TYPE);
	// }
	//
	// String medicateText = ((SwiftoApplication) getApplication())
	// .getMedicationText();
	//
	// if (medicateText != null && medicateText.length() > 0) {
	// SyslogUtils.logEvent(getApplicationContext(),
	// "Medication reminder is showing with text: "
	// + medicateText, EnumLogSeverity.INFORMATIONAL,
	// EnumLogType.INTERNAL_TYPE);
	// AlertUtils.showYes(context, "Medication Reminder",
	// medicateText, "OK", new IDialogResultWaitable() {
	// @Override
	// public void reactOnYes() {
	// SyslogUtils.logEvent(getApplicationContext(),
	// "Medication reminder dismissed",
	// EnumLogSeverity.INFORMATIONAL,
	// EnumLogType.INTERNAL_TYPE);
	// }
	//
	// @Override
	// public void reactOnNo() {
	// // don't get here
	// }
	// });
	// } else {
	// SyslogUtils
	// .logEvent(
	// getApplicationContext(),
	// "Medication reminder didn't showed - no one dog should receive medication",
	// EnumLogSeverity.INFORMATIONAL,
	// EnumLogType.INTERNAL_TYPE);
	// }
	//
	// // vivek
	// mApplication
	// .getDbAdapter()
	// .updateSomeFlagOfStartedWalk(
	// startedWalkId,
	// TableColumns.StartedWalksColumns.REMINDERS_SHOWN_ONCE,
	// true);
	// }
	// }

	private void noInternetConnection() {
		Toast.makeText(getApplicationContext(), "No internet connection",
				Toast.LENGTH_SHORT).show();
		SyslogUtils.logEvent(getApplicationContext(),
				"No internet connection on " + " trying to stop walk",
				EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
	}

	PopupWindow popupWindowTakePhoto;
	Walk currentStartedWalk;
	public void showPopupTakePhoto() {

		if(isimagepopupshowing)
		{
			SyslogUtils.logEvent(getApplicationContext(),
					"showPopupTakePhoto Already in process : ",
					EnumLogSeverity.INFORMATIONAL,
					EnumLogType.INTERNAL_TYPE);
			return;
		}


		if(popupWindowTakePhoto!=null  )
		{
			if(popupWindowTakePhoto.isShowing())
				popupWindowTakePhoto.dismiss();
		}

		isimagepopupshowing = true;


		SyslogUtils.logEvent(getApplicationContext(),
				"showPopupTakePhoto call started : " + mLastEnteredMessage,
				EnumLogSeverity.INFORMATIONAL,
				EnumLogType.INTERNAL_TYPE);

		 currentStartedWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getStartedWalk();


		StartedWalkExt startedWalkExt = mApplication.getDbAdapter()
				.getStartedWalkForId(currentStartedWalk._id);
		if (!startedWalkExt.Completed) {

			SyslogUtils.logEvent(getApplicationContext(),
					"not startedWalkExt.Completed so this should be return ",
					EnumLogSeverity.INFORMATIONAL,
					EnumLogType.INTERNAL_TYPE);
			return;
		}


		// set boolean flag for photo taken
		SharedPreferencesHelper.savePhotoTaken(currentStartedWalk._id,
				getApplicationContext(), true);

		if(globleDogId.equals("")) {
			((SwiftoApplication) getApplication())
					.setDogIdForPhoto(currentStartedWalk.Dogs.get(0)._id);
		}
		else
		{
			((SwiftoApplication) getApplication())
					.setDogIdForPhoto(globleDogId);
		}



		final View popupLayout = LayoutInflater.from(mContext).inflate(
				R.layout.popup_take_picture,
				(ViewGroup) findViewById(R.id.popup_take_picture_layout));
		Button btSend = (Button) popupLayout
				.findViewById(R.id.popup_take_picture_bt_take_picture);

		popupWindowTakePhoto = new PopupWindow(popupLayout, -1, -1, true);





		popupWindowTakePhoto.setBackgroundDrawable(new BitmapDrawable());

		popupWindowTakePhoto.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				//do your code here

				SyslogUtils.logEvent(getApplicationContext(),
						" popupWindowTakePhoto onBackPressed  event find..  isimagepopupshowing is -" + isimagepopupshowing,
						EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);

				isimagepopupshowing = false;
			}
		});


		btSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {


				popupWindowTakePhoto.dismiss();

				startCameraActivity(false);
			}
		});

		TextView txtInfo = (TextView) popupLayout
				.findViewById(R.id.popup_take_picture_txt_info);
		String walkerName = ((SwiftoApplication) getApplication()).getWalker().username;
		String info = String.format(
				getResources().getString(R.string.popup_take_picture_info),
				walkerName, currentStartedWalk.getDogNames());

		if(globleDogName.equals("")) {
			 info = String.format(
					getResources().getString(R.string.popup_take_picture_info),
					walkerName, currentStartedWalk.getDogNames());
		}
		else
		{
			info = String.format(
					getResources().getString(R.string.popup_take_picture_info),
					walkerName,globleDogName);
		}

		txtInfo.setText(info);

		// hack to react on BackButton
		popupWindowTakePhoto.setBackgroundDrawable(new BitmapDrawable());
		popupWindowTakePhoto.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);


		SyslogUtils.logEvent(getApplicationContext(),
				"showPopupTakePhoto call End  : " + mLastEnteredMessage,
				EnumLogSeverity.INFORMATIONAL,
				EnumLogType.INTERNAL_TYPE);
	}

	private String mLastEnteredMessage = "";

	private void showDialogSendMessage() {


		if(popupWindowTakePhoto!=null  )
		{
			if(popupWindowTakePhoto.isShowing())
				popupWindowTakePhoto.dismiss();
		}

		final Activity context = this;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						context);
				LayoutInflater inflater = context.getLayoutInflater();

				final AlertDialog dialog;
				currentStartedWalk = ((SwiftoApplication) getApplication())
						.getWalkGetter().getStartedWalk();
				View view = inflater
						.inflate(R.layout.dialog_send_message, null);
				builder.setView(view);

				final EditText input = (EditText) view.findViewById(R.id.popup_send_message_input_message);
				final TextView txtInfo = (TextView) view.findViewById(R.id.popup_send_message_txt_info);

				if (currentStartedWalk.WalkType.equals("overnight")){
					txtInfo.setText(getResources().getString(R.string.popup_send_message_overnight_stay_summary));
				}else {
					txtInfo.setText(getResources().getString(R.string.popup_send_message_walk_complete));
				}

				mLastEnteredMessage = SharedPreferencesHelper
						.getLastMessage(getApplicationContext());

				if (mLastEnteredMessage != "") {
					SyslogUtils.logEvent(getApplicationContext(),
							"Restoring message: " + mLastEnteredMessage,
							EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);
				}

				input.setText(mLastEnteredMessage);
				Button btSend = (Button) view
						.findViewById(R.id.popup_send_message_bt_send);

				dialog = builder.create();

				dialog.setCanceledOnTouchOutside(false);



				btSend.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mLastEnteredMessage = input.getText().toString();
						mLastEnteredMessage = mLastEnteredMessage.trim();

						if (mLastEnteredMessage == null
								|| mLastEnteredMessage.equals("")) {
							SyslogUtils.logEvent(getApplicationContext(),
									"Pressed \"Send Message\" with empty text",
									EnumLogSeverity.INFORMATIONAL,
									EnumLogType.INTERNAL_TYPE);
							Toast.makeText(getApplicationContext(),
									"Message is empty!", Toast.LENGTH_SHORT)
									.show();
						} else if (mLastEnteredMessage == null
								|| mLastEnteredMessage.length() < 30) {
							Toast.makeText(
									getApplicationContext(),
									"Message is too short , please enter atleast 30 letters!",
									Toast.LENGTH_LONG).show();
						} else {
							if (dialog != null) {
								sendMessage(mLastEnteredMessage);
								dialog.dismiss();
							} else {
								SyslogUtils
										.logEvent(
												context,
												"Error dismissing dialog send message - dialog is null",
												EnumLogSeverity.ERROR,
												EnumLogType.INTERNAL_TYPE);
							}
						}
					}
				});

				if(!dialog.isShowing()) {
					dialog.show();
				}
			}
		});
	}

	private void sendMessage(String message) {
		SharedPreferencesHelper.saveLastMessage(getApplicationContext(),
				message);

		if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			((SwiftoApplication) getApplication()).sendMessage(this, message);
		} else {
//			Toast.makeText(getApplicationContext(), "No internet connection",
//					Toast.LENGTH_SHORT).show();
			SyslogUtils.logEvent(getApplicationContext(),
					"No internet connection on " + "trying to send message",
					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
			
			((SwiftoApplication) getApplication()).taskMap.put("sendMessage", message);
			
			stopWalk();

			Intent scheduleIntent = new Intent(getApplicationContext(),
					ScheduleActivity.class);
			scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(scheduleIntent);
		}
	}

	OnClickListener btNavigateLeftClickListener = new OnClickListener() {
		public void onClick(View v) {
			finish();

			Intent dogInfoIntent = new Intent(getApplicationContext(),
					DogInfoActivity.class);
			startActivity(dogInfoIntent);
		}
	};

	OnClickListener btNavigateRightClickListener = new OnClickListener() {
		public void onClick(View v) {

			// ((SwiftoApplication) getApplication()).debugAfterWalk(false);
			finish();

			Intent scheduleIntent = new Intent(getApplicationContext(),
					ScheduleActivity.class);
			scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(scheduleIntent);
		}
	};

	// --------------------------------------------------------------------
	//
	// GPS
	//
	// ----------------

	private GeoPoint lastPoint;

	private void relocateToLocationExt(WalkGPSPoint point) {

		// ToastUtils.showLong(getApplicationContext(), "point = " + point);
		if (mLayoutButtons.getVisibility() == View.GONE) {
			reactOnFirstLocationReceive();
		}

		int lat = point.getLatitudeE6();
		int lng = point.getLongitudeE6();
		// ToastUtils.showLong(getApplicationContext(), "last lat = "+
		// lat+" last lat = "+lng);
		GeoPoint gp = new GeoPoint(lat, lng);

		LatLng lt = new LatLng(point.getLatitudeE6() / 1E6,
				point.getLongitudeE6() / 1E6);

		polylineOptions = new PolylineOptions();
		// Setting the color of the polyline
		//polylineOptions.color(Color.BLUE);
		polylineOptions.color(Color.RED);

		// Setting the width of the polyline
		polylineOptions.width(3);

		points.add(lt);
		// Setting points of polyline
		polylineOptions.addAll(points);
		// Adding the polyline to the map
		googleMap.addPolyline(polylineOptions);

		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lt, 18.0f));



		try{
			myPositionMarker.remove();
		} catch (Exception e){
			//..
		}

		myPositionMarker = googleMap.addMarker(new MarkerOptions()
				.flat(true)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.circle_blue))
				.anchor(0.5f, 0.5f)
				.position(lt));



		googleMap.moveCamera(CameraUpdateFactory.newLatLng(lt));
		lastPoint = gp;

	}

	private void reactOnFirstLocationReceive() {
		mAnimMoveDown.setAnimationListener(hideSerchingListener);
		mLayoutSearching.startAnimation(mAnimMoveDown);
	}

	public boolean locationChangedExt(final WalkGPSPoint point) {
		runOnUiThread(new Runnable() {
			public void run() {
				//ToastUtils.showShort(getApplicationContext(),"point = "+point);
				relocateToLocationExt(point);
			}
		});

		return true;
	}

	public void onWalkComplete() {
		runOnUiThread(new Runnable() {
			public void run() {
				btStopWalk.setVisibility(View.VISIBLE);
				//Log.d("onWalkCOmplete", "nkp is calling");
				//nkp
				currentStartedWalk = ((SwiftoApplication) getApplication())
						.getWalkGetter().getStartedWalk();
				if (currentStartedWalk.WalkType.equals("overnight"))
					btStopWalk.setText(getResources().getString(R.string.activity_walk_map_bt_overnight_check_out));
				else {
					btStopWalk.setText(getResources().getString(R.string.activity_walk_map_bt_stop_walk));
					mProgressWalk.setProgress(mProgressWalk.getMax());
				}
			}
		});
	}

	public void onSendMessageFailure() {
		showDialogSendMessage();
	}

	public void onSendMessageSuccess() {

		final String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		SharedPreferencesHelper.saveWalkMSG(startedWalkId, getApplicationContext(), "yes");

		stopWalk();

	}

	private void stopWalk() {
		((SwiftoApplication) getApplication()).stopCurrentWalk(this, this);
	}

	public void onStopWalkFailure() {
		if (isFinishing()) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							" Trying to show Stop Walk Fail Alert on Walk Map after WalkMap activity had finished",
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			return;
		}

		AlertUtils.showYes(this, "", "Cannot stop walk, please retry", "Retry",
				new IDialogResultWaitable() {
					public void reactOnYes() {
						stopWalk();
					}

					public void reactOnNo() {
						// do nothing
					}
				});
	}

	public void onStopWalkSuccess() {

		// checkPhotoNMsg();

		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
			mHandler.removeCallbacks(mRunnable);
		}

		//ToastUtils.showLong(getApplicationContext(), " onStopWalkSuccess  called ");
		runOnUiThread(new Runnable() {
			public void run() {
				btStopWalk.setVisibility(View.GONE);
			}
		});

		finish();

		((SwiftoApplication) getApplication()).unsubscribeFromStopWalk();

		Intent scheduleIntent = new Intent(getApplicationContext(),
				ScheduleActivity.class);
		scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(scheduleIntent);
	}

	/**
	 * Will reset UploadPhotoStatus!
	 */
	public void onUploadPhotoSuccess() {


		if(resultFile!=null) {
			deleteFile(resultFile);
		}
		boolean isImageActive = false;
		for(int i = 0; i < SwiftoApplication.imageList.size(); i++) {
			isImageActive = false;
			if (SwiftoApplication.imageList.get(i).getStatus().equals("Active"))
			{
				isImageActive = true;
				SyslogUtils.logEvent(getApplicationContext(),
						"DEBUG LOG Active image found and now this is uploading  path is  " +
								SwiftoApplication.imageList.get(i).getFile().getAbsolutePath(), EnumLogSeverity.ERROR,
						EnumLogType.SERVER_TYPE);

				Uri uri = Uri.fromFile(SwiftoApplication.imageList.get(i).getFile());
				mApplication.setImageUri(uri);

				mApplication.uploadToAmazon(SwiftoApplication.imageList.get(i).getFile());
				//break;

				return;
			}
			else
			{

//				SyslogUtils.logEvent(getApplicationContext(),
//						"DEBUG LOG Processed image found and now this is uploading  path is  " +
//								SwiftoApplication.imageList.get(i).getFile().getAbsolutePath(), EnumLogSeverity.ERROR,
//						EnumLogType.SERVER_TYPE);

			}
		}

		if(SwiftoApplication.imageList.size() > 0 && !isImageActive )
		{
			SyslogUtils.logEvent(getApplicationContext(),
					"mApplication.offlineMsgNWalkChange();  Called   ", EnumLogSeverity.ERROR,
					EnumLogType.SERVER_TYPE);
			mApplication.offlineMsgNWalkChange();
		}
		else
		{
			SyslogUtils.logEvent(getApplicationContext(),
					"mApplication.offlineMsgNWalkChange();  Called does not called  " , EnumLogSeverity.ERROR,
					EnumLogType.SERVER_TYPE);

		}




		if(resultFile!=null) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							" on success called and image path = " +resultFile.getAbsolutePath() ,
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			SharedPreferencesHelper.saveWalkImage(mContext, resultFile.getAbsolutePath());

			SwiftoApplication.imageSuccessList.add(new ImageFileHolder("Success", resultFile));
		}
		else
		{
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							" on success called but null resultFile found" ,
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		}

		runOnUiThread(new Runnable() {
			public void run() {
				// mProgressPhotoUploading.setVisibility(View.GONE);


				if (UploadPhotoStatus.getInstance().getStatus()
						.equals(EnumUploadPhotoStatus.UPLOADING_POPUP)) {
					UploadPhotoStatus.getInstance().setStatusNotUploading();
					//ToastUtils.showLong(getApplicationContext(), " continueStopWalk Called ");
					 // vivek     //continueStopWalk(false);
					// checkPhotoNMsg();
				} else {
					UploadPhotoStatus.getInstance().setStatusNotUploading();
					//ToastUtils.showLong(getApplicationContext(), " continueStopWalk else Called ");
				}
			}
		});
	}

	/**
	 * Will reset UploadPhotoStatus!
	 */
	public void onUploadPhotoFailure(String cause) {
		//ToastUtils.showShort(mContext, "photo onUploadPhotoFailure = " + cause);


		if(resultFile == null)
		{
			resultFile = convertImageUriToFile(mApplication.getImageUri(),
					mContext);
		}

		SharedPreferencesHelper.saveWalkImageProcess(getApplicationContext(), "no");

		String path = SharedPreferencesHelper.getWalkImageIsCompressError(mApplication.getApplicationContext());

		if(path.equals(resultFile.getAbsolutePath()))
		{
			SyslogUtils.logEvent(
					getApplicationContext(),
					"reject to upload multi time because this file has compress error , path is - " + resultFile.getAbsolutePath()
							+ " showPopupTakePhoto method called ",
					EnumLogSeverity.INFORMATIONAL,
					EnumLogType.INTERNAL_TYPE);



			//showPopupTakePhoto();


			//return;
		}
		else {
			runOnUiThread(new Runnable() {
				public void run() {
					// mProgressPhotoUploading.setVisibility(View.GONE);
					resultFile = convertImageUriToFile(mApplication.getImageUri(),
							mContext);
					if (resultFile != null) {
						SyslogUtils.logEvent(
								getApplicationContext(),
								"compressImage, file is "
										+ resultFile.getAbsolutePath(),
								EnumLogSeverity.INFORMATIONAL,
								EnumLogType.INTERNAL_TYPE);
						// vivek need to close
						//ToastUtils.showShort(mContext, "photo try to upload");
						mApplication.uploadToAmazon(resultFile);

					/*
					 * runOnUiThread(new Runnable() { public void run() {
					 * mApplication.uploadToAmazon(resultFile); } });
					 */
					}

					if (UploadPhotoStatus.getInstance().getStatus()
							.equals(EnumUploadPhotoStatus.UPLOADING_POPUP)) {
						UploadPhotoStatus.getInstance().setStatusNotUploading();
						// continueStopWalk();
					} else {
						UploadPhotoStatus.getInstance().setStatusNotUploading();
					}
				}
			});
		}


	}

	public void onSendUnsentRequestsSuccess() {
		runOnUiThread(new Runnable() {
			public void run() {
				SyslogUtils.logEvent(getApplicationContext(),
						"Sending unsent data finished",
						EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);
				Toast.makeText(getApplicationContext(),
						"Sending unsent data finished", Toast.LENGTH_SHORT)
						.show();
				//continueStopWalk();
				// checkPhotoNMsg();
			}
		});
	}

	public void onSendUnsentRequestsFailure() {
		runOnUiThread(new Runnable() {
			public void run() {
				SyslogUtils.logEvent(getApplicationContext(),
						"Sending unsent data failure", EnumLogSeverity.WARNING,
						EnumLogType.INTERNAL_TYPE);
//				Toast.makeText(getApplicationContext(),
//						"Sending unsent data finished", Toast.LENGTH_SHORT)
//						.show();
				continueStopWalk(false);
				// checkPhotoNMsg();
			}
		});
	}

	String globleDogId = "";
	String globleDogName = "";
	public void checkPhoto(String walkId , boolean afterpic) {


		StartedWalkExt startedWalkExt = mApplication.getDbAdapter()
				.getStartedWalkForId(walkId);


		Walk currentWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getCurrentWalk();
		for(int i= 0 ; i < currentWalk.Dogs.size();i++)
		{
			//String phototaken =
			String dogimage = SharedPreferencesHelper.getWalkImageByDog(getApplicationContext(),currentWalk._id, currentWalk.Dogs.get(i)._id);

			if(dogimage.equals("no"))
			{
//				ToastUtils.showLong(getApplicationContext(),"Please take a photo of dog id is -  " + currentWalk.Dogs.get(i)._id+
//				"  with name - " + currentWalk.Dogs.get(i).Name);

				globleDogId = currentWalk.Dogs.get(i)._id;
				globleDogName = currentWalk.Dogs.get(i).Name;

				showPopupTakePhoto();
				return;
			}


		}


		if (startedWalkExt.PhotoSkipped == false) {

			if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {

// buildCheckPhotoRequest

				if(afterpic)
				{
					checkPhotoNMsg();
				}
				else {
					String request = SwiftoRequestBuilder.buildCheckPhotoRequest(walkId);

					//Toast.makeText(getApplication(), "request  = "+request, Toast.LENGTH_LONG).show();
					new CheckPhotoTask(mContext, "Checking photo ...", true).execute(request);

				}
			} else {


				SyslogUtils.logEvent(getApplicationContext(), "No internet connection on getting GPS configuration", EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
				checkPhotoNMsg();
				//Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
			}

		}
		else
		{
			checkPhotoNMsg();
		}
	}

	class CheckPhotoTask extends ServerRequestAsynkTask {
		public CheckPhotoTask(Context ctx, String message,
								boolean showProgress) {
			super(ctx, message, showProgress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			parseCheckPhotoConfigResponse(result);
		}
	}


	private void parseCheckPhotoConfigResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);

		if(error != null) {
			Toast.makeText(getApplication(), error, Toast.LENGTH_SHORT).show();
			//Log.e("login error ", error);
			SyslogUtils.logEvent(getApplicationContext(), "Loading info about check photo error: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
		} else {
			CheckPhotoConfig config = new CheckPhotoParser().parseCheckPhoto(response);

			//Toast.makeText(getApplication(), "response = "+response, Toast.LENGTH_LONG).show();

			if(config.successphoto)
			{
				checkMsg();
			}
			else
			{
				showPhoto();
			}

			//checkWalkerActiveRoutes();
		}
	}


	public Boolean checkMockLocation()
	{
		if (Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
			return false;
		else return true;
	}



	private static final int ONE_MINUTES = 1000 * 60 * 1;

	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > ONE_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -ONE_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			//return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 30;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	PowerManager.WakeLock mWakeLock;

	AlertDialog mDialog;
	public void showHalfWalkAlert()
	{

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "SwiftoHalfWayWakeLock");
		mWakeLock.acquire();

		//prepare dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(WalkMapActivity.this);
		builder.setTitle("Swifto");
		builder.setMessage("Don't forget to take a photo!");
		builder.setIcon(R.drawable.dog_default);
		builder.setCancelable(false);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				try {
					mWakeLock.release();
				} catch (Throwable th) {
					// ignoring this exception, probably wakeLock was already released
				}



				mDialog.dismiss();

			}
		});

		mDialog = builder.create();


		//play sound and vibrate
		Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		MediaPlayer mediaPlayer = new MediaPlayer();

		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{500, 1000, 500}, -1);

		try {
			mediaPlayer.setDataSource(WalkMapActivity.this, defaultRingtoneUri);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

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


	public void showAfterWalk()
	{
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
				mApplication.debugAfterWalk(false);
				//finish();
			}

			public void reactOnNo() {

				try {
					mWakeLock.release();
				} catch (Throwable th) {
					// ignoring this exception, probably wakeLock was already released
				}
				

				isokpressed = "yes";
				//finish();
				continueStopWalk(false);
//				Intent walkMapIntent = new Intent(getApplicationContext(),
//						WalkMapActivity.class);
//				startActivity(walkMapIntent);
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
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

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


	public void deleteRotateFiles()
	{
		String arg[]={"/DCIM/Camera","/Pictures"};
		if(arg == null)
		{
			return;
		}
		for(int k = 0; k<arg.length;k++)
		{


		String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		String targetPath = ExternalStorageDirectoryPath + arg[k];

		//Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();

		FileFilter dateFilter = new FileFilter() {
			public boolean accept(File file) {

				long mills = System.currentTimeMillis() - file.lastModified();
				int Hours = (int) mills/(1000 * 60 * 60);
				int Mins =(int) mills / (1000*60);


				if (file.isFile() && Mins < 4) {
					// filters files whose date less than 2 min
					return true;
				} else {
					return false;
				}
			}
		};


			FileFilter zerodateFilter = new FileFilter() {
				public boolean accept(File file) {



					if (file.isFile() && file.length() < 1) {
						// filters files whose date less than 2 min
						return true;
					} else {
						return false;
					}
				}
			};




		File targetDirector = new File(targetPath);


		File[] files = targetDirector.listFiles(dateFilter);

			File[] zerofiles = targetDirector.listFiles(zerodateFilter);

			if(files == null)
			{
				return;
			}

			if(zerofiles == null)
			{
				return;
			}

		SyslogUtils.logEvent(getApplicationContext(), "delete file size  -  " + files.length,
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		SyslogUtils.logEvent(getApplicationContext(), " swiftoImageArray  -  " + swiftoImageArray,
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);


			for  (File zerofile : zerofiles) {

				if (zerofile.length() < 1) {
					if (!zerofile.delete())

						SyslogUtils.logEvent(getApplicationContext(), "Blank gray image delete -  " + zerofile,
								EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

					MediaScannerConnection.scanFile(getApplicationContext(), new String[]{zerofile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {

						@Override
						public void onScanCompleted(String path, Uri uri) {
							// TODO Auto-generated method stub

						}
					});
				}
			}
		for (File file : files) {

			if(file.length()<1) {
				if (!file.delete())

					SyslogUtils.logEvent(getApplicationContext(), "Failed to delete " + file,
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

				MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {

					@Override
					public void onScanCompleted(String path, Uri uri) {
						// TODO Auto-generated method stub

					}
				});
			}

			if(resultFile!=null) {
				if (file.getAbsolutePath().equals(resultFile.getAbsolutePath())) {
					SyslogUtils.logEvent(getApplicationContext(), "this image should not be delete  -  " + file.getAbsolutePath(),
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
					continue;
				}
			}


			SyslogUtils.logEvent(getApplicationContext(), "delete file name which should be deleted   -  " + file.getAbsolutePath(),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);





			for(int j = 0; j< swiftoImageArray.size(); j++) {

				if (file.getAbsolutePath().equals(swiftoImageArray.get(j))) {

					SyslogUtils.logEvent(getApplicationContext(), "delete file name  -  " + file.getAbsolutePath(),
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

					if (!file.delete())

						SyslogUtils.logEvent(getApplicationContext(), "Failed to delete " + file,
								EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

					MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {

						@Override
						public void onScanCompleted(String path, Uri uri) {
							// TODO Auto-generated method stub

						}
					});
					break;
				}
			}
		}

		}




	/*	sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));


		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, file.));
*/




	}

	public void deleteFile(File file)
	{

		//if(file.getName().contains("SWIFTO_")) {
			if (!file.delete())


				SyslogUtils.logEvent(getApplicationContext(), "Failed to delete " + file,
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {

				@Override
				public void onScanCompleted(String path, Uri uri) {
					// TODO Auto-generated method stub

				}
			});
		//}
	}


	private int    deleteLastImageId(){
		final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
		final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
		//Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
		Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);


		if(imageCursor.moveToFirst()){
			int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));


			String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));

			MediaScannerConnection.scanFile(getApplicationContext(), new String[]{fullPath}, null, new MediaScannerConnection.OnScanCompletedListener() {

				@Override
				public void onScanCompleted(String path, Uri uri) {
					// TODO Auto-generated method stub

				}
			});

			File fdelete = new File(fullPath);
			if (fdelete.exists()) {

				SyslogUtils.logEvent(getApplicationContext(), "fdelete.exists()  True " + fdelete.getAbsolutePath(),
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

				if (fdelete.delete()) {
					System.out.println("file Deleted :" + fullPath);

					SyslogUtils.logEvent(getApplicationContext(), "fdelete.exists()  file Deleted  " + fdelete.getAbsolutePath(),
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

					MediaScannerConnection.scanFile(getApplicationContext(), new String[]{fdelete.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {

						@Override
						public void onScanCompleted(String path, Uri uri) {
							// TODO Auto-generated method stub

						}
					});
				} else {
					System.out.println("file not Deleted :" + fullPath);
					SyslogUtils.logEvent(getApplicationContext(), "fdelete.exists()  file Not Deleted  " + fdelete.getAbsolutePath(),
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
				}
			}
			else
			{
				SyslogUtils.logEvent(getApplicationContext(), "fdelete.exists()  False " + fdelete.getAbsolutePath(),
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			}



			imageCursor.close();
			return id;
		}else{
			return 0;
		}
	}


	private int getLastImageId(){
		final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
		final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
		//Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
		Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);


		if(imageCursor.moveToFirst()){
			int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
			String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
			Log.d(TAG, "getLastImageId::id " + id);
			Log.d(TAG, "getLastImageId::path " + fullPath);

			swiftoImageArray.add(fullPath);

			imageCursor.close();
			return id;
		}else{
			return 0;
		}
	}

}

