package com.haski.swifto.model;

import com.haski.swifto.model.vo.GeoPointExt;


public class GeoPointExtWithData extends GeoPointExt {

	public GeoPointExtWithData(int lat, int lng) {
		super(lat, lng);
	}

	public GeoPointExtWithData(int lat, int lng, long timestamp) {
		super(lat, lng, timestamp);
	}
	
	private String mData;
	public void setData(String mData) {
		this.mData = mData;
	}
	public String getData() {
		return mData;
	}
}
