package com.haski.swifto.interfaces;

public interface ILoadWalksContainingStartedWaitable {
	
	void onLoadWalksContainingStartedSuccess();
	void onLoadWalksContainingStartedFailure(String error);
}
