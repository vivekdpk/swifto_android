package com.haski.swifto.model;

public class PerformedRequests {

	private String MD5;
	public void setMD5(String value) {
		MD5 = value;
	}
	public String getMD5() {
		return MD5;
	}
	
	private boolean mSuccess;
	public void setIsSuccess(boolean value) {
		mSuccess = value;
	}
	public boolean getIsSuccess() {
		return mSuccess;
	}
	
	private String mText;
	public void setText(String value) {
		mText = value;
	}
	public String getText() {
		return mText;
	}
	
	private String mErrorDesccription;
	public void setErrorDescription(String value) {
		mErrorDesccription = value;
	}
	public String getErrorDescription() {
		return mErrorDesccription;
	}
	
	private String mWalkId;
	public void setWalkId(String value) {
		mWalkId = value;
	}
	public String getWalkId() {
		return mWalkId;
	}
}
