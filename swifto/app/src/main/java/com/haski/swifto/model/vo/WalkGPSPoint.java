package com.haski.swifto.model.vo;

public class WalkGPSPoint extends GeoPointExt {

	public WalkGPSPoint(int lat, int lng) {
		super(lat, lng);
		
		Type = "";
		WalkId = "";
		Status = "";
		MD5 = "";
		Accuracy = 0.0f;
	}
	
	public WalkGPSPoint(int lat, int lng, long timestamp) {
		super(lat, lng, timestamp);
		
		Type = "";
		WalkId = "";
		Status = "";
		MD5 = "";
		Accuracy = 0.0f;
	}
	
	public String Type;
	public String WalkId;
	public String Status;
	public String MD5;
	public float Accuracy;
}
