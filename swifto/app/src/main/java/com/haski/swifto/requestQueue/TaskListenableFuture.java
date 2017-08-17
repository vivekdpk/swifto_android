package com.haski.swifto.requestQueue;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ListenableFuture;

public class TaskListenableFuture<V> extends FutureTask<V> implements
		ListenableFuture<V>, Comparable<TaskListenableFuture<V>> {

	private final ExecutionList executionList = new ExecutionList();
	private final Object object;
	
	
	/*
	 * Static creators
	 */
	public static <V> TaskListenableFuture<V> create(Callable<V> callable) {
		return new TaskListenableFuture<V>(callable);
	}
	
	public static <V> TaskListenableFuture<V> create(Runnable runnable, V result) {
		return new TaskListenableFuture<V>(runnable, result);
	}
	
	
	/*
	* Private constructors
	*/
	
	private TaskListenableFuture(Callable<V> callable) {
		super(callable);
		
		object = callable;
	}
	
	private TaskListenableFuture(Runnable runnable, V result) {
		super(runnable, result);
		
		object = runnable;
	}
	
	
	/*
	 * Implementations
	 */
	
	public void addListener(Runnable listener, Executor executor) {
		executionList.add(listener, executor);
	}
	
	@Override
	protected void done() {
		executionList.execute();
	}

	@SuppressWarnings("unchecked")
	public int compareTo(TaskListenableFuture<V> another) {
		if(this == another) {
			return 0;
		}
		
		if(another == null) {
			return -1;
		}
		
		if(object != null && another.object != null) {
			if(object.getClass().equals(another.object.getClass())) {
				if(object instanceof Comparable) {
					return ((Comparable<Object>) object).compareTo(another.object);
				}
			}
		}
		
		return 0;
	}
}
