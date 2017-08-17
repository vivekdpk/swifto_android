package com.haski.swifto.model.vo.dog;

import com.google.android.maps.GeoPoint;
import com.haski.swifto.model.vo.Animal;

import java.util.ArrayList;

public class Dog extends Animal {

	public String AdditionalInfo = "";
	public String feedingInstructions = "";
	public String BirthData = "";
	public String Breed = "";
	public String Gender = "";
	public DogCommands Commands;
	public FeaturesDetails FeaturesDetails;
	public Features Features;
	public String Pic;
	public String Name = "";
	public String ownerID;
	public GeoPoint Location;
	public String EmergencyPhoneVet;
	public String EmergencyPhoneContact;
	public Boolean Feed = false;
	public Boolean Medicate = false;
	public String MedicateInstructions;
	public String FeedInstructions;
	public ArrayList<Comment> Comments;
	
	@Override
	protected Dog clone() throws CloneNotSupportedException {
		Dog toRet = new Dog();
		
		toRet._id = this._id;
		toRet.AdditionalInfo = this.AdditionalInfo;
		toRet.feedingInstructions = this.feedingInstructions;
		toRet.BirthData = this.BirthData;
		toRet.Breed = this.Breed;
		
		toRet.Commands = new DogCommands();
		toRet.Commands.Come = this.Commands.Come;
		toRet.Commands.Praise = this.Commands.Praise;
		toRet.Commands.Sit = this.Commands.Sit;
		toRet.Commands.Stop = this.Commands.Stop;
		
		toRet.Features = new Features();
		toRet.Features.Aggressive= this.Features.Aggressive;
		toRet.Features.Alergic = this.Features.Alergic;
		toRet.Features.ChildNervous = this.Features.ChildNervous;
		toRet.Features.ColdSensitive = this.Features.ColdSensitive;
		toRet.Features.Dognervous = this.Features.Dognervous;
		toRet.Features.Friendly = this.Features.Friendly;
		toRet.Features.Hotsensitive = this.Features.Hotsensitive;
		toRet.Features.Medication = this.Features.Medication;
		toRet.Features.Notreats = this.Features.Notreats;
		toRet.Features.NotreatsNeeded = this.Features.NotreatsNeeded;
		toRet.Features.Pullleash = this.Features.Pullleash;
		toRet.Features.RainSensitive = this.Features.RainSensitive;
		toRet.Features.StrangerNervous = this.Features.StrangerNervous;
		toRet.Features.ToysNervous = this.Features.ToysNervous;
		
		toRet.FeaturesDetails = new FeaturesDetails();
		toRet.FeaturesDetails.Allergic = this.FeaturesDetails.Allergic;
		toRet.FeaturesDetails.Medication = this.FeaturesDetails.Medication;
		toRet.FeaturesDetails.SituationChild = this.FeaturesDetails.SituationChild;
		toRet.FeaturesDetails.SituationDog = this.FeaturesDetails.SituationDog;
		toRet.FeaturesDetails.SituationStranger = this.FeaturesDetails.SituationStranger;
		toRet.FeaturesDetails.SituationToys = this.FeaturesDetails.SituationToys;
		
		toRet.Gender = this.Gender;
		
		if(this.Location != null)
		{
			toRet.Location = new GeoPoint(this.Location.getLatitudeE6(), this.Location.getLongitudeE6());
		}
		
		toRet.Name = this.Name;
		toRet.nid = this.nid;
		toRet.ownerID = this.ownerID;
		toRet.Pic = this.Pic;
		toRet.sync = this.sync;
		
		toRet.EmergencyPhoneContact = this.EmergencyPhoneContact;
		toRet.EmergencyPhoneVet = this.EmergencyPhoneVet;
		
		toRet.Feed = this.Feed;
		
		toRet.Medicate = this.Medicate;
		toRet.MedicateInstructions = this.MedicateInstructions;
		toRet.FeedInstructions = this.FeedInstructions;

        toRet.Comments = new ArrayList<Comment>();

        for (Comment c : this.Comments) {
            toRet.Comments.add(c.getClone());
        }
		
		return toRet;
	}
	
	public Dog getClone() {
		try {
			return clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
