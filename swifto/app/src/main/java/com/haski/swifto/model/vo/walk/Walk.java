package com.haski.swifto.model.vo.walk;

import com.google.android.maps.GeoPoint;
import com.haski.swifto.model.vo.BaseEntity;
import com.haski.swifto.model.vo.dog.Dog;
import com.haski.swifto.model.vo.dog.Services;
import com.haski.swifto.model.vo.owner.OwnerOfDog;
import com.haski.swifto.util.DateTimeUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Walk extends BaseEntity {

	public String WalkType;

	/** In Seconds */
	public int Duration;

	/** In Seconds */
	public long StartDate;
	public String Name;

	/** scheduled, */
	public String Status;

	public String NotesOwner;
	public String Walk_Address_apartment;
	public String Walk_Address_original;
	public String Original;
	public String Formatted;

	public ArrayList<Dog> Dogs;
	public ArrayList<Services> Servicess;

	public String walkerID;

	public OwnerOfDog Owner;

	/** In seconds */
	public long StartTime;

	public long Minus;
	public long Plus;

	public GeoPoint Location;

	public int FeeValue;
	public String FeeValueFormatted;

	public int PriceValue;
	public String PriceValueFormatted;

	public String DogsToFeedIDs;

	private String mDogNames;

	/**
	 * <ul>
	 * <li>Rexi</li>
	 * <li>Rexi & Billiard Boy</li>
	 * <li>Rexi, Billiard Boy & Jenny</li>
	 * </ul>
	 * */
	public void generateDogNames() {
		mDogNames = "";

		for (int i = 0; i < Dogs.size(); i++) {
			mDogNames += i > 0 ? (i == Dogs.size() - 1 ? " & " : ", ") : "";

			mDogNames += Dogs.get(i).Name;
		}
	}

	/**
	 * <ul>
	 * <li>Rexi</li>
	 * <li>Rexi & Billiard Boy</li>
	 * <li>Rexi, Billiard Boy & Jenny</li>
	 * </ul>
	 * */
	public String getDogNames() {
		if (mDogNames == null) {
			generateDogNames();
		}

		return mDogNames;
	}

	private String mDurationPrice;

	/**
	 * <ul>
	 * <li>17 Min ($18)</li>
	 * <li>20 Min - Two Dogs ($25)</li>
	 * </ul>
	 * */
	public void generateDurationPrice() {
		StringBuilder builder = new StringBuilder();
		builder.append(Duration / 60);
		builder.append(" Min");

		// if(Dogs.size() > 1) {
		// builder.append(" - " + StringUtils.NUMBER_AS_TEXT(Dogs.size()));
		// builder.append(" Dogs");
		// }

		builder.append(" (" + FeeValueFormatted + ")");

		if (Minus != -1 && Plus != -1) {
			builder.append("\n(" + getStartTimeMinFormatted() + " -"
					+ getStartTimeMaxFormatted() + ")");

			//builder.append("\n(" + getTimeDiff() + ")");

		}


		mDurationPrice = builder.toString();
	}

	/**
	 * <ul>
	 * <li>17 Min ($18)</li>
	 * <li>20 Min - Two Dogs ($25)</li>
	 * </ul>
	 * */
	public String getDurationPrice() {
		if (mDurationPrice == null) {
			generateDurationPrice();
		}

		return mDurationPrice;
	}

	private ArrayList<String> mDogsImagesPaths;

	public ArrayList<String> getDogImagesPaths() {
		if (mDogsImagesPaths == null) {
			generateDogsImagesPaths();
		}

		return mDogsImagesPaths;
	}

	public void generateDogsImagesPaths() {
		mDogsImagesPaths = new ArrayList<String>(Dogs.size());

		for (Dog d : Dogs) {
			mDogsImagesPaths.add(d.Pic);
		}
	}

	private String mStartTimeFormatted;

	/** 10:30 AM */
	public String getStartTimeFormatted() {
		if (mStartTimeFormatted == null) {
			generateStartTimeFormatted();
		}

		return mStartTimeFormatted;
	}

	/** 10:30 AM */
	private void generateStartTimeFormatted() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(StartTime * 1000);

		Date dat = cal.getTime();

		SimpleDateFormat format = new SimpleDateFormat(
				DateTimeUtils.PATERN_AM_PM, Locale.getDefault());
		mStartTimeFormatted = format.format(dat);
	}

	private String mStartTimeForMaxmatted;

	/** 10:30 AM */
	public String getStartTimeMaxFormatted() {
		if (mStartTimeForMaxmatted == null) {
			generateStartTimeMaxFormatted();
		}

		return mStartTimeForMaxmatted;
	}

	/** 10:30 AM */
	private void generateStartTimeMaxFormatted() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis((StartTime + Plus * 60) * 1000);

		Date dat = cal.getTime();

		SimpleDateFormat format = new SimpleDateFormat(
				DateTimeUtils.PATERN_AM_PM, Locale.getDefault());
		mStartTimeForMaxmatted = format.format(dat);
	}

	private String mStartTimeForMinmatted;

	/** 10:30 AM */
	public String getStartTimeMinFormatted() {
		if (mStartTimeForMinmatted == null) {
			generateStartTimeMinFormatted();
		}

		return mStartTimeForMinmatted;
	}

	/** 10:30 AM */
	private void generateStartTimeMinFormatted() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis((StartTime - Minus * 60) * 1000);

		Date dat = cal.getTime();

		SimpleDateFormat format = new SimpleDateFormat(
				DateTimeUtils.PATERN_AM_PM, Locale.getDefault());
		mStartTimeForMinmatted = format.format(dat);
	}

	public long getTimeDiff() {

		long x = ((StartTime + Plus * 60) * 1000);

		Date d2 = new Date();
		Date d1 = new Date(x);

		System.out.println("time current = " + new Timestamp(d2.getTime()));
		System.out.println("time walk = " + new Timestamp(d1.getTime()));

		// long duration =d2.getTime() - d1.getTime();
		long duration = d2.getTime() - d1.getTime();

		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

		System.out.println("diffInMinutes = " + diffInMinutes);

		return diffInMinutes;
	}



	public long getEarlyTime() {

		long x = ((StartTime - Minus * 60) * 1000);

		Date d2 = new Date();
		Date d1 = new Date(x);

		System.out.println("time current = " + new Timestamp(d2.getTime()));
		System.out.println("time walk = " + new Timestamp(d1.getTime()));

		 long duration =d1.getTime() - d2.getTime();
		//long duration = d2.getTime() - d1.getTime();

		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

		System.out.println("diffInMinutes = " + diffInMinutes);

		return diffInMinutes;
	}

	private String mIdentifierStartEndMillis;

	/**
	 */
	public void setIdentifierStartEndMillis(String value) {
		mIdentifierStartEndMillis = value;
	}

	/**
	 */
	public String getIdentifierStartEndMillis() {
		return mIdentifierStartEndMillis;
	}

	@Override
	protected Walk clone() throws CloneNotSupportedException {
		Walk toRet = new Walk();
		toRet._id = this._id;
		toRet.Duration = this.Duration;
		toRet.FeeValue = this.FeeValue;
		toRet.FeeValueFormatted = this.FeeValueFormatted;
		toRet.Name = this.Name;
		toRet.nid = this.nid;
		toRet.PriceValue = this.PriceValue;
		toRet.PriceValueFormatted = this.PriceValueFormatted;
		toRet.StartDate = this.StartDate;
		toRet.StartTime = this.StartTime;
		toRet.Status = this.Status;
		toRet.sync = this.sync;
		toRet.walkerID = this.walkerID;
		toRet.WalkType = this.WalkType;

		toRet.Minus = this.Minus;
		toRet.Plus = this.Plus;

		toRet.Original = this.Original;
		toRet.Formatted = this.Formatted;
		toRet.NotesOwner = this.NotesOwner;
		toRet.Walk_Address_original  = this.Walk_Address_original;

		toRet.Walk_Address_apartment = this.Walk_Address_apartment;

		toRet.setIdentifierStartEndMillis(this.getIdentifierStartEndMillis());

		toRet.Dogs = new ArrayList<Dog>();

		for (Dog d : this.Dogs) {
			toRet.Dogs.add(d.getClone());
		}
		
		toRet.Servicess = new ArrayList<Services>();

		for (Services s : this.Servicess) {
			toRet.Servicess.add(s.getClone());
		}
		
		toRet.Owner = this.Owner.getClone();

		if (this.Location != null) {
			toRet.Location = new GeoPoint(this.Location.getLatitudeE6(),
					this.Location.getLongitudeE6());
		}

		return toRet;
	}

	public Walk getClone() {
		try {
			return clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String toString() {
		return "Walk{" +
				"Original is " + Original +
				", Formatted is " + Formatted +
				", StartDate is " + StartDate +
				", Name is " + Name +
				", NotesOwner is " + NotesOwner +
				", Walk_Address_original is " + Walk_Address_original +
				'}';
	}

	private String mAddressPhone;
	public String getAddressAndPhone() {
		if(mAddressPhone == null) {
			mAddressPhone = "";
//			if(StaticLocation.Neighborhood != null)
//			{
//				mAddressPhone += StaticLocation.Neighborhood + "\n";
//			}

			if(this.Walk_Address_original != null) {
				mAddressPhone += this.Walk_Address_original + "\n";
			}

			if(this.Walk_Address_apartment != null) {
				mAddressPhone += "Apartment: " +this.Walk_Address_apartment;
			}
		}

		return mAddressPhone;
	}
}
