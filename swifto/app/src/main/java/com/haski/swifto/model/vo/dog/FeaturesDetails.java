package com.haski.swifto.model.vo.dog;

public class FeaturesDetails {
	
	public static final String PATTERN_ALLERGIC = "Is alergic to %s";
	public static final String PATTERN_MEDICATION = "Is on medication: %s";
	public static final String PATTERN_FEEDINSTRUCTIONS = "Feed Instructions: %s";
	public static final String PATTERN_SITUATION_CHILD = "Nervous around kids: ";
	public static final String PATTERN_SITUATION_DOG = "Nervous around other dogs: ";
	public static final String PATTERN_SITUATION_STRANGER = "Nervous around strangers: ";
	public static final String PATTERN_SITUATION_TOYS = "Nervous when touching it's toys: ";
	
	public String Allergic;
	public String Medication;
	public String SituationChild;
	public String SituationDog;
	public String SituationStranger;
	public String SituationToys;
	
	public FeaturesDetails() {
		Allergic = "";			//"Aspirine";
		Medication = "";		//"Validol";
		SituationChild = "";	// "can cut their fingers off";
		SituationDog = "";
		SituationStranger = "";	// "searching it\'s throat";
		SituationToys = "";
	}
}
