package com.haski.swifto.ui.mapOverlays;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ActionsOverlay extends Overlay {

	public ActionsOverlay() {
		super();
		
		mOverlayItems = new ArrayList<GeoPointedBitmap>();
	}
	
	private ArrayList<GeoPointedBitmap> mOverlayItems;
	private Bitmap bmp;
	private Point point;
	private GeoPointedBitmap geoPointedBitmap;
	private int x;
	private int y;

	
	public void addItem(GeoPointedBitmap geoPointedBitmap) {
		mOverlayItems.add(geoPointedBitmap);
	}
	
	public void clear() {
		mOverlayItems.clear();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		for(int i = 0; i < mOverlayItems.size(); i++) {
			geoPointedBitmap = mOverlayItems.get(i);
			
			point = new Point();
			mapView.getProjection().toPixels(geoPointedBitmap, point);                                      

			bmp = geoPointedBitmap.getBitmap();
			x = point.x - bmp.getWidth() / 2;                                                   
			y = point.y - bmp.getHeight();                                                      
			canvas.drawBitmap(bmp, x, y, null);
		}
		
		super.draw(canvas, mapView, shadow);
	}
}
