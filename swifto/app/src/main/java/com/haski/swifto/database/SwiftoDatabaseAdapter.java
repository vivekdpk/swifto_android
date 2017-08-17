 package com.haski.swifto.database;

import java.util.ArrayList;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.google.android.maps.GeoPoint;
import com.haski.swifto.database.TableColumns.COMMON_COLUMNS;
import com.haski.swifto.database.TableColumns.DogsColumns;
import com.haski.swifto.database.TableColumns.CommentsColumn;
import com.haski.swifto.database.TableColumns.GpsPointsOneSessionColumns;
import com.haski.swifto.database.TableColumns.MonthsColumns;
import com.haski.swifto.database.TableColumns.OwnerColumns;
import com.haski.swifto.database.TableColumns.PerformedRequestsColumns;
import com.haski.swifto.database.TableColumns.PortionColumns;
import com.haski.swifto.database.TableColumns.ServicesColumn;
import com.haski.swifto.database.TableColumns.StartedWalksColumns;
import com.haski.swifto.database.TableColumns.WalkGPSPointsColumns;
import com.haski.swifto.database.TableColumns.WalkerColumns;
import com.haski.swifto.database.TableColumns.WalksColumns;
import com.haski.swifto.model.EnumWalkGPSPointSendStatus;
import com.haski.swifto.model.EnumWalkGPSPointType;
import com.haski.swifto.model.PerformedRequests;
import com.haski.swifto.model.vo.OfflineWalkRequest;
import com.haski.swifto.model.vo.Portion;
import com.haski.swifto.model.vo.StartedWalkExt;
import com.haski.swifto.model.vo.StaticLocation;
import com.haski.swifto.model.vo.WalkGPSPoint;
import com.haski.swifto.model.vo.Walker;
import com.haski.swifto.model.vo.WalksForMonth;
import com.haski.swifto.model.vo.dog.Comment;
import com.haski.swifto.model.vo.dog.Dog;
import com.haski.swifto.model.vo.dog.DogCommands;
import com.haski.swifto.model.vo.dog.Features;
import com.haski.swifto.model.vo.dog.FeaturesDetails;
import com.haski.swifto.model.vo.dog.Services;
import com.haski.swifto.model.vo.owner.OwnerOfDog;
import com.haski.swifto.model.vo.owner.SocialInfo;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.util.ArrayUtils;
import com.haski.swifto.util.DogUtils;
import com.haski.swifto.util.GeoUtils;
import com.haski.swifto.util.OwnerUtils;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.WalkUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

 public class SwiftoDatabaseAdapter {

	private SQLiteDatabase mDb;
	private static SwiftoDatabase dbHelper;

	public SwiftoDatabaseAdapter(Context ctx) {
		dbHelper = new SwiftoDatabase(ctx);
		mDb = dbHelper.getWritableDatabase();
	}

	public boolean isOpen() {
		return mDb != null && mDb.isOpen();
	}

	public void open(Context ctx) {
		dbHelper = new SwiftoDatabase(ctx);

		if(!isOpen()) {
			mDb = dbHelper.getWritableDatabase();
		}
	}

	public void close() {
		if(isOpen()) {
			mDb.close();
			mDb = null;
		}

		if(dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
	}


	//--------------------------------------------------------
	//
	//			WALKERS
	//
	//-----------------

	public void insertWalker(Walker walker) {
		deleteWalkers();
		ContentValues cv = new ContentValues();
		cv.put(TableColumns.WalkerColumns.USERNAME, walker.username);
		cv.put(TableColumns.WalkerColumns.PASSWORD, walker.password);
		cv.put(TableColumns.WalkerColumns.WALKER_ID, walker.id);
		cv.put(TableColumns.WalkerColumns.KEY, walker.key);

		insertToTable(Tables.WALKER, cv);
	}


	//--------------------------------------------------------
	//
	//			OFFLINE_WALK_REQUESTS
	//
	//-----------------

	public void insertOfflineWalkRequest(OfflineWalkRequest offlineWalkRequest) {
		//deleteWalkers();
		ContentValues cv = new ContentValues();
		cv.put(TableColumns.OfflineWalkRequestsColumns.WALKID, offlineWalkRequest.getWalkid());
		cv.put(TableColumns.OfflineWalkRequestsColumns.REQUEST, offlineWalkRequest.getRequest());
		cv.put(TableColumns.OfflineWalkRequestsColumns.TYPE, offlineWalkRequest.getType());
		cv.put(TableColumns.OfflineWalkRequestsColumns.STATUS, offlineWalkRequest.getStatus());

		insertToTable(Tables.OFFLINE_WALK_REQUESTS, cv);
	}

	public void upladteOfflineWalkRequest(String _id , String status) {
		ContentValues cv = new ContentValues();
		cv.put(TableColumns.OfflineWalkRequestsColumns.STATUS, status);

		//String query = "UPDATE " + Tables.WALKS + " SET " + WalksColumns.STATUS + "=" + status + " WHERE " + COMMON_COLUMNS.__ID + "=" + walk.i

		/*int rowsAffected = */mDb.update(Tables.OFFLINE_WALK_REQUESTS, cv, COMMON_COLUMNS.__ID + "=?", new String[]{_id});
	}

	public void updateWalker(Walker freshWalker) {
		ContentValues cv = new ContentValues();
		cv.put(TableColumns.WalkerColumns.WALKER_ID, freshWalker.id);
		cv.put(TableColumns.WalkerColumns.KEY, freshWalker.key);

		updateTable(Tables.WALKER, cv, null, null);
	}

	public String selectWalkerID() {
		String toRet = "";
		Cursor c = mDb.query(Tables.WALKER, new String[] {TableColumns.WalkerColumns.WALKER_ID}, null, null, null, null, null);

		if(c.moveToFirst()) {
			toRet = c.getString(0);
		}

		c.close();

		return toRet;
	}

	public Walker selectWalker() {
		Walker toRet = null;
		Cursor c = mDb.query(Tables.WALKER, 
				new String[] {
				TableColumns.WalkerColumns.USERNAME, 
				TableColumns.WalkerColumns.PASSWORD, 
				TableColumns.WalkerColumns.WALKER_ID, 
				TableColumns.WalkerColumns.KEY}, 
				null, null, null, null, null);

		if(c.moveToFirst()) {
			toRet = new Walker();

			toRet.username = c.getString(0);
			toRet.password = c.getString(1);
			toRet.id = c.getString(2);
			toRet.key = c.getString(3);
		}

		c.close();

		return toRet;
	}

	public void deleteWalkers() {
		mDb.delete(Tables.WALKER, null, null);
	}


	//--------------------------------------------------------
	//
	//			WALKS
	//
	//---------------
	
	
	public void upladteWalkState(String walkId, String status) {
		ContentValues cv = new ContentValues();
		cv.put(WalksColumns.STATUS, status);
		
		//String query = "UPDATE " + Tables.WALKS + " SET " + WalksColumns.STATUS + "=" + status + " WHERE " + COMMON_COLUMNS.__ID + "=" + walk.i
		
		/*int rowsAffected = */mDb.update(Tables.WALKS, cv, COMMON_COLUMNS.__ID + "=?", new String[] {walkId});
	}
	
	public void deleteWalksOwnersDogs() {
		mDb.delete(Tables.DOGS, null, null);
		mDb.delete(Tables.OWNERS, null, null);
		mDb.delete(Tables.WALKS, null, null);
	}
	

	public void insertWalk(Walk walk , Context context) {
		//check if walk already in database


		if(objectInDatabase(Tables.WALKS, COMMON_COLUMNS.__ID, walk._id)) {

			SyslogUtils.logEvent(context, String.format(
							Locale.getDefault(), "Walk already in database so its not append with id - " +walk._id ),

					EnumLogSeverity.INFORMATIONAL,
					EnumLogType.INTERNAL_TYPE);
			return;
		}
		
		ContentValues cv = new ContentValues();

		//base entity fields
		cv.put(COMMON_COLUMNS.__ID, walk._id);
		cv.put(COMMON_COLUMNS.NID, walk.nid);
		cv.put(COMMON_COLUMNS.SYNC, walk.sync);

		//fields
		cv.put(WalksColumns.DURATION, walk.Duration);
		cv.put(WalksColumns.FEE_VALUE, walk.FeeValue);
		cv.put(WalksColumns.FEE_VALUE_FORMATTED, walk.FeeValueFormatted);
		cv.put(WalksColumns.PRICE_VALUE, walk.PriceValue);
		cv.put(WalksColumns.PRICE_VALUE_FORMATTED, walk.PriceValueFormatted);
		cv.put(WalksColumns.START_DATE, walk.StartDate);
		cv.put(WalksColumns.TIME_MINUS, walk.Minus);
		cv.put(WalksColumns.TIME_PLUS, walk.Plus);
		cv.put(WalksColumns.START_TIME, walk.StartTime);
		cv.put(WalksColumns.STATUS, walk.Status);
		cv.put(WalksColumns.STATUS, walk.Status);

		//composite fields
		if(walk.Location != null) {
			cv.put(WalksColumns.LOCATION_LAT, walk.Location.getLatitudeE6());
			cv.put(WalksColumns.LOCATION_LNG, walk.Location.getLongitudeE6());
		}

		//another table fields
		String dogIDs = WalkUtils.getDogIDs(walk);
		cv.put(WalksColumns.DOG_IDS, dogIDs);
		cv.put(WalksColumns.DOGS_TO_FEED_IDS, walk.DogsToFeedIDs);
		cv.put(WalksColumns.OWNER_ID, walk.Owner._id);
		cv.put(WalksColumns.WALKER_ID, walk.walkerID);
		cv.put(WalksColumns.WALK_TYPE, walk.WalkType);

		cv.put(WalksColumns.ORIGINAL, walk.Original);
		cv.put(WalksColumns.FORMATTED, walk.Formatted);

		cv.put(WalksColumns.NOTESOWNER, walk.NotesOwner);
		cv.put(WalksColumns.WALK_ADDRESS_ORIGINAL, walk.Walk_Address_original);
		cv.put(WalksColumns.WALK_ADDRESS_APPARTMENT, walk.Walk_Address_apartment);


		cv.put(WalksColumns.IDENTIFIER_START_END, walk.getIdentifierStartEndMillis());

		insertToTable(Tables.WALKS, cv);

		//insert dogs
		for(Dog dog : walk.Dogs) {
			insertDog(dog);
		}
		
		for(Services service : walk.Servicess)
		{
			insertService(service , walk._id);
		}

		//insert owner
		insertOwner(walk.Owner);

		/*SyslogUtils.logEvent(context, String.format(
						Locale.getDefault(), "Walk successfully added  in database"),

				EnumLogSeverity.INFORMATIONAL,
				EnumLogType.INTERNAL_TYPE);*/
	}
	
	public Walk getWalkByID(String walkID) {
		Cursor c = mDb.query(Tables.WALKS, 
				new String[] {
				TableColumns.COMMON_COLUMNS.__ID,
				TableColumns.COMMON_COLUMNS.NID,
				TableColumns.COMMON_COLUMNS.SYNC,

				WalksColumns.IDENTIFIER_START_END,

				WalksColumns.DURATION,
				WalksColumns.FEE_VALUE,
				WalksColumns.FEE_VALUE_FORMATTED,
				WalksColumns.PRICE_VALUE,
				WalksColumns.PRICE_VALUE_FORMATTED,
				WalksColumns.START_DATE,
				WalksColumns.TIME_MINUS,
				WalksColumns.TIME_PLUS,
				WalksColumns.START_TIME,
				WalksColumns.STATUS,
				WalksColumns.WALK_TYPE,

						WalksColumns.ORIGINAL,
						WalksColumns.FORMATTED,
						WalksColumns.NOTESOWNER,
						WalksColumns.WALK_ADDRESS_ORIGINAL,
						WalksColumns.WALK_ADDRESS_APPARTMENT,

				WalksColumns.LOCATION_LAT,
				WalksColumns.LOCATION_LNG,

				WalksColumns.OWNER_ID,
				WalksColumns.WALKER_ID,
				WalksColumns.DOG_IDS,
				WalksColumns.DOGS_TO_FEED_IDS
		}, 
		COMMON_COLUMNS.__ID + "=?", new String[]{walkID},
		null, null, null);
		
		Walk walk = null;
		
		if(c.moveToFirst()) {
			walk = new Walk();

			//Base columns
			walk._id = c.getString(c.getColumnIndex(COMMON_COLUMNS.__ID));
			walk.nid = c.getString(c.getColumnIndex(COMMON_COLUMNS.NID));
			walk.sync = c.getInt(c.getColumnIndex(COMMON_COLUMNS.SYNC));

			//identifier
			walk.setIdentifierStartEndMillis(c.getString(3));

			//walk columns
			walk.Duration = c.getInt(c.getColumnIndex(WalksColumns.DURATION));
			walk.FeeValue = c.getInt(c.getColumnIndex(WalksColumns.FEE_VALUE));
			walk.FeeValueFormatted = c.getString(c.getColumnIndex(WalksColumns.FEE_VALUE_FORMATTED));
			walk.PriceValue = c.getInt(c.getColumnIndex(WalksColumns.PRICE_VALUE));
			walk.PriceValueFormatted = c.getString(c.getColumnIndex(WalksColumns.PRICE_VALUE_FORMATTED));
			walk.StartDate = c.getLong(c.getColumnIndex(WalksColumns.START_DATE));
			
			walk.Plus = c.getLong(c.getColumnIndex(WalksColumns.TIME_PLUS));
			walk.Minus = c.getLong(c.getColumnIndex(WalksColumns.TIME_MINUS));
			
			walk.StartTime = c.getLong(c.getColumnIndex(WalksColumns.START_TIME));
			walk.Status = c.getString(c.getColumnIndex(WalksColumns.STATUS));
			walk.WalkType = c.getString(c.getColumnIndex(WalksColumns.WALK_TYPE));

			walk.Original = c.getString(c.getColumnIndex(WalksColumns.ORIGINAL));
			walk.Formatted = c.getString(c.getColumnIndex(WalksColumns.FORMATTED));

			walk.NotesOwner = c.getString(c.getColumnIndex(WalksColumns.NOTESOWNER));
			walk.Walk_Address_original = c.getString(c.getColumnIndex(WalksColumns.WALK_ADDRESS_ORIGINAL));
			walk.Walk_Address_apartment = c.getString(c.getColumnIndex(WalksColumns.WALK_ADDRESS_APPARTMENT));

			//location
			int lat = c.getInt(c.getColumnIndex(WalksColumns.LOCATION_LAT));
			int lng = c.getInt(c.getColumnIndex(WalksColumns.LOCATION_LNG));
			walk.Location = new GeoPoint(lat, lng);

			//composite columns
			String ownerID = c.getString(c.getColumnIndex(WalksColumns.OWNER_ID));
			walk.Owner = selectOwnerById(ownerID);				

			String walkerID = c.getString(c.getColumnIndex(WalksColumns.WALKER_ID));
			walk.walkerID = walkerID;

			String dogIDs = c.getString(c.getColumnIndex(WalksColumns.DOG_IDS));
			String[] ids = ArrayUtils.splitStringToArray(dogIDs, ",");
			String whereClause = DogUtils.getWhereClause(ids.length);

			walk.Dogs = new ArrayList<Dog>();

			walk.Dogs = selectDogsByID(whereClause, ids);
			
			walk.Servicess = new ArrayList<Services>();
			
			walk.Servicess = selectServicesByID(walk._id);
			
			walk.DogsToFeedIDs = c.getString(c.getColumnIndex(WalksColumns.DOGS_TO_FEED_IDS));

			c.moveToNext();
		}

		c.close();

		return walk;
	}
	public Walk getWalkByIDWithoutOwnerAndDogs(String walkID) {
		Cursor c = mDb.query(Tables.WALKS, 
				new String[] {
				TableColumns.COMMON_COLUMNS.__ID,
				TableColumns.COMMON_COLUMNS.NID,
				TableColumns.COMMON_COLUMNS.SYNC,
				
				WalksColumns.IDENTIFIER_START_END,
				
				WalksColumns.DURATION,
				WalksColumns.FEE_VALUE,
				WalksColumns.FEE_VALUE_FORMATTED,
				WalksColumns.PRICE_VALUE,
				WalksColumns.PRICE_VALUE_FORMATTED,
				WalksColumns.START_DATE,
				WalksColumns.TIME_MINUS,
				WalksColumns.TIME_PLUS,
				WalksColumns.START_TIME,
				WalksColumns.STATUS,
				WalksColumns.WALK_TYPE,
				
				WalksColumns.LOCATION_LAT,
				WalksColumns.LOCATION_LNG,
				
				WalksColumns.OWNER_ID,
				WalksColumns.WALKER_ID,
				WalksColumns.DOG_IDS,
				WalksColumns.DOGS_TO_FEED_IDS
		}, 
		COMMON_COLUMNS.__ID + "=?", new String[]{walkID},
		null, null, null);
		
		Walk walk = null;
		
		if(c.moveToFirst()) {
			walk = new Walk();
			
			//Base columns
			walk._id = c.getString(c.getColumnIndex(COMMON_COLUMNS.__ID));
			walk.nid = c.getString(c.getColumnIndex(COMMON_COLUMNS.NID));
			walk.sync = c.getInt(c.getColumnIndex(COMMON_COLUMNS.SYNC));
			
			//identifier
			walk.setIdentifierStartEndMillis(c.getString(c.getColumnIndex(WalksColumns.IDENTIFIER_START_END)));
			
			//walk columns
			walk.Duration = c.getInt(c.getColumnIndex(WalksColumns.DURATION));
			walk.FeeValue = c.getInt(c.getColumnIndex(WalksColumns.FEE_VALUE));
			walk.FeeValueFormatted = c.getString(c.getColumnIndex(WalksColumns.FEE_VALUE_FORMATTED));
			walk.PriceValue = c.getInt(c.getColumnIndex(WalksColumns.PRICE_VALUE));
			walk.PriceValueFormatted = c.getString(c.getColumnIndex(WalksColumns.PRICE_VALUE_FORMATTED));
			walk.StartDate = c.getLong(c.getColumnIndex(WalksColumns.START_DATE));
			walk.Plus = c.getLong(c.getColumnIndex(WalksColumns.TIME_PLUS));
			walk.Minus = c.getLong(c.getColumnIndex(WalksColumns.TIME_MINUS));
			walk.StartTime = c.getLong(c.getColumnIndex(WalksColumns.START_TIME));
			walk.Status = c.getString(c.getColumnIndex(WalksColumns.STATUS));
			walk.WalkType = c.getString(c.getColumnIndex(WalksColumns.WALK_TYPE));
			
			//location
			int lat = c.getInt(c.getColumnIndex(WalksColumns.LOCATION_LAT));
			int lng = c.getInt(c.getColumnIndex(WalksColumns.LOCATION_LNG));
			walk.Location = new GeoPoint(lat, lng);
			
			//composite columns
			//String ownerID = c.getString(15);
			//walk.Owner = selectOwnerById(ownerID);				
			
			walk.walkerID = c.getString(c.getColumnIndex(WalksColumns.WALKER_ID));
			
			//String dogIDs = c.getString(17);
			//String[] ids = ArrayUtils.splitStringToArray(dogIDs, ",");
			//String whereClause = DogUtils.getWhereClause(ids.length);
			
			//walk.Dogs = new ArrayList<Dog>();
			
			//walk.Dogs = selectDogsByID(whereClause, ids);
			walk.DogsToFeedIDs = c.getString(c.getColumnIndex(TableColumns.WalksColumns.DOGS_TO_FEED_IDS));
			
			c.moveToNext();
		}
		
		c.close();
		
		return walk;
	}

	public ArrayList<Walk> getWalksByIdentifierStartEnd(String walkID) {
		ArrayList<Walk> toRet = null;
		Cursor c = mDb.query(Tables.WALKS, 
				new String[] {
				TableColumns.COMMON_COLUMNS.__ID,
				TableColumns.COMMON_COLUMNS.NID,
				TableColumns.COMMON_COLUMNS.SYNC,

				WalksColumns.IDENTIFIER_START_END,

				WalksColumns.DURATION,
				WalksColumns.FEE_VALUE,
				WalksColumns.FEE_VALUE_FORMATTED,
				WalksColumns.PRICE_VALUE,
				WalksColumns.PRICE_VALUE_FORMATTED,
				WalksColumns.START_DATE,
				WalksColumns.TIME_MINUS,
				WalksColumns.TIME_PLUS,
				WalksColumns.START_TIME,
				WalksColumns.STATUS,
				WalksColumns.WALK_TYPE,

				WalksColumns.LOCATION_LAT,
				WalksColumns.LOCATION_LNG,

				WalksColumns.OWNER_ID,
				WalksColumns.WALKER_ID,
				WalksColumns.DOG_IDS,
				WalksColumns.DOGS_TO_FEED_IDS
		}, 
		TableColumns.WalksColumns.IDENTIFIER_START_END + "=?", new String[]{walkID},
		null, null, null);

		if(c.moveToFirst())
		{
			toRet = new ArrayList<Walk>();

			while(!c.isAfterLast())
			{
				Walk walk = new Walk();

				//Base columns
				walk._id = c.getString(c.getColumnIndex(COMMON_COLUMNS.__ID));
				walk.nid = c.getString(c.getColumnIndex(COMMON_COLUMNS.NID));
				walk.sync = c.getInt(c.getColumnIndex(COMMON_COLUMNS.SYNC));

				//identifier
				walk.setIdentifierStartEndMillis(c.getString(3));

				//walk columns
				walk.Duration = c.getInt(c.getColumnIndex(WalksColumns.DURATION));
				walk.FeeValue = c.getInt(c.getColumnIndex(WalksColumns.FEE_VALUE));
				walk.FeeValueFormatted = c.getString(c.getColumnIndex(WalksColumns.FEE_VALUE_FORMATTED));
				walk.PriceValue = c.getInt(c.getColumnIndex(WalksColumns.PRICE_VALUE));
				walk.PriceValueFormatted = c.getString(c.getColumnIndex(WalksColumns.PRICE_VALUE_FORMATTED));
				walk.StartDate = c.getLong(c.getColumnIndex(WalksColumns.START_DATE));
				walk.Minus = c.getLong(c.getColumnIndex(WalksColumns.TIME_MINUS));
				walk.Plus = c.getLong(c.getColumnIndex(WalksColumns.TIME_PLUS));
				walk.StartTime = c.getLong(c.getColumnIndex(WalksColumns.START_TIME));
				walk.Status = c.getString(c.getColumnIndex(WalksColumns.STATUS));
				walk.WalkType = c.getString(c.getColumnIndex(WalksColumns.WALK_TYPE));

				//location
				int lat = c.getInt(c.getColumnIndex(WalksColumns.LOCATION_LAT));
				int lng = c.getInt(c.getColumnIndex(WalksColumns.LOCATION_LNG));
				walk.Location = new GeoPoint(lat, lng);

				//composite columns
				String ownerID = c.getString(c.getColumnIndex(WalksColumns.OWNER_ID));
				walk.Owner = selectOwnerById(ownerID);				
				
				String walkerID = c.getString(c.getColumnIndex(WalksColumns.WALKER_ID));
				walk.walkerID = walkerID;
				
				String dogIDs = c.getString(c.getColumnIndex(WalksColumns.DOG_IDS));
				String[] ids = ArrayUtils.splitStringToArray(dogIDs, ",");
				String whereClause = DogUtils.getWhereClause(ids.length);
				
				walk.Dogs = new ArrayList<Dog>();
				walk.Dogs = selectDogsByID(whereClause, ids);
				walk.DogsToFeedIDs = c.getString(c.getColumnIndex(WalksColumns.DOGS_TO_FEED_IDS));
				
				toRet.add(walk);
				
				c.moveToNext();
			}
		}

		c.close();

		return toRet;
	}
	
	public int deleteWalks() {
		return mDb.delete(Tables.WALKS, null, null);
	}



	 //--------------------------------------------------------
	 //nkp
	 //			COMMENTS
	 //
	 //--------------

	 public void insertComment(Comment comment) {
		 //check if walk already in database
		 if(objectInDatabase(Tables.COMMENTS, CommentsColumn._ID, comment._id)) {
			 return;
		 }

		 ContentValues cv = new ContentValues();

		 //base entity fields
		 cv.put(COMMON_COLUMNS.NID, comment.nid);

		 //fields
		 cv.put(CommentsColumn.TITLE, comment.title);
		 cv.put(CommentsColumn.BODY, comment.body);
		 cv.put(CommentsColumn.CREATED, comment.created);
		 cv.put(CommentsColumn.CID, comment.cid);
		 cv.put(CommentsColumn._ID, comment._id);
		 cv.put(CommentsColumn.AUTHOR_PICTURE, comment.author_picture);
		 cv.put(CommentsColumn.AUTHOR_NAME, comment.author_name);

		 insertToTable(Tables.COMMENTS, cv);
	 }


	//--------------------------------------------------------
	//
	//			DOGS
	//
	//--------------
	
	public void insertDog(Dog dog) {
		//check if walk already in database
		if(objectInDatabase(Tables.DOGS, COMMON_COLUMNS.__ID, dog._id)) {
			return;
		}
		
		ContentValues cv = new ContentValues();
		
		//base entity fields
		cv.put(COMMON_COLUMNS.__ID, dog._id);
		cv.put(COMMON_COLUMNS.NID, dog.nid);
		cv.put(COMMON_COLUMNS.SYNC, dog.sync);
		
		//fields
		cv.put(DogsColumns.ADDITIONAL_INFO, dog.AdditionalInfo);
		cv.put(DogsColumns.BIRTH_DATE, dog.BirthData);
		cv.put(DogsColumns.BREED, dog.Breed);
		cv.put(DogsColumns.COMMAND_COME, dog.Commands.Come);
		cv.put(DogsColumns.COMMAND_PRAISE, dog.Commands.Praise);
		cv.put(DogsColumns.COMMAND_SIT, dog.Commands.Sit);
		cv.put(DogsColumns.COMMAND_STOP, dog.Commands.Stop);
		cv.put(DogsColumns.EMERGENCY_PHONE_CONTACT, dog.EmergencyPhoneContact);
		cv.put(DogsColumns.EMERGENCY_PHONE_VET, dog.EmergencyPhoneVet);
		cv.put(DogsColumns.FD_ALLERGIC, dog.FeaturesDetails.Allergic);
		cv.put(DogsColumns.FD_MEDICATION, dog.FeaturesDetails.Medication);
		cv.put(DogsColumns.FD_SITUATION_CHILD, dog.FeaturesDetails.SituationChild);
		cv.put(DogsColumns.FD_SITUATION_DOG, dog.FeaturesDetails.SituationDog);
		cv.put(DogsColumns.FD_SITUATION_STRANGER, dog.FeaturesDetails.SituationStranger);
		cv.put(DogsColumns.FD_SITUATION_TOYS, dog.FeaturesDetails.SituationToys);
		
		cv.put(DogsColumns.FEAT_AGGRESSIVE, boolToInt(dog.Features.Aggressive));
		cv.put(DogsColumns.FEAT_ALERGIC, boolToInt(dog.Features.Alergic));
		cv.put(DogsColumns.FEAT_CHILD_NERVOUS, boolToInt(dog.Features.ChildNervous));
		cv.put(DogsColumns.FEAT_COLD_SENSITIVE, boolToInt(dog.Features.ColdSensitive));
		cv.put(DogsColumns.FEAT_DOG_NERVOUS, boolToInt(dog.Features.Dognervous));
		cv.put(DogsColumns.FEAT_FRIENDLY, boolToInt(dog.Features.Friendly));
		cv.put(DogsColumns.FEAT_HOT_SENSITIVE, boolToInt(dog.Features.Hotsensitive));
		cv.put(DogsColumns.FEAT_MEDICATION, boolToInt(dog.Features.Medication));
		cv.put(DogsColumns.FEAT_NO_TREATS_NEEDED, boolToInt(dog.Features.NotreatsNeeded));
		cv.put(DogsColumns.FEAT_NO_TREATS, boolToInt(dog.Features.Notreats));
		cv.put(DogsColumns.FEAT_PULLEASH, boolToInt(dog.Features.Pullleash));
		cv.put(DogsColumns.FEAT_RAIN_SENSITIVE, boolToInt(dog.Features.RainSensitive));
		cv.put(DogsColumns.FEAT_STRANGER_NERVOUS, boolToInt(dog.Features.StrangerNervous));
		cv.put(DogsColumns.FEAT_TOYS_NERVOUS, boolToInt(dog.Features.ToysNervous));
		cv.put(DogsColumns.FEED, boolToInt(dog.Feed));
		
		cv.put(DogsColumns.GENDER, dog.Gender);
		cv.put(DogsColumns.NAME, dog.Name);
		cv.put(DogsColumns.PIC, dog.Pic);

        String commentIDs = DogUtils.getCommentIDs(dog);
        cv.put(DogsColumns.COMMENT_IDS, commentIDs);
		
		//composite fields
		if(dog.Location != null)
		{
			cv.put(DogsColumns.LOCATION_LAT, dog.Location.getLatitudeE6());
			cv.put(DogsColumns.LOCATION_LNG, dog.Location.getLongitudeE6());
		}
		
		//another tables fields
		cv.put(DogsColumns.OWNER_ID, dog.ownerID);
		
		insertToTable(Tables.DOGS, cv);


        //insert comments nkp
        for(Comment comment : dog.Comments) {
            insertComment(comment);
        }
	}
	
	
	//--------------------------------------------------------
		//
		//			SERVIVES
		//
		//--------------
		
		public void insertService(Services service , String walkid) {
			//check if walk already in database
			if(objectInDatabase(Tables.SERVICES, COMMON_COLUMNS.__ID, service._Id)) {
				return;
			}
			
			ContentValues cv = new ContentValues();
			
			//base entity fields
			cv.put(COMMON_COLUMNS.__ID, service._Id);
			cv.put(WalkerColumns.WALKER_ID, walkid);
//			cv.put(COMMON_COLUMNS.SYNC, dog.sync);
			
			//fields
			cv.put(ServicesColumn.FEED, service.FeedInstructions);
			cv.put(ServicesColumn.MEDICINE, service.MedicateInstructions);
			cv.put(ServicesColumn.ISFEED, boolToInt(service.Feed));
			cv.put(ServicesColumn.ISMEDICINE, boolToInt(service.Medicate));
			
			cv.put(ServicesColumn.SERVICEID, service._Id);
			cv.put(ServicesColumn.DOGID, service.Dog_Id);
			
			insertToTable(Tables.SERVICES, cv);
		}


     public ArrayList<Comment> selectCommentByID(String whereClause, String[] IDs) {
         ArrayList<Comment> toRet = null;
         Cursor c = mDb.query(Tables.COMMENTS, null, whereClause, IDs, null, null, CommentsColumn.CID+" ASC");

         if(c.moveToFirst()) {
             toRet = new ArrayList<Comment>();

             while(!c.isAfterLast()) {
                 Comment comment = new Comment();

                 //fields
                 comment._id = c.getString(c.getColumnIndex(CommentsColumn._ID));
                 comment.title = c.getString(c.getColumnIndex(CommentsColumn.TITLE));
                 comment.body = c.getString(c.getColumnIndex(CommentsColumn.BODY));
                 comment.created = c.getString(c.getColumnIndex(CommentsColumn.CREATED));
                 comment.cid = c.getString(c.getColumnIndex(CommentsColumn.CID));
                 comment.author_picture = c.getString(c.getColumnIndex(CommentsColumn.AUTHOR_PICTURE));
                 comment.author_name = c.getString(c.getColumnIndex(CommentsColumn.AUTHOR_NAME));
                 comment.nid = c.getString(c.getColumnIndex(CommentsColumn.N_ID));

                 toRet.add(comment);
                 c.moveToNext();
             }
         }
         c.close();

         return toRet;
     }
	
	public ArrayList<Dog> selectDogsByID(String whereClause, String[] IDs) {
		ArrayList<Dog> toRet = null;
		Cursor c = mDb.query(Tables.DOGS, null, whereClause, IDs, null, null, null);
		
		if(c.moveToFirst()) {
			toRet = new ArrayList<Dog>();
			
			while(!c.isAfterLast()) {
				Dog dog = new Dog();
				
				//Base columns
				dog._id = c.getString(c.getColumnIndex(COMMON_COLUMNS.__ID));
				dog.nid = c.getString(c.getColumnIndex(COMMON_COLUMNS.NID));
				dog.sync = c.getInt(c.getColumnIndex(COMMON_COLUMNS.SYNC));
				
				//fields
				dog.AdditionalInfo = c.getString(c.getColumnIndex(DogsColumns.ADDITIONAL_INFO));
				dog.BirthData = c.getString(c.getColumnIndex(DogsColumns.BIRTH_DATE));
				dog.Breed = c.getString(c.getColumnIndex(DogsColumns.BREED));
				
				dog.Commands = new DogCommands();
				dog.Commands.Come = c.getString(c.getColumnIndex(DogsColumns.COMMAND_COME));
				dog.Commands.Praise = c.getString(c.getColumnIndex(DogsColumns.COMMAND_PRAISE));
				dog.Commands.Sit = c.getString(c.getColumnIndex(DogsColumns.COMMAND_SIT));
				dog.Commands.Stop = c.getString(c.getColumnIndex(DogsColumns.COMMAND_STOP));

				dog.EmergencyPhoneContact = c.getString(c.getColumnIndex(DogsColumns.EMERGENCY_PHONE_CONTACT));
				dog.EmergencyPhoneVet = c.getString(c.getColumnIndex(DogsColumns.EMERGENCY_PHONE_VET));
				
				dog.FeaturesDetails = new FeaturesDetails();
				dog.FeaturesDetails.Allergic = c.getString(c.getColumnIndex(DogsColumns.FD_ALLERGIC));
				dog.FeaturesDetails.Medication = c.getString(c.getColumnIndex(DogsColumns.FD_MEDICATION));
				dog.FeaturesDetails.SituationChild = c.getString(c.getColumnIndex(DogsColumns.FD_SITUATION_CHILD));
				dog.FeaturesDetails.SituationDog = c.getString(c.getColumnIndex(DogsColumns.FD_SITUATION_DOG));
				dog.FeaturesDetails.SituationStranger = c.getString(c.getColumnIndex(DogsColumns.FD_SITUATION_STRANGER));
				dog.FeaturesDetails.SituationToys = c.getString(c.getColumnIndex(DogsColumns.FD_SITUATION_TOYS));
				
				dog.Features = new Features();
				dog.Features.Aggressive = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_AGGRESSIVE)));
				dog.Features.Alergic = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_ALERGIC)));
				dog.Features.ChildNervous = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_CHILD_NERVOUS)));
				dog.Features.ColdSensitive = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_COLD_SENSITIVE)));
				dog.Features.Dognervous = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_DOG_NERVOUS)));
				dog.Features.Friendly = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_FRIENDLY)));
				dog.Features.Hotsensitive = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_HOT_SENSITIVE)));
				dog.Features.Medication = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_MEDICATION)));
				dog.Features.NotreatsNeeded = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_NO_TREATS_NEEDED)));
				dog.Features.Notreats = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_NO_TREATS)));
				dog.Features.Pullleash = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_PULLEASH)));
				dog.Features.RainSensitive = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_RAIN_SENSITIVE)));
				dog.Features.StrangerNervous = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_STRANGER_NERVOUS)));
				dog.Features.ToysNervous = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEAT_TOYS_NERVOUS)));
				dog.Feed = intToBool(c.getInt(c.getColumnIndex(DogsColumns.FEED)));
				
				dog.Gender = c.getString(c.getColumnIndex(DogsColumns.GENDER));
				dog.Name = c.getString(c.getColumnIndex(DogsColumns.NAME));

				dog.Pic = c.getString(c.getColumnIndex(DogsColumns.PIC));

				int lat = c.getInt(c.getColumnIndex(DogsColumns.LOCATION_LAT));
				int lng = c.getInt(c.getColumnIndex(DogsColumns.LOCATION_LNG));
				
				dog.Location = new GeoPoint((int)(lat * GeoUtils.MULTIPLEXOR), (int)(lng * GeoUtils.MULTIPLEXOR));
				
				dog.ownerID = c.getString(c.getColumnIndex(DogsColumns.OWNER_ID));

				//nkp
                String commentIDs = c.getString(c.getColumnIndex(DogsColumns.COMMENT_IDS));
                String[] ids = ArrayUtils.splitStringToArray(commentIDs, ",");
                String whereClause1 = DogUtils.getWhereClauseComment(ids.length);

				//Log.d("nkp whereClause1", ""+ whereClause1 + " ids "+ commentIDs + " dog name "+ dog.Name);

                dog.Comments = new ArrayList<Comment>();

               // dog.Comments = selectCommentByID("nid=?", new String[]{dog.nid});
                dog.Comments = selectCommentByID(whereClause1, ids);

				toRet.add(dog);
				c.moveToNext();
			}
			}
		c.close();
		
		return toRet;
		}
		
		public ArrayList<Services> selectServicesByID(String walkid) {
			ArrayList<Services> toRet = null;
			Cursor c = mDb.query(Tables.SERVICES, null, WalkerColumns.WALKER_ID+"=?", new String[]{walkid}, null, null, null);
			
			
			if(c.moveToFirst()) {
				toRet = new ArrayList<Services>();
				
				while(!c.isAfterLast()) {
					Services service = new Services();
					
					//Base columns
					service._Id = c.getString(c.getColumnIndex(COMMON_COLUMNS.__ID));
					//dog.nid = c.getString(c.getColumnIndex(COMMON_COLUMNS.NID));
					//dog.sync = c.getInt(c.getColumnIndex(COMMON_COLUMNS.SYNC));
					
					//fields
					service.FeedInstructions = c.getString(c.getColumnIndex(ServicesColumn.FEED));
					service.Dog_Id = c.getString(c.getColumnIndex(ServicesColumn.DOGID));
					service.MedicateInstructions = c.getString(c.getColumnIndex(ServicesColumn.MEDICINE));
					service.Feed = intToBool(c.getInt(c.getColumnIndex(ServicesColumn.ISFEED)));
					service.Medicate = intToBool(c.getInt(c.getColumnIndex(ServicesColumn.ISMEDICINE)));
					
					toRet.add(service);
					c.moveToNext();
				}
			}
		
		c.close();
		
		return toRet;
	}
	
	public int deleteDogs() {
		return mDb.delete(Tables.DOGS, null, null);
	}

	 public int deleteComments() {
		 return mDb.delete(Tables.COMMENTS, null, null);
	 }



	//--------------------------------------------------------
	//
	//			OWNERS
	//
	//----------------

	public OwnerOfDog selectOwnerById(String ownerId) {
		OwnerOfDog owner = null;
		
		if(!mDb.isOpen()) {
			mDb = dbHelper.getReadableDatabase();
		}

		Cursor c = mDb.query(Tables.OWNERS, null, 
				TableColumns.COMMON_COLUMNS.__ID + "=?", new String[]{ownerId},
				null, null, null);
		
		if(c.moveToFirst()) {
			owner = new OwnerOfDog();
			
			//int count = 1;
			//base entity
			owner._id = c.getString(c.getColumnIndex(COMMON_COLUMNS.__ID));
			owner.nid = c.getString(c.getColumnIndex(COMMON_COLUMNS.NID));
			owner.sync = c.getInt(c.getColumnIndex(COMMON_COLUMNS.SYNC));
			
			//fields
			owner.AnimalIDs = ArrayUtils.splitString(c.getString(c.getColumnIndex(OwnerColumns.ANIMAL_IDS)), ",");
			owner.BirthYear = c.getInt(c.getColumnIndex(OwnerColumns.BIRTH_YEAR));
			owner.Email = c.getString(c.getColumnIndex(OwnerColumns.EMAIL));
			owner.FirstName = c.getString(c.getColumnIndex(OwnerColumns.FIRST_NAME));
			owner.Gender = c.getString(c.getColumnIndex(OwnerColumns.GENDER));
			owner.LastName = c.getString(c.getColumnIndex(OwnerColumns.LAST_NAME));
			
			StaticLocation staticLocation = new StaticLocation();
			staticLocation.Access = c.getString(c.getColumnIndex(OwnerColumns.LOC_ACCESS));
			staticLocation.AccessInfo = c.getString(c.getColumnIndex(OwnerColumns.LOC_ACCESS_INFO));
			staticLocation.Area = c.getString(c.getColumnIndex(OwnerColumns.LOC_AREA));
			staticLocation.Apartment = c.getString(c.getColumnIndex(OwnerColumns.LOC_APARTMENT));
			staticLocation.City = c.getString(c.getColumnIndex(OwnerColumns.LOC_CITY));
			staticLocation.Country = c.getString(c.getColumnIndex(OwnerColumns.LOC_COUNTRY));
			staticLocation.Formatted = c.getString(c.getColumnIndex(OwnerColumns.LOC_FORMATTED));
			staticLocation.Neighborhood = c.getString(c.getColumnIndex(OwnerColumns.LOC_NEIGHBORHOOD));
			staticLocation.Original = c.getString(c.getColumnIndex(OwnerColumns.LOC_ORIGINAL));
			staticLocation.State = c.getString(c.getColumnIndex(OwnerColumns.LOC_STATE));
			staticLocation.Street = c.getString(c.getColumnIndex(OwnerColumns.LOC_STREET));
			staticLocation.StreetNum = c.getString(c.getColumnIndex(OwnerColumns.LOC_STREET_NUM));
			staticLocation.Timestamp = c.getDouble(c.getColumnIndex(OwnerColumns.LOC_TIMESTAMP));
			staticLocation.Zip = c.getString(c.getColumnIndex(OwnerColumns.LOC_ZIP));
			
			owner.StaticLocation = staticLocation;
			
			owner.MethodEmail = intToBool(c.getInt(c.getColumnIndex(OwnerColumns.METHOD_EMAIL)));
			owner.MethodPush = intToBool(c.getInt(c.getColumnIndex(OwnerColumns.METHOD_PUSH)));
			owner.MethodSms = intToBool(c.getInt(c.getColumnIndex(OwnerColumns.METHOD_SMS)));
			owner.PhonePrimary = c.getString(c.getColumnIndex(OwnerColumns.PHONE_PRIMARY));
			
			owner.SocialInfo = new SocialInfo();
			owner.SocialInfo.FacebookFriends = ArrayUtils.splitString(c.getString(c.getColumnIndex(OwnerColumns.SOCIAL_FACEBOOK_FRIENDS)), ",");
			owner.UID = c.getInt(c.getColumnIndex(OwnerColumns.UID));
			owner.UserName = c.getString(c.getColumnIndex(OwnerColumns.USERNAME));
			
			boolean isLatNull = c.isNull(c.getColumnIndex(OwnerColumns.LOC_LAT));
			boolean isLngNull = c.isNull(c.getColumnIndex(OwnerColumns.LOC_LNG));
			
			if(!isLatNull && !isLngNull) {
				int lat = c.getInt(c.getColumnIndex(OwnerColumns.LOC_LAT));
				int lng = c.getInt(c.getColumnIndex(OwnerColumns.LOC_LNG));
				owner.StaticLocation.Location = new GeoPoint(lat, lng);
			}
		}
		
		c.close();
		
		return owner;
	}

	public void insertOwner(OwnerOfDog owner) {
		//check if walk already in database
		if(objectInDatabase(Tables.OWNERS, COMMON_COLUMNS.__ID, owner._id)) {
			return;
		}
		
		ContentValues cv = new ContentValues();
		
		//base entity fields
		cv.put(COMMON_COLUMNS.__ID, owner._id);
		cv.put(COMMON_COLUMNS.NID, owner.nid);
		cv.put(COMMON_COLUMNS.SYNC, owner.sync);
		
		//fields
		cv.put(OwnerColumns.ANIMAL_IDS, OwnerUtils.getAnimalIDS(owner));
		cv.put(OwnerColumns.BIRTH_YEAR, owner.BirthYear);
		cv.put(OwnerColumns.EMAIL, owner.Email);
		cv.put(OwnerColumns.FIRST_NAME, owner.FirstName);
		cv.put(OwnerColumns.GENDER, owner.Gender);
		cv.put(OwnerColumns.LAST_NAME, owner.LastName);
		cv.put(OwnerColumns.LOC_ACCESS, owner.StaticLocation.Access);
		cv.put(OwnerColumns.LOC_ACCESS_INFO, owner.StaticLocation.AccessInfo);
		cv.put(OwnerColumns.LOC_AREA, owner.StaticLocation.Area);
		cv.put(OwnerColumns.LOC_APARTMENT, owner.StaticLocation.Apartment);
		cv.put(OwnerColumns.LOC_CITY, owner.StaticLocation.City);
		cv.put(OwnerColumns.LOC_COUNTRY, owner.StaticLocation.Country);
		cv.put(OwnerColumns.LOC_FORMATTED, owner.StaticLocation.Formatted);
		cv.put(OwnerColumns.LOC_NEIGHBORHOOD, owner.StaticLocation.Neighborhood);
		cv.put(OwnerColumns.LOC_ORIGINAL, owner.StaticLocation.Original);
		cv.put(OwnerColumns.LOC_STATE, owner.StaticLocation.State);
		cv.put(OwnerColumns.LOC_STREET, owner.StaticLocation.Street);
		cv.put(OwnerColumns.LOC_STREET_NUM, owner.StaticLocation.StreetNum);
		cv.put(OwnerColumns.LOC_TIMESTAMP, owner.StaticLocation.Timestamp);
		cv.put(OwnerColumns.LOC_ZIP, owner.StaticLocation.Zip);
		cv.put(OwnerColumns.METHOD_EMAIL, boolToInt(owner.MethodEmail));
		cv.put(OwnerColumns.METHOD_PUSH, boolToInt(owner.MethodPush));
		cv.put(OwnerColumns.METHOD_SMS, boolToInt(owner.MethodSms));
		cv.put(OwnerColumns.PHONE_PRIMARY, owner.PhonePrimary);
		cv.put(OwnerColumns.SOCIAL_FACEBOOK_FRIENDS, OwnerUtils.getSocialFacebookFriendsIDS(owner));
		cv.put(OwnerColumns.UID, owner.UID);
		cv.put(OwnerColumns.USERNAME, owner.UserName);
		
		//composite fields
		if(owner.StaticLocation.Location == null) {
			cv.putNull(OwnerColumns.LOC_LAT);
			cv.putNull(OwnerColumns.LOC_LNG);
		} else {
			cv.put(OwnerColumns.LOC_LAT, owner.StaticLocation.Location == null ? null : owner.StaticLocation.Location.getLatitudeE6());
			cv.put(OwnerColumns.LOC_LNG, owner.StaticLocation.Location == null ? null : owner.StaticLocation.Location.getLongitudeE6());
		}
		insertToTable(Tables.OWNERS, cv);
	}


	public int deleteOwners() {
		return mDb.delete(Tables.OWNERS, null, null);
	}


	
	
	
	//--------------------------------------------------------
	//
	//			PERFORMED_REQUESTS
	//
	//----------------------------
	
	public void insertPerformedRequest(PerformedRequests performedRequest) {
		ContentValues cv = new ContentValues();
		
		//base entity fields
		cv.put(TableColumns.PerformedRequestsColumns.ERROR_DESCRIPTION, performedRequest.getErrorDescription());
		cv.put(TableColumns.PerformedRequestsColumns.MD5, performedRequest.getMD5());
		cv.put(TableColumns.PerformedRequestsColumns.SUCCESS, boolToInt(performedRequest.getIsSuccess()));
		cv.put(TableColumns.PerformedRequestsColumns.TEXT, performedRequest.getText());
		cv.put(TableColumns.PerformedRequestsColumns.WALK_ID, performedRequest.getWalkId());
		
		insertToTableConflictReplace(Tables.PERFORMED_REQUESTS, cv);
	}
	
	public int deletePerformedRequests() {
		return mDb.delete(Tables.PERFORMED_REQUESTS, null, null);
	}
	
	public int deletePerformedRequerstWithBody(String body) {
		return mDb.delete(Tables.PERFORMED_REQUESTS, PerformedRequestsColumns.TEXT + "=?", new String[]{body});
	}
	
	public ArrayList<PerformedRequests> selectPreformedRequests(String walkId) {
		ArrayList<PerformedRequests> requests = new ArrayList<PerformedRequests>();
		
		//Cursor c = mDb.query(Tables.PERFORMED_REQUESTS, null, PerformedRequestsColumns.SUCCESS + "=?", new String[], groupBy, having, orderBy);
		Cursor c = mDb.query(Tables.PERFORMED_REQUESTS, null, PerformedRequestsColumns.WALK_ID + "=?", new String[] {walkId}, null, null, null);
		
		if(c.moveToFirst()) {
			while(!c.isAfterLast()) {
				PerformedRequests oneRequest = new PerformedRequests();

				oneRequest.setErrorDescription(c.getString(c.getColumnIndex(PerformedRequestsColumns.ERROR_DESCRIPTION)));
				oneRequest.setMD5(c.getString(c.getColumnIndex(PerformedRequestsColumns.MD5)));
				oneRequest.setIsSuccess(intToBool(c.getInt(c.getColumnIndex(PerformedRequestsColumns.SUCCESS))));
				oneRequest.setText(c.getString(c.getColumnIndex(PerformedRequestsColumns.TEXT)));
				oneRequest.setWalkId(c.getString(c.getColumnIndex(PerformedRequestsColumns.WALK_ID)));

				requests.add(oneRequest);

				c.moveToNext();
			}
		}
		
		c.close();
		
		return requests;
	}
	
	
	
	//-----------------------------------------------------
	//
	//			WalkGpsPoints
	//
	//------------------------
	
	
	public void addWalkGpsPoint(int lat, int lng, long timespan, String type, String walkId, String state, String md5, float accuracy) {
		ContentValues cv = new ContentValues();
		cv.put(WalkGPSPointsColumns.LATITUDE, lat);
		cv.put(WalkGPSPointsColumns.LONGITUDE, lng);
		cv.put(WalkGPSPointsColumns.TIMESTAMP, timespan);
		cv.put(WalkGPSPointsColumns.TYPE, type);
		cv.put(WalkGPSPointsColumns.WALK_ID, walkId);
		cv.put(WalkGPSPointsColumns.STATUS, state);
		cv.put(WalkGPSPointsColumns.MD5, md5);
		cv.put(WalkGPSPointsColumns.ACCURACY, accuracy);
		
		insertToTable(Tables.WALK_GPS_POINTS, cv);
	}
	
	public void addWalkGpsPoint(WalkGPSPoint walkGpsPoint) {
		addWalkGpsPoint(walkGpsPoint.getLatitudeE6(), walkGpsPoint.getLongitudeE6(), walkGpsPoint.getTimestamp(), walkGpsPoint.Type, walkGpsPoint.WalkId, walkGpsPoint.Status, walkGpsPoint.MD5, walkGpsPoint.Accuracy);
	}
	
	public ArrayList<WalkGPSPoint> selectWalkGpsPointsForWalkByType(String walkId, String type) {
		final ArrayList<WalkGPSPoint> toRet = new ArrayList<WalkGPSPoint>();
		
		Cursor c = mDb.query(Tables.WALK_GPS_POINTS, 
				new String[]{WalkGPSPointsColumns.ACCURACY, 
							WalkGPSPointsColumns.LATITUDE, 
							WalkGPSPointsColumns.LONGITUDE, 
							WalkGPSPointsColumns.TIMESTAMP,
							WalkGPSPointsColumns.STATUS,
							WalkGPSPointsColumns.MD5},
							WalkGPSPointsColumns.WALK_ID + "=? AND " + WalkGPSPointsColumns.TYPE + "=?", new String[]{walkId, type}, null, null, WalkGPSPointsColumns.TIMESTAMP);
		
		if(c.moveToFirst()) {
			while(!c.isAfterLast()) {
				int lat = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LATITUDE));
				int lng = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LONGITUDE));
				long timestamp = c.getLong(c.getColumnIndex(WalkGPSPointsColumns.TIMESTAMP));
				//String Type = c.getString(c.getColumnIndex(WalkGPSPointsColumns.TYPE));
				//String WalkIdRes = c.getString(c.getColumnIndex(WalkGPSPointsColumns.WALK_ID));
				String State = c.getString(c.getColumnIndex(WalkGPSPointsColumns.STATUS));
				String MD5 = c.getString(c.getColumnIndex(WalkGPSPointsColumns.MD5));
				float accuracy = c.getFloat(c.getColumnIndex(WalkGPSPointsColumns.ACCURACY));

				final WalkGPSPoint point = new WalkGPSPoint(lat, lng, timestamp);
				
				point.Type = type;
				point.WalkId = walkId;
				point.Status = State;
				point.MD5 = MD5;
				point.Accuracy = accuracy;

				toRet.add(point);
				c.moveToNext();
			}
		}
		
		c.close();
		
		return toRet;
	}

	public ArrayList<WalkGPSPoint> selectWalkGpsPointsForWalkByTypeAndStatus(String walkId, String type, String status) {
		ArrayList<WalkGPSPoint> toRet = new ArrayList<WalkGPSPoint>();
		
		Cursor c = mDb.query(Tables.WALK_GPS_POINTS, new String[]{WalkGPSPointsColumns.ACCURACY, 
																WalkGPSPointsColumns.LATITUDE, 
																WalkGPSPointsColumns.LONGITUDE, 
																WalkGPSPointsColumns.TIMESTAMP,
																WalkGPSPointsColumns.MD5},
																WalkGPSPointsColumns.WALK_ID + "=? AND " + WalkGPSPointsColumns.TYPE + "=? AND " + WalkGPSPointsColumns.STATUS + "=?", new String[]{walkId, type, status},
																null, null, WalkGPSPointsColumns.TIMESTAMP);
		
		if(c.moveToFirst()) {
			while(!c.isAfterLast()) {
				int lat = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LATITUDE));
				int lng = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LONGITUDE));
				long timestamp = c.getLong(c.getColumnIndex(WalkGPSPointsColumns.TIMESTAMP));
				//String Type = c.getString(c.getColumnIndex(WalkGPSPointsColumns.TYPE));
				//String WalkIdRes = c.getString(c.getColumnIndex(WalkGPSPointsColumns.WALK_ID));
				//String State = c.getString(c.getColumnIndex(WalkGPSPointsColumns.STATUS));
				String MD5 = c.getString(c.getColumnIndex(WalkGPSPointsColumns.MD5));
				float accuracy = c.getFloat(c.getColumnIndex(WalkGPSPointsColumns.ACCURACY));
				
				WalkGPSPoint point = new WalkGPSPoint(lat, lng, timestamp);
				
				point.Type = type;
				point.WalkId = walkId;
				point.Status = status;
				point.MD5 = MD5;
				point.Accuracy = accuracy;
				
				toRet.add(point);
				c.moveToNext();
			}
		}
		
		c.close();
		
		return toRet;
	}
	
	public ArrayList<WalkGPSPoint> selectUnsentMeetAndPoopPointsOrderByTimestamp(String walkId) {
		Cursor c = mDb.query(Tables.WALK_GPS_POINTS, null, WalkGPSPointsColumns.STATUS + "=? AND (" + WalkGPSPointsColumns.TYPE + "=? OR " + WalkGPSPointsColumns.TYPE + "=?)", new String[]{EnumWalkGPSPointSendStatus.NOT_SENT, EnumWalkGPSPointType.MEET, EnumWalkGPSPointType.POOP}, null, null, WalkGPSPointsColumns.TIMESTAMP);
		ArrayList<WalkGPSPoint> toRet = null;
		
		if(c.moveToFirst()) {
			toRet = new ArrayList<WalkGPSPoint>();
			
			while(!c.isAfterLast()) {
				int lat = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LATITUDE));
				int lng = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LONGITUDE));
				long timestamp = c.getLong(c.getColumnIndex(WalkGPSPointsColumns.TIMESTAMP));
				WalkGPSPoint point = new WalkGPSPoint(lat, lng, timestamp);

				point.Type = c.getString(c.getColumnIndex(WalkGPSPointsColumns.TYPE));
				//String WalkIdRes = c.getString(c.getColumnIndex(WalkGPSPointsColumns.WALK_ID));
				//String State = c.getString(c.getColumnIndex(WalkGPSPointsColumns.STATUS));
				String MD5 = c.getString(c.getColumnIndex(WalkGPSPointsColumns.MD5));
				float accuracy = c.getFloat(c.getColumnIndex(WalkGPSPointsColumns.ACCURACY));
				
				
				point.WalkId = walkId;
				point.Status = EnumWalkGPSPointSendStatus.NOT_SENT;
				point.MD5 = MD5;
				point.Accuracy = accuracy;
				
				toRet.add(point);
				c.moveToNext();
			}
		}
		
		c.close();
		
		return toRet;
	}
	
	public WalkGPSPoint selectLastRouteWalkGpsPoint(String walkId) {
		Cursor c = mDb.query(Tables.WALK_GPS_POINTS, null, WalkGPSPointsColumns.WALK_ID + "=? AND " + WalkGPSPointsColumns.TYPE + "=?", new String[]{walkId, EnumWalkGPSPointType.ROUTE}, null, null, WalkGPSPointsColumns.TIMESTAMP + " DESC", "1");
		WalkGPSPoint toRet = null;
		
		if(c.moveToFirst()) {
			int lat = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LATITUDE));
			int lng = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LONGITUDE));
			long ts = c.getLong(c.getColumnIndex(WalkGPSPointsColumns.TIMESTAMP));
			float accuracy = c.getLong(c.getColumnIndex(WalkGPSPointsColumns.ACCURACY));
			
			toRet = new WalkGPSPoint(lat, lng, ts);

			toRet.MD5 = c.getString(c.getColumnIndex(WalkGPSPointsColumns.MD5));
			toRet.Status = c.getString(c.getColumnIndex(WalkGPSPointsColumns.STATUS));
			toRet.Type = EnumWalkGPSPointType.ROUTE;
			toRet.WalkId = walkId;
			toRet.Accuracy = accuracy;
		}
		
		c.close();
		
		return toRet;
	}
	
	public void updateWalkGpsPointStateByMd5(String stateTo, String md5) {
		ContentValues cv = new ContentValues();
		cv.put(WalkGPSPointsColumns.STATUS, stateTo);
		
		updateTable(Tables.WALK_GPS_POINTS, cv, WalkGPSPointsColumns.MD5 + "=?", new String[]{md5});
	}
	
	public void updateWalkGpsPointsStatusByWalkId(String statusTo, String walkId) {
		ContentValues cv = new ContentValues();
		cv.put(WalkGPSPointsColumns.STATUS, statusTo);
		
		updateTable(Tables.WALK_GPS_POINTS, cv, WalkGPSPointsColumns.WALK_ID + "=?", new String[]{walkId});
	}

	public ArrayList<WalkGPSPoint> selectAllWalkGpsPointsForWalk() {
		ArrayList<WalkGPSPoint> toRet = new ArrayList<WalkGPSPoint>();
		Cursor c = mDb.query(Tables.WALK_GPS_POINTS, null, null, null, null, null, null);
		
		if(c.moveToFirst()) {
			while(!c.isAfterLast()) {
				int lat = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LATITUDE));
				int lng = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LONGITUDE));
				long timestamp = c.getLong(c.getColumnIndex(WalkGPSPointsColumns.TIMESTAMP));
				String Type = c.getString(c.getColumnIndex(WalkGPSPointsColumns.TYPE));
				String WalkIdRes = c.getString(c.getColumnIndex(WalkGPSPointsColumns.WALK_ID));
				float accuracy = c.getFloat(c.getColumnIndex(WalkGPSPointsColumns.ACCURACY));

				WalkGPSPoint point = new WalkGPSPoint(lat, lng, timestamp);
				
				point.Type = Type;
				point.WalkId = WalkIdRes;
				point.Accuracy = accuracy;

				toRet.add(point);
				c.moveToNext();
			}
		}
		
		c.close();
		
		return toRet;
	}
	/*
	public ArrayList<WalkGPSPoint> selectWalkGpsPointsForWalkYoungerThan(String walkId, long time)
	{
		ArrayList<WalkGPSPoint> toRet = new ArrayList<WalkGPSPoint>();
		
		String sTime = String.format("%d", time);
		
		Cursor c = mDb.query(Tables.WALK_GPS_POINTS, null, WalkGPSPointsColumns.WALK_ID + "=? AND " + WalkGPSPointsColumns.TIMESTAMP + ">?", new String[]{walkId, sTime}, null, null, null);
		
		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				int lat = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LATITUDE));
				int lng = c.getInt(c.getColumnIndex(WalkGPSPointsColumns.LONGITUDE));
				long timestamp = c.getLong(c.getColumnIndex(WalkGPSPointsColumns.TIMESTAMP));
				String Type = c.getString(c.getColumnIndex(WalkGPSPointsColumns.TYPE));
				String WalkIdRes = c.getString(c.getColumnIndex(WalkGPSPointsColumns.WALK_ID));

				WalkGPSPoint point = new WalkGPSPoint(lat, lng, timestamp);
				
				point.Type = Type;
				point.WalkId = WalkIdRes;

				toRet.add(point);
				c.moveToNext();
			}
		}
		
		c.close();
		
		return toRet;
	}
	*/
	
	
	
	//--------------------------------------------------------
	//
	//			Portions
	//
	//------------------
	
	public void addPortion(String uniqueId, String state, boolean needReload) {
		//insert
		ContentValues cv = new ContentValues();
		cv.put(PortionColumns.STATE, state);
		cv.put(PortionColumns.UNIQUE_ID, uniqueId);
		
		insertToTableConflictReplace(Tables.PORTIONS, cv);
	}
	
	public void setPortionReloaded(String uniqueId) {
		//update
	}
	
	public void deleteAllPortions() {
		//deleteAllPortions
		mDb.delete(Tables.PORTIONS, null, null);
	}
	
	public Portion getPortion(String uniqueId) {
		//select
		Portion toRet = null;
		Cursor c = mDb.query(Tables.PORTIONS, new String[]{PortionColumns.STATE}, TableColumns.PortionColumns.UNIQUE_ID + "=?", new String[]{uniqueId}, null, null, null);
		
		if(c.moveToFirst()) {
			if(c.getCount() > 1) {
				//error - there are more than one portion with unique id in database
			} else {
				toRet = new Portion();
				toRet.State = c.getString(c.getColumnIndex(PortionColumns.STATE));
				toRet.UniqueId = uniqueId;
			}
		}
		
		c.close();
		
		return toRet;
	}
	
	
	
	
	
	
	//--------------------------------------------------
	//
	//			Started Walks
	//
	//-----------------------
	
	public void addStartedWalk(String walkId, 
							   long startedTime,
							   boolean completed,
							   boolean messageSent,
							   boolean photoSkipped, 
							   boolean photoUploadedTriedOnce, 
							   boolean photoUploaded,
							   boolean remindersShownOnce,
							   boolean unsentDataSent, 
							   boolean unsentDataSkipped,  
							   boolean stopWalkSent)
	{
		ContentValues cv = new ContentValues();
		cv.put(StartedWalksColumns.COMPLETED, boolToInt(completed));
		cv.put(StartedWalksColumns.MESSAGE_SENT, boolToInt(messageSent));
		cv.put(StartedWalksColumns.PHOTO_SKIPPED, boolToInt(photoSkipped));
		cv.put(StartedWalksColumns.PHOTO_UPLOAD_TRIED_ONCE, boolToInt(photoUploadedTriedOnce));
		cv.put(StartedWalksColumns.PHOTO_UPLOADED, boolToInt(photoUploaded));
		cv.put(StartedWalksColumns.REMINDERS_SHOWN_ONCE, boolToInt(remindersShownOnce));
		cv.put(StartedWalksColumns.STARTED_TIME, startedTime);
		cv.put(StartedWalksColumns.STOP_WALK_SENT, boolToInt(stopWalkSent));
		cv.put(StartedWalksColumns.UNSENT_DATA_SENT, boolToInt(unsentDataSent));
		cv.put(StartedWalksColumns.UNSENT_DATA_SKIPPED, boolToInt(unsentDataSkipped));
		cv.put(StartedWalksColumns.WALK_ID, walkId);
		
		insertToTable(Tables.STARTED_WALKS, cv);
	}
	
	public void addStartedWalk(StartedWalkExt startedWalk) {
		addStartedWalk(startedWalk.WalkId, startedWalk.StartedTime, startedWalk.Completed, startedWalk.MessageSent, startedWalk.PhotoSkipped, startedWalk.PhotoUploadTriedOnce, startedWalk.PhotoUploaded, startedWalk.RemindersShownOnce, startedWalk.UnsentDataSent, startedWalk.UnsentDataSkipped, startedWalk.StopWalkSent);
	}
	
	/**CARE with flagName. get them from StartedWalksColumns*/
	public void updateSomeFlagOfStartedWalk(String walkId, String flagName, boolean value) {
		ContentValues cv = new ContentValues();
		cv.put(flagName, value);
		
		updateTable(Tables.STARTED_WALKS, cv, StartedWalksColumns.WALK_ID + "=?", new String[]{walkId});
	}
	
	public void updateStartedWalkStartTimeAndCompleted(String walkId, long startTime, boolean completed) {
		ContentValues cv = new ContentValues();
		cv.put(StartedWalksColumns.STARTED_TIME, startTime);
		cv.put(StartedWalksColumns.COMPLETED, completed);
		
		updateTable(Tables.STARTED_WALKS, cv, StartedWalksColumns.WALK_ID + "=?", new String[]{walkId});
	}
	
	public StartedWalkExt getStartedWalkForId(String walkId) {
		Cursor c = mDb.query(Tables.STARTED_WALKS, null, StartedWalksColumns.WALK_ID + "=?", new String[]{walkId}, null, null, null);
		StartedWalkExt toRet = null;
		
		if(c.moveToFirst()) {
			toRet = new StartedWalkExt();
			
			toRet.Completed = intToBool(c.getInt(c.getColumnIndex(StartedWalksColumns.COMPLETED)));
			toRet.MessageSent = intToBool(c.getInt(c.getColumnIndex(StartedWalksColumns.MESSAGE_SENT)));
			toRet.PhotoSkipped = intToBool(c.getInt(c.getColumnIndex(StartedWalksColumns.PHOTO_SKIPPED)));
			toRet.PhotoUploaded = intToBool(c.getInt(c.getColumnIndex(StartedWalksColumns.PHOTO_UPLOADED)));
			toRet.PhotoUploadTriedOnce = intToBool(c.getInt(c.getColumnIndex(StartedWalksColumns.PHOTO_UPLOAD_TRIED_ONCE)));
			toRet.RemindersShownOnce = intToBool(c.getInt(c.getColumnIndex(StartedWalksColumns.REMINDERS_SHOWN_ONCE)));
			toRet.StopWalkSent = intToBool(c.getInt(c.getColumnIndex(StartedWalksColumns.STOP_WALK_SENT)));
			toRet.UnsentDataSent = intToBool(c.getInt(c.getColumnIndex(StartedWalksColumns.UNSENT_DATA_SENT)));
			toRet.UnsentDataSkipped = intToBool(c.getInt(c.getColumnIndex(StartedWalksColumns.UNSENT_DATA_SKIPPED)));
			toRet.StartedTime = c.getLong(c.getColumnIndex(StartedWalksColumns.STARTED_TIME));
			
			toRet.WalkId = walkId;
		}
		
		c.close();
		
		return toRet;
	}
	
	
	
	
	
	//--------------------------------------------------------
	//
	//			Month infos
	//
	//---------------------
	
	
	public void insertMonthInfo(WalksForMonth info) {
		ContentValues cv = new ContentValues();
		
		cv.put(MonthsColumns.DAYS_IN_MONTH, info.getDaysInMonth());
		cv.put(MonthsColumns.MONTH, info.getMonth());
		cv.put(MonthsColumns.NUMBERS, info.getNumbers());
		cv.put(MonthsColumns.WALKS_IN_MONTH, info.getWalksInMonth());
		cv.put(MonthsColumns.YEAR, info.getYear());
		
		insertToTable(Tables.MONTHS, cv);
	}
	
	public WalksForMonth getMonthInfo(int month, int year) {
		Cursor c = mDb.query(Tables.MONTHS, 
				new String[]{MonthsColumns.DAYS_IN_MONTH, MonthsColumns.NUMBERS,  MonthsColumns.WALKS_IN_MONTH}, 
				MonthsColumns.MONTH + "=? AND " + MonthsColumns.YEAR + "=?"  , 
				new String[]{String.format("%d", month), String.format("%d", year)},
				null, null, null);
		
		WalksForMonth toRet = null;
		
		if(c.moveToFirst()) {
			toRet = new WalksForMonth();
			
			toRet.setDaysInMonth(c.getInt(c.getColumnIndex(MonthsColumns.DAYS_IN_MONTH)));
			toRet.setNumbers(c.getString(c.getColumnIndex(MonthsColumns.NUMBERS)));
			toRet.setWalksInMonth(c.getInt(c.getColumnIndex(MonthsColumns.WALKS_IN_MONTH)));
			
			toRet.setMonth(month);
			toRet.setYear(year);
		}
		
		c.close();
		
		return toRet;
	}
	
	public void deleteAllMonthsInfo() {
		mDb.delete(Tables.MONTHS, null, null);
	}

	
	
	
	//--------------------------------------------------------
	//
	//			One session GPS points
	//
	//--------------------------------
	
	public void insertGpsPointForOneSession(int lat, int lng, long timespan, float accuracy) {
		ContentValues cv = new ContentValues();
		
		cv.put(GpsPointsOneSessionColumns.ACCURACY, accuracy);
		cv.put(GpsPointsOneSessionColumns.LATITUDE, lat);
		cv.put(GpsPointsOneSessionColumns.LONGITUDE, lng);
		cv.put(GpsPointsOneSessionColumns.TIMESTAMP, timespan);
		
		insertToTable(Tables.GPS_POINTS_ONE_SESSION, cv);
	}
	
	public WalkGPSPoint selectBestGpsPointFromSession() {
		//Cursor c = mDb.query(Tables.WALK_GPS_POINTS, null, WalkGPSPointsColumns.WALK_ID + "=? AND " + WalkGPSPointsColumns.TYPE + "=?", new String[]{walkId, EnumWalkGPSPointType.ROUTE}, null, null, WalkGPSPointsColumns.TIMESTAMP + " DESC", "1");
		Cursor c = mDb.query(Tables.GPS_POINTS_ONE_SESSION, null, null, null, null, null, GpsPointsOneSessionColumns.ACCURACY + " ASC, " + GpsPointsOneSessionColumns.TIMESTAMP + " DESC", "1");
		
		WalkGPSPoint toRet = null;
		
		if(c.moveToFirst()) {
			int lat = c.getInt(c.getColumnIndex(GpsPointsOneSessionColumns.LATITUDE));
			int lng = c.getInt(c.getColumnIndex(GpsPointsOneSessionColumns.LONGITUDE));
			long ts = c.getLong(c.getColumnIndex(GpsPointsOneSessionColumns.TIMESTAMP));
			float accuracy = c.getLong(c.getColumnIndex(GpsPointsOneSessionColumns.ACCURACY));
			
			toRet = new WalkGPSPoint(lat, lng, ts);

			toRet.Type = EnumWalkGPSPointType.ROUTE;
			//toRet.WalkId = walkId;
			toRet.Accuracy = accuracy;
		}
		
		c.close();
		
		return toRet;
	}
	
	public void deleteAllGpsPointsForOneSession() {
		mDb.delete(Tables.GPS_POINTS_ONE_SESSION, null, null);
	}
	
	public int selectCountOfGpsPointsForOneSession() {
		Cursor c = mDb.rawQuery("SELECT COUNT(*) FROM " + Tables.GPS_POINTS_ONE_SESSION, null);
		
		int toRet = 0;
		
		if(c.moveToFirst()) {
			toRet = c.getInt(0);
		}
		
		c.close();
		
		return toRet;
	}
	
	
	
	
	
	/*
	 	Helpers
	 */

	private long insertToTable(String tableName, ContentValues cv) {
		return mDb.insert(tableName, null, cv);
	}

	private long insertToTableConflictReplace(String tableName, ContentValues cv) {
		return mDb.insertWithOnConflict(tableName, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
	}

	private void updateTable(String tableName, ContentValues cv, String whereClause, String[] whereArgs) {
		mDb.update(tableName, cv, whereClause, whereArgs);
	}
	
	private boolean intToBool(int toConvert) {
		return toConvert < 0 ? false : true;
	}
	
	private int boolToInt(boolean toConvert) {
		return toConvert ? 1 : -1;
	}
	
	private boolean objectInDatabase(String tableName, String uniqueColumnName, String uniqueColumnValue) {
		Cursor c = mDb.query(tableName, null, uniqueColumnName + "=?", new String[]{uniqueColumnValue}, null, null, null);
		boolean toRet = false;
		
		if(c.moveToFirst()) {
			toRet = true;
		}
		
		c.close();
		
		return toRet;
	}
}
