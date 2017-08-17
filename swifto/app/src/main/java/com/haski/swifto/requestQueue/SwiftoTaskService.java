package com.haski.swifto.requestQueue;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.haski.swifto.requestQueue.tasks.BaseTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SwiftoTaskService extends Service {

	//------------------------------
	//	Binder
	
	private final IBinder mBinder = new SwiftoTaskBinder();
	
	public class SwiftoTaskBinder extends Binder {
		public SwiftoTaskService getService()
		{
			return SwiftoTaskService.this;
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	
	private LinkedBlockingQueue<Runnable> mQueue;
	private volatile ListeningExecutorService mExecutorService;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mQueue = new LinkedBlockingQueue<Runnable>();
		
		//Creates an ExecutorService whose submit and invokeAll methods submit ListenableFutureTask instances to the given delegate executor.
				mExecutorService = MoreExecutors.listeningDecorator(
						
						//Creates a new ThreadPoolExecutor with the given initial parameters and default thread factory and rejected execution handler.
						new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, mQueue));
	}
	
	
	public ListenableFuture<String> enqueueTask(final BaseTask task) {
		TaskListenableFuture<String> futureTask = TaskListenableFuture.create(task);
		
		mExecutorService.execute(futureTask);
		
		return futureTask;
	}
	
	public int tasksInQueue() {
		if(mQueue != null) {
			return mQueue.size();
		}
		
		return 0;
	}
	
	public void shutdownNow() {
		if(mExecutorService != null) {
			mExecutorService.shutdownNow();
			mExecutorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, mQueue));
		}
	}
}
