package com.haski.swifto.ui;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.interfaces.IDialogResultWaitable;
import com.haski.swifto.model.EnumWalkStatuses;
import com.haski.swifto.model.vo.json.GetWalkStateParser;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.server.ServerRequestAsynkTask;
import com.haski.swifto.server.SwiftoRequestBuilder;
import com.haski.swifto.service.GPSTracker;
import com.haski.swifto.util.AlertUtils;
import com.haski.swifto.util.CalendarUtils;
import com.haski.swifto.util.MemoryUtil;
import com.haski.swifto.util.ServiceUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.TaskErrorChecker;
import com.haski.swifto.util.ToastUtils;
import com.haski.swifto.util.WalkUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class DogInfoActivity extends BaseActivity {

	ActionBar actionbar;

	String titleMsg = "Starting walk";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_dog_info);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE

		// R.layout.window_title);

		Walk currentWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getCurrentWalk();


		//ToastUtils.showLong(getApplicationContext(), "currentWalk.Dogs.size() = "+ currentWalk.Dogs.size());
		actionbar = getActionBar();

		if (actionbar == null) {
			// Toast.makeText(getApplicationContext(), "Actionbar is null",
			// 1).show();
			// actionbar.setDisplayHomeAsUpEnabled(false);
			// actionbar.setDisplayShowCustomEnabled(true);
			// actionbar.setCustomView(R.layout.window_title);

		} else {
			setSlidingActionBarEnabled(false);
			// Toast.makeText(getApplicationContext(), "Actionbar ok",
			// 1).show();
			final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
					.inflate(R.layout.window_title, null);

			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayShowTitleEnabled(false);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setDisplayHomeAsUpEnabled(false);
			actionbar.setIcon(android.R.color.transparent);
			actionbar.setCustomView(actionBarLayout);
		}
		mContext = this;
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mContext = this;

		SyslogUtils.logEvent(getApplicationContext(), "DogInfo resumed",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		DogInfoActivity.this.overridePendingTransition(
				R.anim.increase_05x_1x_fade_in, R.anim.increase_1x_3x_fade_out);

		refreshViews();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private Context mContext;
	private DogInfoListAdapter mAdapter;

	private ListView mListDogs;
	private Button mBtStart;
	private Button mBtWalkProgress;
	protected Button btNavLeft;
	protected Button btNavRight;
	protected Button btMenuList;
	protected TextView tvTitle;

	private void initViews() {
		mListDogs = (ListView) findViewById(R.id.activity_dog_info_list_dogs);

		mBtStart = (Button) findViewById(R.id.activity_dog_info_bt_start);
		mBtStart.setOnClickListener(btStartWalk_clickListener);

		mBtWalkProgress = (Button) findViewById(R.id.activity_dog_info_bt_go_to_map);
		mBtWalkProgress.setOnClickListener(btWalkProgress_clickListener);

		btNavLeft = (Button) findViewById(R.id.window_title_bt_left);
		btNavLeft.setText(getResources().getText(
				R.string.window_title_bt_dog_info));
		btNavLeft.setOnClickListener(btNavigateLeftClickListener);

		btMenuList = (Button) findViewById(R.id.window_title_bt_slidemenu);
		// btMenuList.setOnClickListener(btMenuListClickListener);

		btMenuList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				toggle();
			}
		});

		btNavRight = (Button) findViewById(R.id.window_title_bt_right);
		btNavRight.setText(getResources().getString(
				R.string.window_title_bt_schedule));
		btNavRight.setOnClickListener(btNavigateRightClickListener);

		tvTitle = (TextView) findViewById(R.id.window_title_txt_title);
		tvTitle.setVisibility(View.GONE);
	}

	private void refreshViews() {
		tvTitle.setText(getResources()
				.getString(R.string.window_title_dog_info));

		btNavLeft.setText(getResources().getText(
				R.string.window_title_bt_schedule));
		btNavLeft.setOnClickListener(btNavigateLeftClickListener);

		btNavRight.setText(getResources().getString(
				R.string.window_title_bt_map));
		btNavRight.setOnClickListener(btNavigateRightClickListener);

		Walk currentWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getCurrentWalk();


		if (currentWalk == null) {
			AlertUtils
					.showYesNo(
							this,
							"Error",
							"Please, try again. If error is repeating, please, go to schedule and Reload",
							"Try again", "Go to schedule",
							new IDialogResultWaitable() {
								public void reactOnYes() {
									refreshViews();
								}

								public void reactOnNo() {
									finish();

									startScheduleActivity();
								}
							});

			return;
		}

		Log.i("currentWalk 1", "" + currentWalk);
		Log.i("currentWalk 2", "" + currentWalk.WalkType);


		if (currentWalk.WalkType.equals("overnight"))
			titleMsg = "Check-in to Overnight stay";

		else
			titleMsg = "Starting walk";


		mAdapter = new DogInfoListAdapter(this, currentWalk);


		SyslogUtils
				.logEvent(
						getApplicationContext(),
						"original = " + currentWalk.Original + "   formatted = " + currentWalk.Formatted,
						EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);


		mListDogs.setAdapter(mAdapter);

		String startedWalkId = SharedPreferencesHelper
				.getStartedWalkId(getApplicationContext());

		//nkp
		if (currentWalk.WalkType.equals("overnight"))
			mBtStart.setText(getResources().getString(R.string.activity_dog_info_bt_start_overnight_check_in));
		else
			mBtStart.setText(getResources().getString(R.string.activity_dog_info_bt_start_walk));


		// some walk is in progress
		if (!startedWalkId
				.equals(SharedPreferencesHelper.DEFAULT_STARTED_WALK_ID)) {
			// this activity was opened for started walk
			if (startedWalkId.equals(currentWalk._id)) {
				// vivek
				mBtStart.setVisibility(View.GONE);
				btNavRight.setVisibility(View.VISIBLE);
				mBtWalkProgress.setVisibility(View.VISIBLE);
				//nkp
				if (currentWalk.WalkType.equals("overnight"))
					mBtWalkProgress.setText(getResources().getString(R.string.activity_dog_info_bt_overnight_in_progress));
				else
					mBtWalkProgress.setText(getResources().getString(R.string.activity_dog_info_bt_walk_in_progress));

			} else {
				if (currentWalk.Status.equals(WalkUtils.STATUS_COMPLETED)) {
					// vivek
					mBtStart.setVisibility(View.GONE);
				} else {
					mBtStart.setVisibility(View.VISIBLE);
					//nkp
					if (currentWalk.WalkType.equals("overnight"))
						mBtStart.setText(getResources().getString(R.string.activity_dog_info_bt_start_overnight_check_in));
					else
						mBtStart.setText(getResources().getString(R.string.activity_dog_info_bt_start_walk));
				}

				btNavRight.setVisibility(View.GONE);
				mBtWalkProgress.setVisibility(View.GONE);
			}
		} else {
			mBtWalkProgress.setVisibility(View.GONE);
			btNavRight.setVisibility(View.GONE);

			if (currentWalk.Status.equals(WalkUtils.STATUS_COMPLETED)) {
				// vivek
				mBtStart.setVisibility(View.GONE);
			} else {
				mBtStart.setVisibility(View.VISIBLE);

				//nkp
				if (currentWalk.WalkType.equals("overnight"))
					mBtStart.setText(getResources().getString(R.string.activity_dog_info_bt_start_overnight_check_in));

				else
					mBtStart.setText(getResources().getString(R.string.activity_dog_info_bt_start_walk));
			}
		}
	}

	OnClickListener btStartWalk_clickListener = new OnClickListener() {
		public void onClick(View v) {

			// statusCheck();

			// 1) check, is other walk in progress
			if (SharedPreferencesHelper
					.getWalkIsInProcess(getApplicationContext())) {
				AlertUtils.showYes(mContext, "Some walk started",
						"You have walk that is in progress.", "View",
						new IDialogResultWaitable() {
							public void reactOnYes() {
								Walk walk = ((SwiftoApplication) getApplication())
										.getWalkGetter()
										.getStartedWalkWithoutOwnerAndDogs();

								if (walk != null) {
									String walkId = walk._id;
									SharedPreferencesHelper.saveCurrentWalkId(mContext, walkId);

									refreshViews();
								}
							}

							public void reactOnNo() {
								// do nothing
							}
						});

				return;
			}

			checkWalk();
		}
	};

	public boolean statusCheck() {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
			return false;

		}
		return true;

	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int id) {
						dialog.cancel();
						// finish();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();

	}

	private void checkWalk() {
		Walk walk = ((SwiftoApplication) getApplication()).getWalkGetter()
				.getCurrentWalkWithoutOwnerAndDogs();

		Walk currentWalk = ((SwiftoApplication) getApplication())
				.getWalkGetter().getCurrentWalk();
//		GPSTracker gps = new GPSTracker(getApplicationContext(),
//				(SwiftoApplication) getApplication());
//
//		// Intent intent = new Intent(getApplicationContext(),
//		// GPSTracker.class);
//		// intent.putExtra(Service.KEY_TEST, "123456789");
//		// startService(intent);
//
//		// check if GPS enabled
//		Location lc = new Location("Ower Address");
//		lc.setLatitude(walk.Owner.StaticLocation.Location
//				.getLatitudeE6());
//		lc.setLongitude(walk.Owner.StaticLocation.Location
//				.getLongitudeE6());
//
//		if (gps.canGetLocation()) {
//
//			float distance = lc.distanceTo(gps.getLocation());
//			if (distance > 0) {
//				showWrongLactionWalkAlert(distance);
//			}
//		}
//
//		gps.stopUsingGPS();

		// 2) check 2 hours
		// vivek debug ss

		//ToastUtils.showLong(getApplicationContext()," remainig time -  "+ currentWalk.getEarlyTime());

		if (!checkWalkCanBeStartedByTimeWithShowingToast(walk)) {
			return;
		}

		if( currentWalk.getEarlyTime() > 0 )
		{
			showEarlyWalkAlert();
			return;
		}

		//showStartingWalkAlert();
		lowMemoryAlert();
	}

	private void showWrongLactionWalkAlert(float distance) {
		if (isFinishing()) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"Showing start walk alert after Dog Info activity had finished",
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			return;
		}

		String message = String.format(Locale.getDefault(),
				"You are about to start a walk with %f."+ distance);

		AlertUtils.showYesNo(this,titleMsg, message, "Cancel", "Start",
				new IDialogResultWaitable() {
					public void reactOnYes() {

					}

					public void reactOnNo() {
						
					}
				});
	}

	// TODO: change in release
	private static final long WALK_START_TIME_RANGE = 1 * 60 * 300;

	// private static final long WALK_START_TIME_RANGE = Long.MAX_VALUE;

	private boolean checkWalkCanBeStartedByTimeWithShowingToast(Walk walk) {
		double timeNow = CalendarUtils.NOW_IN_SECONDS();
		Calendar currentCal = CalendarUtils.NOW();

		//nkp start
		if(walk.WalkType.equals("overnight")){

			Calendar today_4pm = CalendarUtils.TODAY_4PM_FROM_SECOND(walk.StartTime);
			double timeToday_4pm = (today_4pm.getTimeInMillis()) / 1000;

			Calendar midnight_time = CalendarUtils.TODAY_23_59_59(walk.StartTime);
			double timeMidNight = (midnight_time.getTimeInMillis()) / 1000;

			double currentTimeInSeconds = (currentCal.getTimeInMillis()) / 1000;

			//Log.d("date 4 Pm", timeToday_4pm + " 4 date "+ today_4pm.getTime() +" now "+currentTimeInSeconds + "mid night time "+ midnight_time.getTime());

			SyslogUtils
					.logEvent(
							getApplicationContext(),
							String.format(
									"Checked overnight stay between 4PM to 00:00 MidNight of next day and current date and time is %s seconds",
									currentCal.getTimeInMillis()), EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);



			if (currentTimeInSeconds >= timeToday_4pm && currentTimeInSeconds <= timeMidNight) {
				return true;
			} else {
				if (isFinishing()) {
					SyslogUtils
							.logEvent(
									getApplicationContext(),
									" Checking walk can be started performed after Dog Info activity had finished",
									EnumLogSeverity.ERROR,
									EnumLogType.INTERNAL_TYPE);
					return false;
				}

				AlertUtils
						.showYes(
								this,
								"",
								"You are not allowed to check-in to an overnight stay earlier than 4:00PM that day up to midnight.",
								"OK", new IDialogResultWaitable() {
									public void reactOnYes() {
										// do nothing
									}

									public void reactOnNo() {
										// do nothing
									}
								});

				return false;
			}
			//nkp
		}else {
			double diff = /* Math.abs( */timeNow - walk.StartTime/* ) */;

			SyslogUtils
					.logEvent(
							getApplicationContext(),
							String.format(
									"Checked walk time and current time, diff is %f seconds",
									diff), EnumLogSeverity.INFORMATIONAL,
							EnumLogType.INTERNAL_TYPE);

			if (diff > WALK_START_TIME_RANGE || diff < -WALK_START_TIME_RANGE) {
				if (isFinishing()) {
					SyslogUtils
							.logEvent(
									getApplicationContext(),
									" Checking walk can be started performed after Dog Info activity had finished",
									EnumLogSeverity.ERROR,
									EnumLogType.INTERNAL_TYPE);
					return false;
				}

				AlertUtils
						.showYes(
								this,
								"",
								"You are not allowed to start a dog walk if it's more than five hours before or after the walk",
								"OK", new IDialogResultWaitable() {
									public void reactOnYes() {
										// do nothing
									}

									public void reactOnNo() {
										// do nothing
									}
								});

				return false;
			} else {
				return true;
			}
		}

		//nkp end
	}


	private void showEarlyWalkAlert() {
		if (isFinishing()) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"Showing start walk alert after Dog Info activity had finished",
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			return;
		}

		Walk walk = ((SwiftoApplication) getApplication()).getWalkGetter()
				.getCurrentWalk();


		String message = String
				.format(Locale.getDefault(),
						"It's too early to start the walk for "+walk.getDogNames()+". " +
								"The time window for this walk is "+walk.getStartTimeMinFormatted()+" to " +
								walk.getStartTimeMaxFormatted()+". Please wait "+walk.getEarlyTime()+" minutes " +
								"and start the walk then. Starting it now will create a Walk Incident Report.");

		// You are about to start a walk with [dog's name].
		// Your dog owner will get a text message saying the walk has begun.
		// Please be sure you are physically with the dog and ready to start the
		// walk.
		// This cannot be restarted later.
		//
		// Don't forget to lock the door behind you!

		AlertUtils.showYesNo(this,titleMsg, message,  "Start Anyway","Cancel",
				new IDialogResultWaitable() {
					public void reactOnYes() {

						//						if(statusCheck())
//						{
//							getWalkState();
//						}
							//showStartingWalkAlert();
						lowMemoryAlert();
					}

					public void reactOnNo() {

//


					}
				});
	}



	private void showStartingWalkAlert() {
		if (isFinishing()) {
			SyslogUtils
					.logEvent(
							getApplicationContext(),
							"Showing start walk alert after Dog Info activity had finished",
							EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			return;
		}

		Walk walk = ((SwiftoApplication) getApplication()).getWalkGetter()
				.getCurrentWalk();
		// String message = String
		// .format(Locale.getDefault(),
		// "You are about to start a %d minute walk with %s." +
		// "\nThe dog owner will get a text message saying the walk has begun."
		// +
		// "\nPlease be sure you are outside of the house at this point and ready to "
		// +
		// "start the walk.\nThis cannot be restarted later",
		// walk.Duration / 60, walk.getDogNames());

		if( walk.WalkType.equals("overnight")){
			String message = String
					.format(Locale.getDefault(),
							"You are about to check-in to your overnight stay with %s."
									+ "\nThe dog owner will get a text message saying you are now comfortably settled in for the overnight stay."
									+ "\nPlease be sure you are in the apartment at this point, setting up the video and following the other overnight instructions."
									+"\nPlease leave the app running and close the walk to check out prior to leaving."
									+"\nThis cannot be restarted later.",
							walk.getDogNames());

					//			You are about to check-in to your overnight stay with <dogs names>.
					//			The dog owner will get a text message saying you are now comfortably
					//			settled in for the overnight stay.
					//			Please be sure you are in the apartment at this point, setting up the video
					//			and following the other overnight instructions.
					//			Please leave the app running and close the walk to check out prior to
					//			leaving.
					//			This cannot be restarted later.


			AlertUtils.showYesNo(this, "Check-in to Overnight stay", message, "Check-in", "Cancel",
					new IDialogResultWaitable() {
						public void reactOnYes() {

							//						if(statusCheck())
							//						{
							//							getWalkState();
							//						}
							getWalkState();
						}

						public void reactOnNo() {

						}
					});
		}else {
			String message = String
					.format(Locale.getDefault(),
							"You are about to start a walk with %s."
									+ "\nYour dog owner will get a text message saying the walk has begun."
									+ "\nPlease be sure you are physically with the dog and ready to "
									+ "start the walk.\nThis cannot be restarted later.\n\nDon't forget to lock the door behind you!",
							walk.getDogNames());

			// You are about to start a walk with [dog's name].
			// Your dog owner will get a text message saying the walk has begun.
			// Please be sure you are physically with the dog and ready to start the
			// walk.
			// This cannot be restarted later.
			//
			// Don't forget to lock the door behind you!

			AlertUtils.showYesNo(this, titleMsg, message, "Start", "Cancel",
					new IDialogResultWaitable() {
						public void reactOnYes() {

							//						if(statusCheck())
//						{
//							getWalkState();
//						}
							getWalkState();
						}

						public void reactOnNo() {

						}
					});
		}
	}

	private void getWalkState() {
		if (ServiceUtils.isNetworkAvailable(this)) {
			Walk currentWalk = ((SwiftoApplication) getApplication())
					.getWalkGetter().getCurrentWalkWithoutOwnerAndDogs();

			if (currentWalk == null) {
				ToastUtils.showShort(getApplicationContext(),
						"Error, please try again");

				SyslogUtils
						.logEvent(
								getApplicationContext(),
								"Dog Info Activity: Current walk is null on getting walk state",
								EnumLogSeverity.WARNING,
								EnumLogType.HARDWARE_TYPE);
				return;
			}

			String request = SwiftoRequestBuilder
					.buildGetWalkState(currentWalk._id);

			new GetWalkStateWATask(mContext, "Checking walk state...", true)
					.execute(request);

		} else {
			ToastUtils.showShort(getApplicationContext(),
					"No internet connection");
			SyslogUtils.logEvent(getApplicationContext(),
					"No internet connection on " + " getting walk state",
					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
/*
			//
			//  new work start offline_mode
			//
			Walk currentWalk = ((SwiftoApplication) getApplication())
					.getWalkGetter().getCurrentWalkWithoutOwnerAndDogs();

			if (currentWalk == null) {
				ToastUtils.showShort(getApplicationContext(),
						"Error, please try again");

				SyslogUtils
						.logEvent(
								getApplicationContext(),
								"Dog Info Activity: Current walk is null on getting walk state",
								EnumLogSeverity.WARNING,
								EnumLogType.HARDWARE_TYPE);
				return;
			}

			String request = SwiftoRequestBuilder
					.buildGetWalkState(currentWalk._id);

			// need to insert in table
			//new GetWalkStateWATask(mContext, "Checking walk state...", true)
			//		.execute(request);


			finish();

			((SwiftoApplication) getApplication()).startCurrentWalk(true);

			// open the map
			Intent walkMapIntent = new Intent(getApplicationContext(),
					WalkMapActivity.class);
			walkMapIntent.putExtra("afterwalk", "no");

			//AfterWalkDialogActivity.isokpressed = "no";
			WalkMapActivity.isokpressed = "no";
			startActivity(walkMapIntent);

			//
			//  new work stop offline_mode
			//

			*/

		}
	}

	class GetWalkStateWATask extends ServerRequestAsynkTask {
		public GetWalkStateWATask(Context ctx, String message,
				boolean showProgress) {
			super(ctx, message, showProgress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//Toast.makeText(mContext, "call dog info", Toast.LENGTH_SHORT).show();
			Log.d("nkp result", result);
			parseGetWalkStateResponse(result);
		}
	}

	private void parseGetWalkStateResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);

		if (error != null) {
			Toast.makeText(getApplicationContext().getApplicationContext(),
					"Error getting walk state: " + error, Toast.LENGTH_SHORT)
					.show();
			SyslogUtils.logEvent(getApplicationContext(),
					"Error getting walk state: " + error,
					EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
		} else {
			String walkState = new GetWalkStateParser()
					.parseGetWalkState(response);
			Log.d("response nkp", walkState);

			// walk wasn't started
			if (walkState.equals(EnumWalkStatuses.SCHEDULED)) {
				SyslogUtils.logEvent(getApplicationContext(), "Walk state: "
						+ walkState + " (on Dog Info)",
						EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);

				startCurrentWalk();
			} else if (walkState.equals(EnumWalkStatuses.STARTED)) {
				SyslogUtils.logEvent(getApplicationContext(), "Walk state: "
						+ walkState + " (on Dog Info)",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
				finish();

				((SwiftoApplication) getApplication()).startCurrentWalk(true);

				// open the map
				Intent walkMapIntent = new Intent(getApplicationContext(),
						WalkMapActivity.class);
				walkMapIntent.putExtra("afterwalk", "no");
				walkMapIntent.putExtra("newwalk", "no");

				//AfterWalkDialogActivity.isokpressed = "no";
				WalkMapActivity.isokpressed = "no";
				startActivity(walkMapIntent);
			}
		}
	}

	private void startCurrentWalk() {
		if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			Walk currentWalk = ((SwiftoApplication) getApplication())
					.getWalkGetter().getCurrentWalkWithoutOwnerAndDogs();
			String walkerId = currentWalk.walkerID;
			String startWalkRequest = SwiftoRequestBuilder.buildStartWalk(
					walkerId, currentWalk._id);

			new StartWalkRequestTask(mContext, "Sending start walk...", true)
					.execute(startWalkRequest);
		} else {
			ToastUtils.showShort(getApplicationContext(),
					"No internet connection");
			SyslogUtils.logEvent(getApplicationContext(),
					"No internet connection on starting walk",
					EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
		}
	}

	class StartWalkRequestTask extends ServerRequestAsynkTask {
		public StartWalkRequestTask(Context ctx, String message,
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

			parseStartWalkResponse(result);
		}
	}

	private void parseStartWalkResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);

		if (error != null) {
			ToastUtils.showShort(getApplicationContext(), error);
			SyslogUtils.logEvent(getApplicationContext(),
					"Error starting walk: " + error, EnumLogSeverity.ERROR,
					EnumLogType.SERVER_TYPE);
		} else {
			SyslogUtils.logEvent(getApplicationContext(),
					"Walk started (from Dog info after sending start_walk)",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
			finish();

			((SwiftoApplication) getApplication()).startCurrentWalk(true);

			SharedPreferencesHelper.saveWalkOnTimeExact(mContext, System.currentTimeMillis());

			Intent walkMapIntent = new Intent(getApplicationContext(),
					WalkMapActivity.class);
			walkMapIntent.putExtra("afterwalk", "no");
			walkMapIntent.putExtra("newwalk", "yes");
			startActivity(walkMapIntent);
		}
	}

	OnClickListener btWalkProgress_clickListener = new OnClickListener() {
		public void onClick(View v) {
			finish();

			Intent walkMapIntent = new Intent(getApplicationContext(),
					WalkMapActivity.class);
			walkMapIntent.putExtra("afterwalk", "no");
			walkMapIntent.putExtra("newwalk", "no");
			startActivity(walkMapIntent);
		}
	};

	OnClickListener btNavigateLeftClickListener = new OnClickListener() {
		public void onClick(View v) {
			finish();

			startScheduleActivity();
		}
	};

	private void startScheduleActivity() {
		Intent scheduleIntent = new Intent(getApplicationContext(),
				ScheduleActivity.class);
		scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(scheduleIntent);
	}

	OnClickListener btNavigateRightClickListener = new OnClickListener() {
		public void onClick(View v) {
			finish();

			Intent walkMapIntent = new Intent(getApplicationContext(),
					WalkMapActivity.class);
			walkMapIntent.putExtra("afterwalk", "no");
			walkMapIntent.putExtra("newwalk", "no");
			startActivity(walkMapIntent);
		}
	};



	public void lowMemoryAlert()
	{
		String memoryInfo = MemoryUtil.getMemoryInfo(getApplicationContext());

		// available 348 Mb, total: 962 Mb, threshold: 144 Mb

		String[] memorylist = memoryInfo.split(",");
		int firstNumber = Integer.parseInt(memorylist[0].replaceFirst(".*?(\\d+).*", "$1"));

		// remove some apps OR free some memory
//		- before starting a walk
//			-This may cause some problems

		String msg = "Your device memory is below 100MB [ "+firstNumber +"MB ] please remove some memory before started walk , this may cause some problem.";


		if(firstNumber < 100)
		{

			SyslogUtils.logEvent(getApplicationContext(),
					"in if low memory called and memory is "+ firstNumber,
					EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
			AlertUtils.showYesNo(this,titleMsg, msg,  "Start Anyway","Cancel",
					new IDialogResultWaitable() {
						public void reactOnYes() {

							//						if(statusCheck())
//						{
//							getWalkState();
//						}
							showStartingWalkAlert();
						}

						public void reactOnNo() {

//


						}
					});
		}
		else
		{
			SyslogUtils.logEvent(getApplicationContext(),
					"in else low memory called and memory is "+ firstNumber,
					EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
			showStartingWalkAlert();
		}
	}



}
