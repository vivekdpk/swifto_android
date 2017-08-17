package com.haski.swifto.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.haski.swifto.R;
import com.haski.swifto.model.vo.WalksOneDay;

public class WalksOneDayListAdapter extends ArrayAdapter<WalksOneDay> {

	public WalksOneDayListAdapter(Context context, int textViewResourceId,
			ArrayList<WalksOneDay> objects) {
		super(context, textViewResourceId, objects);
		
		mWalkGroups = objects;
	}
	
	//private Context mContext;
	private LayoutInflater mInflater;
	
	private ArrayList<WalksOneDay> mWalkGroups;
	private WalksOneDay mCurrentWalks;
	
	static class ViewHolder {
		TextView tvDayName;
		TextView tvDate;
		ListView listWalks;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_walks_one_day, null);
			
			holder = new ViewHolder();
			
			holder.tvDayName = (TextView) convertView.findViewById(R.id.list_item_walks_one_day_txt_day_of_week);
			holder.tvDate = (TextView) convertView.findViewById(R.id.list_item_walks_one_day_txt_date);
			holder.listWalks = (ListView) convertView.findViewById(R.id.list_item_walks_one_day_list_walks);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		mCurrentWalks = mWalkGroups.get(position);
		
		holder.tvDate.setText(mCurrentWalks.getDate());
		holder.tvDayName.setText(mCurrentWalks.getDayName());
		
		return convertView;
	}
}
