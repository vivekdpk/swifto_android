package com.haski.swifto.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.interfaces.IDialogResultWaitable;
import com.haski.swifto.model.EnumWalkStatuses;
import com.haski.swifto.model.vo.GPSConfig;
import com.haski.swifto.model.vo.Walker;
import com.haski.swifto.model.vo.WalkerActiveRouteInfo;
import com.haski.swifto.model.vo.json.GetGpsConfigParser;
import com.haski.swifto.model.vo.json.GetWalkStateParser;
import com.haski.swifto.model.vo.json.GetWalkerActiveRoutesParser;
import com.haski.swifto.server.ServerRequestAsynkTask;
import com.haski.swifto.server.SwiftoRequestBuilder;
import com.haski.swifto.util.AlertUtils;
import com.haski.swifto.util.GSONUtils;
import com.haski.swifto.util.ServiceUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.StringUtils;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.TaskErrorChecker;
import com.haski.swifto.util.ToastUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

import java.util.ArrayList;
import java.util.Locale;


public class LoginActivity extends Activity {
	
	private Context mContext;
	private Walker mWalker;
	
	private EditText mInputUsername;
	private EditText mInputPassword;
	
	private Button mBtLogin;
	private TextView frgtPass;
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;

		setContentView(R.layout.activity_login);

		initViews();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mContext = this;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	private void initViews() {
		mInputUsername = (EditText) findViewById(R.id.activity_login_input_username);
		mInputPassword = (EditText) findViewById(R.id.activity_login_input_password);
		
		Walker walker = ((SwiftoApplication)getApplication()).getWalker();
		
		if(walker != null) {
			mInputUsername.setText(walker.username);
			mInputPassword.setText(walker.password);
		}
		
		mBtLogin = (Button) findViewById(R.id.activity_login_button_login);
		mBtLogin.setOnClickListener(btLoginClick_listener);

		frgtPass = (TextView) findViewById(R.id.activity_login_text_fgtpass);
		frgtPass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent scheduleIntent = new Intent(LoginActivity.this, WebViewActivity.class);
				scheduleIntent.putExtra("link", "forgot");

				WebViewActivity.link =  "https://swifto.com/user/password";
				scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(scheduleIntent);
			}
		});
	}
	
	OnClickListener btLoginClick_listener = new OnClickListener() {
		public void onClick(View v) {
			if(ServiceUtils.isNetworkAvailable(getApplicationContext())) {
				String userName = mInputUsername.getText().toString();
				String password = mInputPassword.getText().toString();

				if(inputFieldsValid(userName, password)) {
					mWalker = ((SwiftoApplication)getApplication()).getWalker();
					
					if(mWalker == null) {
						mWalker = new Walker();
					}
					
					userName = StringUtils.removeDuplicateSpaces(userName);
					userName = StringUtils.changeUnm(userName);
					password = StringUtils.removeDuplicateSpaces(password);
					
					mWalker.username = userName;
					mWalker.password = password;
					
					String request = SwiftoRequestBuilder.buildLoginRequest(userName, password);
					
					//ToastUtils.showLong(getApplicationContext(), "request = "+request);
					new LoginRequestAsyncTask(mContext, getResources().getString(R.string.dialog_login), true).execute(request);
				} else {
					ToastUtils.showShort(getApplicationContext(), "Check inputs please");
				}
			} else {
				ToastUtils.showShort(getApplicationContext(), "No internet connection");
				SyslogUtils.logEvent(getApplicationContext(), "No internet connection on login", EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
			}
		}
	};
	
	
	private boolean inputFieldsValid(String username, String password) {
		return !(username == null || username.equals("") || password == null || password.equals(""));

	}
	
	class LoginRequestAsyncTask extends ServerRequestAsynkTask {
		public LoginRequestAsyncTask(Context ctx, String message,
				boolean showProgress) {
			super(ctx, message, showProgress);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//ToastUtils.showLong(getApplicationContext(), "result  = "+result);
			parseLoginResponse(result);
		}
	}
	
	private void parseLoginResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);
		
		if(error != null) {
			if(error.equalsIgnoreCase("authorization error"))
			{
				//ToastUtils.showLong(getApplicationContext(), "This app is for the Swifto Dog Walkers only. If you were not hired as a Swifto Dog Walker you do not have access to the app");
				AlertUtils.showYes(this, "",
						"This app is for the Swifto Dog Walkers only. If you were not hired as a Swifto Dog Walker you do not have access to the app",
						"OK", new IDialogResultWaitable() {
							public void reactOnYes() {
								// do nothing
								mInputUsername.setText("");
								mInputPassword.setText("");
							}

							public void reactOnNo() {
								// do nothing
							}
						});
			}
			else{
				ToastUtils.showShort(getApplicationContext(),error);
			}
			
			SyslogUtils.logEventWithResponce(getApplicationContext(), "Login error with response: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE,response);
		} else {
			Walker preparedWalker = GSONUtils.parseWalker(response);
			preparedWalker.username = mWalker.username;
			preparedWalker.password = mWalker.password;
			
			((SwiftoApplication)getApplication()).getDbAdapter().insertWalker(preparedWalker);
			
			SharedPreferencesHelper.saveWalkerId(getApplicationContext(), preparedWalker.id);
			
			SyslogUtils.logEvent(getApplicationContext(), "User " + preparedWalker.username + " logged in", EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
			
			getAndSaveGpsConfig();
		}
	}
	
	private void getAndSaveGpsConfig() {
		if(ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			String model = Build.MODEL;
			String osVersion = Build.VERSION.RELEASE;
			
			String request = SwiftoRequestBuilder.buildGetGpsConfigRequest(model, osVersion);
			
			new GetGpsConfigTask(mContext, "Getting GPS configuration...", true).execute(request);
		} else {
			SyslogUtils.logEvent(getApplicationContext(), "No internet connection on getting GPS configuration", EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
			Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
		}
	}
	
	class GetGpsConfigTask extends ServerRequestAsynkTask {
		public GetGpsConfigTask(Context ctx, String message,
				boolean showProgress) {
			super(ctx, message, showProgress);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			parseGetGpsConfigResponse(result);
		}
	}
	
	private void parseGetGpsConfigResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);
		
		if(error != null) {
			Toast.makeText(getApplication(), "gps error = "+error, Toast.LENGTH_SHORT).show();
			//Log.e("login error ", error);
			SyslogUtils.logEvent(getApplicationContext(), "Loading info about active routes error: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
		} else {
			GPSConfig config = new GetGpsConfigParser().parseGpsConfig(response);
			
			String toLog = String.format(Locale.getDefault(), "Received GPS Config: gpsSleepTime=%d, maxAttemptsToFindPoints=%d, threshold=%f", config.GpsSleepTime, config.MaxAttemptsToFindPoint, config.Threshold);
			
			SyslogUtils.logEvent(getApplicationContext(), toLog, EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
			
			SharedPreferencesHelper.saveGpsSleepTime(getApplicationContext(), config.GpsSleepTime);
			SharedPreferencesHelper.saveMaxAttemptsToFindPoint(getApplicationContext(), config.MaxAttemptsToFindPoint);
			SharedPreferencesHelper.saveThreshold(getApplicationContext(), config.Threshold);
			SharedPreferencesHelper.saveS3Key(getApplicationContext(), config.s3Key);
			SharedPreferencesHelper.saveS3Secret(getApplicationContext(), config.s3Secret);
			
			checkWalkerActiveRoutes();
		}
	}
	
	private void checkWalkerActiveRoutes() {
		if(ServiceUtils.isNetworkAvailable(getApplicationContext())) {
			String request = SwiftoRequestBuilder.buildGetWalkerActiveRoutes(SharedPreferencesHelper.getWalkerId(getApplicationContext()));
			new GetWalkerActiveRoutesTask(this, "Loading your active walks info...", true).execute(request);
		} else {
			ToastUtils.showShort(getApplicationContext(), "No internet connection");
			SyslogUtils.logEvent(getApplicationContext(), "No internet connection on " + "check walker active routes", EnumLogSeverity.WARNING, EnumLogType.HARDWARE_TYPE);
		}
	}
	
	class GetWalkerActiveRoutesTask extends ServerRequestAsynkTask {
		public GetWalkerActiveRoutesTask(Context ctx, String message,
				boolean showProgress) {
			super(ctx, message, showProgress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			parseWalkerActiveRoutesResponse(result);
		}
	}
	
	private void parseWalkerActiveRoutesResponse(String response) {
		String error = TaskErrorChecker.getErrorInString(response);
		
		if( error != null ) {
			Toast.makeText(getApplication(), error, Toast.LENGTH_SHORT).show();
			SyslogUtils.logEvent(getApplicationContext(), "Loading info about active routes error: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
		} else {
			parseRoutes(response);
		}
	}
	
	private void parseRoutes(String source) {
		if(isFinishing()) {
			SyslogUtils.logEvent(getApplicationContext(), "Parsing routes after Login activity had finished. Silently returning...", EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			return;
		}
		
		final ArrayList<WalkerActiveRouteInfo> routeInfos = new GetWalkerActiveRoutesParser().parseGetWalkerActiveRoutes(source);
		
		//there are no active walks
		if(routeInfos.size() == 0) {
			SyslogUtils.logEvent(getApplicationContext(), "Loaded info about routes - there is no started walks", EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
			((SwiftoApplication)getApplication()).startupCleaning();
			
			startScheduleActivity();
		} else {

			SyslogUtils.logEvent(this, "Loaded info about routes - walker has started walk (s)  active rout - " + source, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
			
			findViewById(R.id.activity_login_root).post(new Runnable() {
				public void run() {
					AlertUtils.showYes(mContext, "Information", "You have started walk (s)! All of them wil be stopped when you start a new walk", "Go to schedule", new IDialogResultWaitable() {
						public void reactOnYes() {
							startScheduleActivity();
						}
						
						public void reactOnNo() {
							//do nothing
						}
					});
				}
			});
		}
	}
	
	private ArrayList<String> mWalkIdsToStop;
	
	private void getOneWalkState() {
		if(mWalkIdsToStop.size() > 0) {
			String walkIdToStop = mWalkIdsToStop.get(0);
			
			String request = SwiftoRequestBuilder.buildGetWalkState(walkIdToStop);
			
			new GetWalkStateTask(walkIdToStop, this, "Getting walk state...", true).execute(request);
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
		
		if(error != null) {
			SyslogUtils.logEvent(getApplicationContext(), "Error getting walk state: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
			Toast.makeText(this, "Error getting walk state: " + error, Toast.LENGTH_SHORT).show();
		} else {
			//TODO: parse and check walk state
			String walkState = new GetWalkStateParser().parseGetWalkState(response);
			
			if(walkState.equals(EnumWalkStatuses.STARTED)) {
				stopOneWalk(walkId);
			} else if(walkState.equals(EnumWalkStatuses.COMPLETED)) {
				int index = mWalkIdsToStop.indexOf(walkId);
				SyslogUtils.logEvent(getApplicationContext(), "One of the active walks stopped", EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
				
				if(index != -1) {
					mWalkIdsToStop.remove(index);
					getOneWalkState();
				}
			}
		}
	}
	
	private void stopOneWalk(String walkId) {
		if(mWalkIdsToStop.size() > 0) {
			String walkIdToStop = walkId;

			long walkofftime = SharedPreferencesHelper.getWalkOffTimeExact(walkId, getApplicationContext());
			String request = SwiftoRequestBuilder.buildStopWalk(SharedPreferencesHelper.getWalkerId(this), walkIdToStop , walkofftime);
			
			new StopWalkTask(walkIdToStop, this, "Stopping walk...", true).execute(request);
		} else {
			//all stopped, go to schedule
			startScheduleActivity();
		}
	}
	
	class StopWalkTask extends ServerRequestAsynkTask {
		private String mWalkId;
		
		public StopWalkTask(String walkId, Context ctx, String message, boolean showProgress) {
			super(ctx, message, showProgress);
			
			mWalkId = walkId;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			parseStopWalkResponse(result, mWalkId);
		}
	}
	
	private void parseStopWalkResponse(String response, String walkId) {
		String error = TaskErrorChecker.getErrorInString(response);
		
		if(error != null) {
			Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
			SyslogUtils.logEvent(getApplicationContext(), "Stopping one of active walks error: " + error, EnumLogSeverity.ERROR, EnumLogType.SERVER_TYPE);
		} else {
			SyslogUtils.logEvent(getApplicationContext(), "One of the active walks stopped", EnumLogSeverity.INFORMATIONAL, EnumLogType.SERVER_TYPE);
			
			int index = mWalkIdsToStop.indexOf(walkId);
			
			if(index != -1) {
				mWalkIdsToStop.remove(index);
				
				getOneWalkState();
			}
		}
	}
	
	private void startScheduleActivity() {
		finish();
		
		Intent scheduleIntent = new Intent(getApplicationContext(), ScheduleActivity.class);
		scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(scheduleIntent);
	}
}
