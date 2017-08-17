package com.haski.swifto.model.vo.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.haski.swifto.model.vo.BaseEntity;
import com.haski.swifto.model.vo.StaticLocation;
import com.haski.swifto.model.vo.dog.Comment;
import com.haski.swifto.model.vo.dog.Dog;
import com.haski.swifto.model.vo.dog.DogCommands;
import com.haski.swifto.model.vo.dog.Features;
import com.haski.swifto.model.vo.dog.FeaturesDetails;
import com.haski.swifto.model.vo.dog.Services;
import com.haski.swifto.model.vo.json.BaseJSONParser.JSON_ATTRIBUTES;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.MONEY;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.SERVICESARRAY;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.STATUS;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.TIME;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.MONEY.FEE;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.MONEY.PRICE;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.DOGS;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.WALKER;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.DOGS.DOG;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.DOGS.DOG.COMMANDS;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.DOGS.DOG.FEATURES;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.DOGS.DOG.FEATURES_DETAILS;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER.LOCATION;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER.SOCIAL;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER.LOCATION.ACCESS;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER.LOCATION.STATIC_LOCATION;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER.SOCIAL.ANIMALS;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER.SOCIAL.BASIC;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER.SOCIAL.COMMS;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER.SOCIAL.COMMS.METHOD;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.PARTICIPANTS.OWNER.SOCIAL.COMMS.PHONE;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.SERVICESARRAY.SERVICESOBJ;
import com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.TIMEWINDOW;
import com.haski.swifto.model.vo.owner.OwnerOfDog;
import com.haski.swifto.model.vo.owner.SocialInfo;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.util.GeoUtils;
import com.haski.swifto.util.StringUtils;
import com.haski.swifto.util.WalkUtils;

public class GetWalksParser {

	interface WALKS {
		String NAME = "walks";

		String WALK_TYPE = "walkType";
		String DURATION = "duration";
		String START_DATE = "startDate";

		interface WALK {
			String WALK_TYPE = "walkType";
			String DURATION = "duration";
			String NAME = "name";
			String START_DATE = "startDate";

			interface STATUS {
				String NAME = "status";

				String STATUS = "status";
			}

			interface NOTES{

				String NAME = "notes";
				String OWNER = "owner";
			}

			interface RETURNADDRESS {

				String NAME = "returnAddress";

				interface STATICLOCATION {
					String NAME = "staticLocation";
					String ORIGINAL = "original";
					String FORMATTED = "formatted";

				}
			}



			interface PARTICIPANTS {
				String NAME = "participants";

				interface DOGS {
					String NAME = "dogs";

					interface DOG {
						String ADDITIONAL_INFO = "additionalInfo";
						// String FEEDING_INSTRUCTIONS = "feedingInstructions";

						String BIRTH_DATE = "birthDate";
						String BREED = "breed";
						String GENDER = "gender";

						String EMERGENCY = "emergency";
						String EMERGENCY_PHONE_CONTACT = "contact_phone";
						String EMERGENCY_PHONE_VET = "contact_name";

						interface COMMANDS {
							String NAME = "commands";

							String COME = "come";
							String PRAISE = "praise";
							String SIT = "sit";
							String STOP = "stop";
						}

						interface COMMENTS {
							String TITLE = "title";
							String BODY = "body";
							String CREATED = "created";
							String CID = "cid";
							String _ID = "_id";
							String AUTHOR = "author";
							String AUTHOR_NAME = "name";
							String AUTHOR_PICTURE = "picture";
						}

						interface FEATURES_DETAILS {
							String NAME = "featuresDetails";

							String DOG_ALLERGIC_DETAILS = "dog_allergic_details";
							String DOG_MEDICATION_DETAILS = "dog_medication_details";
							String DOG_SITUATION_CHILD_DETAILS = "dog_situation_child_details";
							String DOG_SITUATION_DOG_DETAILS = "dog_situation_dog_details";
							String DOG_SITUATION_STRANGER_DETAILS = "dog_situation_stranger_details";
							String DOG_SITUATION_TOYS_DETAILS = "dog_situation_toys_details";
						}

						interface FEATURES {
							String NAME = "features";

							String AGGRESSIVE = "aggressive";
							String ALERGIC = "alergic";
							//String ALERGIC = "Allergies";
							String CHILDNERVOUS = "childnervous";
							String COLDSENSITIVE = "coldsensitive";
							String DOGNERVOUS = "dognervous";
							String FRIENDLY = "friendly";
							String HOTSENSITIVE = "hotsensitive";
							String MEDICATION = "medication";
							String NOTREATS = "notreats";
							String PULLLEASH = "pullleash";
							String RAINSENSITIVE = "rainsensitive";
							String STRANGERNERVOUS = "strangernervous";
							String TOYSNERVOUS = "toysnervous";
						}

						String PIC = "pic";
						String NAME = "name";

						interface OWNER {
							String NAME = "owner";
						}

						interface LOCATION {
							String NAME = "location";
						}

						interface SERVICES {
							String NAME = "services";

							String FEED = "feed";
							String MEDICATEINSTRUCTIONS = "medicateInstructions";
							String MEDICATE = "medicate";
							String FEEDINSTRUCTIONS = "feedInstructions";
						}
					}
				}

				interface WALKER {
					String NAME = "walker";
				}

				interface OWNER {
					String NAME = "owner";

					String EMAIL = "email";
					String USERNAME = "username";
					String UID = "uid";

					interface LOCATION {
						String NAME = "location";

						interface ACCESS {
							String NAME = "access";

							String ACCESS = "access";
							String ACCESS_INFO = "accessInfo";
						}

						interface STATIC_LOCATION {
							String NAME = "staticLocation";

							String TS = "ts";
							String FORMATTED = "formatted";
							String ORIGINAL = "original";
							String _LOC = "loc";
							String ZIP = "zip";
							String COUNTRY = "country";
							String STATE = "state";
							String CITY = "city";
							String AREA = "area";
							String NEIGHBORHOOD = "neighborhood";
							String STREET = "street";
							String STREET_NUM = "streetNum";
							String APARTMENT = "apartment";
						}
					}

					interface SOCIAL {
						String NAME = "social";

						interface FACEBOOK {
							String NAME = "facebook";

							String _FRIENDS = "friends";
						}

						interface ANIMALS {
							String NAME = "animals";

							interface DOGS {
								String NAME = "dogs";
							}
						}

						interface COMMS {
							String NAME = "comms";

							interface METHOD {
								String NAME = "method";

								String PUSH = "push";
								String EMAIL = "email";
								String SMS = "sms";
							}

							interface PHONE {
								String NAME = "phone";

								String PRIMARY = "primary";
							}
						}

						interface BASIC {
							String NAME = "basic";
							String BIRTH_YEAR = "birthYear";
							String FIRST_NAME = "firstName";
							String GENDER = "gender";
							String LAST_NAME = "lastName";
						}
					}
				}
			}

			interface TIMEWINDOW {
				String NAME = "timeWindow";
				String MINUS = "minus";
				String PLUS = "plus";
			}

			interface SERVICESARRAY {
				String NAME = "services";

				interface SERVICESOBJ {
					String NAME = "services";
					String ID = "_id";
					String MEDICATEINSTRUCTIONS = "medicateInstructions";
					String MEDICATE = "medicate";
					String FEEDINSTRUCTIONS = "feedInstructions";
					String FEED = "feed";
				}
				interface DOG {
					
					String NAME = "dog";
					String ID = "_id";
					
				}
			}
			interface TIME {
				String NAME = "time";
				String START = "start";
			}

			interface ADDRESS {
				String NAME = "address";
				String _LOC = "loc";

				interface STATIC_LOCATION {
					String NAME = "staticLocation";
					String LOC = "loc";
					String ORIGINAL = "original";
					String APARTMENT = "apartment";
				}
			}

			interface MONEY {
				String NAME = "money";

				interface FEE {
					String NAME = "fee";

					String VALUE = "value";
					String FORMATTED = "formatted";
				}

				interface PRICE {
					String NAME = "price";

					String VALUE = "value";
					String FORMATTED = "formatted";
				}
			}
		}
	}

	public ArrayList<Walk> parseGetWalks(String json) {
		ArrayList<Walk> parsedWalks = new ArrayList<Walk>();

		try {
			JSONObject jo = new JSONObject(json);

			JSONArray jWalks = jo.getJSONArray(WALKS.NAME);

			for (int i = 0; i < jWalks.length(); i++) {
				JSONObject jWalk = jWalks.getJSONObject(i);

				parsedWalks.add(parseWalk(jWalk));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return parsedWalks;
	}

	private Walk parseWalk(JSONObject jWalk) {
		Walk walk = new Walk();
		try {
			fillBaseJsonFields(jWalk, walk);

			if (jWalk.has(WALK.WALK_TYPE)) {
				walk.WalkType = jWalk.getString(WALK.WALK_TYPE);
			}

			if (jWalk.has(WALK.DURATION)) {
				walk.Duration = jWalk.getInt(WALK.DURATION);
			}

			if (jWalk.has(WALK.START_DATE)) {
				walk.StartDate = jWalk.getLong(WALK.START_DATE);
			}

			if (jWalk.has(WALK.NAME)) {
				walk.Name = jWalk.getString(WALK.NAME);
			}

			// TODO: "status":["scheduled"] !!!
			// String status =
			// jWalk.getJSONObject(WALK.STATUS.NAME).getString(STATUS.STATUS);

			// ["scheduled","started","finished"]

			if (jWalk.has(STATUS.NAME)) {
				JSONObject jStatus = jWalk.getJSONObject(STATUS.NAME);

				if (jStatus.has(STATUS.NAME)) {
					JSONArray arrStatuses = jStatus.getJSONArray(STATUS.NAME);

					walk.Status = WalkUtils.getStatus(arrStatuses);
				} else {
					// Walk have no status!
				}
			} else {
				// Walk have no status!
			}

			if(jWalk.has(WALK.NOTES.NAME))
			{
				JSONObject jNotesOwner = jWalk.getJSONObject(WALK.NOTES.NAME);

				if(jNotesOwner.has(WALK.NOTES.OWNER))
				{
					walk.NotesOwner = jNotesOwner.getString(WALK.NOTES.OWNER);
				}
			}

			if(jWalk.has(WALK.RETURNADDRESS.NAME))
			{
				JSONObject jStaticLocatoin = jWalk.getJSONObject(WALK.RETURNADDRESS.NAME).
						getJSONObject(WALK.RETURNADDRESS.STATICLOCATION.NAME);

				if(jStaticLocatoin.has(WALK.RETURNADDRESS.STATICLOCATION.ORIGINAL))
				{
					walk.Original = jStaticLocatoin.getString(WALK.RETURNADDRESS.STATICLOCATION.ORIGINAL);
				}

				if(jStaticLocatoin.has(WALK.RETURNADDRESS.STATICLOCATION.FORMATTED))
				{
					walk.Formatted = jStaticLocatoin.getString(WALK.RETURNADDRESS.STATICLOCATION.FORMATTED);
				}

			}

			if (jWalk.has(PARTICIPANTS.NAME)) {
				JSONArray jDogs = jWalk.getJSONObject(PARTICIPANTS.NAME)
						.getJSONObject(DOGS.NAME).getJSONArray("_ids");

				walk.Dogs = new ArrayList<Dog>();

				for (int dogInd = 0; dogInd < jDogs.length(); dogInd++) {
					Dog dog = parseDog(jDogs.getJSONObject(dogInd));
					walk.Dogs.add(dog);
				}

				StringBuilder dogsToFeedIds = new StringBuilder("");

				for (Dog dog : walk.Dogs) {
					if (dog.Feed) {
						dogsToFeedIds.append(dog._id);
						dogsToFeedIds.append(",");
					}
				}

				if (dogsToFeedIds.length() > 0) {
					dogsToFeedIds.deleteCharAt(dogsToFeedIds.length() - 1);
				}

				walk.DogsToFeedIDs = dogsToFeedIds.toString();

				JSONObject jOwner = jWalk.getJSONObject(PARTICIPANTS.NAME)
						.getJSONObject(OWNER.NAME)
						.getJSONObject(JSON_ATTRIBUTES._ID);
				OwnerOfDog owner = parseOwner(jOwner);
				walk.Owner = owner;

				walk.walkerID = jWalk.getJSONObject(PARTICIPANTS.NAME)
						.getJSONObject(WALKER.NAME)
						.getString(JSON_ATTRIBUTES._ID);
			}

			// vivek new work start

			if (jWalk.has(SERVICESARRAY.NAME)) {

				JSONArray jServices = jWalk.getJSONArray(SERVICESARRAY.NAME);

				walk.Servicess = new ArrayList<Services>();

				for (int serviceInd = 0; serviceInd < jServices.length(); serviceInd++) {
					Services service = parseService(jServices
							.getJSONObject(serviceInd));
					walk.Servicess.add(service);
				}

			}

			// vivek new work End

			if (jWalk.has(TIME.NAME)) {
				JSONObject jTime = jWalk.getJSONObject(TIME.NAME);

				if (jTime.has(TIME.START)) {
					walk.StartTime = jTime.getLong(TIME.START);
				} else {
					// Walk have no startTime!
				}
			} else {
				// Walk have no startTime!
			}

			if (jWalk.has(TIMEWINDOW.NAME)) {
				JSONObject jTime = jWalk.getJSONObject(TIMEWINDOW.NAME);

				if (jTime.has(TIMEWINDOW.MINUS)) {
					walk.Minus = jTime.getLong(TIMEWINDOW.MINUS);
				} else {
					// Walk have no startTime!
				}

				if (jTime.has(TIMEWINDOW.PLUS)) {
					walk.Plus = jTime.getLong(TIMEWINDOW.PLUS);
				} else {
					// Walk have no startTime!
				}
			} else {
				// Walk have no startTime!
			}

			if (jWalk.has(WALK.ADDRESS.NAME)) {
				JSONObject jAddress = jWalk.getJSONObject(WALK.ADDRESS.NAME);// .getJSONArray(com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.ADDRESS._LOC);

				if (walk.Owner !=null)
				{
					walk.Owner.StaticLocation = parseStaticLocation(jAddress);
				}

				if (jAddress.has(WALK.ADDRESS.STATIC_LOCATION.NAME)) {
					JSONObject jStaticLoc = jAddress
							.getJSONObject(WALK.ADDRESS.STATIC_LOCATION.NAME);

					if(jStaticLoc.has(WALK.ADDRESS.STATIC_LOCATION.ORIGINAL))
					{
						walk.Walk_Address_original = jStaticLoc.getString(WALK.ADDRESS.STATIC_LOCATION.ORIGINAL);
					}

					if(jStaticLoc.has(WALK.ADDRESS.STATIC_LOCATION.APARTMENT))
					{
						walk.Walk_Address_apartment = jStaticLoc.getString(WALK.ADDRESS.STATIC_LOCATION.APARTMENT);
					}


					if (jStaticLoc.has(WALK.ADDRESS.STATIC_LOCATION.LOC)) {
						JSONArray jLoc = jStaticLoc
								.getJSONArray(WALK.ADDRESS.STATIC_LOCATION.LOC);
						walk.Location = parseLocationFromArray(jLoc, false);
					}
				}
			}

			if (jWalk.has(MONEY.NAME)) {
				JSONObject jMoney = jWalk.getJSONObject(MONEY.NAME);

				if (jMoney.has(FEE.NAME)) {
					JSONObject jFee = jMoney.getJSONObject(FEE.NAME);

					if (jFee.has(FEE.VALUE)) {
						walk.FeeValue = jFee.getInt(FEE.VALUE);
					}

					if (jFee.has(FEE.FORMATTED)) {
						walk.FeeValueFormatted = jFee.getString(FEE.FORMATTED);
					}
				}

				if (jMoney.has(PRICE.NAME)) {
					JSONObject jPrice = jMoney.getJSONObject(PRICE.NAME);

					if (jPrice.has(PRICE.VALUE)) {
						walk.PriceValue = jPrice.getInt(PRICE.VALUE);
					}

					if (jPrice.has(PRICE.FORMATTED)) {
						walk.PriceValueFormatted = jPrice
								.getString(PRICE.FORMATTED);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return walk;
	}

	private Services parseService(JSONObject jService) {
		Services srvc = new Services();

		try {
			if (jService.has(SERVICESOBJ.NAME)) {

				JSONObject jservc = jService.getJSONObject(SERVICESOBJ.NAME);
				if (jservc.has(SERVICESOBJ.FEED)) {
					Log.i("SERVICESOBJ.FEED", jservc.getString(SERVICESOBJ.FEED));
					srvc.Feed = jservc.getString(SERVICESOBJ.FEED).equals("after");
				}

				if (jservc.has(SERVICESOBJ.MEDICATE)) {
					Log.i("SERVICESOBJ.MEDICATE", jservc.getString(SERVICESOBJ.MEDICATE));
					srvc.Medicate = jservc.getString(SERVICESOBJ.MEDICATE).equals("yes");
				}

				if (jservc.has(SERVICESOBJ.MEDICATEINSTRUCTIONS)) {
//					Log.i("SERVICESOBJ.MEDICATEINSTRUCTIONS", jservc.getString(SERVICESOBJ.MEDICATEINSTRUCTIONS));
					srvc.MedicateInstructions = jservc
							.getString(SERVICESOBJ.MEDICATEINSTRUCTIONS);
				}
				
				if (jservc.has(SERVICESOBJ.FEEDINSTRUCTIONS)) {
//					Log.i("SERVICESOBJ.FEEDINSTRUCTIONS", jservc.getString(SERVICESOBJ.FEEDINSTRUCTIONS));
					srvc.FeedInstructions = jservc
							.getString(SERVICESOBJ.FEEDINSTRUCTIONS);
				}
			}

			if (jService.has(SERVICESOBJ.ID)) {
				srvc._Id = jService.getString(SERVICESOBJ.ID);
			}

			if (jService.has(SERVICESARRAY.DOG.NAME)) {
				srvc.Dog_Id = jService.getJSONObject(SERVICESARRAY.DOG.NAME)
						.getString(SERVICESARRAY.DOG.ID);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return srvc;
	}

	private Dog parseDog(JSONObject jDog) {
		//nkp
		//Log.i("nkp Jdog", jDog.toString());
		Dog dog = new Dog();

		fillBaseJsonFields(jDog, dog);

		try {
			if (jDog.has(DOG.ADDITIONAL_INFO)) {
				dog.AdditionalInfo = jDog.getString(DOG.ADDITIONAL_INFO).trim();
			}

			// if(jDog.has(DOG.FEEDING_INSTRUCTIONS)) {
			// dog.feedingInstructions =
			// jDog.getString(DOG.FEEDING_INSTRUCTIONS).trim();
			//
			// }

			if (jDog.has(DOG.BIRTH_DATE)) {
				dog.BirthData = jDog.getString(DOG.BIRTH_DATE);
			}

			if (jDog.has(DOG.BREED)) {
				dog.Breed = jDog.getString(DOG.BREED);
			}

			if (jDog.has(DOG.GENDER)) {
				dog.Gender = jDog.getString(DOG.GENDER);
			}

			// commands
			if (jDog.has(COMMANDS.NAME)) {
				JSONObject jCommands = jDog.getJSONObject(COMMANDS.NAME);
				DogCommands commands = new DogCommands();

				if (jCommands.has(COMMANDS.COME)) {
					commands.Come = jCommands.getString(COMMANDS.COME);
				}

				if (jCommands.has(COMMANDS.PRAISE)) {
					commands.Praise = jCommands.getString(COMMANDS.PRAISE);
				}

				if (jCommands.has(COMMANDS.SIT)) {
					commands.Sit = jCommands.getString(COMMANDS.SIT);
				}

				if (jCommands.has(COMMANDS.STOP)) {
					commands.Stop = jCommands.getString(COMMANDS.STOP);
				}

				dog.Commands = commands;
			} else {
				dog.Commands = new DogCommands();
			}

			//Comments

			if (jDog.has("comments")) {

				dog.Comments = new ArrayList<Comment>();
				JSONArray jComments = jDog.getJSONArray("comments");
				if(jComments.length() > 0) {
					for (int commentIndex = 0; commentIndex < jComments.length(); commentIndex++) {
						Comment comment = parseComment(jComments.getJSONObject(commentIndex));
						comment.nid = jDog.getString(JSON_ATTRIBUTES.NID);
						dog.Comments.add(comment);
					}
				}else {
					dog.Comments = new ArrayList<Comment>();
				}

			}else {
				dog.Comments = new ArrayList<Comment>();
			}

			//comment end nkp


			if (jDog.has(DOG.EMERGENCY)) {

				JSONObject jEmergency = jDog.getJSONObject(DOG.EMERGENCY);

				if (jEmergency.has(DOG.EMERGENCY_PHONE_CONTACT)) {
					dog.EmergencyPhoneContact = jEmergency
							.getString(DOG.EMERGENCY_PHONE_CONTACT);
				}

				if (jEmergency.has(DOG.EMERGENCY_PHONE_VET)) {
					dog.EmergencyPhoneVet = jEmergency
							.getString(DOG.EMERGENCY_PHONE_VET);
				}
			}

			// featuresDetails
			if (jDog.has(FEATURES_DETAILS.NAME)) {
				JSONObject jFeaturesDetails = jDog
						.getJSONObject(FEATURES_DETAILS.NAME);
				FeaturesDetails featureDetails = new FeaturesDetails();

				if (jFeaturesDetails.has(FEATURES_DETAILS.DOG_ALLERGIC_DETAILS)) {
					featureDetails.Allergic = jFeaturesDetails
							.getString(FEATURES_DETAILS.DOG_ALLERGIC_DETAILS);
					System.out.println("featureDetails.Allergic = " + featureDetails.Allergic);
				}

				if (jFeaturesDetails
						.has(FEATURES_DETAILS.DOG_MEDICATION_DETAILS)) {
					featureDetails.Medication = jFeaturesDetails
							.getString(FEATURES_DETAILS.DOG_MEDICATION_DETAILS);
				}

				if (jFeaturesDetails
						.has(FEATURES_DETAILS.DOG_SITUATION_CHILD_DETAILS)) {
					featureDetails.SituationChild = jFeaturesDetails
							.getString(FEATURES_DETAILS.DOG_SITUATION_CHILD_DETAILS);
				}

				if (jFeaturesDetails
						.has(FEATURES_DETAILS.DOG_SITUATION_DOG_DETAILS)) {
					featureDetails.SituationDog = jFeaturesDetails
							.getString(FEATURES_DETAILS.DOG_SITUATION_DOG_DETAILS);
				}

				if (jFeaturesDetails
						.has(FEATURES_DETAILS.DOG_SITUATION_STRANGER_DETAILS)) {
					featureDetails.SituationStranger = jFeaturesDetails
							.getString(FEATURES_DETAILS.DOG_SITUATION_STRANGER_DETAILS);
				}

				if (jFeaturesDetails
						.has(FEATURES_DETAILS.DOG_SITUATION_TOYS_DETAILS)) {
					featureDetails.SituationToys = jFeaturesDetails
							.getString(FEATURES_DETAILS.DOG_SITUATION_TOYS_DETAILS);
				}

				dog.FeaturesDetails = featureDetails;
			} else {
				dog.FeaturesDetails = new FeaturesDetails();
			}

			// features
			if (jDog.has(FEATURES.NAME)) {
				JSONObject jFeatures = jDog.getJSONObject(FEATURES.NAME);
				Features features = new Features();

				if (jFeatures.has(FEATURES.AGGRESSIVE)) {
					features.Aggressive = jFeatures
							.getBoolean(FEATURES.AGGRESSIVE);
				}

				if (jFeatures.has(FEATURES.ALERGIC)) {
					features.Alergic = jFeatures.getBoolean(FEATURES.ALERGIC);
				}

				if (jFeatures.has(FEATURES.CHILDNERVOUS)) {
					features.ChildNervous = jFeatures
							.getBoolean(FEATURES.CHILDNERVOUS);
				}

				if (jFeatures.has(FEATURES.COLDSENSITIVE)) {
					features.ColdSensitive = jFeatures
							.getBoolean(FEATURES.COLDSENSITIVE);
				}

				if (jFeatures.has(FEATURES.DOGNERVOUS)) {
					features.Dognervous = jFeatures
							.getBoolean(FEATURES.DOGNERVOUS);
				}

				if (jFeatures.has(FEATURES.FRIENDLY)) {
					features.Friendly = jFeatures.getBoolean(FEATURES.FRIENDLY);
				}

				if (jFeatures.has(FEATURES.HOTSENSITIVE)) {
					features.Hotsensitive = jFeatures
							.getBoolean(FEATURES.HOTSENSITIVE);
				}

				if (jFeatures.has(FEATURES.MEDICATION)) {
					features.Medication = jFeatures
							.getBoolean(FEATURES.MEDICATION);
				}

				if (jFeatures.has(FEATURES.PULLLEASH)) {
					features.Pullleash = jFeatures
							.getBoolean(FEATURES.PULLLEASH);
				}

				if (jFeatures.has(FEATURES.RAINSENSITIVE)) {
					features.RainSensitive = jFeatures
							.getBoolean(FEATURES.RAINSENSITIVE);
				}

				if (jFeatures.has(FEATURES.STRANGERNERVOUS)) {
					features.StrangerNervous = jFeatures
							.getBoolean(FEATURES.STRANGERNERVOUS);
				}

				if (jFeatures.has(FEATURES.TOYSNERVOUS)) {
					features.ToysNervous = jFeatures
							.getBoolean(FEATURES.TOYSNERVOUS);
				}

				if (jFeatures.isNull(FEATURES.NOTREATS)) {
					features.NotreatsNeeded = false;
				} else {
					features.NotreatsNeeded = true;
					features.Notreats = jFeatures.getBoolean(FEATURES.NOTREATS);
				}

				dog.Features = features;
			} else {
				dog.Features = new Features();
			}

			if (jDog.has(DOG.PIC)) {
				dog.Pic = jDog.getString(DOG.PIC);
				dog.Pic = StringUtils.httpsToHttp(dog.Pic);
			}

			if (jDog.has(DOG.NAME)) {
				dog.Name = jDog.getString(DOG.NAME);
			}

			if (jDog.has(DOG.OWNER.NAME)) {
				dog.ownerID = jDog.getJSONObject(DOG.OWNER.NAME).getString(
						JSON_ATTRIBUTES._ID);
			}

			if (jDog.has(DOG.LOCATION.NAME)) {
				JSONArray jLocation = jDog
						.getJSONObject(DOG.LOCATION.NAME)
						.getJSONArray(
								com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.ADDRESS._LOC);

				if (jLocation != null && jLocation.length() > 0) {
					double lat = jLocation.getDouble(0);
					double lng = jLocation.getDouble(1);
					dog.Location = new GeoPoint((int) (lat * 1E6),
							(int) (lng * 1E6));
				}
			}
			// "services": {
			// "medicateInstructions": "Give him the blue pill.",
			// "medicate": "yes",
			// "feedInstructions":
			// "Put the dog in the bowl and spray the food on top.",
			// "feed": "after"
			// }
			if (jDog.has(DOG.SERVICES.NAME)) {
				JSONObject jServices = jDog.getJSONObject(DOG.SERVICES.NAME);

				if (jServices.has(DOG.SERVICES.FEED)) {
					String feedStr = jServices.getString(DOG.SERVICES.FEED);

					if (feedStr.equals("after")) {
						dog.Feed = true;
					}
				}

				if (jServices.has(DOG.SERVICES.MEDICATEINSTRUCTIONS)) {
					dog.MedicateInstructions = jServices
							.getString(DOG.SERVICES.MEDICATEINSTRUCTIONS);
				}

				if (jServices.has(DOG.SERVICES.MEDICATE)) {
					String MedicateStr = jServices
							.getString(DOG.SERVICES.MEDICATE);

					if (MedicateStr.equals("yes")) {
						dog.Medicate = true;
					}
				}

				if (jServices.has(DOG.SERVICES.FEEDINSTRUCTIONS)) {
					dog.FeedInstructions = jServices
							.getString(DOG.SERVICES.FEEDINSTRUCTIONS);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return dog;
	}

	//comment nkp
	private Comment parseComment(JSONObject jCommentObject) {
		Comment comment = new Comment();
		String title = null;
		try {
			if (jCommentObject.has(DOG.COMMENTS.TITLE)) {
				comment.title = jCommentObject.getString(DOG.COMMENTS.TITLE);
			}

			if (jCommentObject.has(DOG.COMMENTS.TITLE)) {
				comment.body = jCommentObject.getString(DOG.COMMENTS.BODY);
			}

			if (jCommentObject.has(DOG.COMMENTS.TITLE)) {
				comment.created = jCommentObject.getString(DOG.COMMENTS.CREATED);
			}

			if (jCommentObject.has(DOG.COMMENTS.TITLE)) {
				comment.cid = jCommentObject.getString(DOG.COMMENTS.CID);
			}

			if (jCommentObject.has(DOG.COMMENTS.TITLE)) {
				comment._id = jCommentObject.getString(DOG.COMMENTS._ID);
			}

			if (jCommentObject.has(DOG.COMMENTS.AUTHOR)) {
				JSONObject jAuthorObject = jCommentObject.getJSONObject(DOG.COMMENTS.AUTHOR);

				if (jAuthorObject.has(DOG.COMMENTS.AUTHOR_NAME)) {
					comment.author_name = jAuthorObject.getString(DOG.COMMENTS.AUTHOR_NAME);
				}

				if (jAuthorObject.has(DOG.COMMENTS.AUTHOR_PICTURE)) {
					comment.author_picture = jAuthorObject.getString(DOG.COMMENTS.AUTHOR_PICTURE);
				}
			}


			return comment;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	//nkp end


	private OwnerOfDog parseOwner(JSONObject jOwner) {
		OwnerOfDog owner = new OwnerOfDog();
		fillBaseJsonFields(jOwner, owner);

		try {
			if (jOwner.has(OWNER.EMAIL)) {
				owner.Email = jOwner.getString(OWNER.EMAIL);
			}

			if (jOwner.has(OWNER.UID)) {
				owner.UID = jOwner.getInt(OWNER.UID);
			}

			if (jOwner.has(OWNER.USERNAME)) {
				owner.UserName = jOwner.getString(OWNER.USERNAME);
			}

//			if (jOwner.has(LOCATION.NAME)) {
//				owner.StaticLocation = parseStaticLocation(jOwner
//						.getJSONObject(LOCATION.NAME));
//			}

			if (jOwner.has(SOCIAL.NAME)) {
				owner.SocialInfo = parseSocialInfo(jOwner
						.getJSONObject(SOCIAL.NAME));
			}

			if (jOwner.has(ANIMALS.NAME)) {
				owner.AnimalIDs = parseAnimalIDS(jOwner
						.getJSONObject(ANIMALS.NAME));
			}

			if (jOwner.has(COMMS.NAME)) {
				JSONObject jComms = jOwner.getJSONObject(COMMS.NAME);

				if (jComms.has(METHOD.NAME)) {
					JSONObject jCommsMethod = jComms.getJSONObject(METHOD.NAME);

					owner.MethodPush = jCommsMethod.getBoolean(METHOD.PUSH);
					owner.MethodEmail = jCommsMethod.getBoolean(METHOD.EMAIL);
					owner.MethodSms = jCommsMethod.getBoolean(METHOD.SMS);
				}

				if (jComms.has(PHONE.NAME)) {
					JSONObject jPhone = jComms.getJSONObject(PHONE.NAME);

					if (jPhone.has(PHONE.PRIMARY)) {
						owner.PhonePrimary = jPhone.getString(PHONE.PRIMARY);
					}
				}
			}

			if (jOwner.has(BASIC.NAME)) {
				JSONObject jBasic = jOwner.getJSONObject(BASIC.NAME);

				if (!jBasic.isNull(BASIC.BIRTH_YEAR)) {
					owner.BirthYear = jBasic.getInt(BASIC.BIRTH_YEAR);
				}

				if (jBasic.has(BASIC.FIRST_NAME)) {
					owner.FirstName = jBasic.getString(BASIC.FIRST_NAME);
				}

				if (jBasic.has(BASIC.GENDER)) {
					owner.Gender = jBasic.getString(BASIC.GENDER);
				}

				if (jBasic.has(BASIC.LAST_NAME)) {
					owner.LastName = jBasic.getString(BASIC.LAST_NAME);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return owner;
	}

	private ArrayList<String> parseAnimalIDS(JSONObject jAnimals) {
		ArrayList<String> animalIDS = new ArrayList<String>();

		// dogs
		try {
			JSONArray dogs = jAnimals.getJSONObject(ANIMALS.DOGS.NAME)
					.getJSONArray("_ids");

			for (int i = 0; i < dogs.length(); i++) {
				animalIDS.add(dogs.getString(i));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return animalIDS;
	}

	private SocialInfo parseSocialInfo(JSONObject jSocialInfo) {
		SocialInfo socialInfo = new SocialInfo();

		try {
			JSONArray fbFriends = jSocialInfo.getJSONObject(
					SOCIAL.FACEBOOK.NAME)
					.getJSONArray(SOCIAL.FACEBOOK._FRIENDS);
			socialInfo.FacebookFriends = new ArrayList<String>();

			for (int i = 0; i < fbFriends.length(); i++) {
				String friend = fbFriends.getString(i);
				socialInfo.FacebookFriends.add(friend);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return socialInfo;
	}

	private StaticLocation parseStaticLocation(JSONObject jStaticLocation) {
		StaticLocation staticLoc = new StaticLocation();

		try {
			if (jStaticLocation.has(LOCATION.ACCESS.NAME)) {
				JSONObject access = jStaticLocation
						.getJSONObject(LOCATION.ACCESS.NAME);

				if (access.has(ACCESS.ACCESS)) {
					staticLoc.Access = access.getString(ACCESS.ACCESS);
				}

				if (access.has(ACCESS.ACCESS_INFO)) {
					staticLoc.AccessInfo = access.getString(ACCESS.ACCESS_INFO);
				}
			}

			if (jStaticLocation.has(STATIC_LOCATION.NAME)) {
				JSONObject jStaticLoc = jStaticLocation
						.getJSONObject(STATIC_LOCATION.NAME);

				if (jStaticLoc.has(STATIC_LOCATION.TS)) {
					staticLoc.Timestamp = Double.parseDouble(jStaticLoc
							.getString(STATIC_LOCATION.TS));
				}

				if (jStaticLoc.has(STATIC_LOCATION.FORMATTED)) {
					staticLoc.Formatted = jStaticLoc
							.getString(STATIC_LOCATION.FORMATTED);
				}

				if (jStaticLoc.has(STATIC_LOCATION._LOC)) {
					JSONArray jLocation = jStaticLoc
							.getJSONArray(com.haski.swifto.model.vo.json.GetWalksParser.WALKS.WALK.ADDRESS._LOC);

					if (jLocation != null && jLocation.length() > 0) {
						staticLoc.Location = parseLocationFromArray(jLocation,
								false);
					}
				}

				if (jStaticLoc.has(STATIC_LOCATION.ZIP)) {
					staticLoc.Zip = jStaticLoc.getString(STATIC_LOCATION.ZIP);
				}

				if (jStaticLoc.has(STATIC_LOCATION.COUNTRY)) {
					staticLoc.Country = jStaticLoc
							.getString(STATIC_LOCATION.COUNTRY);
				}

				if (jStaticLoc.has(STATIC_LOCATION.STATE)) {
					staticLoc.State = jStaticLoc
							.getString(STATIC_LOCATION.STATE);
				}

				if (jStaticLoc.has(STATIC_LOCATION.CITY)) {
					staticLoc.City = jStaticLoc.getString(STATIC_LOCATION.CITY);
				}

				if (jStaticLoc.has(STATIC_LOCATION.AREA)) {
					staticLoc.Area = jStaticLoc.getString(STATIC_LOCATION.AREA);
				}

				if (jStaticLoc.has(STATIC_LOCATION.NEIGHBORHOOD)) {
					staticLoc.Neighborhood = jStaticLoc
							.getString(STATIC_LOCATION.NEIGHBORHOOD);
				}

				if (jStaticLoc.has(STATIC_LOCATION.STREET)) {
					staticLoc.Street = jStaticLoc
							.getString(STATIC_LOCATION.STREET);
				}

				if (jStaticLoc.has(STATIC_LOCATION.STREET_NUM)) {
					staticLoc.StreetNum = jStaticLoc
							.getString(STATIC_LOCATION.STREET_NUM);
				}

				if (jStaticLoc.has(STATIC_LOCATION.APARTMENT)) {
					staticLoc.Apartment = jStaticLoc
							.getString(STATIC_LOCATION.APARTMENT);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return staticLoc;
	}

	private GeoPoint parseLocationFromArray(JSONArray jLocation,
			boolean swapLatLng) {
		if (jLocation != null && jLocation.length() > 0) {
			double lat;
			double lng;
			try {
				// TODO: ??????????? ? ???????? ? ???????
				if (!swapLatLng) {
					lat = jLocation.getDouble(0);
					lng = jLocation.getDouble(1);
				} else {
					lat = jLocation.getDouble(1);
					lng = jLocation.getDouble(0);
				}
				return new GeoPoint((int) (lat * GeoUtils.MULTIPLEXOR),
						(int) (lng * GeoUtils.MULTIPLEXOR));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void fillBaseJsonFields(JSONObject jObject, BaseEntity objectToFill) {
		try {
			if (jObject.has(JSON_ATTRIBUTES._ID)) {
				objectToFill._id = jObject.getString(JSON_ATTRIBUTES._ID);
			}

			if (jObject.has(JSON_ATTRIBUTES.NID)) {
				objectToFill.nid = jObject.getString(JSON_ATTRIBUTES.NID);
			}

			if (jObject.has(JSON_ATTRIBUTES.SYNC)) {
				objectToFill.sync = jObject.getInt(JSON_ATTRIBUTES.SYNC);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
