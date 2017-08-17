package com.haski.swifto.ui;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.haski.swifto.R;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.ui.mapOverlays.ActionsOverlay;
import com.haski.swifto.ui.mapOverlays.GeoPointedBitmap;
import com.haski.swifto.util.GeoUtils;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.ToastUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

public class OwnerMapActivity extends BaseActivity {

	/*@Override
	protected boolean isRouteDisplayed() {
		return false;
	}*/
	
	@Override
	public void onBackPressed() {
		finish();
	}
	
//	private MapView mapView;
	// Google Map
    private GoogleMap googleMap;
    ActionBar actionbar;
    protected Button btMenuList;
	
	@SuppressLint("NewApi") @Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		OwnerMapActivity.this.overridePendingTransition(R.anim.increase_05x_1x_fade_in, R.anim.increase_1x_3x_fade_out);
		
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_owner_map);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
		
		actionbar = getActionBar();

		if (actionbar == null) {

		} else {
			setSlidingActionBarEnabled(false);
			//Toast.makeText(getApplicationContext(), "Actionbar ok", 1).show();
			final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
					.inflate(R.layout.window_title, null);

			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setDisplayShowTitleEnabled(false);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setIcon(android.R.color.transparent);
			actionbar.setCustomView(actionBarLayout);
		}
		
		initViews();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SyslogUtils.logEvent(getApplicationContext(), "OwnerMap opened", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		initilizeMap();
		refreshViews();
	}
	
	protected Button btNavLeft;
	protected Button btNavRight;
	protected TextView tvTitle;

	private void initViews() {
		btNavLeft = (Button) findViewById(R.id.window_title_bt_left);
		btNavLeft.setText(getResources().getString(R.string.window_title_bt_dog_info));
		btNavLeft.setOnClickListener(btNavigateLeftClickListener);
		
		btNavRight = (Button) findViewById(R.id.window_title_bt_right);
		btNavRight.setText(getResources().getString(R.string.window_title_bt_schedule));
		btNavRight.setOnClickListener(btNavigateRightClickListener);
		
		tvTitle = (TextView) findViewById(R.id.window_title_txt_title);
		tvTitle.setText(getResources().getString(R.string.window_title_owner_map));
		
		btMenuList = (Button) findViewById(R.id.window_title_bt_slidemenu);
		// btMenuList.setOnClickListener(btMenuListClickListener);

		btMenuList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				toggle();
			}
		});
		
	/*	mapView = (MapView) findViewById(R.id.activity_owner_map_map_view);
		mapView.getController().setZoom(20);*/
		initilizeMap();
		
	}
	
	/**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                    R.id.activity_owner_map_map_view)).getMap();
 

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
				return;
            }
			else
			{
				googleMap.getUiSettings().setZoomControlsEnabled(true);
				googleMap.animateCamera( CameraUpdateFactory.zoomTo( 20.0f ) );
			}

        }
    }
	private void refreshViews() {
		ActionsOverlay overlay = new ActionsOverlay();
		
		Walk walk = ((SwiftoApplication)getApplication()).getWalkGetter().getCurrentWalk();
		
		try {
			GeoPoint location = walk.Location;

			//GeoPoint location = new GeoPoint(22, 75);
			
			GeoPointedBitmap geoPointBitmap = new GeoPointedBitmap(location.getLatitudeE6(), 
					location.getLongitudeE6(), BitmapFactory.decodeResource(getResources(), 
							R.drawable.pin_owner_small));
			overlay.addItem(geoPointBitmap);
			
			SyslogUtils.logEvent(getApplicationContext(), String.format(Locale.getDefault(), "Opened Onwer map at lat:%f, lng:%f", ((double)location.getLatitudeE6() / GeoUtils.MULTIPLEXOR), ((double)location.getLongitudeE6() / GeoUtils.MULTIPLEXOR)), EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);

			/*mapView.getOverlays().add(overlay);
			mapView.getController().animateTo(location);
			mapView.postInvalidate();*/
			
			//ToastUtils.showLong(getApplicationContext(), "location = "+location);
			//ToastUtils.showLong(getApplicationContext(), "location.getLatitudeE6() = "+location.getLatitudeE6());
			//ToastUtils.showLong(getApplicationContext(), "location.getLongitudeE6() = "+location.getLongitudeE6());
			
			// 22.6826233
			// 75.8696465
			
			LatLng latLng = new LatLng(location.getLatitudeE6()/1E6, 
					location.getLongitudeE6()/1E6);
			
			//LatLng latLng = new LatLng(location.getLatitudeE6(), 
					///location.getLongitudeE6());
			BitmapDescriptor btmpDesc = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), 
					R.drawable.pin_owner_small));
			googleMap.addMarker(new MarkerOptions().
					position(latLng).icon(btmpDesc));
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
			
		} catch(NullPointerException e) {			
			ToastUtils.showShort(getApplicationContext(), "Error opening owner map. Please, send log");
			SyslogUtils.logEvent(getApplicationContext(), "Error opening owner map: " + e.toString(), EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			finish();
			return;
		} catch(Exception e) {
			ToastUtils.showShort(getApplicationContext(), "Error opening owner map: " + e.toString());
			SyslogUtils.logEvent(getApplicationContext(), "Error opening owner map: " + e.toString(), EnumLogSeverity.ERROR, EnumLogType.INTERNAL_TYPE);
			finish();
			return;
		}
	}
	
	
	OnClickListener btNavigateLeftClickListener = new OnClickListener() {
		public void onClick(View v) {
			Intent dogInfoIntent = new Intent(getApplicationContext(), DogInfoActivity.class);
			
			finish();
			startActivity(dogInfoIntent);
		}
	};

	OnClickListener btNavigateRightClickListener = new OnClickListener() {
		public void onClick(View v) {
			Intent scheduleIntent = new Intent(getApplicationContext(), ScheduleActivity.class);
			scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finish();
			startActivity(scheduleIntent);
		}
	};
}
