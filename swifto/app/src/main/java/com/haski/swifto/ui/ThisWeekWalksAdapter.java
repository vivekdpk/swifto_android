package com.haski.swifto.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
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
import com.haski.swifto.SwiftoApplication;
import com.haski.swifto.components.ListViewExt;
import com.haski.swifto.model.vo.WalksOneDay;
import com.haski.swifto.model.vo.walk.EnumWalkTypes;
import com.haski.swifto.model.vo.walk.Walk;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.SyslogUtils;
import com.haski.swifto.util.WalkUtils;
import com.haski.swifto.util.log.EnumLogSeverity;
import com.haski.swifto.util.log.EnumLogType;

import java.util.ArrayList;
import java.util.Locale;

public class ThisWeekWalksAdapter extends BaseExpandableListAdapter {

	public ThisWeekWalksAdapter(Context ctx, ArrayList<WalksOneDay> items ) {
		super();
		mContext = ctx;
		mWalkGroups = items;
		mInflater = LayoutInflater.from(ctx);
		mApplication = (SwiftoApplication)((Activity) mContext).getApplication();
		
		initHeights();
	}
	
	private void initHeights() {
		one = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105, mContext.getResources().getDisplayMetrics());
		two = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210, mContext.getResources().getDisplayMetrics());
		three = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 315, mContext.getResources().getDisplayMetrics());
		four = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, mContext.getResources().getDisplayMetrics());
	}
	
	private Context mContext;
	private ArrayList<WalksOneDay> mWalkGroups;
	private LayoutInflater mInflater;
	private SwiftoApplication mApplication;
	
	public void clear() {
		mWalkGroups.clear();
	}
	
	public void addGroup(WalksOneDay walksOneDay) {
		mWalkGroups.add(walksOneDay);
		notifyDataSetChanged();
	}
	
	
	public Object getChild(int groupPosition, int childPosition) {
		return mWalkGroups.get(groupPosition).getmListWalks().get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	private static class ChildViewHolder {
		TextView tvTime;
		TextView tvDogNames;
		TextView tvDurationFee;
		TextView tvAddressPhone;
		
		TextView complTvTime;
		TextView complTvDogNames;
		TextView complTvDurationFee;

		ListViewExt listPhotos;

		Button btCall;
		Button btSms;
		Button btMap;
		Button btDropoff;
		TextView txtMeetGreet;
	}
	
	private Walk mCurrentWalk;
	private GeoPoint mCurrentOwnerLocation;
	public String addr= "";
	//private ArrayList<Walk> mCurrentListWalk;

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		
		ChildViewHolder holder;
		
		final int chPos = childPosition;
		final int grPos = groupPosition;

		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_walk_info, null);

			holder = new ChildViewHolder();
			holder.btCall = (Button) convertView.findViewById(R.id.list_item_walk_info_bt_call);
			holder.btMap = (Button) convertView.findViewById(R.id.list_item_walk_info_bt_map);
			holder.btSms = (Button) convertView.findViewById(R.id.list_item_walk_info_bt_sms);
			holder.btDropoff = (Button) convertView.findViewById(R.id.list_item_walk_info_bt_dropoff);
			holder.txtMeetGreet = (TextView) convertView.findViewById(R.id.list_item_walk_info_bt_meet_and_greet);

			holder.tvAddressPhone = (TextView) convertView.findViewById(R.id.list_item_walk_info_txt_address_phone);
			holder.tvDogNames = (TextView) convertView.findViewById(R.id.list_item_walk_info_txt_dog_names);
			holder.tvDurationFee = (TextView) convertView.findViewById(R.id.list_item_walk_info_txt_duration_fee);
			holder.tvTime = (TextView) convertView.findViewById(R.id.list_item_walk_info_txt_time);
			
			holder.listPhotos = (ListViewExt) convertView.findViewById(R.id.list_item_walk_info_list_photos);
			
			holder.complTvDogNames = (TextView) convertView.findViewById(R.id.list_item_walk_info_completed_txt_dog_names);
			holder.complTvDurationFee = (TextView) convertView.findViewById(R.id.list_item_walk_info_completed_txt_duration_fee);
			holder.complTvTime = (TextView) convertView.findViewById(R.id.list_item_walk_info_completed_txt_time);
			
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}




		mCurrentWalk = mWalkGroups.get(groupPosition).getmListWalks().get(childPosition);
		if(mApplication.getWalkGetter().getWalkById(mContext,mCurrentWalk._id )!=null) {
			mCurrentWalk = mApplication.getWalkGetter().getWalkById(mContext, mCurrentWalk._id);
		}
		mCurrentOwnerLocation = mCurrentWalk.Location;




		if(mCurrentWalk.Original!=null && mCurrentWalk.NotesOwner!=null)
		{
			//holder.btDropoff.setVisibility(View.VISIBLE);
			addr = mCurrentWalk.Owner.getAddressAndPhone();
			if(Build.VERSION.SDK_INT > 20 ){
				// Do some stuff
				SpannableStringBuilder builder = new SpannableStringBuilder();
				builder.append(addr + mCurrentWalk.getAddressAndPhone()+" ")
						.append(" ", new ImageSpan(mContext, R.drawable.drop_off), 0);

				holder.tvAddressPhone.setText(builder);
			}

			else{
				SpannableStringBuilder builder = new SpannableStringBuilder();
				builder.append(addr + mCurrentWalk.getAddressAndPhone()+" ").append(" ");
				builder.setSpan(new ImageSpan(mContext, R.drawable.drop_off),
						builder.length() - 1, builder.length(), 0);


				holder.tvAddressPhone.setText(builder);
			}
		}
		else
		{
			addr = mCurrentWalk.Owner.getAddressAndPhone();
			holder.btDropoff.setVisibility(View.INVISIBLE);
			holder.tvAddressPhone.setText(addr + mCurrentWalk.getAddressAndPhone());
		}


		if(mCurrentWalk.Status.equals(WalkUtils.STATUS_COMPLETED)) {
			holder.complTvTime.setVisibility(View.VISIBLE);
			holder.complTvTime.setBackgroundResource(R.color.white);
			holder.complTvTime.setTextColor(mContext.getResources().getColor(R.color.list_walks_time));
			if (mCurrentWalk.WalkType.equals("overnight")) {
				holder.complTvTime.setText("Overnight stay");
				holder.complTvDurationFee.setVisibility(View.GONE);
			}
			else {
				holder.complTvTime.setText(mCurrentWalk.getStartTimeFormatted());
				holder.complTvDurationFee.setVisibility(View.VISIBLE);
			}
			
			holder.complTvDogNames.setVisibility(View.VISIBLE);
			holder.complTvDogNames.setText(mCurrentWalk.getDogNames().toUpperCase(Locale.getDefault()));
			holder.complTvDogNames.setPaintFlags(holder.complTvDogNames.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//			holder.complTvDogNames.setText(Html.fromHtml(String.format(mContext.getResources().getString(R.string.list_item_walk_info_striked), mCurrentWalk.getDogNames().toUpperCase())));
			
			//holder.complTvDurationFee.setVisibility(View.VISIBLE);
			holder.complTvDurationFee.setText(mCurrentWalk.getDurationPrice());
			
			holder.tvAddressPhone.setVisibility(View.VISIBLE);
			holder.tvAddressPhone.setPadding(115, 0, 0, 0);
			//holder.tvAddressPhone.setText(mCurrentWalk.Owner.getAddressAndPhone());
			addr = mCurrentWalk.Owner.getAddressAndPhone();
			holder.tvAddressPhone.setText(addr+mCurrentWalk.getAddressAndPhone());

			//mCurrentWalk.getA

			
			holder.btCall.setVisibility(View.INVISIBLE);
			holder.btSms.setVisibility(View.INVISIBLE);
			holder.btMap.setVisibility(View.INVISIBLE);
			holder.btDropoff.setVisibility(View.INVISIBLE);
			holder.listPhotos.setVisibility(View.GONE);
			//holder.tvAddressPhone.setVisibility(View.GONE);
			holder.tvTime.setVisibility(View.INVISIBLE);
			holder.txtMeetGreet.setVisibility(View.GONE);
			holder.tvDogNames.setVisibility(View.GONE);
			holder.tvDurationFee.setVisibility(View.GONE);
		} else {
			holder.complTvDogNames.setVisibility(View.GONE);
			holder.complTvDurationFee.setVisibility(View.GONE);
			holder.complTvTime.setVisibility(View.GONE);
			
			holder.tvTime.setVisibility(View.VISIBLE);
			
			if(mCurrentWalk.Status.equals(WalkUtils.STATUS_SCHEDULED)) {
				holder.tvTime.setBackgroundResource(R.color.white);
				holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.list_walks_time));
				
				if (mCurrentWalk.getTimeDiff() > 14) {
					holder.tvDurationFee.setTextColor(Color.RED);
				} else {
					holder.tvDurationFee.setTextColor(Color
							.parseColor("#014F85"));
				}
				
			} else if(mCurrentWalk.Status.equals(WalkUtils.STATUS_STARTED)) {
				holder.tvTime.setBackgroundResource(R.drawable.bg_shape_green);
				holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.list_walks_time_started));
			}

			holder.tvDogNames.setVisibility(View.VISIBLE);
			holder.tvDogNames.setText(mCurrentWalk.getDogNames().toUpperCase(Locale.getDefault()));
			
			holder.tvDurationFee.setVisibility(View.VISIBLE);
			holder.tvDurationFee.setText(mCurrentWalk.getDurationPrice());
			
			holder.tvAddressPhone.setVisibility(View.VISIBLE);
			holder.tvAddressPhone.setPadding(0, 0, 0, 0);

			addr = mCurrentWalk.Owner.getAddressAndPhone();
			holder.tvAddressPhone.setText(addr+mCurrentWalk.getAddressAndPhone());

			if(mCurrentWalk.Original!=null && mCurrentWalk.NotesOwner!=null)
			{
				//holder.btDropoff.setVisibility(View.VISIBLE);
				addr = mCurrentWalk.Owner.getAddressAndPhone();
				if(Build.VERSION.SDK_INT > 20 ){
					// Do some stuff
					SpannableStringBuilder builder = new SpannableStringBuilder();
					builder.append(addr + mCurrentWalk.getAddressAndPhone()+" ")
							.append(" ", new ImageSpan(mContext, R.drawable.drop_off), 0);

					holder.tvAddressPhone.setText(builder);
				}

				else{
					SpannableStringBuilder builder = new SpannableStringBuilder();
					builder.append(addr + mCurrentWalk.getAddressAndPhone()+" ").append(" ");
					builder.setSpan(new ImageSpan(mContext, R.drawable.drop_off),
							builder.length() - 1, builder.length(), 0);


					holder.tvAddressPhone.setText(builder);
				}
			}
			else
			{
				addr = mCurrentWalk.Owner.getAddressAndPhone();
				holder.btDropoff.setVisibility(View.INVISIBLE);
				holder.tvAddressPhone.setText(addr + mCurrentWalk.getAddressAndPhone());
			}

			holder.btCall.setVisibility(View.VISIBLE);
			holder.btCall.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Walk selectedWalk = mWalkGroups.get(grPos).getmListWalks().get(chPos);
					Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + selectedWalk.Owner.PhonePrimary));
					mContext.startActivity(callIntent);
				}
			});

			holder.btMap.setVisibility(mCurrentOwnerLocation == null ? View.GONE : View.VISIBLE);

			holder.btMap.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Walk selectedWalk =  mWalkGroups.get(grPos).getmListWalks().get(chPos);
					
					//mApplication.setCurrentWalk(selectedWalk);
					String walkId = selectedWalk._id;
					SharedPreferencesHelper.saveCurrentWalkId(mContext, walkId);

					Intent ownerMapIntent = new Intent(mContext, OwnerMapActivity.class);
					mContext.startActivity(ownerMapIntent);
				}
			});

			holder.btSms.setVisibility(View.VISIBLE);
			holder.btSms.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					
					Walk selectedWalk =  mWalkGroups.get(grPos).getmListWalks().get(chPos);
					
					Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + selectedWalk.Owner.PhonePrimary));
					mContext.startActivity(smsIntent);
				}
			});

			if(!mCurrentWalk.WalkType.equals(EnumWalkTypes.FREE)) {
				holder.txtMeetGreet.setVisibility(View.GONE);
			} else {
				holder.txtMeetGreet.setVisibility(View.VISIBLE);
			}

			ImagesAdapter adapter = new ImagesAdapter(mContext, R.layout.list_item_image, mCurrentWalk.getDogImagesPaths());

			holder.listPhotos.setVisibility(View.VISIBLE);
			holder.listPhotos.setAdapter(adapter);
			
			cnt = adapter.getCount();
			
			if(cnt == 1) {
				holder.listPhotos.getLayoutParams().height = one;
			} else if(cnt == 2) {
				holder.listPhotos.getLayoutParams().height = two;
			} else if(cnt == 3) {
				holder.listPhotos.getLayoutParams().height = three;
			} else if(cnt == 4) {
				holder.listPhotos.getLayoutParams().height = four;
			}

			if (mCurrentWalk.WalkType.equals("overnight")) {
				holder.tvTime.setText("Overnight stay");
				holder.tvDurationFee.setVisibility(View.GONE);
			}

			else {
				holder.tvTime.setText(mCurrentWalk.getStartTimeFormatted());
				holder.tvDurationFee.setVisibility(View.VISIBLE);
			}
		}
		
		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Walk selectedWalk =  mWalkGroups.get(grPos).getmListWalks().get(chPos);
				//mApplication.setCurrentWalk(selectedWalk);
				Log.i("WeekWalksList","chPos = "+ chPos +"  grPos = "+grPos);
				String walkId = selectedWalk._id;
				Log.i("WeekWalksList", "Selected walk: " + walkId);
				SharedPreferencesHelper.saveCurrentWalkId(mContext, walkId);
				
				Intent dogInfoIntent = new Intent(mContext.getApplicationContext(), DogInfoActivity.class);
				mContext.startActivity(dogInfoIntent);
			}
		});
				
		return convertView;
	}
	
	private int cnt;
	
	private int one;// = 100;
	private int two;// = 200;
	private int three;// = 300;
	private int four;// = 400;

	public int getChildrenCount(int groupPosition) {
		return mWalkGroups.get(groupPosition).getmListWalks().size();
	}
	
	private static class GroupViewHolder {
		TextView tvDayName;
		TextView tvDate;
	}

	private WalksOneDay mWalksOneDay;
	
	public Object getGroup(int groupPosition) {
		return mWalkGroups.get(groupPosition);
	}

	public int getGroupCount() {
		return mWalkGroups.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	//private 
	
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupViewHolder groupViewHolder;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_expandable_group, null);
			
			groupViewHolder = new GroupViewHolder();
			groupViewHolder.tvDate = (TextView) convertView.findViewById(R.id.list_item_expandable_group_txt_date);
			groupViewHolder.tvDayName = (TextView) convertView.findViewById(R.id.list_item_expandable_group_txt_day_name);
			
			convertView.setTag(groupViewHolder);
		} else {
			//Log.d("WeekAdapter", String.format("GROUP tag is %s", convertView.getTag()));
			groupViewHolder = (GroupViewHolder) convertView.getTag();
		}
		
		mWalksOneDay = mWalkGroups.get(groupPosition);
		
		groupViewHolder.tvDate.setText(mWalksOneDay.getDate());
		groupViewHolder.tvDayName.setText(mWalksOneDay.getDayName());
		
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
}
