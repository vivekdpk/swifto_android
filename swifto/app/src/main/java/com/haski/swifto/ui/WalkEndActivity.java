package com.haski.swifto.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.haski.swifto.R;

public class WalkEndActivity extends BaseActivity {
	//private SwiftoApplication mApplication;
	
	@Override
	public void onBackPressed() {
		Toast.makeText(this, "Please, don't leave this screen until all walk finishing tasks will be done", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_walk_end);
		
		//mApplication = (SwiftoApplication) getApplication();
		setSlidingActionBarEnabled(false);
		initViews();
	}
	
	
	private Button mBtSendData;
	private Button mBtSendMessage;
	private Button mBtStopWalk;
	private Button mBtTakePhoto;
	
	private void initViews() {
		mBtSendData = (Button) findViewById(R.id.activity_walk_end_bt_send_data);
		mBtSendData.setOnClickListener(btSendDataClickListener);
		
		mBtSendMessage = (Button) findViewById(R.id.activity_walk_end_bt_send_message);
		mBtSendMessage.setOnClickListener(btSendMessageClickListener);
		
		mBtStopWalk = (Button) findViewById(R.id.activity_walk_end_bt_stop_walk);
		mBtStopWalk.setOnClickListener(btStopWalkClickListener);
		
		mBtTakePhoto = (Button) findViewById(R.id.activity_walk_end_bt_take_photo);
		mBtTakePhoto.setOnClickListener(btTakePhotoClickListener);
	}
	
	OnClickListener btSendDataClickListener = new OnClickListener() {

		public void onClick(View v) {
		}
	};

	OnClickListener btSendMessageClickListener = new OnClickListener() {
		public void onClick(View v) {
		}
	};
	OnClickListener btStopWalkClickListener = new OnClickListener() {
		public void onClick(View v) {
		}
	};
	OnClickListener btTakePhotoClickListener = new OnClickListener() {
		public void onClick(View v) {
		}
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		refreshViews();
	}
	
	private void refreshViews() {
		boolean photoUploaded = false;
		boolean allDataSent = false;
		boolean messageSent = false;
		
		if(photoUploaded) {
			mBtTakePhoto.setVisibility(View.GONE);
		} else {
			mBtTakePhoto.setVisibility(View.VISIBLE);
		}

		if(allDataSent) {
			mBtSendData.setVisibility(View.GONE);
		} else {
			mBtSendData.setVisibility(View.VISIBLE);
		}
		
		if(messageSent) {
			mBtSendMessage.setVisibility(View.GONE);
		} else {
			mBtSendMessage.setVisibility(View.VISIBLE);
		}
	}
}
