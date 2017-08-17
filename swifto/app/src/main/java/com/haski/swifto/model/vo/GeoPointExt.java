package com.haski.swifto.model.vo;

import com.google.android.maps.GeoPoint;

/**
 * GeoPoint with timestamp.
 * @author n1k1ch
 */
public class GeoPointExt extends GeoPoint {

	public GeoPointExt(int lat, int lng) {
		super(lat, lng);
		mTimestamp = System.currentTimeMillis();
	}

	public GeoPointExt(int lat, int lng, long timestamp) {
		super(lat, lng);
		mTimestamp = timestamp;
	}
	
	private long mTimestamp;
	public long getTimestamp() {
		return mTimestamp;
	}
	public void setTimestamp(long value) {
		mTimestamp = value;
	}
}
