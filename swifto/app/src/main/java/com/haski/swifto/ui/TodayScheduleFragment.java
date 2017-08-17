package com.haski.swifto.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.components.calendar.CalendarView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

public class TodayScheduleFragment extends Fragment implements
		ILoadWalksContainingStartedWaitable, WalksLoadedCallback {

	public static Fragment newInstance() {
		return new TodayScheduleFragment();
	}

	public TodayScheduleFragment() {
	}

	public static String getPortionId() {
		Calendar tm = CalendarUtils.TODAY_MIDNIGHT();
		long timeMidnignt = tm.getTimeInMillis() / 1000;

		Calendar td = CalendarUtils.TODAY_23_59();
		long timeTwentyThree = td.getTimeInMillis() / 1000;

		String walkIdentifier = String.format(Locale.getDefault(), "%d-%d",
				timeMidnignt, timeTwentyThree);

		return walkIdentifier;
	}

	private ILoadWalksContainingStartedWaitable thisLink;

	private ListView mListWalks;
	private WalksListAdapter mAdapter;
	private ArrayList<Walk> mWalksToday;

	private RelativeLayout mLayoutNoWalks;

	@Override
	public void onStart() {
		super.onStart();

		thisLink = this;

		mApplication = (SwiftoApplication) getActivity().getApplication();





		if (((ScheduleActivity) getActivity()).SelectedFragmentIndex == 0) {

//			if (((ScheduleActivity) getActivity()).needReloading()) {
//				reload();
//			} else {
//				reload();
//				// vivek hack
//				// updateWalks();
//			}

			Date date= new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);

			String res = SharedPreferencesHelper.getMonthLoadingResponce(getActivity().getApplicationContext(), month);

			if (res.equals("nodata")) {
				loadForMonth();
			}
			else
			{
				if (((ScheduleActivity) getActivity()).needReloading()) {
					reload();
				} else {
					reload();
					// vivek hack
					// updateWalks();
				}
			}



		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();

		updateWalks();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_schedule_today, null);

		mListWalks = (ListView) root
				.findViewById(R.id.fragment_schedule_today_list);
		mWalksToday = new ArrayList<Walk>();

		mAdapter = new WalksListAdapter(getActivity(),
				R.layout.list_item_walk_info, mWalksToday);
		mListWalks.setAdapter(mAdapter);

		mLayoutNoWalks = (RelativeLayout) root
				.findViewById(R.id.fragment_schedule_today_layout_no_walks);

		return root;
	}

	public void reload() {
		SyslogUtils.logEvent(getActivity(), "Reload from Today",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		if (ServiceUtils.isNetworkAvailable(getActivity())) {
			String startedWalkUniqueId = SharedPreferencesHelper
					.getStartedWalkUniqueId(getActivity());

			if (startedWalkUniqueId
					.equals(SharedPreferencesHelper.DEFAULT_STARTED_WALK_UNIQUE_ID)) {
				((SwiftoApplication) getActivity().getApplication())
						.getPortionsLoader().reloadAllPortions();
				((SwiftoApplication) getActivity().getApplication())
						.clearWalksForMonths();
				SharedPreferencesHelper.saveWeekLoadStatus(getActivity(),
						EnumWeekLoadStatus.NOT_LOADED);

				updateWalks();
			}
			// ���� ������� �������� - ������ �������� ��� ���, ����������� ��
			else {
				((SwiftoApplication) getActivity().getApplication())
						.loadWalksForDayContainingStartedWalk(getActivity(),
								thisLink);
			}




		} else {
			ToastUtils.showShort(getActivity(), "No internet connection");
		}
	}

	public void onLoadWalksContainingStartedSuccess() {
		updateWalks();


	}

	public void onLoadWalksContainingStartedFailure(String error) {
		ToastUtils.showShort(getActivity(), error);
	}

	public void pageChangedToMe() {
		updateWalks();
	}

	private void updateWalks() {
		Portion portion;

		portion = ((SwiftoApplication) getActivity().getApplication())
				.getPortionsLoader().loadWalksForPortion(getPortionId(), this,
						true, getActivity());

		/*SyslogUtils.logEvent(getActivity(), "portion id  in Today Schedule - " + getPortionId(),
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);*/





		if (portion == null) {
			// do nothing - callback will handle loading
		} else if (portion.State.equals(EnumPortionState.EMPTY)) {

			/*SyslogUtils.logEvent(getActivity(), "value of portion   in Today Schedule - " + portion.toString(),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			ToastUtils.showLong(getActivity(), "port.State  is empty");*/
			showNoWalks();
		} else {
			/*SyslogUtils.logEvent(getActivity(), "value of portion   in Today Schedule - " + portion.toString(),
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);*/
			// do nothing - callback will handle loading
		}


	}

	private void fillWalks(ArrayList<Walk> source) {
		mWalksToday.clear();

		for (Walk walk : source) {
			mWalksToday.add(walk);
		}

		SyslogUtils.logEvent(getActivity(), "Length  of walk list on Daily  view   - " + mWalksToday.size(),
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		refreshAdapter();
	}

	private void refreshAdapter() {
		mAdapter.notifyDataSetChanged();
	}

	private void showNoWalks() {
		mListWalks.setVisibility(View.GONE);
		mLayoutNoWalks.setVisibility(View.VISIBLE);
	}

	private void showListWalks() {
		mListWalks.setVisibility(View.VISIBLE);
		mLayoutNoWalks.setVisibility(View.GONE);
	}

	public void walksLoaded(final ArrayList<Walk> walks, final String portionId) {
		// TODO: will fall, if loading starts at 23:59:59 and end in 00:00:05 -
		// days will switch. Or not will fall. But who walk dog at midnight??
		if (portionId.equals(getPortionId())) {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					Portion port = ((SwiftoApplication) getActivity()
							.getApplication()).getPortionsLoader()
							.getPortionById(portionId);

					if (port.State.equals(EnumPortionState.EMPTY)) {
						ToastUtils.showLong(getActivity(), "port.State  is empty");
						showNoWalks();
					} else {
						showListWalks();
						fillWalks(walks);
					}
				}
			});
		}
	}

	public void interrupt() {
		// do nothing
	}


	private final AtomicBoolean mMonthIsReloading = new AtomicBoolean(false);
	//private CalendarView calendar;
	private SwiftoApplication mApplication;

	private void loadForMonth() {
		mMonthIsReloading.set(true);



		if (ServiceUtils.isNetworkAvailable(getActivity())) {
			//TimeZone tzDefault = TimeZone.getDefault();
			TimeZone tzDefault = TimeZone.getTimeZone("GMT-4.00");
			Date now = new Date();
			int offset = tzDefault.getOffset(now.getTime());
			int offsetInMillis = offset / 1000;


			Date date= new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);

			String request = SwiftoRequestBuilder.buildGetWalksNumberPerMonth(SharedPreferencesHelper.getWalkerId(getActivity()), StringUtils.MONTH_NAME(month), offsetInMillis, year);

			new LoadForMonthAsyncTask(getActivity(), "Loading walks for month...", true).execute(request);
		} else {
			ToastUtils.showShort(getActivity(), "No internet connection");
			SyslogUtils.logEvent(getActivity(), "No internet connection on " + "trying to load month info", EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
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

		if (error != null) {
			ToastUtils.showShort(getActivity(), error);
			SyslogUtils.logEventWithResponce(getActivity(), "Month information loading error: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE, response);
		} else {

			Date date= new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int monthNum = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);

			SharedPreferencesHelper.saveMonthLoadingResponce(getActivity().getApplicationContext(),monthNum,response);

			if (getActivity() != null) {
				SharedPreferencesHelper.saveTimeLastLoading(getActivity().getApplicationContext(), System.currentTimeMillis());

				WalksForMonth walksForMonth = new GetWalksNumberPerMonthParser().parseWalksForMonth(response);






				walksForMonth.setYear(year);
				walksForMonth.setMonth(monthNum);

				SyslogUtils.logEvent(getActivity(), String.format(Locale.getDefault(), "Loaded walks for month %d, %d, response: %s", monthNum + 1, year, response), EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

				mApplication.getDbAdapter().insertMonthInfo(walksForMonth);


			}

		}

		if (((ScheduleActivity) getActivity()).needReloading()) {
			reload();
		} else {
			reload();
			// vivek hack
			// updateWalks();
		}
	}

}
