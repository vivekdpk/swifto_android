package com.haski.swifto.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.interfaces.ILoadWalksContainingStartedWaitable;
import com.haski.swifto.interfaces.WalksLoadedCallback;
import com.haski.swifto.model.EnumPortionState;
import com.haski.swifto.model.EnumWeekLoadStatus;
import com.haski.swifto.model.vo.Portion;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.util.CalendarUtils;
import com.haski.swifto.util.ServiceUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.ToastUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TomorrowScheduleFragment extends Fragment implements ILoadWalksContainingStartedWaitable, WalksLoadedCallback {
	
	public TomorrowScheduleFragment() {
	}

	public static Fragment newInstance() {
		return new TomorrowScheduleFragment();
	}

	//private SwiftoApplication mApplication;
	//private Context mContext;
	//private String mLastIdentifier;

	private ArrayList<Walk> mWalksTomorrow;

	private ListView mListWalks;
	private WalksListAdapter mAdapter;
	
	//private RelativeLayout mLayoutBottom;
	//private Button mBtReload;
	//private Button mBtSendLog;
	private RelativeLayout mLayoutNoWalks;
	private ILoadWalksContainingStartedWaitable thisLink;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		thisLink = this;
		
		if(((ScheduleActivity)getActivity()).SelectedFragmentIndex == 1) {
			if(((ScheduleActivity)getActivity()).needReloading()) {
				reload();
			} else {
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
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//mApplication = (SwiftoApplication) getActivity().getApplication();
		thisLink = this;
		
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_schedule_tomorrow, null);
		
		mListWalks = (ListView) root.findViewById(R.id.fragment_schedule_tomorrow_list);
		mWalksTomorrow = new ArrayList<Walk>();
		
		mAdapter = new WalksListAdapter(getActivity(), R.layout.list_item_walk_info, mWalksTomorrow);
		mListWalks.setAdapter(mAdapter);
		
		mLayoutNoWalks = (RelativeLayout) root.findViewById(R.id.fragment_schedule_tomorrow_layout_no_walks);
		
		return root;
	}
	
	public void reload() {
		SyslogUtils.logEvent(getActivity(), "Reload from Tomorrow", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		
		if(ServiceUtils.isNetworkAvailable(getActivity())) {
			String startedWalkUniqueId = SharedPreferencesHelper.getStartedWalkUniqueId(getActivity());

			if(startedWalkUniqueId.equals(SharedPreferencesHelper.DEFAULT_STARTED_WALK_UNIQUE_ID)) {
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
		updateWalks();
	}

	public void onLoadWalksContainingStartedFailure(String error) {
		try {
			SyslogUtils.logEvent(getActivity().getApplicationContext(), "Error loading started walk: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
			Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
		} catch(Exception e) {
			//handle error
		}
	}
	
	public void pageChangedToMe() {
		updateWalks();
	}
	
	
	private String getPortionId() {
		Calendar tm = CalendarUtils.DAY_MIDNIGHT(1);
		long timeMidnignt = tm.getTimeInMillis() / 1000;

		Calendar td = CalendarUtils.DAY_23_59(1);
		long timeTwentyThree = td.getTimeInMillis() / 1000;

		String walkIdentifier = String.format(Locale.getDefault(), "%d-%d", timeMidnignt, timeTwentyThree);
		
		return walkIdentifier;
	}
	
	public void updateWalks() {
		Portion portion = ((SwiftoApplication)getActivity().getApplication()).getPortionsLoader().loadWalksForPortion(getPortionId(), this, true, getActivity());
		
		if(portion == null) {
			//do nothing - callback will handle loading
		} else if(portion.State.equals(EnumPortionState.EMPTY)) {
			showNoWalks();
		} else {
			//do nothing - callback will handle loading
		}
	}
	
	private void fillWalks(ArrayList<Walk> source) {
		mWalksTomorrow.clear();
		
		for(Walk walk : source) {
			mWalksTomorrow.add(walk);
		}
		
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

	public void walksLoaded(ArrayList<Walk> walks, String portionId) {
		if(portionId.equals(getPortionId())) {
			Portion portion = ((SwiftoApplication)getActivity().getApplication()).getPortionsLoader().getPortionById(portionId);
			
			if(portion.State.equals(EnumPortionState.EMPTY)) {
				showNoWalks();
			} else {
				showListWalks();
				fillWalks(walks);
			}
		} else {
			//do nothing
		}
	}

	public void interrupt() {
		//do nothing
	}
}
