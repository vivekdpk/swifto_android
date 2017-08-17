package com.haski.swifto.model.vo.dog;

public class DogCommands {
	
	public static final String PATTERN_COME = "To come - %s ";
	public static final String PATTERN_PRAISE = "To praise - %s";
	public static final String PATTERN_SIT = "To sit - %s";
	public static final String PATTERN_STOP = "To stop - %s";

	public static final String DEFAULT_COME = "default_come";
	public static final String DEFAULT_PRAISE = "default_good";
	public static final String DEFAULT_SIT = "default_sit";
	public static final String DEFAULT_STOP = "default_stop";
	
	public DogCommands() {
		Come = DEFAULT_COME;
		Praise = DEFAULT_PRAISE;
		Sit = DEFAULT_SIT;
		Stop = DEFAULT_STOP;
	}
	
	public String Come;
	public String Praise;
	public String Sit;
	public String Stop;
}
