package com.haski.swifto.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.haski.swifto.R;

public class ImagesAdapter extends ArrayAdapter<String> {

	public ImagesAdapter(Context context, int textViewResourceId,
			ArrayList<String> objects) {
		super(context, textViewResourceId, objects);
		
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mImagePaths = objects;
		
	}
	
	private ArrayList<String> mImagePaths;
	
	private Context mContext;
	private LayoutInflater mInflater;
	private String mCurrentImgPath;
	private AQuery listAq;
	
	private static class ViewHolder {
		ImageView imageView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_image, null);
			listAq = new AQuery(convertView);
			holder = new ViewHolder();
			
			holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_image_img);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		mCurrentImgPath = mImagePaths.get(position);

		//ImageOptions options = new ImageOptions();
		//options.round = 10;
		
		AQuery aq = listAq.recycle(convertView);
		
		//aq.id(holder.imageView).image(mCurrentImgPath, true, true, 0, 0, null, AQuery.FADE_IN).height(100, true);
//		aq.id(holder.imageView).image("http://stage.swifto.com/sites/default/files/dog-rain-coat_1.jpg", true, true).height(100, true);
		//aq.id(holder.imageView).image(mCurrentImgPath, true, true, 0, 0, null, 0).height(100, true);
		aq.id(holder.imageView).image(mCurrentImgPath, true, true, 0, R.drawable.dog_default_big).height(100);
		
		return convertView;
	}
}
