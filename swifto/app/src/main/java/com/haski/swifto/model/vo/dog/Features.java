package com.haski.swifto.model.vo.dog;

public class Features {
	
	public Features() {
		Aggressive = false;
		Friendly = false;
		NotreatsNeeded = false;
		Notreats = false;
		Pullleash = false;
		ToysNervous = false;
		
		Alergic = false;
		Medication = false;
		ChildNervous = false;
		Dognervous = false;
		StrangerNervous = false;
		ColdSensitive = false;
		Hotsensitive = false;
		RainSensitive = false;
	
	}
	
	public static String PATTERN_AGGRESSIVE = "Is aggressive";
	public static String PATTERN_FRIENDLY = "Is friendly";
	public static String PATTERN_NO_TREATS_TRUE = "Please do not give treats to %s";
	public static String PATTERN_NO_TREATS_FALSE = "%s can receive treats for good behavior";
	public static String PATTERN_PULL_LEASH = "Can pull leash.";
	public static String PATTERN_TOYS_NERVOUS = "Nervous when touching its toys.";

	
	//non-detailed
	public boolean Aggressive;
	public boolean Friendly;
	public boolean NotreatsNeeded;
	public boolean Notreats;
	public boolean Pullleash;
	public boolean ToysNervous;
	
	//detailed
	public boolean Alergic;
	public boolean Medication;
	public boolean ChildNervous;
	public boolean Dognervous;
	public boolean StrangerNervous;
	
	//sensitive
	public boolean ColdSensitive;
	public boolean Hotsensitive;
	public boolean RainSensitive;
}