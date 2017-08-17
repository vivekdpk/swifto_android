package com.haski.swifto.model;

import org.apache.http.annotation.GuardedBy;

public class UploadPhotoStatus {
	
	private static UploadPhotoStatus mInstance;
	
	public static UploadPhotoStatus getInstance() {
		if(mInstance == null) {
			mInstance = new UploadPhotoStatus();
		}
		
		return mInstance;
	}
	
	@GuardedBy("this")
	private volatile String mCurrentStatus = EnumUploadPhotoStatus.NOT_UPLOADING;
	
	public synchronized String getStatus() {
		return mCurrentStatus;
	}
	
	public synchronized void setStatusUploadingButtonTakePhoto() {
		mCurrentStatus = EnumUploadPhotoStatus.UPLOADING_BUTTON_TAKE_PHOTO;
	}

	public synchronized void setStatusUploadingPopup() {
		mCurrentStatus = EnumUploadPhotoStatus.UPLOADING_POPUP;
	}
	
	public synchronized void setStatusNotUploading() {
		mCurrentStatus = EnumUploadPhotoStatus.NOT_UPLOADING;
	}
}
