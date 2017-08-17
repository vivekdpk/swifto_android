package com.haski.swifto.model.vo.owner;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.haski.swifto.model.vo.BaseEntity;
import com.haski.swifto.model.vo.StaticLocation;

public class OwnerOfDog extends BaseEntity {
	public int UID;
	public String Email;
	public String UserName;
	public StaticLocation StaticLocation;
	public SocialInfo SocialInfo;
	
	public ArrayList<String> AnimalIDs;
	
	public boolean MethodPush;
	public boolean MethodEmail;
	public boolean MethodSms;
	
	public String PhonePrimary;
	
	public int BirthYear;
	public String FirstName;
	public String LastName;
	public String Gender;
	
	private String mAddressPhone;
	private String mAddressPhoneForCotact;
	
	public String getAddressAndPhone() {
		if(mAddressPhone == null) {
			mAddressPhone = "";
//			if(StaticLocation.Neighborhood != null)
//			{
//				mAddressPhone += StaticLocation.Neighborhood + "\n";
//			}
			if(this.FirstName != null) {
				mAddressPhone += this.FirstName;
				
				if(this.LastName != null) {
					mAddressPhone += " " + this.LastName;
				}
				
				mAddressPhone += "\n";
			}
			
			if(PhonePrimary != null) {
				mAddressPhone += PhonePrimary + "\n";
			}
			
//			if(StaticLocation.Formatted != null) {
//				mAddressPhone += StaticLocation.Formatted + "\n";
//			}
//
//			if(StaticLocation.Apartment != null) {
//				mAddressPhone += "Apartment: " + StaticLocation.Apartment;
//			}
		}
		
		return mAddressPhone;
	}
	
	public String getAddressAndPhoneForContact() {
		if(mAddressPhoneForCotact == null) {
			mAddressPhoneForCotact = "<b>";
//			if(StaticLocation.Neighborhood != null)
//			{
//				mAddressPhone += StaticLocation.Neighborhood + "\n";
//			}
			if(this.FirstName != null) {
				mAddressPhoneForCotact += this.FirstName;
				
				if(this.LastName != null) {
					mAddressPhoneForCotact += " " + this.LastName;
				}
				
				mAddressPhoneForCotact += "</b><br>\n";
			}
			if(PhonePrimary != null) {
				mAddressPhoneForCotact += PhonePrimary + "\n";
			}
		}
		
		return mAddressPhoneForCotact;
	}
	
	@Override
	protected OwnerOfDog clone() throws CloneNotSupportedException {
		OwnerOfDog toRet = new OwnerOfDog();
		toRet._id = this._id;
		toRet.Email = this.Email;
		toRet.AnimalIDs = new ArrayList<String>();
		
		for(String aniamlID : this.AnimalIDs) {
			toRet.AnimalIDs.add(aniamlID);
		}
		
		toRet.BirthYear = this.BirthYear;
		toRet.FirstName = this.FirstName;
		toRet.Gender = this.Gender;
		toRet.LastName = this.LastName;
		toRet.MethodEmail = this.MethodEmail;
		toRet.MethodPush = this.MethodPush;
		toRet.MethodSms = this.MethodSms;
		toRet.nid = this.nid;
		toRet.PhonePrimary = this.PhonePrimary;
		toRet.SocialInfo = new SocialInfo();
		
		toRet.SocialInfo.FacebookFriends = new ArrayList<String>();

		for(String oneFriend : this.SocialInfo.FacebookFriends) {
			toRet.SocialInfo.FacebookFriends.add(oneFriend);
		}
		
		toRet.StaticLocation = new StaticLocation();
		toRet.StaticLocation.Access = this.StaticLocation.Access;
		toRet.StaticLocation.AccessInfo = this.StaticLocation.AccessInfo;
		toRet.StaticLocation.Area = this.StaticLocation.Area;
		toRet.StaticLocation.City = this.StaticLocation.City;
		toRet.StaticLocation.Country = this.StaticLocation.Country;
		toRet.StaticLocation.Formatted = this.StaticLocation.Formatted;
		toRet.StaticLocation.Neighborhood = this.StaticLocation.Neighborhood;
		toRet.StaticLocation.Original = this.StaticLocation.Original;
		toRet.StaticLocation.State = this.StaticLocation.State;
		toRet.StaticLocation.Street = this.StaticLocation.Street;
		toRet.StaticLocation.StreetNum = this.StaticLocation.StreetNum;
		toRet.StaticLocation.Timestamp = this.StaticLocation.Timestamp;
		toRet.StaticLocation.Zip = this.StaticLocation.Zip;

		if(this.StaticLocation.Location != null) {
			toRet.StaticLocation.Location = new GeoPoint(this.StaticLocation.Location.getLatitudeE6(), this.StaticLocation.Location.getLongitudeE6());
		}
	
		toRet.sync = this.sync;
		toRet.UID = this.UID;
		toRet.UserName = this.UserName;
		
		return toRet;
	}
	
	public OwnerOfDog getClone() {
		try {
			return clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
