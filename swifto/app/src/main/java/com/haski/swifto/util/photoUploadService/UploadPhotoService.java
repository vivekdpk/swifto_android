package com.haski.swifto.util.photoUploadService;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.haski.swifto.requestQueue.TaskListenableFuture;
import com.haski.swifto.requestQueue.tasks.BaseTask;

public class UploadPhotoService extends Service {

	private final IBinder mBinder = new UploadPhotoServiceBinder();
	
	public class UploadPhotoServiceBinder extends Binder {
		public UploadPhotoService getService()
		{
			return UploadPhotoService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private LinkedBlockingQueue<Runnable> mQueue;
	
	private volatile ListeningExecutorService mExecutorService;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mQueue = new LinkedBlockingQueue<Runnable>(1);
		
		mExecutorService = MoreExecutors.listeningDecorator(
				new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, 
						mQueue, 
						Executors.defaultThreadFactory(), 
						new ThreadPoolExecutor.AbortPolicy()
						));
	}
	
	public ListenableFuture<String> enqueueTask(final BaseTask task) {
		TaskListenableFuture<String> futureTask = TaskListenableFuture.create(task);
		
		mExecutorService.execute(futureTask);
		
		return futureTask;
	}
	
	public void shutdownNow() {
		if(mExecutorService != null) {
			mExecutorService.shutdownNow();
			
			mExecutorService = MoreExecutors.listeningDecorator(
					new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, 
							mQueue, 
							Executors.defaultThreadFactory(), 
							new ThreadPoolExecutor.AbortPolicy()
							));
		}
	}
	
	public void awaitTermination(long timeToWait, TimeUnit timeUnit) {
		if(mExecutorService != null) {
			try {
				mExecutorService.awaitTermination(timeToWait, timeUnit);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			} finally {
				mExecutorService = MoreExecutors.listeningDecorator(
						new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, 
								mQueue, 
								Executors.defaultThreadFactory(), 
								new ThreadPoolExecutor.AbortPolicy()
								));
			}
		}
	}
}
