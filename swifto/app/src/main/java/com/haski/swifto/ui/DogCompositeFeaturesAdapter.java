package com.haski.swifto.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haski.swifto.R;
import com.haski.swifto.model.vo.dog.DogCompositeFeature;

public class DogCompositeFeaturesAdapter extends ArrayAdapter<DogCompositeFeature> {

	public DogCompositeFeaturesAdapter(Context context, int textViewResourceId, ArrayList<DogCompositeFeature> objects) {
		super(context, textViewResourceId, objects);
		
		mInflater = LayoutInflater.from(context);
		mCompositeFeatures = objects;
		
		mDrawableYes = context.getResources().getDrawable(R.drawable.feature_yes);
		mDrawableNo = context.getResources().getDrawable(R.drawable.feature_no);
	}

	private ArrayList<DogCompositeFeature> mCompositeFeatures;
	private DogCompositeFeature mCurrentFeature;
	
	private LayoutInflater mInflater;
	private final Drawable mDrawableYes;
	private final Drawable mDrawableNo;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_dog_feature, null);
		}
		
		mCurrentFeature = mCompositeFeatures.get(position);
		
		((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(mCurrentFeature.getYes() ? mDrawableYes : mDrawableNo, null, null, null);
		
		((TextView) convertView).setText(mCurrentFeature.getDescription());
		
		return convertView;
	}
}
