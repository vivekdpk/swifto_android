package com.haski.swifto.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsAdapter;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.interfaces.IDialogResultWaitable;
import com.haski.swifto.interfaces.ISwipeyTabsPageChangeNotifiable;
import com.haski.swifto.model.EnumWeekLoadStatus;
import com.haski.swifto.util.AlertUtils;
import com.haski.swifto.util.CalendarUtils;
import com.haski.swifto.util.MemoryUtil;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.StringUtils;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

public class ScheduleActivity extends BaseActivity implements ISwipeyTabsPageChangeNotifiable {

	

	// TODO: change in release
	/**
	 * 2 hours
	 */
	public static final long PERIOD_RELOADING = 1 * 1000 * 60 * 30;
	// private static final long PERIOD_RELOADING = 1 * 1000 * 60 * 10;

	private SwipeyTabs mSwipeyTabs;
	private ViewPager mViewPager;
	private TodayScheduleFragment mFragmentToday;
	private TomorrowScheduleFragment mFragmentTomorrow;
	private ThisWeekScheduleFragment mFragmentThisWeek;
	private MonthlyScheduleFragment mFragmentMonthly;

	private String[] mTitles;
	RelativeLayout logOut;
	ActionBar actionbar;
	public Button btNavLeft,btMenuList;

	/**
	 * Got from {@link FragmentPagerAdapter}
	 * makeFragmentName()
	 * 
	 * @param parentId
	 * 
	 * @param position
	 */
	private static String getFragmentTag(int parentId, int position) {
		return "android:switcher:" + parentId + ":" + position;
	}

	@SuppressLint("NewApi")
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem reloadItem = menu.add("Reload").setIcon(R.drawable.arrow__eldiv_final);
		reloadItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		reloadItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				int position = mViewPager.getCurrentItem();

				SyslogUtils.logEvent(getApplicationContext(),
						String.format(Locale.getDefault(), "Reload by pushing button. For position %d", position),
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

				reloadFragmentAt(position);

				return true;
			}
		});

		//SubMenu subMenu1 = menu.addSubMenu("");

		// Send Log

		
		MenuItem slideItem =menu.add("SlidList").setIcon(R.drawable.menu_list2);
		slideItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		slideItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						SyslogUtils.logEvent(getApplicationContext(), "Opening SlidList",
								EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
						//sendEmailLog();
						//getSlidingMenu().setMode(SlidingMenu.RIGHT);
						toggle();
						return true;
					}
				});

		//MenuItem subMenu1Item = subMenu1.getItem();
		//subMenu1Item.setIcon(R.drawable.ic_action_actions);
		//subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}
*/
	private void reloadFragmentAt(int position) {
		if (position == 0) {
			//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			if (mFragmentToday == null) {
				SyslogUtils.logEvent(getApplicationContext(), "Fragment Today is null", EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);
				mFragmentToday = (TodayScheduleFragment) getSupportFragmentManager()
						.findFragmentByTag(getFragmentTag(mViewPager.getId(), position));
			}

			if (mFragmentToday == null) {
				SyslogUtils.logEvent(getApplicationContext(),
						"Fragment Today is null after getting from fragment manager", EnumLogSeverity.ERROR,
						EnumLogType.INTERNAL_TYPE);
			} else {
				mFragmentToday.reload();
			}
		}

		else if (position == 1) {
			//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			if (mFragmentThisWeek == null) {
				SyslogUtils.logEvent(getApplicationContext(), "Fragment Week is null", EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);
				mFragmentThisWeek = (ThisWeekScheduleFragment) getSupportFragmentManager()
						.findFragmentByTag(getFragmentTag(mViewPager.getId(), position));
			}

			if (mFragmentThisWeek == null) {
				SyslogUtils.logEvent(getApplicationContext(),
						"Fragment Week is null after getting from fragment manager", EnumLogSeverity.ERROR,
						EnumLogType.INTERNAL_TYPE);
			} else {
				mFragmentThisWeek.reload();
			}
		} else if (position == 2) {
			//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			if (mFragmentMonthly == null) {
				SyslogUtils.logEvent(getApplicationContext(), "Fragment Monthly is null", EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);
				mFragmentMonthly = (MonthlyScheduleFragment) getSupportFragmentManager()
						.findFragmentByTag(getFragmentTag(mViewPager.getId(), position));
			}

			if (mFragmentMonthly == null) {
				SyslogUtils.logEvent(getApplicationContext(),
						"Fragment Monthly is null after getting from fragment manager", EnumLogSeverity.INFORMATIONAL,
						EnumLogType.INTERNAL_TYPE);
			} else {
				mFragmentMonthly.reload();
			}
		} else {
			//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			// do nothing
		}
	}

	private void startLoginActivity() {
		finish();

		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(loginIntent);
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		final boolean walkInProgress = SharedPreferencesHelper.getWalkIsInProcess(getApplicationContext());

		// TODO: remove?
		AlertUtils.showYesNo(this, "Exiting...", "Really exit?", "Yes", "No", new IDialogResultWaitable() {
			public void reactOnYes() {
				if (walkInProgress) {
					SyslogUtils.logEvent(getApplicationContext(),
							"Walker decided to stop application when some walk was started", EnumLogSeverity.ERROR,
							EnumLogType.INTERNAL_TYPE);
				}

				finish();
			}

			public void reactOnNo() {
				// do nothing
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		SyslogUtils.logEvent(getApplicationContext(), "Schedule resumed", EnumLogSeverity.INFORMATIONAL,
				EnumLogType.INTERNAL_TYPE);
		ScheduleActivity.this.overridePendingTransition(R.anim.decrease_3x_1x_fade_in, R.anim.decrease_1x_05x_fade_out);
	}

	public boolean needReloading() {
		long lastLoadTime = SharedPreferencesHelper.getTimeLastLoading(getApplicationContext());
		long diff = System.currentTimeMillis() - lastLoadTime;

		if (diff > PERIOD_RELOADING) {
			SyslogUtils.logEvent(getApplicationContext(), "Reloading walks beacuse last loading was 2 hours ago",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			return true;
		} else {
			return false;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initTitles();

		setContentView(R.layout.activity_schedule);
		setSlidingActionBarEnabled(false);

		SelectedFragmentIndex = SharedPreferencesHelper.getSelectedFragmentIndexOnSchedule(getApplicationContext());

		String memoryInfo = MemoryUtil.getMemoryInfo(getApplicationContext());

		


		SyslogUtils.logEvent(
				getApplicationContext(), String.format(Locale.getDefault(),
						"Schedule created, selectedFragmentIndex: %d, memory: %s", SelectedFragmentIndex, memoryInfo),
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		// initViews(SelectedFragmentIndex);
		actionbar = getActionBar();
		
		if (actionbar == null) {
			// Toast.makeText(getApplicationContext(), "Actionbar is null",1).show();
			// actionbar.setDisplayHomeAsUpEnabled(false);
			// actionbar.setDisplayShowCustomEnabled(true);
			// actionbar.setCustomView(R.layout.window_title);

		} else {
			setSlidingActionBarEnabled(false);
			//Toast.makeText(getApplicationContext(), "Actionbar ok", 1).show();
			final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
					.inflate(R.layout.window_title1, null);

			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayShowTitleEnabled(false);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setDisplayHomeAsUpEnabled(false);
			actionbar.setIcon(android.R.color.transparent);
			actionbar.setCustomView(actionBarLayout);
		}
		
		
		initViews(0);
		//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//		if(checkMockLocation())
//		{
//			buildAlertMessageMockLocation();
//		}

		//statusCheck();

		//getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		logOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				SyslogUtils.logEvent(getApplicationContext(), "Logout app because logout button pressed",
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
				
				if (SharedPreferencesHelper.getWalkIsInProcess(getApplicationContext())) {
					SyslogUtils.logEvent(getApplicationContext(), "Try to Log out while walk is in progress!",
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
					Toast.makeText(getApplicationContext(), "You have walk in progress!!!", Toast.LENGTH_SHORT).show();
				} else {
					((SwiftoApplication) getApplication()).getPortionsLoader().reloadAllPortions();// context,
																									// dbAdapter);//
					((SwiftoApplication) getApplication()).clearWalksForMonths();
					SharedPreferencesHelper.saveWeekLoadStatus(getApplicationContext(), EnumWeekLoadStatus.NOT_LOADED);
					SharedPreferencesHelper.deleteGpsSleepTime(getApplicationContext());
					SharedPreferencesHelper.deleteMaxAttemptsToFindPoint(getApplicationContext());
					SharedPreferencesHelper.deleteThreshold(getApplicationContext());
					SharedPreferencesHelper.deleteSelectedDay(getApplicationContext());
					SharedPreferencesHelper.deleteSelectedMonth(getApplicationContext());
					SharedPreferencesHelper.deleteSelectedYear(getApplicationContext());
					((SwiftoApplication) getApplication()).deleteWalker();

					SyslogUtils.logEvent(getApplicationContext(), "User logged out, gps config reset",
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

					startLoginActivity();
				}

				// Toast.makeText(getApplicationContext(), "Log Out Test",
				// Toast.LENGTH_LONG).show();

			}
		});
	}

	public int SelectedFragmentIndex = 0;

	private void initTitles() {
		mTitles = new String[3];

		Calendar c = CalendarUtils.NOW();

		mTitles[0] = "TODAY " + StringUtils.getFormattedMonthDay(c);

		c.add(Calendar.DAY_OF_YEAR, 1);

		// mTitles[1] = "TOMORROW " + StringUtils.getFormattedMonthDay(c);
		mTitles[1] = "THIS WEEK";
		mTitles[2] = "MONTHLY";
	}

	private void initViews(int currSelectedFragment) {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mSwipeyTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

		SwipeyTabsPagerAdapter adapter = new SwipeyTabsPagerAdapter(this, getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mSwipeyTabs.setAdapter(adapter);
		mSwipeyTabs.setSwipeyTabsPageChangedNotifiable(this);
		mViewPager.setOnPageChangeListener(mSwipeyTabs);
		mViewPager.setCurrentItem(currSelectedFragment);

		logOut = (RelativeLayout) findViewById(R.id.logout);
		
		btNavLeft = (Button) findViewById(R.id.window_title_bt_right1);
		
		btNavLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = mViewPager.getCurrentItem();

				SyslogUtils.logEvent(getApplicationContext(),
						String.format(Locale.getDefault(), "Reload by pushing button. For position %d", position),
						EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

				reloadFragmentAt(position);
			}
		});

		btMenuList = (Button) findViewById(R.id.window_title_bt_slidemenu1);
		// btMenuList.setOnClickListener(btMenuListClickListener);

		btMenuList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				toggle();
			}
		});
		
	}

	private class SwipeyTabsPagerAdapter extends FragmentPagerAdapter implements SwipeyTabsAdapter {
		public SwipeyTabsPagerAdapter(Context ctx, FragmentManager fm) {
			super(fm);

			mContext = ctx;
		}

		private Context mContext;

		public TextView getTab(final int position, SwipeyTabs root) {
			TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.swipey_tab_indicator, root,
					false);
			view.setText(mTitles[position]);
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mViewPager.setCurrentItem(position);
				}
			});

			return view;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				mFragmentToday = (TodayScheduleFragment) TodayScheduleFragment.newInstance();

				return mFragmentToday;
			}
			// else if(position == 1) {
			// mFragmentTomorrow = (TomorrowScheduleFragment)
			// TomorrowScheduleFragment.newInstance();
			//
			// return mFragmentTomorrow;
			// }
			else if (position == 1) {
				//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
				mFragmentThisWeek = (ThisWeekScheduleFragment) ThisWeekScheduleFragment.newInstance();

				return mFragmentThisWeek;
			} else if (position == 2) {
				//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
				mFragmentMonthly = (MonthlyScheduleFragment) MonthlyScheduleFragment.newInstance();
                
				return mFragmentMonthly;
			} else {
				//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
				return null;
			}
		}

		@Override
		public int getCount() {
			return mTitles.length;
		}
	}

	public void onPageChanged(int position) {
		SelectedFragmentIndex = position;
		SharedPreferencesHelper.saveSelectedFragmentIndexOnSchedule(getApplicationContext(), SelectedFragmentIndex);

		if (position == 0) {
			//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			if (mFragmentToday != null) {
				mFragmentToday.pageChangedToMe();
			}
		}
		// else if(position == 1) {
		// if(mFragmentTomorrow != null) {
		// mFragmentTomorrow.pageChangedToMe();
		// }
		// }
		else if (position == 1) {
			//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			if (mFragmentThisWeek != null) {
				mFragmentThisWeek.pageChangedToMe();
			}
		} else if (position == 2) {
			//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			if (mFragmentMonthly != null) {
				mFragmentMonthly.pageChangedToMe();
			}
		} else {
			// do nothing
		}
	}

	public void sendEmailLog() {
		File toSend = SyslogUtils.readLastEntries(this);
		SyslogUtils.clearLogFile(this);

		Intent intentEmail = new Intent(Intent.ACTION_SEND);
		intentEmail.setType("text/message");

		// TODO: change in release!
		// intentEmail.putExtra(Intent.EXTRA_EMAIL, new
		// String[]{"n1ck_kharkov@mail.ru"});
		intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[] { "apploggs@swifto.com" });

		String version = getResources().getString(R.string.app_ver);

		intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Swifto Log for app ver " + version);

		Uri uri = Uri.fromFile(toSend);

		intentEmail.putExtra(Intent.EXTRA_STREAM, uri);

		String body = "Phone Model: " + Build.MODEL + ", Android version: " + Build.VERSION.RELEASE;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		body = body + "\n\n\n   Please provide as much detail as possible about the problem you "
				+ "experienced to help us identify the issue.";
		// body = body + "\n\n\n Time:- "+Calendar.HOUR + ":" + Calendar.MINUTE;
		body = body + "\n\n\n DateTime :- " + sdf.format(c.getTime());
		intentEmail.putExtra(Intent.EXTRA_TEXT, body);

		// intentEmail.putExtra(Intent.EXTRA_TEXT, body);
		startActivity(intentEmail);

	}

	public void statusCheck() {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();

		}

	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
						// finish();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();

	}

	private void buildAlertMessageMockLocation() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your Mock Loaction seems to be enabled, That can be using fake location , " +
				"please disable to use our app").setCancelable(false)
				.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
				finish();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();

	}

	public Boolean checkMockLocation()
	{
		if (Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
			return false;
		else return true;
	}


}
