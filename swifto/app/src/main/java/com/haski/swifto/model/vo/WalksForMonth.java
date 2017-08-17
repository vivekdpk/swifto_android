package com.haski.swifto.model.vo;

import java.util.ArrayList;

public class WalksForMonth extends BaseJSONObject {
	
	public WalksForMonth() {
		//this.mNumbersOfWalksForDay = new ArrayList<String>();
	}
	
	private int mDaysInMonth;
	public void setDaysInMonth(int mDaysInMonth) {
		this.mDaysInMonth = mDaysInMonth;
	}
	public int getDaysInMonth() {
		return mDaysInMonth;
	}
	
	private int mWalksInMonth;
	public void setWalksInMonth(int mWalksInMonth) {
		this.mWalksInMonth = mWalksInMonth;
	}
	public int getWalksInMonth() {
		return mWalksInMonth;
	}
	
	private int mYear;
	public void setYear(int mYear) {
		this.mYear = mYear;
	}
	
	public int getYear() {
		return mYear;
	}
	
	private int mMonth;
	public void setMonth(int mMonth) {
		this.mMonth = mMonth;
	}
	public int getMonth() {
		return mMonth;
	}
	
	private String mNumbers;
	public void setNumbers(String mNumbers) {
		this.mNumbers = mNumbers;
	}
	public String getNumbers()
	{
		return this.mNumbers;
	}
	
	public ArrayList<String> getNumbersAsArrayList() {
		ArrayList<String> toRet = new ArrayList<String>();
		
		if(mNumbers.equals("")) {
			
		} else {
			String[] splitted = mNumbers.split(",");
			
			for(int i = 0; i < splitted.length; i++) {
				toRet.add(splitted[i]);
			}
		}
		
		return toRet;
	}
	
	public boolean getIsMonthEmpty() {
		boolean toRet = true;
		
		if(mWalksInMonth != 0) {
			String[] splitted = mNumbers.split(",");
			
			for(int i = 0; i < splitted.length; i++) {
				if(splitted[i].equals("0") == false ) {
					toRet = false;
					break;
				}
			}
		}
		
		return toRet;
	}
}
