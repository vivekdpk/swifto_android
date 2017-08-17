package com.haski.swifto.util.photoUploadService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.exceptions.PhotoUploadException;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.requestQueue.tasks.BaseTask;
import com.haski.swifto.server.SwiftoRequestBuilder;
import com.haski.swifto.util.BitmapUtils;
import com.haski.swifto.util.HttpUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

public class UploadPhotoTask extends BaseTask {
	
	public UploadPhotoTask(File file, String dogIdForPhoto, SwiftoApplication application)
	{
		mFile = file;
		mApplication = application;
		mDogIdForPhoto = dogIdForPhoto;
	}
	
	private final File mFile;
	private final SwiftoApplication mApplication;
	private final String mDogIdForPhoto;
	
	private String mResponse;
	
	//TODO: change release bucket
	private static final String BUCKET_STAGE = "dog-pictures-stage";
	private static final String BUCKET_PROD = "dog-pictures";
	private static final String BUCKET = BUCKET_PROD;

	public String call() throws Exception {
		SyslogUtils.logEvent(mApplication.getApplicationContext(), "Starting to compress", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		try {
			Bitmap b = BitmapUtils.decodeFileExt(mFile);
			int quality = 80;
	
			while(mFile.length() > 409000) {
				OutputStream stream = new FileOutputStream(mFile);
				b.compress(CompressFormat.JPEG, quality, stream);
				stream.flush();
				quality--;
			}
		} catch(Exception e) {
			saveFile(String.format( Locale.getDefault(), "Compression error: %s", e.getMessage()),"PhotoUploadException.txt");

			SharedPreferencesHelper.saveWalkImageIsCompressError(mApplication.getApplicationContext(), mFile.getAbsolutePath());

			throw new PhotoUploadException(String.format(Locale.getDefault(), "Compression error: %s", e.getMessage()));
		}

		SyslogUtils.logEvent(mApplication.getApplicationContext(), "Photo compressed", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		
		Walk currStartedWalk = mApplication.getWalkGetter().getStartedWalkWithoutOwnerAndDogs();

		try {
			//----------------------------------------
			//UPLOAD TO AMAZON
			ClientConfiguration clientConfig = new ClientConfiguration()
			.withConnectionTimeout(30000)
			.withSocketTimeout(30000);
			
			String key = SharedPreferencesHelper.getS3Key(mApplication);
			String secret = SharedPreferencesHelper.getS3Secret(mApplication);
			AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(key, secret), clientConfig);
	
			//app-snapshots/DOG_ID/WALKER_ID
			final String path = "app-snapshots/" + mDogIdForPhoto + "/" + currStartedWalk.walkerID + "/" + mFile.getName();
			final PutObjectRequest por = new PutObjectRequest(BUCKET, path, mFile);
			final CannedAccessControlList cacl = CannedAccessControlList.PublicRead;
	
			por.setCannedAcl(cacl);
			
			SyslogUtils.logEvent(mApplication.getApplicationContext(), "Start putting object for " + path, EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		
			PutObjectResult result = s3Client.putObject(por);
		} catch (Exception e) {
			
			saveFile(String.format( "Upload to Amazon failed: %s: %s", e.toString(), e.getMessage()),"PhotoUploadException.txt");
			throw new PhotoUploadException(String.format(Locale.getDefault(), "Upload to Amazon failed: %s: %s", e.toString(), e.getMessage()));
		}

		SyslogUtils.logEvent(mApplication.getApplicationContext(), "Photo uplodaded to Amazon", EnumLogSeverity.INFORMATIONAL, EnumLogType.INTERNAL_TYPE);
		
		//-----------------------------------------------------
		//SEND DATA TO SWIFTO
		
		if(mFile == null) {
			saveFile("mFile in UploadPhotoTask is null, probably killed by system.","PhotoUploadException.txt");
			throw new PhotoUploadException(String.format(Locale.getDefault(), "mFile in UploadPhotoTask is null, probably killed by system."));
		}

		try {
			String fileName = mFile.getName();
			fileName = fileName.substring(0, fileName.length()-4);
			
			String request = SwiftoRequestBuilder.buildUploadedPhotoRequest(currStartedWalk._id, mDogIdForPhoto, currStartedWalk.walkerID, fileName);
			HttpClient httpClient = new HttpUtils().getHttpClientInstance(20000, 20000);
			HttpResponse httpResponse;
	
			httpResponse = httpClient.execute(new HttpGet(request));
			StatusLine statusLine = httpResponse.getStatusLine();

			if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	
				//TODO: dive into
				httpResponse.getEntity().writeTo(byteStream);
	
				mResponse = byteStream.toString();
			} else {
				saveFile(String.format("Http error: %d: %s", statusLine.getStatusCode(), statusLine.getReasonPhrase()),"PhotoUploadException.txt");
				throw new PhotoUploadException(String.format(Locale.getDefault(), "Http error: %d: %s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
			}
		}catch(PhotoUploadException pue) {
			throw pue;
		}
		catch (Exception e) {
			saveFile(String.format(Locale.getDefault(), "Upload result to Swifto failed: %s: %s", e, e.getMessage()),"PhotoUploadException.txt");
			throw new PhotoUploadException(String.format(Locale.getDefault(), "Upload result to Swifto failed: %s: %s", e, e.getMessage()));
		}
		
		saveFile(""+mResponse,"mResponse.txt");
		return mResponse;
	}
	
	public void saveFile(String txt,String filename) {

		try {
			File myFile = new File("/sdcard/"+filename+".txt");
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(txt);
			myOutWriter.close();
			fOut.close();
		} catch (Exception e) {
		}
	}
}
