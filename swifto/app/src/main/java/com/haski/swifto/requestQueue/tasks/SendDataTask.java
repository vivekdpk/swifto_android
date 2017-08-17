package com.haski.swifto.requestQueue.tasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;

import com.haski.swifto.exceptions.SendDataException;
import com.haski.swifto.util.HttpUtils;

public class SendDataTask extends BaseTask {
	
	private String mRequestString;
	private String mResponse;
	
	public SendDataTask(String requestString) {
		mRequestString = requestString;
	}
	

	public String call() throws Exception {
		try {
			HttpClient httpClient = new HttpUtils().getHttpClientInstance(20000, 20000);
			HttpResponse httpResponse;
			httpResponse = httpClient.execute(new HttpGet(mRequestString));
			StatusLine statusLine = httpResponse.getStatusLine();
			
			if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				httpResponse.getEntity().writeTo(byteStream);
				mResponse = byteStream.toString();
			} else {
				throw new SendDataException(String.format(Locale.getDefault(), "Send data failed. %d: %s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
			}
		} catch(ConnectTimeoutException e) {
			throw new ExecutionException(e.toString(), e.getCause());
		} catch(ClientProtocolException e) {
			throw new ExecutionException(e.toString(), e.getCause());
		} catch(IOException e) {
			throw new ExecutionException(e.toString(), e.getCause());
		}

		return mResponse;
	}
}
