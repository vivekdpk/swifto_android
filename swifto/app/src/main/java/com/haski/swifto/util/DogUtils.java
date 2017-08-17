package com.haski.swifto.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.haski.swifto.model.vo.dog.Comment;
import com.haski.swifto.model.vo.dog.Dog;
import com.haski.swifto.model.vo.dog.DogCommands;
import com.haski.swifto.model.vo.dog.DogCompositeFeature;
import com.haski.swifto.model.vo.dog.Features;
import com.haski.swifto.model.vo.dog.FeaturesDetails;
import com.haski.swifto.model.vo.dog.Services;

public class DogUtils {

	/**TODO: can be moved to StringUtils
	 * Generates WHERE clause for database query based on number of arguments<br>
	 * <br>
	 * <b>"_ID_=? OR _ID_=? OR ..."</b>
	 * */

	//nkp
	public static String getCommentIDs(Dog dog)
	{
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < dog.Comments.size(); i++)
		{
			Comment comment = dog.Comments.get(i);
			builder.append(comment._id);

			if(i < dog.Comments.size() - 1)
			{
				builder.append(",");
			}
		}

		return builder.toString();
	}

	public static String getWhereClause(int number) {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < number; i++) {
			builder.append("_ID_=?");
			
			if(i < number - 1) {
				builder.append(" OR ");
			}
		}
		
		return builder.toString();
	}

	public static String getWhereClauseComment(int number) {
		StringBuilder builder = new StringBuilder();

		for(int i = 0; i < number; i++) {
			builder.append("_ID=?");

			if(i < number - 1) {
				builder.append(" OR ");
			}
		}

		return builder.toString();
	}
	
	public static String generateDogDescription(Dog dog) {
		StringBuilder builder = new StringBuilder();
		
		if(!dog.Breed.equals("")) {
			builder.append(dog.Breed);
			builder.append(",");
		}
		
		if(!dog.Gender.equals("")) {
			builder.append(dog.Gender);
			builder.append(",\n");
		}
		
		if(!dog.BirthData.equals("")) {
			//2011-08-01 00:00:00
			try {
				Date d = new SimpleDateFormat(DateTimeUtils.PATTERN_YYYY_MM_DD_HH_MM_SS).parse(dog.BirthData);

				String age = DateTimeUtils.getAge(d);
				builder.append(age);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		String s = builder.toString();
		
		if(s.endsWith(",")) {
			s = s.substring(0, s.length()-1);
		} else if(s.endsWith(",\n")) {
			s = s.substring(0, s.length()-2);
		}
		
		return s;
	}
	
	public static String generateCommands(Dog dog) {
		StringBuilder builder = new StringBuilder();
		
		//go over dog.Commands
		String commandCome = dog.Commands.Come;
		String commandPraise = dog.Commands.Praise;
		String commandSit = dog.Commands.Sit;
		String commandStop = dog.Commands.Stop;
		
		System.out.println("commandCome = "+commandCome);
		System.out.println("commandPraise = "+commandPraise);
		System.out.println("commandSit = "+commandSit);
		System.out.println("commandStop = "+commandStop);
		
		if(!commandCome.equals(DogCommands.DEFAULT_COME) && !commandCome.equals("")) {
			builder.append(String.format(DogCommands.PATTERN_COME, commandCome));
			builder.append("\n");
		}
		
		if(!commandPraise.equals(DogCommands.DEFAULT_PRAISE) && !commandPraise.equals("")) {
			builder.append(String.format(DogCommands.PATTERN_PRAISE, commandPraise));
			builder.append("\n");
		}
		
		if(!commandSit.equals(DogCommands.DEFAULT_SIT) && !commandSit.equals("")) {
			builder.append(String.format(DogCommands.PATTERN_SIT, commandSit));
			builder.append("\n");
		}
		
		if(!commandStop.equals(DogCommands.DEFAULT_STOP) && !commandStop.equals("")) {
			builder.append(String.format(DogCommands.PATTERN_STOP, commandStop));
		}
		
		return builder.toString();
	}
	
	public static String generateEmergancyInfo(Dog dog) {
		boolean contactExists = dog.EmergencyPhoneContact != null;
		boolean vetExists = dog.EmergencyPhoneVet != null;
		
		if(!vetExists) {
			if(!contactExists) {
				return "";
			} else {
				return String.format("Emergency info:\nContact: %s", dog.EmergencyPhoneContact);
			}
		} else {
			if(!contactExists) {
				return String.format("Emergency info:\nContact name: %s", dog.EmergencyPhoneVet);
			} else {
				return String.format("Emergency info:\nContact name: %s\nContact phone: %s", dog.EmergencyPhoneVet, dog.EmergencyPhoneContact);
			}
		}
	}
	
	public static ArrayList<DogCompositeFeature> generateCompositeFeatures(Dog dog , Services mCurrentServices) {
		ArrayList<DogCompositeFeature> compositeFeatures = new ArrayList<DogCompositeFeature>();
		
		Features features = dog.Features;
		FeaturesDetails featuresDetails = dog.FeaturesDetails;
		
		//non-detailed features
		if(features.Aggressive) {
			DogCompositeFeature feat = new DogCompositeFeature();
			feat.setYes(true);
			feat.setDescription(Features.PATTERN_AGGRESSIVE);
			
			compositeFeatures.add(feat);
		}
		
		if(features.Friendly) {
			DogCompositeFeature feat = new DogCompositeFeature();
			feat.setYes(true);
			feat.setDescription(Features.PATTERN_FRIENDLY);
			
			compositeFeatures.add(feat);
		}
		
		if(features.NotreatsNeeded) {
			DogCompositeFeature feat = new DogCompositeFeature();
			
			if(features.Notreats == false) {
				feat.setYes(true);
				feat.setDescription(String.format(Features.PATTERN_NO_TREATS_FALSE, dog.Name));
			} else {
				feat.setYes(false);
				feat.setDescription(String.format(Features.PATTERN_NO_TREATS_TRUE, dog.Name));
				compositeFeatures.add(feat);
			}
			
			//compositeFeatures.add(feat);
		}
		
		if(features.Pullleash) {
			DogCompositeFeature feat = new DogCompositeFeature();
			feat.setYes(true);
			feat.setDescription(Features.PATTERN_PULL_LEASH);
			
			compositeFeatures.add(feat);
		}
		
		if(features.ToysNervous) {
			DogCompositeFeature feat = new DogCompositeFeature();
			feat.setYes(true);
			feat.setDescription(Features.PATTERN_TOYS_NERVOUS);
			
			compositeFeatures.add(feat);
		}
		
		//detailed features
		if(features.Alergic == true) {
			DogCompositeFeature feat = new DogCompositeFeature();
			feat.setYes(true);
			feat.setDescription(String.format(FeaturesDetails.PATTERN_ALLERGIC, featuresDetails.Allergic));
			
			compositeFeatures.add(feat);
		}
		
//		if(features.Medication == true) {
//			DogCompositeFeature feat = new DogCompositeFeature();
//			feat.setYes(true);
//			feat.setDescription(String.format(FeaturesDetails.PATTERN_MEDICATION, featuresDetails.Medication));
//
//			compositeFeatures.add(feat);
//		}

		// new code
		if(mCurrentServices.Feed != null && mCurrentServices.Feed && mCurrentServices.FeedInstructions != null) {
			DogCompositeFeature Feed = new DogCompositeFeature();
			Feed.setYes(true);
			Feed.setDescription(String.format(FeaturesDetails.PATTERN_FEEDINSTRUCTIONS, mCurrentServices.FeedInstructions));

			compositeFeatures.add(Feed);
		}

		if(mCurrentServices.Medicate != null && mCurrentServices.Medicate && mCurrentServices.MedicateInstructions != null) {
			DogCompositeFeature Medicate = new DogCompositeFeature();
			Medicate.setYes(true);
			Medicate.setDescription(String.format(FeaturesDetails.PATTERN_MEDICATION, mCurrentServices.MedicateInstructions));

			compositeFeatures.add(Medicate);
		}

		// End of new code new code
		
		if(features.ChildNervous == true) {
			DogCompositeFeature feat = new DogCompositeFeature();
			feat.setYes(true);
			
			String descr = FeaturesDetails.PATTERN_SITUATION_CHILD;
			
			if(featuresDetails.SituationChild != "" && featuresDetails.SituationChild.length() > 1)
			{
				descr += " " + featuresDetails.SituationChild.substring(0, 1).toUpperCase(Locale.getDefault()) + featuresDetails.SituationChild.substring(1);
			}
			
			feat.setDescription(descr);
			
			compositeFeatures.add(feat);
		}
		
		if(features.Dognervous == true) {
			DogCompositeFeature feat = new DogCompositeFeature();
			feat.setYes(true);
			
			String descr = FeaturesDetails.PATTERN_SITUATION_DOG;
			
			if(featuresDetails.SituationDog != "" && featuresDetails.SituationDog.length() > 1)
			{
				descr += " " + featuresDetails.SituationDog.substring(0, 1).toUpperCase(Locale.getDefault()) + featuresDetails.SituationDog.substring(1);
			}
			
			feat.setDescription(descr);
			
			compositeFeatures.add(feat);
		}
		
		if(features.StrangerNervous == true) {
			DogCompositeFeature feat = new DogCompositeFeature();
			feat.setYes(true);
			
			String descr = FeaturesDetails.PATTERN_SITUATION_STRANGER;
			
			if(featuresDetails.SituationStranger != "" && featuresDetails.SituationStranger.length() > 1) {
				descr += " " + featuresDetails.SituationStranger.substring(0, 1).toUpperCase(Locale.getDefault()) + featuresDetails.SituationStranger.substring(1);
			}
			
			feat.setDescription(descr);
			
			compositeFeatures.add(feat);
		}
		
		if(features.ToysNervous == true){
			DogCompositeFeature feat = new DogCompositeFeature();
			feat.setYes(true);
			
			String descr = FeaturesDetails.PATTERN_SITUATION_TOYS;
			
			if(featuresDetails.SituationToys != "" && featuresDetails.SituationToys.length() > 0){
				descr += " " + featuresDetails.SituationToys;
			}
			
			feat.setDescription(descr);
			
			compositeFeatures.add(feat);
		}
		
		return compositeFeatures;
	}
}
