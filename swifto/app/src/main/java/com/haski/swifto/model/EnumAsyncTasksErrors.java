package com.haski.swifto.model;

public class EnumAsyncTasksErrors {
	
	public static final String EXCEPTION = "Exception";

	public static final String IO_EXCEPTION = "IoException";

	public static final String HTTP_CONNECT_TIMEOUT = "HttpConnectTimeoutException";
	public static final String HTTP_CLIENT_PROTOCOL = "HttpClientProtocolException";
	
	public static final String AMAZON_SERVICE_EXCEPTION = "AmazonServiceException";
	public static final String AMAZON_CLIENT_EXCEPTION = "AmazonClientException";

	public static final String FILE_NOT_FOUND = "FileNotFoundException";
	
	public static final String UPLOAD_PHOTO_FAILURE_EXCEPTION = "UPLOAD_PHOTO_FAILURE_EXCEPTION";
	
	private static String[] SUPPORTED_EXCEPTIONS  = {
														EXCEPTION, 
														
														IO_EXCEPTION,
														FILE_NOT_FOUND,
														
														HTTP_CLIENT_PROTOCOL,
														HTTP_CONNECT_TIMEOUT,
														
														AMAZON_CLIENT_EXCEPTION,
														AMAZON_SERVICE_EXCEPTION,
														
														UPLOAD_PHOTO_FAILURE_EXCEPTION
													};
	
	public static boolean supportsException(String toCheck) {
		boolean toRet = false;
		
		for(int i = 0; i < SUPPORTED_EXCEPTIONS.length; i++) {
			if(toCheck.equals(SUPPORTED_EXCEPTIONS[i]))
			{
				toRet = true;
				break;
			}
		}
		
		return toRet;
	}
}
