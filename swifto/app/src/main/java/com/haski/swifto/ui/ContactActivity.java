package com.haski.swifto.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.interfaces.ILoadWalksContainingStartedWaitable;
import com.haski.swifto.interfaces.WalksLoadedCallback;
import com.haski.swifto.model.EnumPortionState;
import com.haski.swifto.model.EnumWeekLoadStatus;
import com.haski.swifto.model.vo.ContactDog;
import com.haski.swifto.model.vo.ContactDogList;
import com.haski.swifto.model.vo.Portion;
import com.haski.swifto.model.vo.WalksOneDay;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.util.CalendarUtils;
import com.haski.swifto.util.DateTimeUtils;
import com.haski.swifto.util.ServiceUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.ToastUtils;
import com.haski.swifto.util.WalkUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

public class ContactActivity extends BaseActivity implements
		ILoadWalksContainingStartedWaitable, WalksLoadedCallback {

	private ActionBar actionbar;
	TextView tv;
	protected Button btNavLeft, btMenuList;
	protected Button btNavRight;
	protected TextView tvTitle;

	//public ImageView btHrSms, btSchedualSms, btHrMail, btShedualMail;
	private ShareImageAdapter imageAdapter;

	private ArrayList<WalksOneDay> mWalksGroups;
	private ArrayList<ContactDog> mContactDogList;
	private ArrayList<ContactDogList> mContactDogExpndList;

	private ContactDogList mContactDogExpnd;
	private ContactDog mContactDog;

	private ExpandableListView mExpandableList;
	private ContactWalksAdapter mAdapter;

	private LinearLayout mProgressLayout;

	private RelativeLayout mLayout;
	// private RelativeLayout mLayoutNoWalks;

	private ILoadWalksContainingStartedWaitable thisLink;

	private String[] mPortionIds;
	private int NOOFDAYSHOW = 14, STARTDAY = 0;
	private final AtomicInteger mDaysLoaded = new AtomicInteger(STARTDAY);

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.contact);

		mLayout = (RelativeLayout) findViewById(R.id.contact_fragment_schedule_week_layout);
		// mLayoutNoWalks = (RelativeLayout)
		// findViewById(R.id.fragment_schedule_week_layout_no_walks);

		mExpandableList = (ExpandableListView) findViewById(R.id.contact_fragment_schedule_week_expandable_list);
		mWalksGroups = new ArrayList<WalksOneDay>();
		mContactDogExpndList = new ArrayList<ContactDogList>();
		mContactDogList = new ArrayList<ContactDog>();
		mAdapter = new ContactWalksAdapter(ContactActivity.this,
				mContactDogExpndList);
		mExpandableList.setAdapter(mAdapter);


		mProgressLayout = (LinearLayout) findViewById(R.id.contact_fragment_schedule_week_progress);

		Log.i("contact", "start contact activity");

		setSlidingActionBarEnabled(false);

		actionbar = getActionBar();
		if (actionbar == null) {

		} else {
			actionbar.setDisplayHomeAsUpEnabled(false);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setDisplayShowTitleEnabled(false);
			actionbar.setIcon(android.R.color.transparent);
			
			final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
					.inflate(R.layout.window_title, null);
			actionbar.setCustomView(actionBarLayout);
			//actionbar.setCustomView(R.layout.window_title);

			btNavLeft = (Button) findViewById(R.id.window_title_bt_left);
			btNavLeft.setText(getResources().getText(
					R.string.window_title_bt_schedule));

			btMenuList = (Button) findViewById(R.id.window_title_bt_slidemenu);
			// btMenuList.setOnClickListener(btMenuListClickListener);

			btMenuList.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					toggle();
				}
			});
			btNavLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent scheduleIntent = new Intent(getApplicationContext(),
							ScheduleActivity.class);
					scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					startActivity(scheduleIntent);
				}
			});
			btNavRight = (Button) findViewById(R.id.window_title_bt_right);
			btNavRight.setVisibility(View.GONE);
			// btNavRight.setText(getResources().getString(R.string.window_title_bt_schedule));
			// btNavRight.setOnClickListener(btNavigateRightClickListener);

			tvTitle = (TextView) findViewById(R.id.window_title_txt_title);
			tvTitle.setVisibility(View.GONE);

		}
	}

	private void initPortionIds() {
		mPortionIds = new String[NOOFDAYSHOW - STARTDAY];
		Log.i("Total day length = ", "" + (NOOFDAYSHOW - STARTDAY));

		int diffFromMonday;

		for (int i = STARTDAY; i < NOOFDAYSHOW; i++) {
			diffFromMonday = CalendarUtils.DIFFERENCE_FROM_MONDAY() + i;
			Calendar tm = CalendarUtils.DAY_MIDNIGHT(diffFromMonday);
			long timeMidnignt = tm.getTimeInMillis() / 1000;

			Calendar td = CalendarUtils.DAY_23_59(diffFromMonday);
			long timeTwentyThree = td.getTimeInMillis() / 1000;

			String walkIdentifier = String.format(Locale.getDefault(), "%d-%d",
					timeMidnignt, timeTwentyThree);

			mPortionIds[i - STARTDAY] = walkIdentifier;
			Log.i("mPortionIds[i-STARTDAY] = ", "" + (i - STARTDAY));
		}
	}

	public boolean needReloading() {
		long lastLoadTime = SharedPreferencesHelper
				.getTimeLastLoading(getApplicationContext());
		long diff = System.currentTimeMillis() - lastLoadTime;

		if (diff > ScheduleActivity.PERIOD_RELOADING) {
			SyslogUtils.logEvent(getApplicationContext(),
					"Reloading walks beacuse last loading was 2 hours ago",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		String weekLoadStatus = SharedPreferencesHelper
				.getWeekLoadStatus(getApplicationContext());

		if (needReloading()) {
			reload();
		} else {
			updateWalks();
		}

		if (weekLoadStatus.equals(EnumWeekLoadStatus.LOADED)) {
			updateWalks();
		}

	}

	public void reload() {
		SyslogUtils.logEvent(getApplicationContext(), "Reload from This Week",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		if (updatingNow.get()) {
			ToastUtils.showShort(getApplicationContext(),
					"Wait for all week finish loading");
			return;
		}

		if (ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			String startedWalkUniqueId = SharedPreferencesHelper
					.getStartedWalkUniqueId(getApplicationContext());

			if (startedWalkUniqueId
					.equals(SharedPreferencesHelper.DEFAULT_STARTED_WALK_UNIQUE_ID)) {
				mAdapter.clear();
				((SwiftoApplication) getApplication()).getPortionsLoader()
						.reloadAllPortions();
				((SwiftoApplication) getApplication()).clearWalksForMonths();
				SharedPreferencesHelper.saveWeekLoadStatus(
						getApplicationContext(), EnumWeekLoadStatus.NOT_LOADED);

				updateWalks();
			}
			// ���� ������� �������� - ������ �������� ��� ���, ����������� ��
			else {
				((SwiftoApplication) getApplication())
						.loadWalksForDayContainingStartedWalk(
								getApplicationContext(), thisLink);
			}
		} else {
			ToastUtils.showShort(getApplicationContext(),
					"No internet connection");
		}
	}

	

	public void onLoadWalksContainingStartedSuccess() {
		mAdapter.clear();
		updateWalks();
	}

	public void onLoadWalksContainingStartedFailure(String error) {
		SyslogUtils.logEvent(getApplicationContext(),
				"Error loading started walk: " + error, EnumLogSeverity.ERROR,
				EnumLogType.SERVER_TYPE);
		Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT)
				.show();
	}

	public void pageChangedToMe() {
		if (SharedPreferencesHelper.getWeekLoadStatus(getApplicationContext())
				.equals(EnumWeekLoadStatus.NOT_LOADED)) {
			updateWalks();
		}
	}

	private AtomicBoolean updatingNow = new AtomicBoolean(false);

	public void updateWalks() {
		if (updatingNow.get()) {
			return;
		}

		initPortionIds();

		updatingNow.set(true);

		mDaysLoaded.set(STARTDAY);

		mWalksGroups.clear();
		mContactDogExpndList.clear();

		mAdapter.clear();

		refreshAdapter();

		mContactDogExpndList = new ArrayList<ContactDogList>();
		mAdapter = new ContactWalksAdapter(ContactActivity.this,
				mContactDogExpndList);
		mExpandableList.setAdapter(mAdapter);

		showNothing();
		showBottomProgress();

		loadNextDay();
	}

	public void loadNextDay() {
		if (mDaysLoaded.get() == NOOFDAYSHOW) {
			updatingNow.set(false);

			hideBottomProgress();
			showListWalks();

			SharedPreferencesHelper.saveWeekLoadStatus(getApplicationContext(),
					EnumWeekLoadStatus.LOADED);

			return;
		}

		Portion portion = ((SwiftoApplication) getApplication())
				.getPortionsLoader().loadWalksForPortion(
						mPortionIds[mDaysLoaded.get() - STARTDAY], this, false,
						getApplicationContext());

		if (portion == null) {
			// callback will handler loading
		} else if (portion.State.equals(EnumPortionState.EMPTY)) {
			// load next
			mDaysLoaded.incrementAndGet();

			loadNextDay();
		} else {
			// do nothing - callback will handle loading
		}
	}

	private void addPortion(ArrayList<Walk> loadedWalks) {
		WalksOneDay loadedGroup = new WalksOneDay();

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(loadedWalks.get(0).StartTime * 1000);
		SimpleDateFormat format = new SimpleDateFormat(
				DateTimeUtils.PATTERN_DAY_NAME, Locale.getDefault());
		String dayName = format.format(c.getTime());

		format = new SimpleDateFormat(DateTimeUtils.PATTERN_MONTH_DAY,
				Locale.getDefault());

		String date = format.format(c.getTime());

		loadedGroup.setDate(date);
		loadedGroup.setDayName(dayName);

		for (Walk walk : loadedWalks) {
			walk.setIdentifierStartEndMillis(WalkUtils
					.getIdentifier(walk.StartTime));
			loadedGroup.getmListWalks().add(walk);
		}

		addLoadedGroup(loadedGroup);

		mDaysLoaded.incrementAndGet();

		loadNextDay();
	}

	private void addLoadedGroup(WalksOneDay loadedGroup) {

		for (int i = 0; i < loadedGroup.getmListWalks().size(); i++) {
			String fname = loadedGroup.getmListWalks().get(i).Owner.FirstName;
			String lname = loadedGroup.getmListWalks().get(i).Owner.LastName;
			String _id = loadedGroup.getmListWalks().get(i).Owner._id;
			Log.i("Walk id on page ", _id);
			String PhonePrimary = loadedGroup.getmListWalks().get(i).Owner.PhonePrimary;

			mContactDog = new ContactDog(_id, fname, lname, PhonePrimary,false);
			if (isAddedDog(loadedGroup.getmListWalks().get(i))) {
				mContactDogList.add(mContactDog);
			} else {
				Log.i("remove", "duplicate");
			}
		}

		Collections.sort(mContactDogList, new Comparator<ContactDog>() {
		    public int compare(ContactDog v1, ContactDog v2) {
		        return v1.FirstName.compareTo(v2.FirstName);
		    }
		});
		
		mContactDogExpndList.clear();
		setPrimaryObject();
		mContactDogExpnd = new ContactDogList();
		mContactDogExpnd.setmDayName("Clients");
		mContactDogExpnd.setContactDog(mContactDogList);
		mContactDogExpndList.add(mContactDogExpnd);

		if (mLayout.getVisibility() == View.GONE) {
			showListWalks();
		}
		
		refreshAdapter();
	}
	
	public void setPrimaryObject() {
		
		ArrayList<ContactDog> arr = new ArrayList<ContactDog>();
	/*	ContactDog hr = new ContactDog("hr_id",
				"HR", "", getString(R.string.hr_phno),true);*/

		ContactDog hr = new ContactDog("hr_id",
				"HR", "","",true);

		ContactDog schedul = new ContactDog("schedual_id",
				"Scheduling", "", getString(R.string.scheduling_phno),true);

		hr.setEmail(getString(R.string.hr_email));
		schedul.setEmail(getString(R.string.scheduling_email));
		arr.add(hr);
		arr.add(schedul);
		
		mContactDogExpnd = new ContactDogList();
		mContactDogExpnd.setmDayName("Swifto Contacts");
		mContactDogExpnd.setContactDog(arr);
		mContactDogExpndList.add(mContactDogExpnd);
	}

	private boolean isAddedDog(Walk loadedGroup) {
		for (int i = 0; i < mContactDogList.size(); i++) {

			try {
				if (mContactDogList.get(i).FirstName
						.equals(loadedGroup.Owner.FirstName)&& mContactDogList.get(i).LastName
						.equals(loadedGroup.Owner.LastName)) {
					return false;
				}
			} catch (Exception e) {
				return true;
			}
		}
		return true;
	}

	public void walksLoaded(ArrayList<Walk> walks, String portionId) {
		if (portionIsForMe(portionId)) {
			Portion portion = ((SwiftoApplication) getApplication())
					.getPortionsLoader().getPortionById(portionId);

			if (portion.State.equals(EnumPortionState.EMPTY)) {
				mDaysLoaded.incrementAndGet();
				loadNextDay();
			} else {
				addPortion(walks);
			}
		}
	}

	public void interrupt() {
		mDaysLoaded.incrementAndGet();
		loadNextDay();
	}

	private boolean portionIsForMe(String portionId) {
		for (int i = STARTDAY; i < NOOFDAYSHOW; i++) {
			if (mPortionIds[i].equals(portionId)) {
				return true;
			}
		}

		return false;
	}

	private void refreshAdapter() {
		mAdapter.notifyDataSetChanged();
	}

	private void showListWalks() {
		mLayout.setVisibility(View.VISIBLE);
		// mLayoutNoWalks.setVisibility(View.GONE);
	}

	private void showNothing() {
		mLayout.setVisibility(View.GONE);
		// mLayoutNoWalks.setVisibility(View.GONE);
	}

	private void showBottomProgress() {
		mProgressLayout.setVisibility(View.VISIBLE);
	}

	private void hideBottomProgress() {
		mProgressLayout.setVisibility(View.GONE);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();

		thisLink = this;
	}

	@Override
	public void onPause() {
		super.onPause();

		this.updatingNow.set(false);
	}

	public void sendEmailLog(String email) {
		Intent intentEmail = new Intent(Intent.ACTION_SEND);
		intentEmail.setType("text/message");
		intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
		startActivity(intentEmail);
	}
	
	public void sendSMS(String no) {
		Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
				+ no));
		startActivity(smsIntent);
	}

}
