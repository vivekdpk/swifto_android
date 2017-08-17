package com.haski.swifto.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class HttpUtils {
	
	public DefaultHttpClient getHttpClientInstance(int timeoutConnection, int timeoutSocket) {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		
		try {
			schemeRegistry.register(new Scheme("https", new CustomSSLSocketFactory(), 443));
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		
		
		HttpParams httpParams = new BasicHttpParams();
		
		//int timeoutConnection = 30000;
		//int timeoutSocket = 30000;
		
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		
		DefaultHttpClient httpCLient = new DefaultHttpClient(cm, httpParams);
		
		//httpCLient.setParams(httpParams);
		
		return httpCLient;
	}
	
	public class CustomSSLSocketFactory extends SSLSocketFactory {
		private javax.net.ssl.SSLSocketFactory FACTORY = HttpsURLConnection.getDefaultSSLSocketFactory();
		
		public CustomSSLSocketFactory() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
			super(null);
			
			try {
				SSLContext context = SSLContext.getInstance("TLS");
				TrustManager[] tm = new TrustManager[] {new FullX509TrustManager()};
				context.init(null, tm, new SecureRandom());
				
				FACTORY = context.getSocketFactory();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public Socket createSocket() throws IOException {
			return FACTORY.createSocket();
		}
		
		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException {
			return FACTORY.createSocket(socket, host, port, autoClose);
		}
		
	}
	
	public class FullX509TrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[]{};
		}
	}
	
	private static String BOUNDARY = "0.4824051586911082";
	private static String UPLOAD_PHOTO_URL = "https://api.swifto.com:443/photos/upload/android/key/";//walkerId=12345&dogId=12345";
	
	
	public static HttpPost getPostForUploadPhoto(String walkerId, String dogId, String binaryData) {
		String url = UPLOAD_PHOTO_URL + "walkerId=" + walkerId + "&dogId=" + dogId;
		
		HttpPost post = new HttpPost(url);
		
		String body = "--" + BOUNDARY + "\r\n";
		body += "Content-Disposition: form-data; name=\"photo\"; filename=\"dog_photo_android.png\"\r\n";
		body += "Content-Type: image/jpeg\r\n\r\n";
		body += binaryData;
		body += "\r\n--" + BOUNDARY + "--\r\n";
		
		try {
			post.setEntity(new StringEntity(body, HTTP.ASCII));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		post.setHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		//post.setHeader("Content-Length", String.format("%d", body.length()));
		
		return post;
	}
	
	public static HttpPost getMultipartPost(String walkerId, String dogId, String photoData) {
		String url = UPLOAD_PHOTO_URL + "walkerId=" + walkerId + "&dogId=" + dogId;
		
		HttpPost post = new HttpPost(url);
		
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, BOUNDARY, Charset.defaultCharset());
		try {
			entity.addPart("image", new StringBody(photoData));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		post.setHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		
		post.setEntity(entity);
		
		return post;
	}
	
	/*
	public void sendUploadPhotoWithUrlConnection(String base64data)
	{
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundaby = BOUNDARY;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		//dos.writeBytes(str)
	}
	*/
	
	/*
	public static HttpPost getPostForUploadPhoto(String walkerId, String dogId, String binaryData)
	{
		String url = UPLOAD_PHOTO_URL + "walkerId=" + walkerId + "&dogId=" + dogId;
		
		HttpPost post = new HttpPost(url);
		
		String strTop = "--" + BOUNDARY + "\r\n";
		strTop += "Content-Disposition: form-data; name=\"photo\"; filename=\"dog_photo_android.png\"\r\n";
		strTop += "Content-Type: image/jpeg\r\n\r\n";
		
		
		//body += binaryData;
		
		
		String strBottom = "\r\n--" + BOUNDARY + "--\r\n";
		
		
		MultipartEntity multpartEntity = new MultipartEntity(HttpMultipartMode.STRICT, BOUNDARY, Charset.forName("US-ASCII"));
		
		try {
			FormBodyPart top = new FormBodyPart("", new StringBody(strTop, Charset.forName("US-ASCII")));
			multpartEntity.addPart(top);
			
			ContentBody cb = new StringBody(binaryData);
			FormBodyPart middle = new FormBodyPart("", cb);
			multpartEntity.addPart(middle);
			
			//FormBodyPart bottom = new FormBodyPart("", new StringBody(strBottom, Charset.forName("US-ASCII")));
			//multpartEntity.addPart(bottom);
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		post.setEntity(multpartEntity);
		post.setHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		post.setHeader("Content-Length", String.format("%d", binaryData.length()));
		
		return post;
	}
	*/
	
	public static void test(String walkerId, String dogId, String binaryData) {
		HttpURLConnection cnn;
		
		try {
			String lineEnd = "\r\n"; 
			String twoHyphens = "--"; 
			String boundary =  "0.4824051586911082"; 
			
			String url = UPLOAD_PHOTO_URL + "walkerId=" + walkerId + "&dogId=" + dogId;
			
			URL u = new URL(url);
			
			cnn = (HttpURLConnection) u.openConnection();
			
			cnn.setDoInput(true);
			cnn.setDoOutput(true);
			cnn.setUseCaches(false);
			
			cnn.setRequestMethod("POST");
			cnn.setRequestProperty("Connection", "Keep-Alive"); 
			cnn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
			cnn.setRequestProperty("accept", "*/*");
			//cnn.connect();
			
			DataOutputStream dos = new DataOutputStream(cnn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"dog_photo_android.png\"\r\n");
			
			dos.writeBytes(binaryData);
			
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			
			//String s = cnn.getResponseMessage();

			//Log.d("SERVER", s);
			//cnn.connect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
