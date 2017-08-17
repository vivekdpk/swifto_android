package com.haski.swifto.model;

import org.apache.http.annotation.GuardedBy;

public class UploadVideoStatus {
	
	private static UploadVideoStatus mInstance;
	
	public static UploadVideoStatus getInstance() {
		if(mInstance == null) {
			mInstance = new UploadVideoStatus();
		}
		
		return mInstance;
	}
	
	@GuardedBy("this")
	private volatile String mCurrentStatus = EnumUploadVideoStatus.NOT_UPLOADING;
	
	public synchronized String getStatus() {
		return mCurrentStatus;
	}
	
	public synchronized void setStatusUploadingButtonTakePhoto() {
		mCurrentStatus = EnumUploadVideoStatus.UPLOADING_BUTTON_TAKE_VEDIO;
	}

	public synchronized void setStatusUploadingPopup() {
		mCurrentStatus = EnumUploadVideoStatus.UPLOADING_POPUP;
	}
	
	public synchronized void setStatusNotUploading() {
		mCurrentStatus = EnumUploadVideoStatus.NOT_UPLOADING;
	}
}
