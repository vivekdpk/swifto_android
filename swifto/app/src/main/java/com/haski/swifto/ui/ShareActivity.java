package com.haski.swifto.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

import com.haski.swifto.R;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends BaseActivity {

	private ActionBar actionbar;
	TextView tv;
	protected Button btNavLeft,btMenuList;
	protected Button btNavRight;
	protected TextView tvTitle;

	private ShareImageAdapter imageAdapter;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
		setContentView(R.layout.activity_share);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		imageAdapter = new ShareImageAdapter(this);
		gridview.setAdapter(imageAdapter);

		String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		String targetPath = ExternalStorageDirectoryPath + "/swiftoImages";

		Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();

			FileFilter dateFilter = new FileFilter() {
				public boolean accept(File file) {

					long mills = System.currentTimeMillis() - file.lastModified();
					int Hours = (int) mills/(1000 * 60 * 60);
					int Mins =(int) mills / (1000*60);


					SyslogUtils.logEvent(getApplicationContext(), "DIFF -  " +Hours + ":" + Mins,
							EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);



					if (file.isFile() && Hours < 2) {
						// filters files whose size greater than 3MB
						return true;
					} else {
						return false;
					}
				}
			};

			File targetDirector = new File(targetPath);

		//File[] files = targetDirector.listFiles(dateFilter);
			File[] files = targetDirector.listFiles();
		for (File file : files) {

			if(file.getAbsolutePath().contains(".noMedia"))
			{

			}
			else {
				imageAdapter.add(file.getAbsolutePath());
			}
		}

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				try {
					final String path = imageAdapter.getItem(arg2);
					final Dialog dialog = new Dialog(ShareActivity.this);
					dialog.setContentView(R.layout.dialod_image_confirm);
					dialog.setTitle("Share Photo");
					final ImageView image1 = (ImageView) dialog.findViewById(R.id.image);
					TextView tv = (TextView) dialog.findViewById(R.id.dialog_text);
					tv.setVisibility(View.GONE);
					BitmapFactory.Options options = new BitmapFactory.Options();

					// down sizing image as it throws OutOfMemory Exception for
					// larger
					// images
					//options.inSampleSize = 8;
					final Bitmap bitmap = BitmapFactory.decodeFile(path, options);
					image1.setImageBitmap(bitmap);

					Button ok, cancle, rotate;
					ok = (Button) dialog.findViewById(R.id.dialog_ButtonOK);
					cancle = (Button) dialog.findViewById(R.id.dialog_ButtonCancle);
					rotate = (Button) dialog.findViewById(R.id.dialog_Rotate);

					rotate.setVisibility(View.GONE);
					ok.setText("Share");

					ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent share = new Intent(Intent.ACTION_SEND);
							share.setType("image/*");
							File imageFileToShare = new File(path);

							Uri uri = Uri.fromFile(imageFileToShare);
							share.putExtra(Intent.EXTRA_STREAM, uri);

							startActivity(Intent.createChooser(share, "Share Image!"));
						}
					});

					cancle.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							SyslogUtils.logEvent(getApplicationContext(), "\"No\" pressed",
									EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
							dialog.dismiss();
						}
					});

					dialog.show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "error = "+e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}

		});

		setSlidingActionBarEnabled(false);

		actionbar = getActionBar();
		if (actionbar == null) {

		} else {
			actionbar.setDisplayHomeAsUpEnabled(false);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setDisplayHomeAsUpEnabled(false);
			actionbar.setCustomView(R.layout.window_title);
			actionbar.setIcon(android.R.color.transparent);

			btNavLeft = (Button) findViewById(R.id.window_title_bt_left);
			btNavLeft.setText(getResources().getText(R.string.window_title_bt_schedule));

			btMenuList = (Button) findViewById(R.id.window_title_bt_slidemenu);
			//btMenuList.setOnClickListener(btMenuListClickListener);
			
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
	}catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Toast.makeText(getApplicationContext(), "error = "+e.getMessage(), Toast.LENGTH_LONG).show();
	}
		
	}
}
