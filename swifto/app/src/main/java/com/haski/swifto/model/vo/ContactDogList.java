package com.haski.swifto.model.vo;

import java.util.ArrayList;



public class ContactDogList {

	ArrayList<ContactDog> contactDog = new ArrayList<ContactDog>();
	private String mDayName;
	
	public ArrayList<ContactDog> getContactDog() {
		return contactDog;
	}
	public void setContactDog(ArrayList<ContactDog> contactDog) {
		this.contactDog = contactDog;
	}
	public String getmDayName() {
		return mDayName;
	}
	public void setmDayName(String mDayName) {
		this.mDayName = mDayName;
	}
	
	
}
