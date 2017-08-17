package com.haski.swifto.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haski.swifto.R;
import com.haski.swifto.model.vo.FeedMedicine;
import com.haski.swifto.util.ToastUtils;

public class FeedMedicineListAdapter extends BaseAdapter {

	public FeedMedicineListAdapter(Context ctx,
			ArrayList<FeedMedicine> feedMedicineList) {
		super();

		mContext = ctx;
		mFeedMedicineList = feedMedicineList;
		mInflater = LayoutInflater.from(mContext);
	}

	private Context mContext;
	private ArrayList<FeedMedicine> mFeedMedicineList;
	private LayoutInflater mInflater;

	/*
	 * Overridden
	 */
	public int getCount() {
		return mFeedMedicineList.size();
	}

	public Object getItem(int position) {
		return mFeedMedicineList;
	}

	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		TextView tvLebel;
		CheckBox check;
		RelativeLayout rowFeed;
	}

	private FeedMedicine mCurrentFeedMedicine;

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final int grPos = position;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_feed_medicine,
					null);

			holder = new ViewHolder();

			holder.tvLebel = (TextView) convertView.findViewById(R.id.label);
			holder.check = (CheckBox) convertView.findViewById(R.id.check);
			holder.rowFeed = (RelativeLayout) convertView
					.findViewById(R.id.row_feed_medicine);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		mCurrentFeedMedicine = mFeedMedicineList.get(position);
		if (mCurrentFeedMedicine.isCheck()) {
			holder.check.setSelected(true);
		} else {
			holder.check.setSelected(false);
		}

		holder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				//ToastUtils.showLong(mContext, "isChecked = "+isChecked);
				FeedMedicine mLocalFeedMedicine = mFeedMedicineList.get(grPos);
				mLocalFeedMedicine.setCheck(isChecked);
			}
		});
		holder.tvLebel.setText(mCurrentFeedMedicine.getMsg());

		holder.rowFeed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FeedMedicine mLocalFeedMedicine = mFeedMedicineList.get(grPos);
				if (mLocalFeedMedicine.isCheck()) {
					mLocalFeedMedicine.setCheck(false);
				} else {
					mLocalFeedMedicine.setCheck(true);
				}
				notifyDataSetChanged();
			}
		});

		return convertView;
	}
}
