package com.haski.swifto.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.haski.swifto.R;
import com.haski.swifto.model.vo.dog.Dog;
import com.haski.swifto.model.vo.dog.DogCompositeFeature;
import com.haski.swifto.model.vo.dog.Services;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.util.DogUtils;
import com.haski.swifto.util.StringUtils;

public class DogInfoListAdapter extends BaseAdapter {
	int counter;

	public DogInfoListAdapter(Context ctx, Walk walk) {
		super();

		mContext = ctx;
		mWalk = walk;
		mInflater = LayoutInflater.from(mContext);
		counter = 0;
	}

	private Context mContext;
	private Walk mWalk;
	private LayoutInflater mInflater;
	String sens = "";

	/*
	 * Overridden
	 */
	public int getCount() {
		return mWalk.Dogs.size();
	}

	public Object getItem(int position) {
		return mWalk;
	}

	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		TextView tvName;
		TextView tvDescription;
		TextView tvAddress;
		TextView tvAccess;

		ListView listFeatures;

		LinearLayout layoutSensitives;
		TextView tvSens;
		ImageView imgDog;

		ImageView imgCold;
		ImageView imgHot;
		ImageView imgRain;

		TextView tvCommands;
		TextView tvCommandsValues;
		TextView tvAdditionalInfo;

		TextView tvAddNewComment;
		LinearLayout lLCommentContainer, lLCommentListContainer;
		ListView commentListView;
		
	}

	private Dog mCurrentDog;
	private Services mCurrentServices;

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_dog_info, null);

			holder = new ViewHolder();

			holder.tvAccess = (TextView) convertView
					.findViewById(R.id.list_item_dog_info_txt_access);
			holder.tvAddress = (TextView) convertView
					.findViewById(R.id.list_item_dog_info_txt_address);
			holder.tvCommands = (TextView) convertView
					.findViewById(R.id.list_item_dog_info_txt_commands);
			holder.tvCommandsValues = (TextView) convertView
					.findViewById(R.id.list_item_dog_info_txt_commands_values);
			holder.tvDescription = (TextView) convertView
					.findViewById(R.id.list_item_dog_info_txt_description);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.list_item_dog_info_txt_name);
			holder.tvSens = (TextView) convertView
					.findViewById(R.id.list_item_dog_info_txt_sens);
			holder.tvAdditionalInfo = (TextView) convertView
					.findViewById(R.id.list_item_dog_info_txt_additional_info);

			holder.imgDog = (ImageView) convertView
					.findViewById(R.id.list_item_dog_info_img);

			holder.listFeatures = (ListView) convertView
					.findViewById(R.id.list_item_dog_info_list_features);

			holder.layoutSensitives = (LinearLayout) convertView
					.findViewById(R.id.list_item_dog_info_layout_sens);
			holder.imgCold = (ImageView) convertView
					.findViewById(R.id.list_item_dog_info_img_sens_cold);
			holder.imgHot = (ImageView) convertView
					.findViewById(R.id.list_item_dog_info_img_sens_hot);
			holder.imgRain = (ImageView) convertView
					.findViewById(R.id.list_item_dog_info_img_sens_rain);

			holder.tvAddNewComment = (TextView) convertView.findViewById(R.id.add_new_comment);
			holder.lLCommentContainer = (LinearLayout) convertView.findViewById(R.id.commentContainer);
			holder.lLCommentListContainer = (LinearLayout) convertView.findViewById(R.id.commentListContainer);
			holder.commentListView = (ListView) convertView.findViewById(R.id.commentListView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		mCurrentDog = mWalk.Dogs.get(position);
		mCurrentServices = mWalk.Servicess.get(position);

		//Log.d("comme ", " nkp " + mWalk.Dogs.get(position).Comments);

		// new
		// AQuery(convertView).id(R.id.list_item_dog_info_img).image(mCurrentDog.Pic,
		// true, true, 0, 0, null, AQuery.FADE_IN).width(100, true).height(100,
		// true);
		// new
		// AQuery(convertView).id(R.id.list_item_dog_info_img).image(mCurrentDog.Pic,
		// true, true, 0, R.drawable.dog_default_big).width(100,
		// true).height(100, true);
		new AQuery(convertView)
				.id(holder.imgDog)
				.image(mCurrentDog.Pic, true, true, 0,
						R.drawable.dog_default_big).width(100, true)
				.height(100, true);

		holder.tvName.setText(StringUtils
				.capitalizeFirstLettersOfEachWord(mCurrentDog.Name));

		holder.tvDescription.setText(DogUtils
				.generateDogDescription(mCurrentDog));

		//String address = mWalk.Owner.StaticLocation.Formatted;

		String address = mWalk.Walk_Address_original;

		if (mWalk.Owner.StaticLocation.Apartment != null) {
			address += "\nApartment: " + mWalk.Owner.StaticLocation.Apartment;
		}


//		if (mWalk.Original != null) {
//			address += "\n\nNotes: " + "\n * ***DROP OFF AT*** " + mWalk.Original+"\n";
//		}

		if (mWalk.NotesOwner != null) {
			address += "\n\nNotes: " + "\n" + mWalk.NotesOwner+"\n";
		}


		if(position==0) {
			holder.tvAddress.setText(address);
		}


		String access = "Access to apartment: "
				+ mWalk.Owner.StaticLocation.Access;

		String accessInfo = mWalk.Owner.StaticLocation.AccessInfo;

		if (accessInfo != null && !accessInfo.equals("")) {
			access += "\n" + accessInfo;
		}

		if (mCurrentDog.AdditionalInfo != null
				&& !mCurrentDog.AdditionalInfo.equals("")) {
			access += "\nAdditional info: " + mCurrentDog.AdditionalInfo;
		}

//		if (mCurrentServices.Feed != null && mCurrentServices.Feed && mCurrentServices.FeedInstructions != null) {
//
//			access += "\n\nFeeding Instructions: " + mCurrentServices.FeedInstructions;
//		}



		// TODO: remove
		// access += String.format(Locale.getDefault(), "\nFeed: %b",
		// mWalk.DogsToFeedIDs.indexOf(mCurrentDog._id) != -1);

		if(position==0) {
			holder.tvAccess.setText(access);
		}

		ArrayList<DogCompositeFeature> compositeFeatures = DogUtils
				.generateCompositeFeatures(mCurrentDog , mCurrentServices);

		if (compositeFeatures.size() > 0) {
			holder.listFeatures.setVisibility(View.VISIBLE);
			DogCompositeFeaturesAdapter adapter = new DogCompositeFeaturesAdapter(
					mContext, R.layout.list_item_dog_feature, compositeFeatures);
			holder.listFeatures.setAdapter(adapter);
		} else {
			compositeFeatures = null;
			holder.listFeatures.setVisibility(View.GONE);
		}

		if (mCurrentDog.Features.ColdSensitive == false
				&& mCurrentDog.Features.Hotsensitive == false
				&& mCurrentDog.Features.RainSensitive == false) {
			holder.layoutSensitives.setVisibility(View.GONE);
		} else {
			//holder.layoutSensitives.setVisibility(View.VISIBLE);

			if (mCurrentDog.Features.ColdSensitive) {
				if (sens.equals("")) {
					sens = "cold";
				} else {
					sens = sens + ", cold";
				}
			}
			
			if (mCurrentDog.Features.RainSensitive) {
				if (sens.equals("")) {
					sens = "rain";
				} else {
					sens = sens + ", rain";
				}
			}
			
			if (mCurrentDog.Features.Hotsensitive) {
				if (sens.equals("")) {
					sens = "heat";
				} else {
					sens = sens + ", heat";
				}
			}
			
			holder.tvSens.setText(String.format("%s is sensitive to: %s",
					mCurrentDog.Name,sens));

//			holder.imgCold
//					.setVisibility(mCurrentDog.Features.ColdSensitive ? View.VISIBLE
//							: View.GONE);
//			holder.imgHot
//					.setVisibility(mCurrentDog.Features.Hotsensitive ? View.VISIBLE
//							: View.GONE);
//			holder.imgRain
//					.setVisibility(mCurrentDog.Features.RainSensitive ? View.VISIBLE
//							: View.GONE);

		}

		String commands = DogUtils.generateCommands(mCurrentDog);

		if (commands.equals("")) {
			holder.tvCommands.setVisibility(View.GONE);
			holder.tvCommandsValues.setVisibility(View.GONE);
		} else {
			holder.tvCommands.setVisibility(View.VISIBLE);
			holder.tvCommandsValues.setVisibility(View.VISIBLE);

			holder.tvCommandsValues.setText(commands);
		}

		String emergencyInfo = DogUtils.generateEmergancyInfo(mCurrentDog);

		if (emergencyInfo != "") {
			holder.tvAdditionalInfo.setVisibility(View.VISIBLE);
			holder.tvAdditionalInfo.setText(emergencyInfo);
		} else {
			holder.tvAdditionalInfo.setVisibility(View.GONE);
		}

		if(position == 0  && counter == 0){
			holder.lLCommentContainer.setVisibility(View.VISIBLE);
			counter++;
		}else {
			holder.lLCommentContainer.setVisibility(View.VISIBLE);
			//holder.lLCommentContainer.setVisibility(View.GONE);
		}

		if (mCurrentDog.Comments != null){
			//Log.i("comments nkp", ""+ mCurrentDog.Comments.get(0).title);
			if(mCurrentDog.Comments.size() > 0 ){
				holder.lLCommentListContainer.setVisibility(View.VISIBLE);
				if(holder.commentListView != null){
					CommentAdapter commentAdapter = new CommentAdapter(mContext, R.layout.comment_list_row, mCurrentDog.Comments);
					holder.commentListView.setAdapter(commentAdapter);
				}
			}
		}


		final String nid = mCurrentDog.nid;
	//Log.d("nid nkp adapt", nid);
		holder.tvAddNewComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://swifto.com/node/" + nid +"#comments"));
				mContext.startActivity(browserIntent);
				//Toast.makeText(mContext, "click on " + nid , Toast.LENGTH_SHORT).show();
			}
		});

		return convertView;
	}
}
