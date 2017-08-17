package com.haski.swifto.model;

import com.haski.swifto.model.vo.GeoPointExt;
import com.haski.swifto.model.vo.walk.Walk;

import java.util.ArrayList;

public class StartedWalk {
	
	public StartedWalk() {
		mPoints = new ArrayList<GeoPointExt>();
		mPointsMeet = new ArrayList<GeoPointExtWithData>();
		mPointsPoo = new ArrayList<GeoPointExtWithData>();
		
		//mStartTime = System.currentTimeMillis();
		
		/*mPhotoUploaded = false;
		mIsMessageSent = false;
		mIsCompleted = false;*/
		
		mIsCompleted = false;
		mIsMessageSent = false;
		mIsStopWalkSent = false;
		mPhotoUploaded = false;
		mPhotoUploadSkipped = false;
		mPhotoUploadTriedOnce = false;
		mSendUnsentRequests = false;
		mUnsentRequestsSkipped = false;
	}
	
	/*public StartedWalk(Walk relatedWalk) {
		super();
		mRelatedWalk = relatedWalk;
		
		//mWalkId = walkID;
	}*/
	
	/*private String mWalkId;
//	public void setWalkId(String value)
//	{
//		mWalkId = value;
//	}
	public String getWalkId()
	{
		return mWalkId;
	}
	*/
	private Walk mRelatedWalk;
	public void setRelatedWalk(Walk value) {
		mRelatedWalk = value;
	}
	public Walk getRelatedWalk() {
		return mRelatedWalk;
	}
	
	private long mStartTime;
	/***/
	public void setStartTime(long mStartTime) {
		this.mStartTime = mStartTime;
	}
	public long getStartTime() {
		return mStartTime;
	}
	
	private ArrayList<GeoPointExt> mPoints;
	public void setPoints(ArrayList<GeoPointExt> mPoints) {
		this.mPoints = mPoints;
	}
	public ArrayList<GeoPointExt> getPoints() {
		return mPoints;
	}
	
	private ArrayList<GeoPointExtWithData> mPointsPoo;
	public void setPointsPoo(ArrayList<GeoPointExtWithData> mPointsPoo) {
		this.mPointsPoo = mPointsPoo;
	}
	public ArrayList<GeoPointExtWithData> getPointsPoo() {
		return mPointsPoo;
	}
	
	private ArrayList<GeoPointExtWithData> mPointsMeet;
	public void setPointsMeet(ArrayList<GeoPointExtWithData> mPointsPlay) {
		this.mPointsMeet = mPointsPlay;
	}
	public ArrayList<GeoPointExtWithData> getPointsMeet() {
		return mPointsMeet;
	}
	
	private boolean mIsCompleted;
	public void setIsCompleted(boolean value) {
		mIsCompleted = value;
	}
	public boolean getIsCompleted() {
		return mIsCompleted;
	}
	
	//--------------------------------------
	//
	//			Walk Stop Tasks flags
	//
	//-------------------------------
	
	private boolean mPhotoUploaded;
	public void setPhotoUploaded(boolean mPhotoUploaded) {
		this.mPhotoUploaded = mPhotoUploaded;
	}
	public boolean getPhotoUploaded() {
		return mPhotoUploaded;
	}
	
	private boolean mPhotoUploadTriedOnce;
	public void setPhotoUploadTriedOnce(boolean value) {
		mPhotoUploadTriedOnce = value;
	}
	public boolean getPhotoUploadTriedOnce() {
		return mPhotoUploadTriedOnce;
	}
	
	private boolean mPhotoUploadSkipped;
	public void setPhotoUploadSkipped(boolean mPhotoUploadSkipped) {
		this.mPhotoUploadSkipped = mPhotoUploadSkipped;
	}
	public boolean getPhotoUploadSkipped() {
		return mPhotoUploadSkipped;
	}
	
	private boolean mSendUnsentRequests;
	public void setSendUnsentRequests(boolean value) {
		mSendUnsentRequests = value;
	}
	public boolean getSendUnsetRequests() {
		return mSendUnsentRequests;
	}
	
	private boolean mUnsentRequestsSkipped;
	public void setUnsentRequestsSkipped(boolean mUnsentRequestsSkipped) {
		this.mUnsentRequestsSkipped = mUnsentRequestsSkipped;
	}
	public boolean getUnsentRequestsSkipped() {
		return mUnsentRequestsSkipped;
	}
	
	private boolean mIsMessageSent;
	public void setIsMessageSent(boolean value) {
		mIsMessageSent = value;
	}
	public boolean getIsMessageSent() {
		return mIsMessageSent;
	}
	
	private boolean mIsStopWalkSent;
	public void setIsStopWalkSent(boolean value) {
		mIsStopWalkSent = value;
	}
	public boolean getIsStopWalkSent() {
		return mIsStopWalkSent;
	}
}
