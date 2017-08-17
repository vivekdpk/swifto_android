package com.haski.swifto.model.vo.dog;


public class Services {

	public Boolean Feed = false;
	public Boolean Medicate = false;
	public String MedicateInstructions;
	public String FeedInstructions;
	public String _Id;
	public String Dog_Id;

	@Override
	protected Services clone() throws CloneNotSupportedException {
		Services toRet = new Services();
		toRet._Id = this._Id;
		toRet.Feed = this.Feed;
		toRet.Medicate = this.Medicate;
		toRet.MedicateInstructions = this.MedicateInstructions;
		toRet.FeedInstructions = this.FeedInstructions;
		toRet.Dog_Id = this.Dog_Id;
		return toRet;
	}

	public Services getClone() {
		try {
			return clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
