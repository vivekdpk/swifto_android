 package com.haski.swifto.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.components.ListViewExt;
import com.haski.swifto.components.calendar.CalendarView;
import com.haski.swifto.components.calendar.GenerateWalkCountCallback;
import com.haski.swifto.interfaces.ILoadWalksContainingStartedWaitable;
import com.haski.swifto.interfaces.WalksLoadedCallback;
import com.haski.swifto.model.EnumPortionState;
import com.haski.swifto.model.EnumWeekLoadStatus;
import com.haski.swifto.model.vo.Portion;
import com.haski.swifto.model.vo.WalksForMonth;
import com.haski.swifto.model.vo.json.GetWalksNumberPerMonthParser;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.server.ServerRequestAsynkTask;
import com.haski.swifto.server.SwiftoRequestBuilder;
import com.haski.swifto.util.CalendarUtils;
import com.haski.swifto.util.ServiceUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.StringUtils;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.TaskErrorChecker;
import com.haski.swifto.util.ToastUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

public class MonthlyScheduleFragment extends Fragment implements OnItemClickListener, GenerateWalkCountCallback, ILoadWalksContainingStartedWaitable, WalksLoadedCallback {

	public static Fragment newInstance(){
		return new MonthlyScheduleFragment();
	}
	
	public MonthlyScheduleFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		mApplication = (SwiftoApplication) getActivity().getApplication();
		thisLink = this;
		
		if(((ScheduleActivity)getActivity()).SelectedFragmentIndex == 3) {
			if(((ScheduleActivity)getActivity()).needReloading()) {
				reload();
			} else {
				if(calendar != null) {
					int selMonth = calendar.getSelectedMonthNumber();
					int selYear = calendar.getSelectedYear();
					updateMonth(selMonth, selYear);
				} else {
					//handle
					SyslogUtils.logEvent(getActivity(), "Month.onResume() - calendar is null", EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
				}
			}
		}
		
		if(mListWalksAdapter != null) {
			mListWalksAdapter.notifyDataSetChanged();
		}
		
	}

	private SwiftoApplication mApplication;
	
	private ILoadWalksContainingStartedWaitable thisLink;
	
	private CalendarView calendar;
	
	//private ListView mListWalks;
	private ListViewExt mListWalks;
	private WalksListAdapter mListWalksAdapter;
	private ArrayList<Walk> mWalks;
	
	private RelativeLayout mLayoutEmpty;
	
	private TextView mTxtEmpty;
	
	private TextView mTxtSelectDay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mApplication = (SwiftoApplication) getActivity().getApplication();
		thisLink = this;

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_schedule_month, null);
		
		calendar = (CalendarView) root.findViewById(R.id.fragment_schedule_month_calendar);
		//calendar = (CalendarView) LayoutInflater.from(getActivity()).inflate(R.layout.calendar, null);
		//calendar = new CalendarView(getActivity(), this, this);
		
		int savedYear = SharedPreferencesHelper.getSelectedYear(getActivity());
		int savedMonth = SharedPreferencesHelper.getSelectedMonth(getActivity());
		int savedDay = SharedPreferencesHelper.getSelectedDay(getActivity());
		
		if(
			savedDay != SharedPreferencesHelper.DEFAULT_SELECTED_DAY
			&&
			savedMonth != SharedPreferencesHelper.DEFAULT_SELECTED_MONTH
			&&
			savedYear != SharedPreferencesHelper.DEFAULT_SELECTED_YEAR
		) {
			Calendar month = Calendar.getInstance();
			month.set(Calendar.YEAR, savedYear);
			month.set(Calendar.MONTH, savedMonth);
			month.set(Calendar.DAY_OF_MONTH, savedDay);

			calendar.postConstruct(getActivity(), month, this, this);
		} else {
			calendar.postConstruct(getActivity(), this, this);
		}
		
		mListWalks = (ListViewExt) root.findViewById(R.id.fragment_schedule_month_list_walks);
		
		mWalks = new ArrayList<Walk>();
		mListWalksAdapter = new WalksListAdapter(getActivity(), R.layout.list_item_walk_info, mWalks);
		mListWalks.setAdapter(mListWalksAdapter);
		
		mLayoutEmpty = (RelativeLayout) root.findViewById(R.id.fragment_schedule_month_layout_empty);
		
		mTxtEmpty = (TextView) root.findViewById(R.id.fragment_schedule_month_txt_empty);
		
		mTxtSelectDay = (TextView) root.findViewById(R.id.fragment_schedule_month_txt_select_day);
		
		return root;
	}
	
	public void reload() {
		SyslogUtils.logEvent(getActivity(), "Reload from Monthly", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		
		//if(ServiceUtils.isNetworkAvailable(getActivity())) {
			String startedWalkUniqueId = SharedPreferencesHelper.getStartedWalkUniqueId(getActivity());

			//there is no started walk
			if(startedWalkUniqueId.equals(SharedPreferencesHelper.DEFAULT_STARTED_WALK_UNIQUE_ID)) {
				((SwiftoApplication)getActivity().getApplication()).getPortionsLoader().reloadAllPortions();
				((SwiftoApplication)getActivity().getApplication()).clearWalksForMonths();
				SharedPreferencesHelper.saveWeekLoadStatus(getActivity(), EnumWeekLoadStatus.NOT_LOADED);
				
				showSelectDay();

				checkReloaded();

			} else {
				mApplication.loadWalksForDayContainingStartedWalk(getActivity(), thisLink);
			}
		//} else {
		//	ToastUtils.showShort(getActivity(), "No internet connection");
		//	SyslogUtils.logEvent(getActivity(), "No internet connection on " + "trying to reload month info", EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
		//}
	}
	
	public void onLoadWalksContainingStartedSuccess() {
		checkReloaded();
	}

	public void onLoadWalksContainingStartedFailure(String error) {
		ToastUtils.showShort(getActivity(), error);
	}
	
	private void checkReloaded() {
		int selMonth = calendar.getSelectedMonthNumber();
		int selYear = calendar.getSelectedYear();
		updateMonth(selMonth, selYear);
	}
	
	
	
	//-----------------------------------------------------
	//
	//			Months
	//
	//--------------------
	
	public ArrayList<String> onMonthChanged(int month) {
		SharedPreferencesHelper.deleteSelectedDay(getActivity());
		
		ArrayList<String> countsForColoredDots = new ArrayList<String>();
		int selMonth = calendar.getSelectedMonthNumber();
		int selYear = calendar.getSelectedYear();
		
		changeMonthSelection(selMonth, selYear);
		updateMonth(selMonth, selYear);
		
		return countsForColoredDots;
	}
	
	private void changeMonthSelection(int toMonth, int toYear) {
		SharedPreferencesHelper.saveSelectedMonth(getActivity(), toMonth);
		SharedPreferencesHelper.saveSelectedYear(getActivity(), toYear);
	}
	
	private final AtomicBoolean mMonthIsReloading = new AtomicBoolean(false);
	
	private void updateMonth(int toMonth, int toYear) {

		WalksForMonth current = mApplication.getDbAdapter().getMonthInfo(toMonth, toYear);

		//month data lready loaded
		if(current != null) {
			if(current.getIsMonthEmpty()) {
				//ToastUtils.showLong(getActivity(), "if changed month "+toMonth);
				showMonthIsEmpty();
			} else {
				showSelectDay();
				//ToastUtils.showLong(getActivity(), "if changed month "+toMonth);
				
				//showDaydata(1);
			}

			int savedDay = SharedPreferencesHelper.getSelectedDay(getActivity());
			
			if(savedDay != SharedPreferencesHelper.DEFAULT_SELECTED_DAY) {
				updateAdapterForMonth(current, savedDay-1);
				
				//also, we need to fill list walks
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(calendar.getCalendar().getTimeInMillis());
				cal.set(Calendar.DAY_OF_MONTH, savedDay);
				
				Days daysBetween = Days.daysBetween(new DateTime().toDateMidnight(), new DateTime(cal.getTime()).toDateMidnight());
				int days = daysBetween.getDays();
				
				replaceWalks(days);
			} else {
				updateAdapterForMonth(current, -1);
			}
			
		} else {
			//ToastUtils.showLong(getActivity(), "else changed month "+toMonth);
			
			loadForMonth();
			
		}
		
	}


	private void loadForMonth() {
		mMonthIsReloading.set(true);

		String res = SharedPreferencesHelper.getMonthLoadingResponce(getActivity().getApplicationContext(), calendar.getSelectedMonthNumber());

		if (res.equals("nodata")) {


			if (ServiceUtils.isNetworkAvailable(getActivity())) {


				//TimeZone tzDefault = TimeZone.getDefault();
				TimeZone tzDefault = TimeZone.getTimeZone("GMT-4:00");
				Date now = new Date();
				int offset = tzDefault.getOffset(now.getTime());
				int offsetInMillis = offset / 1000;

				String request = SwiftoRequestBuilder.buildGetWalksNumberPerMonth(SharedPreferencesHelper.getWalkerId(getActivity()), StringUtils.MONTH_NAME(calendar.getSelectedMonthNumber()), offsetInMillis, calendar.getSelectedYear());

				new LoadForMonthAsyncTask(getActivity(), "Loading walks for month...", true).execute(request);

				SyslogUtils.logEventWithResponce(getActivity(), " Loading walks for month... request : " + request, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE, request);

			}
			/*else if(res.equals("changedata"))
			{
				TimeZone tzDefault = TimeZone.getDefault();
				Date now = new Date();
				int offset = tzDefault.getOffset(now.getTime());
				int offsetInMillis = offset / 1000;

				String request = SwiftoRequestBuilder.buildGetWalksNumberPerMonth(SharedPreferencesHelper.getWalkerId(getActivity()), StringUtils.MONTH_NAME(calendar.getSelectedMonthNumber()), offsetInMillis, calendar.getSelectedYear());

				new LoadForMonthAsyncTask(getActivity(), "Loading walks for month...", true).execute(request);

				SyslogUtils.logEventWithResponce(getActivity(), " Loading walks for month... request : " + request, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE, request);

			}*/

			else {
				ToastUtils.showShort(getActivity(), "No internet connection");
				SyslogUtils.logEvent(getActivity(), "No internet connection on " + "trying to load month info", EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
			}
		}
		else
		{


			if (ServiceUtils.isNetworkAvailable(getActivity())) {


				TimeZone tzDefault = TimeZone.getDefault();
				Date now = new Date();
				int offset = tzDefault.getOffset(now.getTime());
				int offsetInMillis = offset / 1000;

				String request = SwiftoRequestBuilder.buildGetWalksNumberPerMonth(SharedPreferencesHelper.getWalkerId(getActivity()), StringUtils.MONTH_NAME(calendar.getSelectedMonthNumber()), offsetInMillis, calendar.getSelectedYear());

				new LoadForMonthAsyncTask(getActivity(), "Loading walks for month...", true).execute(request);

				SyslogUtils.logEventWithResponce(getActivity(), " Loading walks for month... request : " + request, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE, request);

//				SyslogUtils.logEventWithResponce(getActivity(), " Preloaded month responce : " + res, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE, "NA");
//
//				parseServerResponse(res);
			}

			else {

				SyslogUtils.logEventWithResponce(getActivity(), " Preloaded month responce : " + res, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE, "NA");

				parseServerResponse(res);
			}
		}
	}
	
	private class LoadForMonthAsyncTask extends ServerRequestAsynkTask {
		public LoadForMonthAsyncTask(Context ctx, String message, boolean showProgress) {
			super(ctx, message, showProgress);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			parseServerResponse(result);
		}
	}
	
	private void parseServerResponse(String response) {
		mMonthIsReloading.set(false);
		
		String error = TaskErrorChecker.getErrorInString(response);
		
		if(error != null) {
			ToastUtils.showShort(getActivity(), error);
			SyslogUtils.logEventWithResponce(getActivity(), "Month information loading error: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE, response);
		} else {
			if(getActivity() != null) {
				SharedPreferencesHelper.saveTimeLastLoading(getActivity().getApplicationContext(), System.currentTimeMillis());
				
				WalksForMonth walksForMonth = new GetWalksNumberPerMonthParser().parseWalksForMonth(response);
				
				int year = calendar.getSelectedYear();
				int monthNum = calendar.getSelectedMonthNumber();
				
				walksForMonth.setYear(year);
				walksForMonth.setMonth(monthNum);
				
				SyslogUtils.logEvent(getActivity(), String.format(Locale.getDefault(), "Loaded walks for month %d, %d, response: %s", monthNum + 1, year, response), EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
				
				mApplication.getDbAdapter().insertMonthInfo(walksForMonth);
				
				if(walksForMonth.getIsMonthEmpty()) {
					showMonthIsEmpty();
				} else {
					showSelectDay();
				}
				
				updateAdapterForMonth(walksForMonth, -1);
			}
			showDefaultDate();
		}
	}
	
	private void updateAdapterForMonth(WalksForMonth walksForMonth, int indexOfCellInCalendarToSelect) {
		calendar.refreshWithNewData(walksForMonth.getNumbersAsArrayList(), indexOfCellInCalendarToSelect);
	}
	
	
	
	//-----------------------------------------------------------
	//
	//			Days
	//
	//--------------------------

	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		//ToastUtils.showShort(getActivity(), "showDaydata before called - "+position);
		showDaydata(position);
	}
	
	public void showDefaultDate()
	{
		int position = 0;
		//Log.i("calendar.getStartPossitionOfMonth();", ""+calendar.getStartPossitionOfMonth());
		if(calendar.getTodayIsInCurrentMonth()) {
			position = calendar.getTodaysPosition();
		}
		else
		{
			for (int i = calendar.getStartPossitionOfMonth(); i < 43; i++) {

				int numberOfWalksForSelectedDay = calendar.getNumberOfWalksForDay(i);
				if (numberOfWalksForSelectedDay != -1) {
					Log.i("position in if ", "position - " + i);

					//SyslogUtils.logEvent(getActivity(), "position in if position - " + i, EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);

					position = i;
					break;
				}
			}
		}


		final int day = calendar.getNumberOfDayAtPosition(position);

		//final int day = calendar.getTodaysPosition();

		SyslogUtils.logEvent(getActivity(), "Month view day -   "+day , EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);


		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(calendar.getCalendar().getTimeInMillis());
		cal.set(Calendar.DAY_OF_MONTH, day);
		
		Days daysBetween = Days.daysBetween(new DateTime().toDateMidnight(), new DateTime(cal.getTime()).toDateMidnight());
		int days = daysBetween.getDays();
		replaceWalks(days);
		
	}
	
	public void showDaydata(int position)
	{
		int numberOfWalksForSelectedDay = calendar.getNumberOfWalksForDay(position);
		final int day = calendar.getNumberOfDayAtPosition(position);
		//ToastUtils.showShort(getActivity(), "day - "+day);
		
		SharedPreferencesHelper.saveSelectedDay(getActivity(), day);
		
		if(numberOfWalksForSelectedDay == -1) {
			showDayWalksEmpty();
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(calendar.getCalendar().getTimeInMillis());
			cal.set(Calendar.DAY_OF_MONTH, day);
			Days daysBetween = Days.daysBetween(new DateTime().toDateMidnight(), new DateTime(cal.getTime()).toDateMidnight());
			int days = daysBetween.getDays();
			replaceWalks(days);
		}
	}
	
	private String getPortionId() {
		int offsetDay= SharedPreferencesHelper.getLastDayOffsetForMonthlyView(getActivity());
		
		Calendar tm = CalendarUtils.DAY_MIDNIGHT(offsetDay);
		long tms = tm.getTimeInMillis() / 1000;
		
		Calendar td = CalendarUtils.DAY_23_59(offsetDay);
		long tds = td.getTimeInMillis() / 1000;
		
		String walkIdentifier = String.format(Locale.getDefault(), "%d-%d", tms, tds);
		
		return walkIdentifier;
	}
	
	private void replaceWalks(int days) {
		SharedPreferencesHelper.saveLastDayOffsetForMonthlyView(getActivity(), days);
		
		String portionId = getPortionId();




		Portion portion = ((SwiftoApplication)getActivity().getApplication()).getPortionsLoader().loadWalksForPortion(portionId, this, true, getActivity());

		/*SyslogUtils.logEvent(getActivity(), "portion id  in Monthly Schedule - " + getPortionId(),
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);*/




		if(portion == null) {
			//callback will handler loading
		} else if(portion.State.equals(EnumPortionState.EMPTY)) {
			showDayWalksEmpty();
			/*SyslogUtils.logEvent(getActivity(), "value of portion   in Monthly Schedule - " + portion.toString(),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);*/
		} else {
			//do nothing - callback will handle loading
			/*SyslogUtils.logEvent(getActivity(), "value of portion   in Monthly Schedule - " + portion.toString(),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);*/
		}
	}
	
	private void updateWalks(ArrayList<Walk> walks) {
		mWalks.clear();
		
		mListWalks.setHeightToLargest();
		
		mListWalksAdapter.notifyDataSetChanged();
		
		for(Walk walk : walks) {
			mWalks.add(walk);
		}

		SyslogUtils.logEvent(getActivity(), "Length  of walk list on month view   - " + mWalks.size(),
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);


		mListWalksAdapter.notifyDataSetChanged();
	}

	public void pageChangedToMe() {
		checkReloaded();
	}
	
	private void showSelectDay() {
		mListWalks.setVisibility(View.GONE);
		mLayoutEmpty.setVisibility(View.GONE);
		mTxtSelectDay.setVisibility(View.VISIBLE);
	}
	
	private void showMonthIsEmpty() {
		mListWalks.setVisibility(View.GONE);
		mTxtSelectDay.setVisibility(View.GONE);
		mLayoutEmpty.setVisibility(View.VISIBLE);
		mTxtEmpty.setText(getResources().getString(R.string.fragment_schedule_month_txt_no_walks_month));
	}
	
	private void showDayWalksEmpty() {
		mListWalks.setVisibility(View.GONE);
		mTxtSelectDay.setVisibility(View.GONE);
		mLayoutEmpty.setVisibility(View.VISIBLE);
		mTxtEmpty.setText(getResources().getString(R.string.fragment_schedule_month_txt_no_walks_day));
	}
	
	private void showListWalks() {
		mListWalks.setVisibility(View.VISIBLE);
		mLayoutEmpty.setVisibility(View.GONE);
		mTxtSelectDay.setVisibility(View.GONE);
	}
	
	public void walksLoaded(ArrayList<Walk> walks, String portionId) {
		if(portionId.equals(getPortionId())) {
			Portion portion = ((SwiftoApplication)getActivity().getApplication()).getPortionsLoader().getPortionById(portionId);
			
			if(portion.State.equals(EnumPortionState.EMPTY)) {
				showDayWalksEmpty();
			} else {

				showListWalks();
				updateWalks(walks);
			}
		}
	}

	public void interrupt() {
		//do nothing
		showListWalks();
	}
}
