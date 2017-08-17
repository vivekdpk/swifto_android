package com.haski.swifto.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class ListViewExt extends ListView {

	public ListViewExt(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	private static int LARGEST_HEIGHT = 7777;
	private int old_count = 0;
	private android.view.ViewGroup.LayoutParams params;
	
	
	public void setHeightToLargest() {
		params = getLayoutParams();
		
		if(params != null) {
			params.height = LARGEST_HEIGHT;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		params = getLayoutParams();
		
		if(getCount() != old_count || params.height == LARGEST_HEIGHT) {
			old_count = getCount();
			//params.height = getCount() * (old_count > 0 ? getChildAt(0).getHeight() : 0);
			
			int h = 0;
			
			for(int i = 0; i < getChildCount(); i++) {
				View v = getChildAt(i);
				h += v.getMeasuredHeight();
			}
			
			params.height = h;
			setLayoutParams(params);
		}
		super.onDraw(canvas);
	}
}
