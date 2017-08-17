package com.haski.swifto.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.haski.swifto.R;
import com.haski.swifto.model.vo.ContactDog;
import com.haski.swifto.model.vo.ContactDogList;

import java.util.ArrayList;

public class ContactWalksAdapter extends BaseExpandableListAdapter {

	public ContactWalksAdapter(Context ctx, ArrayList<ContactDogList> items) {
		super();
		mContext = ctx;
		mWalkGroups = items;
		mInflater = LayoutInflater.from(ctx);
		// mApplication = (SwiftoApplication)((Activity)
		// mContext).getApplication();

		initHeights();
	}

	private void initHeights() {
		one = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105,
				mContext.getResources().getDisplayMetrics());
		two = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210,
				mContext.getResources().getDisplayMetrics());
		three = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				315, mContext.getResources().getDisplayMetrics());
		four = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				420, mContext.getResources().getDisplayMetrics());
	}

	private Context mContext;
	private ArrayList<ContactDogList> mWalkGroups;
	private LayoutInflater mInflater;

	// private SwiftoApplication mApplication;

	public void clear() {
		mWalkGroups.clear();
	}

	public void addGroup(ContactDogList walksOneDay) {
		mWalkGroups.add(walksOneDay);
		notifyDataSetChanged();
	}

	public Object getChild(int groupPosition, int childPosition) {
		return mWalkGroups.get(groupPosition).getContactDog()
				.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	private static class ChildViewHolder {

		TextView tvAddressPhone;
		Button btCall;
		Button btSms;

	}

	private ContactDog mCurrentWalk;
	private GeoPoint mCurrentOwnerLocation;

	// private ArrayList<Walk> mCurrentListWalk;

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		ChildViewHolder holder;

		final int chPos = childPosition;
		final int grPos = groupPosition;

		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.contact_list_item_walk_info, null);

			holder = new ChildViewHolder();
			holder.btCall = (Button) convertView
					.findViewById(R.id.contact_list_item_walk_info_bt_call);
			holder.btSms = (Button) convertView
					.findViewById(R.id.contact_list_item_walk_info_bt_sms);

			holder.tvAddressPhone = (TextView) convertView
					.findViewById(R.id.contact_list_item_walk_info_txt_address_phone);
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}

		mCurrentWalk = mWalkGroups.get(groupPosition).getContactDog()
				.get(childPosition);

		holder.tvAddressPhone.setVisibility(View.VISIBLE);
		holder.tvAddressPhone.setPadding(0, 0, 0, 0);
		holder.tvAddressPhone.setText(Html.fromHtml(mCurrentWalk
				.getAddressAndPhoneForContactDog()));

		if (mCurrentWalk.isStatic) {
			holder.btCall.setBackgroundResource(R.drawable.mail_icon);
		}
		
		else {
			holder.btCall.setBackgroundResource(R.drawable.bt_call);
		}

		holder.btCall.setVisibility(View.VISIBLE);
		holder.btCall.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ContactDog selectedWalk = mWalkGroups.get(grPos)
						.getContactDog().get(chPos);
				if (selectedWalk.isStatic) {
					sendEmailLog(selectedWalk.email);

				} else {
					Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + selectedWalk.PhonePrimary));
					mContext.startActivity(callIntent);
				}

			}
		});

		holder.btSms.setVisibility(View.VISIBLE);

		holder.btSms.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				ContactDog selectedWalk = mWalkGroups.get(grPos)
						.getContactDog().get(chPos);

				Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri
						.parse("smsto:" + selectedWalk.PhonePrimary));
				mContext.startActivity(smsIntent);
			}
		});

		/*convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				ContactDog selectedWalk = mWalkGroups.get(grPos)
						.getContactDog().get(chPos);
				Log.i("ContactWalksAdapter","chPos = "+ chPos +"  grPos = "+grPos);
				if (!selectedWalk.isStatic) {
					// mApplication.setCurrentWalk(selectedWalk);

					String walkId = selectedWalk._id;
					Log.i("ContactWalksList", "Selected walk: " + walkId);
					SharedPreferencesHelper.saveCurrentWalkId(mContext, walkId);

					Intent dogInfoIntent = new Intent(mContext
							.getApplicationContext(), DogInfoActivity.class);
					mContext.startActivity(dogInfoIntent);
				}

			}
		});*/

		return convertView;
	}

	private int cnt;

	private int one;// = 100;
	private int two;// = 200;
	private int three;// = 300;
	private int four;// = 400;

	public int getChildrenCount(int groupPosition) {
		return mWalkGroups.get(groupPosition).getContactDog().size();
	}

	private static class GroupViewHolder {
		TextView tvDayName;
		TextView tvDate;
	}

	private ContactDogList mWalksOneDay;

	public Object getGroup(int groupPosition) {
		return mWalkGroups.get(groupPosition);
	}

	public int getGroupCount() {
		return mWalkGroups.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	// private

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder groupViewHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_item_expandable_group, null);

			groupViewHolder = new GroupViewHolder();
			groupViewHolder.tvDate = (TextView) convertView
					.findViewById(R.id.list_item_expandable_group_txt_date);
			groupViewHolder.tvDayName = (TextView) convertView
					.findViewById(R.id.list_item_expandable_group_txt_day_name);

			convertView.setTag(groupViewHolder);
		} else {
			// Log.d("WeekAdapter", String.format("GROUP tag is %s",
			// convertView.getTag()));
			groupViewHolder = (GroupViewHolder) convertView.getTag();
		}

		mWalksOneDay = mWalkGroups.get(groupPosition);

		// groupViewHolder.tvDate.setText(mWalksOneDay.getmDayName());
		groupViewHolder.tvDayName.setText(mWalksOneDay.getmDayName());

		groupViewHolder.tvDate.setVisibility(View.GONE);
		// groupViewHolder.tvDayName.setVisibility(View.GONE);

		ExpandableListView elv = (ExpandableListView) parent;
		elv.expandGroup(groupPosition);

		convertView.setOnClickListener(groupClickListener);

		return convertView;
	}

	OnClickListener groupClickListener = new OnClickListener() {
		public void onClick(View v) {
		}
	};

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public void sendEmailLog(String email) {
		Intent intentEmail = new Intent(Intent.ACTION_SEND);
		intentEmail.setType("text/message");
		intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
		mContext.startActivity(intentEmail);
	}
}
