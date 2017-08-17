package com.haski.swifto.ui;

import java.util.ArrayList;

import com.haski.swifto.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ShareImageAdapter extends BaseAdapter {

	private Context context;
	ArrayList<String> imageList = new ArrayList<String>();
	private static LayoutInflater inflater = null;

	public ShareImageAdapter(Context c) {
		context = c;
		inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	void add(String path) {
		imageList.add(path);
	}

	@Override
	public int getCount() {
		return imageList.size();
	}

	@Override
	public String getItem(int arg0) {
		// TODO Auto-generated method stub
		return imageList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;

		Holder holder = new Holder();
		View rowView;

		rowView = inflater.inflate(R.layout.grid_item, null);
		holder.img = (ImageView) rowView.findViewById(R.id.imageView1);

		BitmapFactory.Options options = new BitmapFactory.Options();

		// down sizing image as it throws OutOfMemory Exception for
		// larger
		// images
		options.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeFile(imageList.get(position), options);
		holder.img.setImageBitmap(bitmap);
		return rowView;
	}

	public class Holder {
		ImageView img;
	}

	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

		Bitmap bm = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	public int calculateInSampleSize(

	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

}