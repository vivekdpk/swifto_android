package com.haski.swifto.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haski.swifto.R;


public class NavigationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		setContentView(R.layout.activity_splash);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
		
		initViews();
	}
	
	
	protected Button btNavLeft;
	protected Button btNavRight;
	protected TextView tvTitle;
	
	private void initViews() {
		btNavLeft = (Button) findViewById(R.id.window_title_bt_left);
		btNavRight = (Button) findViewById(R.id.window_title_bt_right);
		tvTitle = (TextView) findViewById(R.id.window_title_txt_title);
	}
}
