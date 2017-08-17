package com.haski.swifto.interfaces;

public interface IGetWalkStateWaitable {
	
	void onGetWalkStateSuccess(String walkState);
	void onGetWalkStateFailure();
}
