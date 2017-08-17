package com.haski.swifto.requestQueue;

public interface TaskUpdateCallback<T> {

	void success(T result);
	void failure();
	
}
