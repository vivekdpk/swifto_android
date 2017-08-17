package com.haski.swifto.ui.mapOverlays;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PathOverlay extends Overlay {

	public PathOverlay() {
		super();
		
		mPaintPaths = new Paint();
		mPaintPaths.setAntiAlias(true);
		mPaintPaths.setColor(Color.argb(255, 20, 255, 20));
		mPaintPaths.setStrokeCap(Paint.Cap.ROUND);
		mPaintPaths.setStrokeJoin(Paint.Join.ROUND);
		mPaintPaths.setStrokeWidth(5);
		mPaintPaths.setStyle(Paint.Style.STROKE);
		
		mPoints = new ArrayList<GeoPoint>();
		p = new Path();
		startPoint = new Point();
		point = new Point();
	}
	
	private ArrayList<GeoPoint> mPoints;
	private Paint mPaintPaths;
	
	private Path p;
	private Point startPoint;
	private Point point;
	private Projection projection;
	

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		projection = mapView.getProjection();
		
		if (mPoints.size() > 1) {
			p.reset();
			//startPoint = new Point();
			projection.toPixels(mPoints.get(0), startPoint);
			p.moveTo(startPoint.x, startPoint.y);
			
			for(int i = 1; i < mPoints.size(); i++) {
				point = new Point();
				projection.toPixels(mPoints.get(i), point);
				p.lineTo(point.x, point.y);
			}

			canvas.drawPath(p, mPaintPaths);
		}
		super.draw(canvas, mapView, shadow);
	}
	
	public void addItem(GeoPoint point) {
		mPoints.add(point);
	}
	
	public void clear() {
		mPoints.clear();
	}
}
