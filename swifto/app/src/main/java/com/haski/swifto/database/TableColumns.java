package com.haski.swifto.database;

public interface TableColumns {

	interface COMMON_COLUMNS {
		String __ID = "_ID_";
		String SYNC = "sync";
		String NID = "nid";
	}
	
	interface WalkerColumns {
		String KEY = "key";
		String PASSWORD = "password";
		String USERNAME = "username";
		String WALKER_ID = "walker_id";
	}

	interface WalksColumns {
		String WALK_TYPE = "walk_type";

		String ORIGINAL = "original";
		String NOTESOWNER = "notesowner";

		String WALK_ADDRESS_ORIGINAL = "walk_address_original";
		String WALK_ADDRESS_APPARTMENT = "walk_address_apartment";

		String FORMATTED = "formatted";

		String DURATION = "duration";
		String START_DATE = "start_date";
		String TIME_PLUS = "time_plus";
		String TIME_MINUS = "time_minus";
		String STATUS = "status";
		String DOG_IDS = "dog_IDs";
		String DOGS_TO_FEED_IDS = "dogs_to_feed_IDs";
		String WALKER_ID = "walker_ID";
		String OWNER_ID = "owner_ID";
		String START_TIME = "start_time";
		String LOCATION_LAT = "location_lat";
		String LOCATION_LNG = "location_lng";
		String FEE_VALUE = "fee_value";
		String FEE_VALUE_FORMATTED = "fee_value_formatted";
		String PRICE_VALUE = "price_value";
		String PRICE_VALUE_FORMATTED = "price_value_formatted";
		String IDENTIFIER_START_END = "identifier_start_end";
	}

	interface DogsColumns {
		
		String FEAT_AGGRESSIVE = "feat_aggressive";
		String FEAT_ALERGIC = "feat_allergic";
		String FEAT_CHILD_NERVOUS = "feat_child_nervous";
		String FEAT_COLD_SENSITIVE = "feat_cold_sensitive";
		String FEAT_DOG_NERVOUS = "feat_dog_nervous";
		String FEAT_FRIENDLY = "feat_friendly";
		String FEAT_HOT_SENSITIVE = "feat_hot_sensitive";
		String FEAT_MEDICATION = "feat_medication";
		String FEAT_NO_TREATS_NEEDED = "feat_no_treats_needed";
		String FEAT_NO_TREATS = "feat_no_treats";
		String FEAT_PULLEASH = "feat_pullleash";
		String FEAT_RAIN_SENSITIVE = "feat_rain_sensitive";
		String FEAT_STRANGER_NERVOUS = "feat_stranger_nervous";
		String FEAT_TOYS_NERVOUS = "feat_toys_nervous";
		String FEED = "feed";

		String ADDITIONAL_INFO = "additional_info";
		String BIRTH_DATE = "birth_date";
		String BREED = "breed";
		String GENDER = "gender";

		String COMMAND_COME = "command_come";
		String COMMAND_PRAISE = "command_praise";
		String COMMAND_SIT = "command_sit";
		String COMMAND_STOP = "commans_stop";

        String COMMENT_IDS = "comment_ids";

		/**Feature Details*/
		String FD_ALLERGIC = "fd_allergic";
		String FD_MEDICATION = "fd_medication";
		String FD_SITUATION_CHILD = "fd_situation_child";
		String FD_SITUATION_DOG = "fd_situation_dog";
		String FD_SITUATION_STRANGER = "fd_situation_stranger";
		String FD_SITUATION_TOYS = "fd_situation_toys";

		String PIC = "pic";
		String NAME = "name";
		String OWNER_ID = "owner_id";
		String LOCATION_LAT = "location_lat";
		String LOCATION_LNG = "location_lng";	
		
		String EMERGENCY_PHONE_VET = "emergency_phone_vet";
		String EMERGENCY_PHONE_CONTACT = "emergency_phone_contact";
	}

	//nkp
	interface CommentsColumn
	{
		String TITLE="title";
		String BODY = "body";
		String CREATED = "created";
		String CID = "cid";
		String _ID = "_id";
		String AUTHOR_PICTURE = "author_picture";
		String AUTHOR_NAME = "author_name";
		String AUTO_ID = "auto_id";
		String N_ID = "nid";
	}
	
	interface ServicesColumn
	{
		String ISFEED="isfeed";
		String FEED = "feed";
		String ISMEDICINE = "ismedicine";
		String MEDICINE = "medicine";
		String SERVICEID = "serviceid";
		String DOGID = "dogid";
	}



	interface OwnerColumns {
		String UID = "uid";
		String EMAIL = "email";
		String USERNAME = "username";
		String ANIMAL_IDS = "animal_ids";

		String METHOD_PUSH = "method_push";
		String METHOD_EMAIL = "method_email";
		String METHOD_SMS = "method_sms";

		String PHONE_PRIMARY = "phone_primary";
		String BIRTH_YEAR = "birth_year";
		String FIRST_NAME = "first_name";
		String LAST_NAME = "last_name";
		String GENDER = "gender";

		String LOC_ACCESS = "loc_access";
		String LOC_ACCESS_INFO = "loc_access_info";
		String LOC_APARTMENT = "loc_apartment";
		String LOC_TIMESTAMP = "loc_timestamp";
		String LOC_FORMATTED = "loc_formatted";
		String LOC_ORIGINAL = "loc_original";
		String LOC_LAT = "loc_lat";
		String LOC_LNG = "loc_lng";
		String LOC_ZIP = "loc_zip";
		String LOC_COUNTRY = "loc_country";
		String LOC_STATE = "loc_state";
		String LOC_CITY = "loc_city";
		String LOC_AREA = "loc_area";
		String LOC_NEIGHBORHOOD = "loc_neighborhood";
		String LOC_STREET = "loc_street";
		String LOC_STREET_NUM = "loc_street_num";

		String SOCIAL_FACEBOOK_FRIENDS = "social_facebook_friends";
	}
	
	interface PerformedRequestsColumns {
		String ERROR_DESCRIPTION = "error_description";
		String MD5 = "MD5";
		String SUCCESS = "success";
		String TEXT = "text";
		String WALK_ID = "walk_id";
	}
	
	interface WalkGPSPointsColumns {
		String ACCURACY = "acc";
		String LATITUDE = "lat";
		String LONGITUDE = "lng";
		String MD5 = "md5";
		String STATUS = "status";
		String TIMESTAMP = "ts";
		String TYPE = "type";
		String WALK_ID = "walk_id";
	}
	
	interface PortionColumns {
		String UNIQUE_ID = "unique_id";
		String STATE = "state";
	}
	
	interface StartedWalksColumns {
		String COMPLETED = "completed";
		String MESSAGE_SENT = "message_sent";
		String PHOTO_SKIPPED = "photo_skipped";
		String PHOTO_UPLOAD_TRIED_ONCE = "photo_upload_tried_once";
		String PHOTO_UPLOADED = "photo_uploaded";
		String REMINDERS_SHOWN_ONCE = "reminders_shown";
		String STARTED_TIME = "started_time";
		String STOP_WALK_SENT = "stop_walk_sent";
		String UNSENT_DATA_SENT = "unsent_data_sent";
		String UNSENT_DATA_SKIPPED = "unsent_data_skipped";
		String WALK_ID = "walk_id";
	}
	
	interface MonthsColumns {
		String DAYS_IN_MONTH = "days_in_month";
		String MONTH = "month";
		String NUMBERS = "numbers";
		String WALKS_IN_MONTH = "walks_in_month";
		String YEAR = "year";
	}
	
	interface GpsPointsOneSessionColumns {
		String ACCURACY = "acc";
		String LATITUDE = "lat";
		String LONGITUDE = "lng";
		String TIMESTAMP = "ts";
	}

	interface OfflineWalkRequestsColumns
	{
		String _ID = "_id";
		String WALKID = "walkid";
		String REQUEST = "request";
		String TYPE = "type";
		String STATUS = "status";
	}
}
