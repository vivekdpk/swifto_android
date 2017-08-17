package com.haski.swifto.server;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.haski.swifto.R;
import com.haski.swifto.model.EnumAsyncTasksErrors;
import com.haski.swifto.util.HttpUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author n1k1ch
 */
public class ServerRequestAsynkTask extends AsyncTask<String, Void, String> {
	
	private static final int TIMEOUT = 40000;

	protected Context mContext;
	private String mMessage;
	private boolean mShowProgress;
	
	public ServerRequestAsynkTask(Context ctx, String message, boolean showProgress) {
		mContext = ctx;
		mMessage = message;
		mShowProgress = showProgress;
	}
	
	private String response = null;
	private ProgressDialog mProgressDialog;
	protected boolean cancelled = false;

	@Override
	protected String doInBackground(String... params) {
		try {
			//HttpClient httpClient = new DefaultHttpClient();
			HttpClient httpClient = new HttpUtils().getHttpClientInstance(TIMEOUT, TIMEOUT);
			HttpResponse httpResponse;
			httpResponse = httpClient.execute(new HttpGet(params[0]));
			StatusLine statusLine = httpResponse.getStatusLine();
			
			if(isCancelled()) {
				return null;
			}
			
			if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				
				httpResponse.getEntity().writeTo(byteStream);
				
				response = byteStream.toString();
			}
		} catch(ConnectTimeoutException e) {
			return EnumAsyncTasksErrors.HTTP_CONNECT_TIMEOUT;
		} catch(ClientProtocolException e) {
			return EnumAsyncTasksErrors.HTTP_CLIENT_PROTOCOL;
		} catch(IOException e) {
			return EnumAsyncTasksErrors.IO_EXCEPTION;
		} catch(Exception e) {
			return EnumAsyncTasksErrors.EXCEPTION;
		}
		
		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if(mShowProgress) {
			try {
				mProgressDialog.dismiss();
			} catch(Exception e) {
				//do nothing
			}
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		if(mShowProgress) {
			try {
				mProgressDialog = ProgressDialog.show(mContext, null, mMessage, false);
			} catch(Exception e) {
				//do nothing
			}
		}
	}
	
	@Override
	protected void onCancelled() {
		//super.onCancelled();
		
		if(mShowProgress) {
			try {
				mProgressDialog.dismiss();
			} catch(Exception e) {
				//do nothing
			}
		}
		
		cancelled = true;
		Toast.makeText(mContext, mContext.getResources().getString(R.string.dialog_timeout), Toast.LENGTH_SHORT).show();
	}
}
