package com.haski.swifto.components.calendar;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haski.swifto.R;

public class CalendarView extends LinearLayout implements OnClickListener,
		OnItemClickListener {
	
	private final int ROW_HEIGHT_IN_PX = 35;
	private final int HEADER_HEIGHT_IN_PX = 105;
	private final int ROW_HEIGHT;
	private final int HEADER_HEIGHT;
	

	private Context context;
	private OnItemClickListener calendarCellListener;
	private GenerateWalkCountCallback callback;
	
	private Calendar month;
	private CalendarAdapter adapter;
	private Handler getDayWalkCountHandler;
	
	private int mLastSelectedIndex = -1;
	private int mPositionOfToday = -1;
	private boolean mTodayIsInCurrentMonth = false;

	private int itemPosition;
	
	public CalendarView(Context context) {
		super(context);
		this.context = context;
		
		HEADER_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEADER_HEIGHT_IN_PX, context.getResources().getDisplayMetrics());
		ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_HEIGHT_IN_PX, context.getResources().getDisplayMetrics());
	}
	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		HEADER_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEADER_HEIGHT_IN_PX, context.getResources().getDisplayMetrics());
		ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_HEIGHT_IN_PX, context.getResources().getDisplayMetrics());
	}
//	public CalendarView(Context context, AttributeSet attrs, int a)
//	{
//		super(context, attrs, a);
//		this.context = context;
//		
//		HEADER_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEADER_HEIGHT_IN_PX, ((Activity)context).getResources().getDisplayMetrics());
//		ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_HEIGHT_IN_PX, ((Activity)context).getResources().getDisplayMetrics());
//	}

	public CalendarView(Context context, GenerateWalkCountCallback callback, OnItemClickListener calendarCellListener) {
		super(context);
		
		HEADER_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEADER_HEIGHT_IN_PX, context.getResources().getDisplayMetrics());
		ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_HEIGHT_IN_PX, context.getResources().getDisplayMetrics());

		this.context = context;
		this.callback = callback;
		this.calendarCellListener = calendarCellListener;

		inflate(context, R.layout.calendar, this);

		initCalendarComponents();
		initViews();
	}
	
	public void postConstruct(Context ctx, GenerateWalkCountCallback callback, OnItemClickListener calendarCellListener) {
		this.context = ctx;
		this.callback = callback;
		this.calendarCellListener = calendarCellListener;

		inflate(ctx, R.layout.calendar, this);

		initCalendarComponents();
		initViews();
	}

	public void postConstruct(Context ctx, Calendar defaultMonth, GenerateWalkCountCallback callback, OnItemClickListener calendarCellListener) {
		this.context = ctx;
		this.callback = callback;
		this.calendarCellListener = calendarCellListener;
		
		inflate(ctx, R.layout.calendar, this);
		
		initCalendarComponents(defaultMonth);
		initViews();
	}
	
	public int getNumberOfDayAtPosition(int position) {
		return adapter.getNumberOfDayAtPosition(position);
	}
	
	public int getStartPossitionOfMonth() {

		return adapter.getStartPossitionOfMonth();
	}

	
	/**
	 * Returns number of walks for day, that is selected.
	 * Returns "-1", if the day contains no walks
	 * */
	public int getNumberOfWalksForDay(int positon) {
		//return 80;
		return adapter.getNumberOfWalksForDay(positon);
	}

	public int getTodaysPosition()
	{
		return adapter.getTodaysPosition();
	}


	public boolean getTodayIsInCurrentMonth()
	{
		return adapter.getTodayIsInCurrentMonth();
	}

	
	public int getSelectedYear() {
		return adapter.getSelectedYear();
	}
	
	public Calendar getCalendar() {
		return adapter.getCalendar();
	}

	private void initCalendarComponents() {
		month = Calendar.getInstance();
	}
	private void initCalendarComponents(Calendar defaultMonth) {
		month = defaultMonth;
	}

	private ImageView previous;
	private ImageView next;
	
	private void initViews() {
		GridView gridview = (GridView) findViewById(R.id.gridview);
		/*gridview.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			public void onGlobalLayout() {
				gridview.getViewTreeObserver().removeGlobalOnLayoutListener( this );
				View lastChild = gridview.getChildAt( gridview.getChildCount() - 1 );
				gridview.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, lastChild.getBottom() ) );
			}
		});*/
		
		adapter = new CalendarAdapter(this.context, month);
		mRowNumber = adapter.getRowNumber();
		
		initHeight();
		gridview.setAdapter(adapter);
		getDayWalkCountHandler = new Handler();
		//getDayWalkCountHandler.post(getnerateCountCircules);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
		previous = (ImageView) findViewById(R.id.previous);
		previous.setOnClickListener(this);
		next = (ImageView) findViewById(R.id.next);
		next.setOnClickListener(this);
		gridview.setOnItemClickListener(this);
	}
	
	public int getSelectedMonthNumber() {
		return month.get(Calendar.MONTH);
	}

	public Runnable getnerateCountCircules = new Runnable() {
		public void run() {
			
			/*items = */callback.onMonthChanged(month.get(Calendar.MONTH));
			
			//adapter.setItems(items);
			//adapter.notifyDataSetChanged();
		}
	};
	
	/**
	 * 
	 * @param newItems - each item in this collection is a string with number of walks for day
	 * @param indexOfCellInCalendarToSelect - 
	 */
	public void refreshWithNewData(ArrayList<String> newItems, int indexOfCellInCalendarToSelect) {
		//our "selectedIndex" take in opinion also "previuosMonthDays" and "nextMonthDays",
		//so, we need to add "previousMonthDays" length to "indexOfDayInMonthToSelect" to be in correct state
		adapter.setItems(newItems);
		adapter.notifyDataSetChanged();
		previous.setEnabled(true);
		next.setEnabled(true);
		mPositionOfToday = adapter.getTodaysPosition();
		mTodayIsInCurrentMonth = adapter.getTodayIsInCurrentMonth();
		
		if(indexOfCellInCalendarToSelect != -1) {
			indexOfCellInCalendarToSelect += adapter.getPrevMonthIndexList().size();
			this.itemPosition = indexOfCellInCalendarToSelect;
			
			mLastSelectedIndex = indexOfCellInCalendarToSelect;

			adapter.setSelectedPositionInGrid(indexOfCellInCalendarToSelect);
		} else {
			mLastSelectedIndex = -1;
			adapter.setSelectedPositionInGrid(-1);
		}
	}
	

	public void onClick(View v) {
		int viewID = v.getId();
		switch (viewID) {
			case R.id.previous: {
				previous.setEnabled(false);
				next.setEnabled(false);
				getPreviousMonth();
				mLastSelectedIndex = -1;
				break;
			}
			case R.id.next: {
				previous.setEnabled(false);
				next.setEnabled(false);
				getNextMonth();
				mLastSelectedIndex = -1;
				break;
			}
		}
	}

	private void getPreviousMonth() {
		boolean previousMonthValide = validatePreviouseMonth();
		if (previousMonthValide) {
			month.set((month.get(Calendar.YEAR) - 1),month.getActualMaximum(Calendar.MONTH), 1);
		} else {
			month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
		}
		refreshCalendar();
	}

	private boolean validatePreviouseMonth() {
		return month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH);
	}

	private void getNextMonth() {
		boolean nextMonthValide = validateNextMonth();
		if (nextMonthValide) {
			month.set((month.get(Calendar.YEAR) + 1), month.getActualMinimum(Calendar.MONTH), 1);
		} else {
			month.set(Calendar.MONTH, month.get(Calendar.MONTH) + 1);
		}
		refreshCalendar();
	}
	
	public void setDateTo(int yearTo, int monthTo, int dayTo)
	{
		month.set(Calendar.YEAR, yearTo);
		month.set(Calendar.MONTH, monthTo);
		month.set(Calendar.DAY_OF_MONTH, dayTo);
		
		refreshCalendar();
	}
	
	private boolean validateNextMonth() {
		return month.get(Calendar.MONTH) == month.getActualMaximum(Calendar.MONTH);
	}
	
	private void refreshCalendar() {
		TextView title = (TextView) findViewById(R.id.title);
		adapter.refreshDays();
		mRowNumber = adapter.getRowNumber();
		initHeight();
		adapter.notifyDataSetChanged();
		getDayWalkCountHandler.post(getnerateCountCircules);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		this.itemPosition = position;
		this.adapter.setSelectedPositionInGrid(position);
		boolean monthChanged = false;
		
		if (mLastSelectedIndex != position) {
			monthChanged = changeSelectionAndGetIsMonthChanged(position, view);
		}
		
		if(!monthChanged) {
			mLastSelectedIndex = position;
			calendarCellListener.onItemClick(adapter, view, position, id);
		} else {
			mLastSelectedIndex = -1;
		}
	}
	
	/**
	 * Returns true if month changes*/
	private boolean changeSelectionAndGetIsMonthChanged(int newPosition, View newSelectedView) {
		ArrayList<Integer> previousMonthDaysIndex = this.adapter.getPrevMonthIndexList();
		ArrayList<Integer> nextMonthDaysIndex = this.adapter.getNextMontIndexList();

		boolean previousMonthSelected = previousMonthDaysIndex.contains(Integer.valueOf(itemPosition));
		boolean nextMonthSelected = nextMonthDaysIndex.contains(Integer.valueOf(itemPosition));

		if (previousMonthSelected) {
			getPreviousMonth();
		} else if (nextMonthSelected) {
			getNextMonth();
		}
		
		if(previousMonthSelected || nextMonthSelected) {
			mLastSelectedIndex = -1;
			return true;
		} else {
			//refreshing previously selected view
			View previouslySelectedView = this.adapter.getCurrentSelectedView();
			
			if(previouslySelectedView != null) {
				final ImageView oldImgCircleWalkNumber = ((ImageView) previouslySelectedView.findViewById(R.id.img_circle_walks_number_bg));
				final TextView oldTxtWalksNumber = ((TextView) previouslySelectedView.findViewById(R.id.txt_walks_number));
				final TextView oldTxtDayNumber = ((TextView) previouslySelectedView.findViewById(R.id.txt_day_number));
				
				if(mTodayIsInCurrentMonth && mLastSelectedIndex == mPositionOfToday)
				{
					todayNonSelected(previouslySelectedView, oldImgCircleWalkNumber, oldTxtDayNumber, oldTxtWalksNumber);
				} else {
					simpleNonSelected(previouslySelectedView, oldImgCircleWalkNumber, oldTxtDayNumber, oldTxtWalksNumber);
				}
			}
			
			//refreshing newly selected view
			final ImageView newImgCircleWalkNumber = ((ImageView) newSelectedView.findViewById(R.id.img_circle_walks_number_bg));
			final TextView newTxtWalksNumber = ((TextView) newSelectedView.findViewById(R.id.txt_walks_number));
			final TextView newTxtDayNumber = ((TextView) newSelectedView.findViewById(R.id.txt_day_number));
			
			if(mTodayIsInCurrentMonth && newPosition == mPositionOfToday) {
				todaySelected(newSelectedView, newImgCircleWalkNumber, newTxtDayNumber, newTxtWalksNumber);
			} else {
				simpleSelected(newSelectedView, newImgCircleWalkNumber, newTxtDayNumber, newTxtWalksNumber);
			}

			this.adapter.setCurrentSelectedView(newSelectedView);
			mLastSelectedIndex = newPosition;
			this.adapter.notifyDataSetChanged();
			
			return false;
		}
	}
	
	private static void todaySelected(View root, ImageView bgCircle, TextView txtDayNumber, TextView txtWalksNumber) {
		root.setBackgroundResource(R.drawable.cell_bg_today);
		bgCircle.setImageResource(R.drawable.calendar_circle_white);
		txtDayNumber.setTextColor(Color.WHITE);
		txtWalksNumber.setTextColor(Color.BLACK);
	}
	
	private static void todayNonSelected(View root, ImageView bgCircle, TextView txtDayNumber, TextView txtWalksNumber) {
		root.setBackgroundResource(R.drawable.cell_bg_today);
		bgCircle.setImageResource(R.drawable.calendar_circle_white);
		txtDayNumber.setTextColor(Color.BLACK);
		txtWalksNumber.setTextColor(Color.BLACK);
	}
	
	private static void simpleSelected(View root, ImageView bgCircle, TextView txtDayNumber, TextView txtWalksNumber) {
		root.setBackgroundResource(R.drawable.cell_bg_selected);
		bgCircle.setImageResource(R.drawable.calendar_circle_white);
		txtDayNumber.setTextColor(Color.WHITE);
		txtWalksNumber.setTextColor(Color.BLACK);
	}
	
	private static void simpleNonSelected(View root, ImageView bgCircle, TextView txtDayNumber, TextView txtWalksNumber) {
		root.setBackgroundResource(R.drawable.list_item_background_s);
		bgCircle.setImageResource(R.drawable.calendar_circle_blue);
		txtDayNumber.setTextColor(Color.BLACK);
		txtWalksNumber.setTextColor(Color.WHITE);
	}
	
	private int mRowNumber;
	
	private void initHeight() {
		final android.view.ViewGroup.LayoutParams params = getLayoutParams();

		if(params != null) {
			params.height = ROW_HEIGHT * mRowNumber + HEADER_HEIGHT;

			setLayoutParams(params);
		}
	}
}