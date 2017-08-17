package com.haski.swifto.model.vo;

public class ContactDog {

	public String mAddressPhoneForCotact;
	public String FirstName;
	public String LastName;
	public String PhonePrimary;
	public String _id;
	public boolean isStatic;
	public String email;
	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ContactDog(String _id,String firstName, String lastName, String phonePrimary , boolean isStatic) {
		this._id = _id;
		FirstName = firstName;
		LastName = lastName;
		PhonePrimary = phonePrimary;
		this.isStatic = isStatic;
	}

	public String getAddressAndPhoneForContactDog() {
		if (mAddressPhoneForCotact == null) {
			mAddressPhoneForCotact = "<b>";
			// if(StaticLocation.Neighborhood != null)
			// {
			// mAddressPhone += StaticLocation.Neighborhood + "\n";
			// }
			if (this.FirstName != null) {
				mAddressPhoneForCotact += this.FirstName;

				if (this.LastName != null) {
					mAddressPhoneForCotact += " " + this.LastName;
				}

				mAddressPhoneForCotact += "</b><br>\n";
			}
			if (PhonePrimary != null) {
				mAddressPhoneForCotact += PhonePrimary + "\n";
			}
		}

		return mAddressPhoneForCotact;
	}
}
