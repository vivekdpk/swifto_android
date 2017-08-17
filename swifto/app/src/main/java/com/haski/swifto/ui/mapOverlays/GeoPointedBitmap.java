package com.haski.swifto.ui.mapOverlays;

import android.graphics.Bitmap;

import com.google.android.maps.GeoPoint;

public class GeoPointedBitmap extends GeoPoint {

	public GeoPointedBitmap(int lat, int lng, Bitmap bitmap) {
		super(lat, lng);
		
		mBitmap = bitmap;
	}
	
	private Bitmap mBitmap;
	public Bitmap getBitmap() {
		return mBitmap;
	}
}
