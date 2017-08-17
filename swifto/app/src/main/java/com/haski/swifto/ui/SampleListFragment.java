package com.haski.swifto.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.model.EnumWeekLoadStatus;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.ToastUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SampleListFragment extends ListFragment {

	ListView lv;
	//public String urlHead = "https://next.swifto.com/";
	public String urlHead = "https://swifto.com/";

/*	public String[] menuList = { "Contacts","My Dashboard", "Available Walks", "Availability",
			"Time off", "Invite", "Keys",
			"Share", "Report App Error" , "Walks Map"};

	public String[] urlList = {"", urlHead+"dashboard", urlHead+"dashboard/walks",
			urlHead+"dashboard/availability", urlHead+"dashboard/vacation",
			urlHead+"dashboard/invite", urlHead+"dashboard/keys", "", "",
			urlHead+"dashboard/walks-map" };*/


	public String[] menuList = { "Contacts","My Dashboard", "Available Walks", "Availability",
			"Time off",  "Keys",
			"Share", "Report App Error" , "Log Out" };

	public String[] urlList = {"", urlHead+"dashboard", urlHead+"dashboard/walks",
			urlHead+"dashboard/availability", urlHead+"dashboard/vacation",
			 urlHead+"dashboard/keys", "", ""  , ""
			 };
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// lv = getListView();
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());
		// for (int i = 0; i < 20; i++) {
		// adapter.add(new SampleItem("Sample List",
		// android.R.drawable.ic_menu_search));
		// }

		adapter.add(new SampleItem("Contacts", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("My Dashboard", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Available Walks", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Availability", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Time off", android.R.drawable.ic_menu_search));
		//adapter.add(new SampleItem("Invite", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Keys", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Share", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Report App Error", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Log Out", android.R.drawable.ic_menu_search));
		//adapter.add(new SampleItem("Walks Map", android.R.drawable.ic_menu_search));
		
		
		
		// lv.setAdapter(adapter);

		/*
		 * 
		 * My dashboard (which should take them to this page:
		 * https://next.swifto.com/dashboard
		 * 
		 * Available Walks (which should take them to this page:
		 * https://next.swifto.com/dashboard/walks)
		 * 
		 * Availability (which should take them to this page:
		 * https://next.swifto.com/dashboard/availability)
		 * 
		 * Time off (which should take them to this page:
		 * (https://next.swifto.com/dashboard/vacation)
		 * 
		 * Invite which should take them to this page:
		 * https://next.swifto.com/dashboard/invite)
		 * 
		 * Keys (which should take them to this page:
		 * https://next.swifto.com/dashboard/keys)
		 * 
		 */
		setListAdapter(adapter);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Do your stuff..
		// Toast.makeText(getActivity(), urlList[position], 1).show();
		//Log.i("contact", "start contact activity");
		if (menuList[position].equals("Share")) {
			startShareActivity();
		}

		else if (menuList[position].equals("Log Out")) {
			logout();
		}
		else if (menuList[position].equals("Report App Error")) {
			sendEmailLog();
		}
		else if (menuList[position].equals("Contacts")) {
			Log.i("contact", "start contact activity");
			
			startContactActivity();
		}
		else {
			//ToastUtils.showLong(getActivity(), urlList[position]);
			startWebViewActivity(urlList[position]);
		}


	}

	public void logout() {
		SyslogUtils.logEvent(getActivity(), "Logout app because logout button pressed",
				EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

		if (SharedPreferencesHelper.getWalkIsInProcess(getActivity())) {
			SyslogUtils.logEvent(getActivity(), "Try to Log out while walk is in progress!",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
			Toast.makeText(getActivity(), "You have walk in progress!!!", Toast.LENGTH_SHORT).show();
		} else {
			((SwiftoApplication) getActivity().getApplication()).getPortionsLoader().reloadAllPortions();// context,
			// dbAdapter);//
			((SwiftoApplication) getActivity().getApplication()).clearWalksForMonths();
			SharedPreferencesHelper.saveWeekLoadStatus(getActivity(), EnumWeekLoadStatus.NOT_LOADED);
			SharedPreferencesHelper.deleteGpsSleepTime(getActivity());
			SharedPreferencesHelper.deleteMaxAttemptsToFindPoint(getActivity());
			SharedPreferencesHelper.deleteThreshold(getActivity());
			SharedPreferencesHelper.deleteSelectedDay(getActivity());
			SharedPreferencesHelper.deleteSelectedMonth(getActivity());
			SharedPreferencesHelper.deleteSelectedYear(getActivity());
			((SwiftoApplication) getActivity().getApplication()).deleteWalker();

			SyslogUtils.logEvent(getActivity(), "User logged out, gps config reset",
					EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			startLoginActivity();

		}
	}

	private void startLoginActivity() {
		getActivity().finish();

		Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
		startActivity(loginIntent);
	}

	private void startWebViewActivity(String Text) {
		// getActivity().finish();

		Intent scheduleIntent = new Intent(getActivity(), WebViewActivity.class);
		scheduleIntent.putExtra("link", Text);
		WebViewActivity.link = Text;
		scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(scheduleIntent);
	}

	private void startShareActivity() {
		// getActivity().finish();

		Intent scheduleIntent = new Intent(getActivity(), ShareActivity.class);
		scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(scheduleIntent);
	}
	
	private void startContactActivity() {
		// getActivity().finish();
		Log.i("contact", "start contact activity");
		Intent scheduleIntent = new Intent(getActivity(), ContactActivity.class);
		scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(scheduleIntent);
	}

	private class SampleItem {
		public String tag;
		public int iconRes;

		public SampleItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			icon.setVisibility(View.GONE);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
			return convertView;
		}
	}
	
	
	public void sendEmailLog() {
		try {
			File toSend = SyslogUtils.readLastEntries(getActivity());
			SyslogUtils.clearLogFile(getActivity());

			Intent intentEmail = new Intent(Intent.ACTION_SEND);
			intentEmail.setType("text/message");

			// TODO: change in release!
			// intentEmail.putExtra(Intent.EXTRA_EMAIL, new
			// String[]{"n1ck_kharkov@mail.ru"});
			intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"apploggs@swifto.com",
					"tiwarivivek2217@gmail.com", "swiftotest@gmail.com"});
//		intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {
//				"tiwarivivek2217@gmail.com","swiftotest@gmail.com"});
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
		catch (Exception e)
		{
			ToastUtils.showLong(getActivity(),"Exception is - "+e.toString());

			SyslogUtils.logEvent(getActivity(),"sendEmailLog Exception is - "+e.toString(), EnumLogSeverity.ERROR,
					EnumLogType.SERVER_TYPE);
		}

	}
}
