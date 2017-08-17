package com.haski.swifto.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.haski.swifto.database.TableColumns.COMMON_COLUMNS;

public class SwiftoDatabase extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "swifto.sqlite";
	
	//8 - added MONTHS
	//9 - added GPS_POINTS_ONE_SESSION
	//10 - added fd_situation_toys
	//11 - added (to walk) list of dogs to be fed
	//12 - added StartedWalks.REMINDERS_SHOWN_ONCE
	private static final int DATABASE_VERSION = 12;

	public SwiftoDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.WALKER + "("
				+ BaseColumns._ID 							+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				//base entity
				+ COMMON_COLUMNS.__ID 						+ " TEXT UNIQUE,"
				+ COMMON_COLUMNS.NID 						+ " TEXT,"
				+ COMMON_COLUMNS.SYNC 						+ " INTEGER,"
				
				//identifier
				+ TableColumns.WalkerColumns.WALKER_ID 		+ " TEXT NOT NULL,"
				
				//fields
				+ TableColumns.WalkerColumns.USERNAME 		+ " TEXT NOT NULL,"
				+ TableColumns.WalkerColumns.PASSWORD 		+ " TEXT NOT NULL,"
				+ TableColumns.WalkerColumns.KEY 			+ " TEXT NOT NULL"
				
				+")"
				);
		
		db.execSQL("CREATE TABLE " + Tables.SERVICES + "("
				+ BaseColumns._ID 							+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				//base entity
				+ COMMON_COLUMNS.__ID 						+ " TEXT UNIQUE,"
				//+ COMMON_COLUMNS.NID 						+ " TEXT,"
				//+ COMMON_COLUMNS.SYNC 						+ " INTEGER,"
				
				//identifier
				+ TableColumns.WalkerColumns.WALKER_ID 		+ " TEXT NOT NULL,"
				
				//fields
				+ TableColumns.ServicesColumn.FEED 		+ " TEXT,"
				+ TableColumns.ServicesColumn.SERVICEID 		+ " TEXT,"
				+ TableColumns.ServicesColumn.DOGID 		+ " TEXT,"
				+ TableColumns.ServicesColumn.MEDICINE + " TEXT,"
				+ TableColumns.ServicesColumn.ISFEED + " INTEGER,"
				+ TableColumns.ServicesColumn.ISMEDICINE + " INTEGER"
				
				+")"
				);
		
		
		db.execSQL("CREATE TABLE " + Tables.WALKS + "("
				+ BaseColumns._ID 									+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				//base entity
				+ COMMON_COLUMNS.__ID 								+ " TEXT UNIQUE,"
				+ COMMON_COLUMNS.NID 								+ " TEXT,"
				+ COMMON_COLUMNS.SYNC 								+ " INTEGER,"
				
				//identifier
				+ TableColumns.WalksColumns.IDENTIFIER_START_END 	+ " TEXT,"	
				
				//fields
				+ TableColumns.WalksColumns.DURATION 				+ " INTEGER,"
				+ TableColumns.WalksColumns.FEE_VALUE 				+ " INTEGER,"
				+ TableColumns.WalksColumns.FEE_VALUE_FORMATTED 	+ " TEXT,"
				+ TableColumns.WalksColumns.PRICE_VALUE 			+ " INTEGER,"
				+ TableColumns.WalksColumns.PRICE_VALUE_FORMATTED 	+ " TEXT,"
				+ TableColumns.WalksColumns.START_DATE 				+ " INTEGER,"
				+ TableColumns.WalksColumns.TIME_MINUS 				+ " INTEGER,"
				+ TableColumns.WalksColumns.TIME_PLUS 				+ " INTEGER,"
				+ TableColumns.WalksColumns.START_TIME 				+ " INTEGER,"
				+ TableColumns.WalksColumns.STATUS 					+ " TEXT NOT NULL,"
				+ TableColumns.WalksColumns.WALK_TYPE 				+ " TEXT NOT NULL,"
                        + TableColumns.WalksColumns.ORIGINAL 				+ " TEXT,"
						+ TableColumns.WalksColumns.NOTESOWNER 				+ " TEXT,"
						+ TableColumns.WalksColumns.WALK_ADDRESS_ORIGINAL 				+ " TEXT,"
						+ TableColumns.WalksColumns.WALK_ADDRESS_APPARTMENT				+ " TEXT,"

						+ TableColumns.WalksColumns.FORMATTED 				+ " TEXT,"

				//composite fields
				+ TableColumns.WalksColumns.LOCATION_LAT 			+ " INTEGER,"
				+ TableColumns.WalksColumns.LOCATION_LNG 			+ " INTEGER,"
				
				//another table fields
				+ TableColumns.WalksColumns.OWNER_ID 				+ " TEXT NOT NULL,"
				+ TableColumns.WalksColumns.DOG_IDS 				+ " TEXT NOT NULL,"
				+ TableColumns.WalksColumns.DOGS_TO_FEED_IDS		+ " TEXT,"
				+ TableColumns.WalksColumns.WALKER_ID 				+ " TEXT NOT NULL"
					
				+ ")"
				);
		//nkp
		db.execSQL("CREATE TABLE " + Tables.COMMENTS + "("
				+ TableColumns.CommentsColumn.AUTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"

                + COMMON_COLUMNS.NID + " TEXT,"
				
				//fields
				+ TableColumns.CommentsColumn.TITLE 			+ " TEXT,"
				+ TableColumns.CommentsColumn.BODY 				+ " TEXT,"
				+ TableColumns.CommentsColumn.CREATED 			+ " TEXT,"
				+ TableColumns.CommentsColumn.CID 				+ " TEXT,"
				+ TableColumns.CommentsColumn._ID 				+ " TEXT,"
				+ TableColumns.CommentsColumn.AUTHOR_PICTURE 	+ " TEXT,"
				+ TableColumns.CommentsColumn.AUTHOR_NAME 		+ " TEXT"
				+ ")"
		);

		db.execSQL("CREATE TABLE " + Tables.DOGS + "("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"

				//base entity
				+ COMMON_COLUMNS.__ID + " TEXT UNIQUE,"
				+ COMMON_COLUMNS.NID + " TEXT,"
				+ COMMON_COLUMNS.SYNC + " INTEGER,"

				//fields
				+ TableColumns.DogsColumns.ADDITIONAL_INFO 			+ " TEXT,"
				+ TableColumns.DogsColumns.BIRTH_DATE 				+ " TEXT,"
				+ TableColumns.DogsColumns.BREED 					+ " TEXT,"
				+ TableColumns.DogsColumns.COMMAND_COME 			+ " TEXT,"
				+ TableColumns.DogsColumns.COMMAND_PRAISE 			+ " TEXT,"
				+ TableColumns.DogsColumns.COMMAND_SIT 				+ " TEXT,"
				+ TableColumns.DogsColumns.COMMAND_STOP 			+ " TEXT,"
				+ TableColumns.DogsColumns.EMERGENCY_PHONE_CONTACT 	+ " TEXT,"
				+ TableColumns.DogsColumns.EMERGENCY_PHONE_VET 		+ " TEXT,"
				+ TableColumns.DogsColumns.FD_ALLERGIC 				+ " TEXT,"
				+ TableColumns.DogsColumns.FD_MEDICATION 			+ " TEXT,"
				+ TableColumns.DogsColumns.FD_SITUATION_CHILD 		+ " TEXT,"
				+ TableColumns.DogsColumns.FD_SITUATION_DOG 		+ " TEXT,"
				+ TableColumns.DogsColumns.FD_SITUATION_STRANGER 	+ " TEXT,"
				+ TableColumns.DogsColumns.FD_SITUATION_TOYS 		+ " TEXT,"
				+ TableColumns.DogsColumns.FEAT_AGGRESSIVE 			+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_ALERGIC 			+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_CHILD_NERVOUS 		+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_COLD_SENSITIVE 		+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_DOG_NERVOUS 		+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_FRIENDLY 			+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_HOT_SENSITIVE		+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_MEDICATION			+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_NO_TREATS_NEEDED	+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_NO_TREATS 			+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_PULLEASH 			+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_RAIN_SENSITIVE 		+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_STRANGER_NERVOUS 	+ " INTEGER,"
				+ TableColumns.DogsColumns.FEAT_TOYS_NERVOUS 		+ " INTEGER,"
				+ TableColumns.DogsColumns.FEED 					+ " INTEGER,"
				+ TableColumns.DogsColumns.GENDER					+ " TEXT,"
				+ TableColumns.DogsColumns.NAME						+ " TEXT,"
				+ TableColumns.DogsColumns.PIC						+ " TEXT,"

				//composite fields
				+ TableColumns.DogsColumns.LOCATION_LAT 			+ " INTEGER,"
				+ TableColumns.DogsColumns.LOCATION_LNG 			+ " INTEGER,"

				//another table fields
				+ TableColumns.DogsColumns.COMMENT_IDS 				+ " TEXT NOT NULL,"
				+ TableColumns.DogsColumns.OWNER_ID 				+ " TEXT"


				+ ")"
		);
		
		db.execSQL("CREATE TABLE " + Tables.OWNERS + "("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				//base entity
				+ COMMON_COLUMNS.__ID + " TEXT UNIQUE,"
				+ COMMON_COLUMNS.NID + " TEXT,"
				+ COMMON_COLUMNS.SYNC + " INTEGER,"
				
				//fields
				+ TableColumns.OwnerColumns.ANIMAL_IDS 					+ " TEXT,"
				+ TableColumns.OwnerColumns.BIRTH_YEAR 					+ " INTEGER,"
				+ TableColumns.OwnerColumns.EMAIL 						+ " TEXT,"
				+ TableColumns.OwnerColumns.FIRST_NAME 					+ " TEXT,"
				+ TableColumns.OwnerColumns.GENDER 						+ " TEXT,"
				+ TableColumns.OwnerColumns.LAST_NAME 					+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_ACCESS 					+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_ACCESS_INFO 			+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_APARTMENT				+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_AREA 					+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_CITY 					+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_COUNTRY 				+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_FORMATTED 				+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_NEIGHBORHOOD 			+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_ORIGINAL 				+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_STATE 					+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_STREET 					+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_STREET_NUM 				+ " TEXT,"
				+ TableColumns.OwnerColumns.LOC_TIMESTAMP 				+ " REAL,"
				+ TableColumns.OwnerColumns.LOC_ZIP 					+ " TEXT,"
				+ TableColumns.OwnerColumns.METHOD_EMAIL 				+ " INTEGER,"
				+ TableColumns.OwnerColumns.METHOD_PUSH 				+ " INTEGER,"
				+ TableColumns.OwnerColumns.METHOD_SMS 					+ " INTEGER,"
				+ TableColumns.OwnerColumns.PHONE_PRIMARY 				+ " TEXT,"
				+ TableColumns.OwnerColumns.SOCIAL_FACEBOOK_FRIENDS 	+ " TEXT,"
				+ TableColumns.OwnerColumns.UID 						+ " INTEGER,"
				+ TableColumns.OwnerColumns.USERNAME 					+ " TEXT,"
				
				//composite fields
				+ TableColumns.OwnerColumns.LOC_LAT 					+ " INTEGER,"
				+ TableColumns.OwnerColumns.LOC_LNG 					+ " INTEGER"
				
				+ ")"
				);
		
		db.execSQL("CREATE TABLE " + Tables.PERFORMED_REQUESTS + "("
				+ BaseColumns._ID 									+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				+ TableColumns.PerformedRequestsColumns.ERROR_DESCRIPTION 	+ " TEXT,"
				+ TableColumns.PerformedRequestsColumns.MD5					+ " TEXT NOT NULL,"
				+ TableColumns.PerformedRequestsColumns.SUCCESS 			+ " INTEGER NOT NULL,"
				+ TableColumns.PerformedRequestsColumns.TEXT 				+ " TEXT UNIQUE NOT NULL, "
				+ TableColumns.PerformedRequestsColumns.WALK_ID				+ " TEXT NOT NULL"
				
				+")"
				);
		db.execSQL("CREATE TABLE " + Tables.WALK_GPS_POINTS + "("
				+ BaseColumns._ID         									+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				+ TableColumns.WalkGPSPointsColumns.ACCURACY						+ " REAL, "
				+ TableColumns.WalkGPSPointsColumns.LATITUDE						+ " INTEGER, "
				+ TableColumns.WalkGPSPointsColumns.LONGITUDE						+ " INTEGER, "
				+ TableColumns.WalkGPSPointsColumns.MD5						    	+ " TEXT, "
				+ TableColumns.WalkGPSPointsColumns.STATUS						    + " TEXT, "
				+ TableColumns.WalkGPSPointsColumns.TIMESTAMP						+ " INTEGER, "
				+ TableColumns.WalkGPSPointsColumns.TYPE							+ " TEXT, "
				+ TableColumns.WalkGPSPointsColumns.WALK_ID						+ " TEXT"
				
				+ ")"
				);
		
		db.execSQL("CREATE TABLE " + Tables.PORTIONS + "("
				+ BaseColumns._ID         									+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				+ TableColumns.PortionColumns.STATE							+ " TEXT NOT NULL,"
				+ TableColumns.PortionColumns.UNIQUE_ID						+ " TEXT UNIQUE NOT NULL"
				
				+ ")"
				);
		
		db.execSQL("CREATE TABLE " + Tables.STARTED_WALKS + "("
				+ BaseColumns._ID         										+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				+ TableColumns.StartedWalksColumns.COMPLETED					+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.MESSAGE_SENT					+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.PHOTO_SKIPPED				+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.PHOTO_UPLOAD_TRIED_ONCE		+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.PHOTO_UPLOADED				+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.REMINDERS_SHOWN_ONCE			+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.STARTED_TIME					+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.STOP_WALK_SENT				+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.UNSENT_DATA_SENT				+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.UNSENT_DATA_SKIPPED			+ " INTEGER NOT NULL,"
				+ TableColumns.StartedWalksColumns.WALK_ID						+ " TEXT UNIQUE NOT NULL"
				
				+ ")"
				);

		db.execSQL("CREATE TABLE " + Tables.MONTHS + "("
				+ BaseColumns._ID         										+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				+ TableColumns.MonthsColumns.DAYS_IN_MONTH						+ " INTEGER,"
				+ TableColumns.MonthsColumns.MONTH								+ " INTEGER,"
				+ TableColumns.MonthsColumns.NUMBERS							+ " TEXT,"
				+ TableColumns.MonthsColumns.WALKS_IN_MONTH						+ " INTEGER,"
				+ TableColumns.MonthsColumns.YEAR								+ " INTEGER"
				
				+ ")"
				);
		
		db.execSQL("CREATE TABLE " + Tables.GPS_POINTS_ONE_SESSION + "("
				+ BaseColumns._ID         									+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				
				+ TableColumns.GpsPointsOneSessionColumns.ACCURACY			+ " REAL, "
				+ TableColumns.GpsPointsOneSessionColumns.LATITUDE			+ " INTEGER, "
				+ TableColumns.GpsPointsOneSessionColumns.LONGITUDE			+ " INTEGER, "
				+ TableColumns.GpsPointsOneSessionColumns.TIMESTAMP			+ " INTEGER"
				
				+ ")"
				);


		db.execSQL("CREATE TABLE " + Tables.OFFLINE_WALK_REQUESTS + "("
						+ TableColumns.OfflineWalkRequestsColumns._ID    	+ " INTEGER PRIMARY KEY AUTOINCREMENT,"

						+ TableColumns.OfflineWalkRequestsColumns.WALKID	+ " TEXT NOT NULL,"
						+ TableColumns.OfflineWalkRequestsColumns.REQUEST	+ " TEXT NOT NULL,"
						+ TableColumns.OfflineWalkRequestsColumns.TYPE		+ " TEXT NOT NULL,"
						+ TableColumns.OfflineWalkRequestsColumns.STATUS	+ " TEXT NOT NULL"

						+ ")"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int fromVer, int toVer) {
		db.execSQL("DROP TABLE IF EXISTS " + Tables.WALKER);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.WALKS);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.COMMENTS); //nkp
		db.execSQL("DROP TABLE IF EXISTS " + Tables.DOGS);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.SERVICES);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.OWNERS);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.PERFORMED_REQUESTS);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.WALK_GPS_POINTS);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.PORTIONS);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.STARTED_WALKS);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.MONTHS);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.GPS_POINTS_ONE_SESSION);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.OFFLINE_WALK_REQUESTS);
		
		onCreate(db);
	}
}
