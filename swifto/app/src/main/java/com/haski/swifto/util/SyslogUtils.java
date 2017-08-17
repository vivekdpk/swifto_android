package com.haski.swifto.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Build;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.haski.swifto.R;
import com.haski.swifto.util.log.LogEntry;


public class SyslogUtils {
	 

	public static void logEvent(Context context, String message, String severity, String type)
	{
		LogEntry logEntry = new LogEntry();
		//long ts  = System.currentTimeMillis();
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		Date date = new Date(stamp.getTime());
		//System.out.println(date);
		logEntry.Date = date.toString();
		logEntry.Message = message;
		logEntry.Severity = severity;
		logEntry.Type = type;
		logEntry.Responce = "NA";
		
		String toSave = new Gson().toJson(logEntry, LogEntry.class);
		toSave += ", \n";
		
		//LogEntry parsed = new Gson().fromJson(toSave, LogEntry.class);
		
		try {
			
			File file = context.getFileStreamPath("log.json");
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
			
			bufferedWriter.write(toSave);
			bufferedWriter.flush();
			bufferedWriter.close();
			
			/*FileOutputStream f = context.openFileOutput("log.json", Context.MODE_PRIVATE);
			f.write(toSave.getBytes());*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void logEventWithResponce(Context context, String message, String severity, String type, String res)
	{
		LogEntry logEntry = new LogEntry();
		//logEntry.Date = System.currentTimeMillis();

		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		Date date = new Date(stamp.getTime());
		//System.out.println(date);
		logEntry.Date = date.toString();

		logEntry.Message = message;
		logEntry.Severity = severity;
		logEntry.Type = type;
		logEntry.Responce = res;

		String toSave = new Gson().toJson(logEntry, LogEntry.class);
		toSave += ", \n";

		//LogEntry parsed = new Gson().fromJson(toSave, LogEntry.class);

		try {

			File file = context.getFileStreamPath("log.json");

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));

			bufferedWriter.write(toSave);
			bufferedWriter.flush();
			bufferedWriter.close();

			/*FileOutputStream f = context.openFileOutput("log.json", Context.MODE_PRIVATE);
			f.write(toSave.getBytes());*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static final int NUMBER_ENTITIES_LAST = 1000 + 1;
	
	public static File readLastEntries(Context context)
	{
		//get needed number of entries
		
		FileInputStream fis;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("[");
		
		byte[] buff;
		try {
			fis = context.openFileInput("log.json");
			
			buff = new byte[fis.available()];
			while (fis.read(buff) != -1) {
				builder.append(new String(buff));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		String contents = builder.toString();
		
		if(contents.length() > 4)
		{
			contents = contents.substring(0, contents.length()-3);
			contents += "]";
		}
		
		Type logEntryCollectionType = new TypeToken<ArrayList<LogEntry>>() {

			private static final long serialVersionUID = 3469194603661476556L;
		}.getType();
		
		ArrayList<LogEntry> entries = new Gson().fromJson(contents, logEntryCollectionType);
		
		Calendar c = CalendarUtils.NOW();
		
		SimpleDateFormat format = new SimpleDateFormat(DateTimeUtils.PATTERN_LOG_YYYY_MM_DD___HH_MM_SS);
		String logName = "swifto_android_log_v" + context.getResources().getString(R.string.app_ver) + "_" + Build.MODEL + "_" + Build.VERSION.RELEASE + "_" + format.format(c.getTime()) + ".json";
		
		int numberToTake = Math.min(entries.size(), NUMBER_ENTITIES_LAST - 1);
		
		int indexFirst = entries.size() - numberToTake;
		
		ArrayList<LogEntry> neededEntries = new ArrayList<LogEntry>(numberToTake);
		//ArrayList<LogEntry> neededEntries = (ArrayList<LogEntry>) entries.subList(indexFirst, entries.size());
		//List<LogEntry> neededEntries = entries.subList(indexFirst, entries.size()-1);
		
		
		for(int i = 0; i < numberToTake; i++)
		{
			neededEntries.add(entries.get(indexFirst+i));
		}
		
		
		String results = new Gson().toJson(neededEntries);
		
		//write entries to file
		
		File out = new File(context.getExternalFilesDir(null), logName );
		
		try {
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(out));
			bufferedWriter.write(results);
			bufferedWriter.flush();
			bufferedWriter.close();
			
			/*FileOutputStream f = context.openFileOutput("log.json", Context.MODE_PRIVATE);
			f.write(toSave.getBytes());*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out;
	}
	
	public static void clearLogFile(Context context)
	{
		try
		{
			File file = new File(context.getFilesDir(), "log.json");
			
			//delete file if it is more that 1 Mb
			if(file.length() > 1024 * 1024)
			{
				context.deleteFile("log.json");
				file = new File(context.getFilesDir(), "log.json");
			}
		}
		catch(Exception e)
		{
			
		}
	}
}
