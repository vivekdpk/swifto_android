package com.haski.swifto.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.interfaces.ILoadWalksContainingStartedWaitable;
import com.haski.swifto.interfaces.WalksLoadedCallback;
import com.haski.swifto.model.EnumPortionState;
import com.haski.swifto.model.EnumWeekLoadStatus;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ThisWeekScheduleFragment extends Fragment implements ILoadWalksContainingStartedWaitable, WalksLoadedCallback {
	
	private ArrayList<WalksOneDay> mWalksGroups;
	private final AtomicInteger mDaysLoaded = new AtomicInteger(0);
	
	private ExpandableListView mExpandableList;
	private ThisWeekWalksAdapter mAdapter;
	
	private LinearLayout mProgressLayout;
	
	private RelativeLayout mLayout;
	private RelativeLayout mLayoutNoWalks;
	
	private ILoadWalksContainingStartedWaitable thisLink;
	
	public static Fragment newInstance() {
		return new ThisWeekScheduleFragment();
	}
	
	public ThisWeekScheduleFragment() {
	}
	
	private String[] mPortionIds;

	/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("Week", "onCreate()");
	}
	*/
	
	private void initPortionIds() {
		mPortionIds = new String[7];
		
		int diffFromMonday;

		for (int i = 0; i < 7; i++) {
			diffFromMonday = CalendarUtils.DIFFERENCE_FROM_MONDAY() + i;
			Calendar tm = CalendarUtils.DAY_MIDNIGHT(diffFromMonday);
			long timeMidnignt = tm.getTimeInMillis() / 1000;
			
			Calendar td = CalendarUtils.DAY_23_59(diffFromMonday);
			long timeTwentyThree = td.getTimeInMillis() / 1000;
			
			String walkIdentifier = String.format(Locale.getDefault(), "%d-%d", timeMidnignt, timeTwentyThree);
			
			mPortionIds[i] = walkIdentifier;
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		String weekLoadStatus = SharedPreferencesHelper.getWeekLoadStatus(getActivity());
		
		if(((ScheduleActivity)getActivity()).SelectedFragmentIndex == 2) {
			if(((ScheduleActivity)getActivity()).needReloading()) {
				reload();
			} else {
				updateWalks();
			}
		} else {
			if(weekLoadStatus.equals(EnumWeekLoadStatus.LOADED))
			{
				updateWalks();
			}
		}
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_schedule_week, null);

		mLayout = (RelativeLayout) root.findViewById(R.id.fragment_schedule_week_layout);
		mLayoutNoWalks = (RelativeLayout) root.findViewById(R.id.fragment_schedule_week_layout_no_walks);
		
		mExpandableList = (ExpandableListView) root.findViewById(R.id.fragment_schedule_week_expandable_list);
		mWalksGroups = new ArrayList<WalksOneDay>();
		mAdapter = new ThisWeekWalksAdapter(getActivity(), mWalksGroups);
		mExpandableList.setAdapter(mAdapter);
		
		mProgressLayout = (LinearLayout) root.findViewById(R.id.fragment_schedule_week_progress);
		
		return root;
	}
	
	public void reload() {
		SyslogUtils.logEvent(getActivity(), "Reload from This Week", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		
		if(updatingNow.get()) {
			ToastUtils.showShort(getActivity(), "Wait for all week finish loading");
			return;
		}
		
		if(ServiceUtils.isNetworkAvailable(getActivity())) {
			String startedWalkUniqueId = SharedPreferencesHelper.getStartedWalkUniqueId(getActivity());

			if(startedWalkUniqueId.equals(SharedPreferencesHelper.DEFAULT_STARTED_WALK_UNIQUE_ID)) {
				mAdapter.clear();
				((SwiftoApplication)getActivity().getApplication()).getPortionsLoader().reloadAllPortions();
				((SwiftoApplication)getActivity().getApplication()).clearWalksForMonths();
				SharedPreferencesHelper.saveWeekLoadStatus(getActivity(), EnumWeekLoadStatus.NOT_LOADED);
				
				updateWalks();
			}
			else {
				((SwiftoApplication)getActivity().getApplication()).loadWalksForDayContainingStartedWalk(getActivity(), thisLink);
			}
		} else {
			ToastUtils.showShort(getActivity(), "No internet connection");
		}
	}

	public void onLoadWalksContainingStartedSuccess() {
		mAdapter.clear();
		updateWalks();
	}

	public void onLoadWalksContainingStartedFailure(String error) {
		SyslogUtils.logEvent(getActivity().getApplicationContext(), "Error loading started walk: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
		Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
	}
	
	public void pageChangedToMe() {
		if(SharedPreferencesHelper.getWeekLoadStatus(getActivity()).equals(EnumWeekLoadStatus.NOT_LOADED)) {
			updateWalks();
		}
	}
	
	private AtomicBoolean updatingNow = new AtomicBoolean(false);
	
	public void updateWalks() {
		if(updatingNow.get()) {
			return;
		}
		
		initPortionIds();

		updatingNow.set(true);
		
		mDaysLoaded.set(0);
		
		mWalksGroups.clear();
		
		mAdapter.clear();
		
		refreshAdapter();
		
		mWalksGroups = new ArrayList<WalksOneDay>();
		mAdapter = new ThisWeekWalksAdapter(getActivity(), mWalksGroups);
		mExpandableList.setAdapter(mAdapter);
		
		showNothing();
		showBottomProgress();
		
		loadNextDay();
	}
	
	public void loadNextDay() {
		if(mDaysLoaded.get() == 7) {
			updatingNow.set(false);
			
			hideBottomProgress();
			showListWalks();
			
			SharedPreferencesHelper.saveWeekLoadStatus(getActivity(), EnumWeekLoadStatus.LOADED);
			
			return;
		}
		
		Portion portion = ((SwiftoApplication)getActivity().getApplication()).getPortionsLoader().loadWalksForPortion(mPortionIds[mDaysLoaded.get()], this, false, getActivity());
		
		if(portion == null) {
			//callback will handler loading
		} else if(portion.State.equals(EnumPortionState.EMPTY)) {
			//load next
			mDaysLoaded.incrementAndGet();
			
			loadNextDay();
		} else {
			//do nothing - callback will handle loading
		}
	}
	
	private void addPortion(ArrayList<Walk> loadedWalks) {
		WalksOneDay loadedGroup = new WalksOneDay();

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(loadedWalks.get(0).StartTime * 1000);
		SimpleDateFormat format = new SimpleDateFormat(DateTimeUtils.PATTERN_DAY_NAME, Locale.getDefault());
		String dayName = format.format(c.getTime());

		format = new SimpleDateFormat(DateTimeUtils.PATTERN_MONTH_DAY, Locale.getDefault());

		String date = format.format(c.getTime());

		loadedGroup.setDate(date);
		loadedGroup.setDayName(dayName);

		for(Walk walk : loadedWalks) {
			walk.setIdentifierStartEndMillis(WalkUtils.getIdentifier(walk.StartTime));
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
		}
		
		mWalksGroups.add(loadedGroup);
		
		if(mLayout.getVisibility() == View.GONE) {
			showListWalks();
		}
		
		refreshAdapter();
	}
	
	
	/*
	 * WalksLoadedCallback implementation
	 */
	
	public void walksLoaded(ArrayList<Walk> walks, String portionId) {
		if(portionIsForMe(portionId)) {
			Portion portion = ((SwiftoApplication)getActivity().getApplication()).getPortionsLoader().getPortionById(portionId);
			
			if(portion.State.equals(EnumPortionState.EMPTY)) {
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
		for(int i = 0; i < 7; i++) {
			if(mPortionIds[i].equals(portionId)) {
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
		mLayoutNoWalks.setVisibility(View.GONE);
	}
	
	private void showNothing() {
		mLayout.setVisibility(View.GONE);
		mLayoutNoWalks.setVisibility(View.GONE);
	}
	
	private void showBottomProgress() {
		mProgressLayout.setVisibility(View.VISIBLE);
	}
	
	private void hideBottomProgress() {
		mProgressLayout.setVisibility(View.GONE);
	}
}
