package com.haski.swifto.requestQueue.tasks;

import java.util.concurrent.Callable;

public abstract class BaseTask implements Callable<String>, Comparable<BaseTask> {

	public int priority;
	
	public int compareTo(BaseTask another) {
		if(priority < another.priority) {
			return -1;
		} else if(priority > another.priority) {
			return 1;
		} else {
			return 0;
		}
	}
}