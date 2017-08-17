package com.haski.swifto.ui.mapOverlays;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MarkerOverlay extends Overlay {

	public MarkerOverlay(Bitmap marker) {
		super();
		
		mMarker = marker;
		
		mHalfWidth = marker.getWidth() / 2;
		mHalfHeight = marker.getHeight() / 2;
		
		mGeoPoint = new GeoPoint(0, 0);
		mPoint = new Point();
	}
	
	private Bitmap mMarker;
	private GeoPoint mGeoPoint;
	private Point mPoint;
	private int mHalfWidth;
	private int mHalfHeight;
	private int x;
	private int y;
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		mapView.getProjection().toPixels(mGeoPoint, mPoint);      
		
		x = mPoint.x - mHalfWidth;                                                   
		y = mPoint.y - mHalfHeight;                

		canvas.drawBitmap(mMarker, x, y, null);
		
		super.draw(canvas, mapView, shadow);
	}
	
	public void moveTo(GeoPoint point)
	{
		mGeoPoint = point;
	}
	
	public void clear()
	{
		mGeoPoint = new GeoPoint(0, 0);
		mPoint = new Point();
	}
}
