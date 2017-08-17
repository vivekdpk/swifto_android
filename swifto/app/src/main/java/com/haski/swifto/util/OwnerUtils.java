package com.haski.swifto.util;

import com.haski.swifto.model.vo.owner.OwnerOfDog;

public class OwnerUtils {

	public static String getAnimalIDS(OwnerOfDog owner)
	{
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < owner.AnimalIDs.size(); i++) {
			String id = owner.AnimalIDs.get(i);
			
			builder.append(id);
			
			if(i < owner.AnimalIDs.size() - 1)
			{
				builder.append(",");
			}
		}
		
		return builder.toString();
	}

	public static String getSocialFacebookFriendsIDS(OwnerOfDog owner)
	{
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < owner.SocialInfo.FacebookFriends.size(); i++) {
			String friend = owner.SocialInfo.FacebookFriends.get(i);
			
			builder.append(friend);
			
			if(i < owner.SocialInfo.FacebookFriends.size() - 1)
			{
				builder.append(",");
			}
		}
		
		return builder.toString();
	}
	
}
