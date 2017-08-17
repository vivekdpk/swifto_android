package com.haski.swifto.interfaces;

public interface IUploadPhotoNotifiable {
	
	void onUploadPhotoSuccess();
	void onUploadPhotoFailure(String cause);
}
