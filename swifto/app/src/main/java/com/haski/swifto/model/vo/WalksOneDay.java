package com.haski.swifto.model.vo;

import java.util.ArrayList;

import com.haski.swifto.model.vo.walk.Walk;

public class WalksOneDay {
	
	public WalksOneDay() {
		mListWalks = new ArrayList<Walk>();
	}

	private String mDayName;
	public String getDayName() {
		return mDayName;
	}
	public void setDayName(String mDayName) {
		this.mDayName = mDayName;
	}
	
	private String mDate;
	public void setDate(String mDate) {
		this.mDate = mDate;
	}
	public String getDate() {
		return mDate;
	}
	
	private ArrayList<Walk> mListWalks;
	public void setmListWalks(ArrayList<Walk> mListWalks) {
		this.mListWalks = mListWalks;
	}
	
	public ArrayList<Walk> getmListWalks() {
		return mListWalks;
	}
}
