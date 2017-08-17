package com.haski.swifto.ui;

import com.haski.swifto.R;
import com.haski.swifto.util.ToastUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class WebViewActivity extends BaseActivity {

	private ActionBar actionbar;
	TextView tv;
	protected Button btNavLeft ,btMenuList;
	protected Button btNavRight;
	protected TextView tvTitle;
	OnClickListener btNavigateLeftClickListener;
	private WebView webView;
	public  boolean ispagefinished = false;

	public static String link = "";
	public boolean  isoncreate = false;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		ispagefinished = false;
		isoncreate = true;
		// set the Above View

		//ToastUtils.showLong(getApplicationContext(), "onResume isoncreate " + isoncreate);

		//ToastUtils.showLong(getApplicationContext(), "Web View on create called ");

		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_web_view);

		webView = (WebView) findViewById(R.id.webview);
		String text = getIntent().getStringExtra("link");

		//ToastUtils.showLong(getApplicationContext()," onCreate link - " + link);

		startWebView(link);
		//
		setSlidingActionBarEnabled(false);

		getSlidingMenu().toggle();

		actionbar = getActionBar();
		if (actionbar == null) {

		} else {
			actionbar.setDisplayHomeAsUpEnabled(false);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setDisplayShowTitleEnabled(false);
			actionbar.setDisplayHomeAsUpEnabled(false);
			actionbar.setIcon(android.R.color.transparent);
			
			final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
					.inflate(R.layout.window_title, null);
			actionbar.setCustomView(actionBarLayout);
			//actionbar.setCustomView(R.layout.window_title);

			btNavLeft = (Button) findViewById(R.id.window_title_bt_left);
			btNavLeft.setText(getResources().getText(R.string.window_title_bt_schedule));
			
			btNavLeft.setOnClickListener(btNavigateLeftClickListener);

			btMenuList = (Button) findViewById(R.id.window_title_bt_slidemenu);
			//btMenuList.setOnClickListener(btMenuListClickListener);
			
			btMenuList.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					toggle();
				}
			});

			if(text.equals("forgot"))
			{
				btNavLeft.setVisibility(View.GONE);
				btMenuList.setVisibility(View.GONE);
			}
			
			btNavLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent scheduleIntent = new Intent(getApplicationContext(), ScheduleActivity.class);
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

	public void startWebView(String url) {
		// Create new webview Client to show progress dialog
		// When opening a url or click on link

		webView.setWebViewClient(new WebViewClient() {
			ProgressDialog progressDialog;

			// If you will not use this method url links are opeen in new brower
			// not in webview
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			// Show loader on url load
			public void onLoadResource(WebView view, String url) {
				if (progressDialog == null && !ispagefinished) {
					// in standard case YourActivity.this
					progressDialog = new ProgressDialog(WebViewActivity.this);
					progressDialog.setMessage("Loading...");
					progressDialog.show();
				}
			}

			public void onPageFinished(WebView view, String url) {


				//ToastUtils.showLong(getApplicationContext(),"  onPageFinished called - ");
				try {
					if (progressDialog != null && progressDialog.isShowing()) {
						ispagefinished = true;
						progressDialog.dismiss();
						progressDialog = null;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}




		});

		// Javascript inabled on webview
		webView.getSettings().setJavaScriptEnabled(true);

		// Other webview options
		/*
		 * webView.getSettings().setLoadWithOverviewMode(true);
		 * webView.getSettings().setUseWideViewPort(true);
		 * webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		 * webView.setScrollbarFadingEnabled(false);
		 * webView.getSettings().setBuiltInZoomControls(true);
		 */

		/*
		 * String summary =
		 * "<html><body>You scored <b>192</b> points.</body></html>";
		 * webview.loadData(summary, "text/html", null);
		 */

		// Load url in webview
		webView.loadUrl(url);
	}

	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			// Let the system handle the back button
			super.onBackPressed();
		}
	}

	public void onResume() {
		super.onResume();
		ispagefinished = false;
//		ToastUtils.showLong(getApplicationContext(), "onResume isoncreate " + isoncreate);
//		if(isoncreate)
//		{
//			return;
//		}
		String text = getIntent().getStringExtra("link");

		//ToastUtils.showLong(getApplicationContext()," onResume link - " + link);

		startWebView(link);
//		//
//		setSlidingActionBarEnabled(false);
//
		getSlidingMenu().toggle();


		//ToastUtils.showLong(getApplicationContext(), "Web View on Resume called ");
	}

	}
