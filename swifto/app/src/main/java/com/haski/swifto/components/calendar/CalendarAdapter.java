package com.haski.swifto.components.calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haski.swifto.R;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends BaseAdapter {
	static final int FIRST_DAY_OF_WEEK = 0;
	private static final String TWO_ZEROS = "00";
	private Context context;

	private java.util.Calendar month;
	private Calendar todaysDate;

	private int mTodaysPosition;

	/**
	 * Contains string representations of days' numbers, that is displayed<br>
	 * Displayed numbers:
	 * <ul>
	 * <li>Last days of previous month</li>
	 * <li>All days of current month</li>
	 * <li>First days of next month</li>
	 * </ul>
	 * <table>
	 * <tr>
	 * <td>29</td>
	 * <td>30</td>
	 * <td>31</td>
	 * <td><b>1</b></td>
	 * <td><b>2</b></td>
	 * <td><b>3</b></td>
	 * <td><b>4</b></td>
	 * </tr>
	 * <tr>
	 * <td><b>5</b></td>
	 * <td><b>6</b></td>
	 * <td><b>7</b></td>
	 * <td><b>8</b></td>
	 * <td><b>9</b></td>
	 * <td><b>10</b></td>
	 * <td><b>11</b></td>
	 * </tr>
	 * <tr>
	 * <td><b>12</b></td>
	 * <td><b>13</b></td>
	 * <td><b>14</b></td>
	 * <td><b>15</b></td>
	 * <td><b>16</b></td>
	 * <td><b>17</b></td>
	 * <td><b>18</b></td>
	 * </tr>
	 * <tr>
	 * <td><b>19</b></td>
	 * <td><b>20</b></td>
	 * <td><b>21</b></td>
	 * <td><b>22</b></td>
	 * <td><b>23</b></td>
	 * <td><b>24</b></td>
	 * <td><b>25</b></td>
	 * </tr>
	 * <tr>
	 * <td><b>26</b></td>
	 * <td><b>27</b></td>
	 * <td><b>28</b></td>
	 * <td><b>29</b></td>
	 * <td><b>30</b></td>
	 * <td>1</td>
	 * <td>2</td>
	 * </tr>
	 * </table>
	 * 
	 */
	public String[] days;

	/**
	 * Contains string representations of numbers of walks for each day, that is
	 * displayed<br>
	 * Displayed numbers:
	 * <ul>
	 * <li>For previous month days - value is always "00"</li>
	 * <li>For current month days:
	 * <ul>
	 * <li>if day has no walks - value will be "00"</li>
	 * <li>if day has walks - value will be string representation of number of
	 * walks ("1", "2", "10", ...)</li>
	 * </ul>
	 * </li>
	 * <li>For next month days - value is always "00"</li>
	 * </ul>
	 */
	private ArrayList<String> numbersOfWalksForDays;

	public int getCount() {
		return days.length;
	}

	private ArrayList<Integer> prevMonthIndex = new ArrayList<Integer>();
	private ArrayList<Integer> nextMontIndex = new ArrayList<Integer>();

	public CalendarAdapter(Context context, Calendar monthCalendar) {
		this.context = context;
		month = monthCalendar;
		todaysDate = Calendar.getInstance();
		month.set(Calendar.DAY_OF_MONTH, 1);
		numbersOfWalksForDays = new ArrayList<String>();
		refreshDays();
	}

	public ArrayList<Integer> getPrevMonthIndexList() {
		return prevMonthIndex;
	}

	public ArrayList<Integer> getNextMontIndexList() {
		return nextMontIndex;
	}

	public int getTodaysPosition() {
		return mTodaysPosition;
	}

	private View mCurrentSelectedView;

	public View getCurrentSelectedView() {
		return mCurrentSelectedView;
	}

	public void setCurrentSelectedView(View value) {
		mCurrentSelectedView = value;
	}

	public int getNumberOfDayAtPosition(int position) {
		return Integer.valueOf(days[position]);
	}

	public int getStartPossitionOfMonth() {

		for (int i = 0; i < days.length; i++) {
			if (days[i].equals("1")) {
				return i;
			}
			
		}
		return 0;
	}

	/**
	 * Returns number of walks for day, that is selected. Returns "-1", if the
	 * day contains no walks
	 */
	public int getNumberOfWalksForDay(int position) {

		if (numbersOfWalksForDays.size() <= position) {
			return -1;
		}

		String item = numbersOfWalksForDays.get(position);
		if (item.equals(TWO_ZEROS)) {
			return -1;
		}

		return Integer.parseInt(item);
	}

	public int getSelectedYear() {
		return month.get(Calendar.YEAR);
	}

	public Calendar getCalendar() {
		return month;
	}

	public void setItems(ArrayList<String> items) {
		generateNumbersOfWalksForDays(items);
	}

	private boolean mTodayIsInCurrentMonth = false;

	public boolean getTodayIsInCurrentMonth() {
		return mTodayIsInCurrentMonth;
	}


	private void generateNumbersOfWalksForDays(ArrayList<String> source) {

		boolean equalYear = month.get(Calendar.YEAR) == todaysDate
				.get(Calendar.YEAR);
		boolean equalMonth = month.get(Calendar.MONTH) == todaysDate
				.get(Calendar.MONTH);
		String todayStr = "" + todaysDate.get(Calendar.DAY_OF_MONTH);
		numbersOfWalksForDays = new ArrayList<String>();
		int count = -1;
		String item;
		int numberOfDays = source.size();
		int lengthDays = days.length;
		boolean todayIsInCurrentMonthSet = false;

		for (int i = 0; i < lengthDays; i++) {
			if (days[i].equals("1") && count == -1) {
				count = 0;
			}

			if (count == -1) {
				numbersOfWalksForDays.add(TWO_ZEROS);
			} else {
				if (numberOfDays == 0) {
					item = TWO_ZEROS;
				} else if (count < numberOfDays) {
					item = source.get(count);

					if (item.equals("0")) {
						item = TWO_ZEROS;
					}
				} else {
					item = TWO_ZEROS;
				}

				if (equalMonth && equalYear && days[i].equals(todayStr)
						&& !todayIsInCurrentMonthSet) {
					mTodaysPosition = i;
					mTodayIsInCurrentMonth = true;
					todayIsInCurrentMonthSet = true;
				}

				numbersOfWalksForDays.add(item);
				count++;
			}
		}
		
		mTodayIsInCurrentMonth = todayIsInCurrentMonthSet;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	private int mSelectedPositionInGrid = -1;

	public void setSelectedPositionInGrid(int value) {
		mSelectedPositionInGrid = value;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		TextView dayView;
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = vi.inflate(R.layout.calendar_item, null);
		view.setBackgroundColor(0xffffff);
		dayView = (TextView) view.findViewById(R.id.txt_day_number);

		if (days[position].equals("")) {
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else {
			// selected cells
			if (position == mSelectedPositionInGrid) {
				if (mTodayIsInCurrentMonth
						&& mTodaysPosition == mSelectedPositionInGrid) {
					markSelectedCurrentDay(position, dayView, view);
				} else {
					markSelectedDay(position, dayView, view);
				}

				mCurrentSelectedView = view;
			}
			// non-selected cells
			else {
				if (mTodayIsInCurrentMonth && mTodaysPosition == position) {
					markCurrentDay(view, position);
				} else {
					markSimpleDay(view, position);
				}
			}
		}

		dayView.setText(days[position]);
		markDaysFromAnotherWeek(position, dayView, view);

		if (numbersOfWalksForDays.size() > 0) {
			String number = numbersOfWalksForDays.get(position);
			ImageView iw = (ImageView) view
					.findViewById(R.id.img_circle_walks_number_bg);
			TextView tv = (TextView) view.findViewById(R.id.txt_walks_number);

			// iw.setVisibility(View.INVISIBLE);
			// tv.setVisibility(View.INVISIBLE);

			if (number.equals(TWO_ZEROS)) {
				iw.setVisibility(View.INVISIBLE);
				tv.setVisibility(View.INVISIBLE);
			} else {
				iw.setVisibility(View.VISIBLE);
				tv.setVisibility(View.VISIBLE);
				tv.setText(number);
			}
		}

		return view;
	}

	private void markCurrentDay(View view, int position) {
		view.setBackgroundResource(R.drawable.cell_bg_today);
		((ImageView) view.findViewById(R.id.img_circle_walks_number_bg))
				.setImageResource(R.drawable.calendar_circle_white);
		TextView dateView = ((TextView) view
				.findViewById(R.id.txt_walks_number));
		dateView.setTextColor(Color.BLACK);
	}

	private void markSimpleDay(View view, int position) {
		view.setBackgroundResource(R.drawable.list_item_background_s);
		((ImageView) view.findViewById(R.id.img_circle_walks_number_bg))
				.setImageResource(R.drawable.calendar_circle_blue);
		((TextView) view.findViewById(R.id.txt_walks_number))
				.setTextColor(Color.WHITE);
	}

	private void markDaysFromAnotherWeek(int position, TextView dayView,
			View vii) {
		if ((prevMonthIndex.contains(Integer.valueOf(position)))
				|| (nextMontIndex.contains(Integer.valueOf(position)))) {
			dayView.setTextColor(Color.GRAY);
			vii.setBackgroundResource(R.drawable.list_item_background_s);
		}
	}

	private void markSelectedDay(int position, TextView dayView, View vii) {
		dayView.setTextColor(Color.WHITE);
		vii.setBackgroundResource(R.drawable.cell_bg_selected);
	}

	private void markSelectedCurrentDay(int position, TextView dayView, View vii) {
		dayView.setTextColor(Color.GRAY);
		vii.setBackgroundResource(R.drawable.cell_bg_today);
		((ImageView) vii.findViewById(R.id.img_circle_walks_number_bg))
				.setImageResource(R.drawable.calendar_circle_white);
		((TextView) vii.findViewById(R.id.txt_walks_number))
				.setTextColor(Color.BLACK);
		dayView.setTextColor(Color.WHITE);
	}

	public void refreshDays() {
		numbersOfWalksForDays.clear();
		prevMonthIndex.clear();
		nextMontIndex.clear();

		int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstDay = month.get(Calendar.DAY_OF_WEEK);

		// 1 - is Sunday, it must be last in row
		if (firstDay == 1) {
			firstDay = 7;
		} else {
			firstDay--;
		}

		String[] mock;

		final int totalNumber;

		if (firstDay == 1) {
			totalNumber = lastDay + (FIRST_DAY_OF_WEEK * 6);
		} else {

			totalNumber = lastDay + firstDay - (FIRST_DAY_OF_WEEK + 1);
		}

		mock = new String[totalNumber];

		int j = FIRST_DAY_OF_WEEK;

		if (firstDay > 1) {
			for (j = 0; j < firstDay - FIRST_DAY_OF_WEEK; j++) {
				Calendar calen = (Calendar) month.clone();

				if (month.get(Calendar.MONTH) == month
						.getActualMinimum(Calendar.MONTH)) {
					calen.set((month.get(Calendar.YEAR) - 1),
							month.getActualMaximum(Calendar.MONTH), 1);
				} else {
					calen.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
				}

				int maxDayOfMonth = calen
						.getActualMaximum(Calendar.DAY_OF_MONTH);

				mock[j] = "" + (maxDayOfMonth - (firstDay - 2 - j));
				prevMonthIndex.add(j - 1);
			}

			prevMonthIndex.remove(0);
		} else {
			for (j = 0; j < FIRST_DAY_OF_WEEK * 6; j++) {
				mock[j] = "";
			}

			j = FIRST_DAY_OF_WEEK * 6 + 1;
		}

		int dayNumber = 1;

		for (int i = j - 1; i < mock.length; i++) {
			mock[i] = "" + dayNumber;
			dayNumber++;
		}

		int numDaysInLastRow = mock.length % 7;

		if (numDaysInLastRow == 0) {
			days = new String[mock.length];
		} else {
			int numDaysToAdd = 7 - numDaysInLastRow;
			days = new String[mock.length + numDaysToAdd];
		}

		System.arraycopy(mock, 0, days, 0, mock.length);

		int v = 1;

		for (int i = mock.length; i < days.length; i++) {
			days[i] = String.valueOf(v++);
			nextMontIndex.add(i);
		}

		mRowNumber = days.length / 7 + days.length % 7;
	}

	private int mRowNumber = 0;

	public int getRowNumber() {
		return mRowNumber;
	}
}