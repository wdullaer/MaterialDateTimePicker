package com.wdullaer.materialdatetimepicker.date;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateChangedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DayPickerRecyclerView extends RecyclerView implements DayPickerViewInterface, OnDateChangedListener{

    private static final String TAG = "MonthFragment";

    protected static final int SCROLL_HYST_WEEKS = 2;  // Affects when the month selection will change while scrolling up
    protected static final int GOTO_SCROLL_DURATION = 250; // How long the GoTo fling animation should las
    protected static final int SCROLL_CHANGE_DELAY = 40; // How long to wait after receiving an onScrollStateChanged notification before acting on it
    public static final int DAYS_PER_WEEK = 7; // The number of days to display in each week
    public static int LIST_TOP_OFFSET = -1; // so that the top line will be under the separator

    // You can override these numbers to get a different appearance
    protected int mNumWeeks = 6;
    protected boolean mShowWeekNumber = false;
    protected int mDaysPerWeek = 7;
    private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
    protected float mFriction = 1.0f; // These affect the scroll speed and feel

    protected Context mContext;
    protected Handler mHandler;

    // highlighted time
    protected MonthAdapter.CalendarDay mSelectedDay = new MonthAdapter.CalendarDay();
    protected MonthRecyclerViewAdapter mAdapter;
    protected MonthAdapter.CalendarDay mTempDay = new MonthAdapter.CalendarDay();

    protected int mFirstDayOfWeek; // When the week starts; numbered like Time.<WEEKDAY> (e.g. SUNDAY=0).
    protected CharSequence mPrevMonthName; // The last name announced by accessibility
    protected int mCurrentMonthDisplayed; // which month should be displayed/highlighted [0-11]
    protected long mPreviousScrollPosition; // used for tracking during a scroll
    protected int mPreviousScrollState = RecyclerView.SCROLL_STATE_IDLE; // used for tracking what state listview is in
    protected int mCurrentScrollState = RecyclerView.SCROLL_STATE_IDLE; // used for tracking what state listview is in

    private DatePickerController mController;
    private boolean mPerformingScroll;
    protected int mScrollDirection;

    public DayPickerRecyclerView(Context context, DatePickerController controller, int scrollDirection) {
        super(context);
        mController = controller;
        mScrollDirection = scrollDirection;
        setUpRecyclerView();
    }

    public DayPickerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DayPickerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
         * Sets all the required fields for the recyclerview. Override this method to
         * set a different recyclerview behavior.
         */
    protected void setUpRecyclerView() {
        setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));

        setHasFixedSize(true);
        setVerticalScrollBarEnabled(false);
        setFadingEdgeLength(0);

        LinearLayoutManager layoutManager;
        if(mScrollDirection == LinearLayoutManager.VERTICAL || mScrollDirection == LinearLayoutManager.HORIZONTAL)
            layoutManager = new LinearLayoutManager(getContext(), mScrollDirection, false);
        else layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        setLayoutManager(layoutManager);

        refreshAdapter();
        onDateChanged();
    }

    /**
     * Creates a new adapter if necessary and sets up its parameters. Override
     * this method to provide a custom adapter.
     */
    protected void refreshAdapter() {
        if (mAdapter == null) {
            mAdapter = new MonthRecyclerViewAdapter(getContext(),mController);
        } else {
            mAdapter.setSelectedDay(mSelectedDay);
        }
        // refresh the view with the new parameters
        setAdapter(mAdapter);
    }

    /**
     * Gets the position of the view that is most prominently displayed within the list view.
     */
    public int getMostVisiblePosition() {
        final int firstPosition = ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition();
        final int height = getHeight();

        int maxDisplayedHeight = 0;
        int mostVisibleIndex = 0;
        int i=0;
        int bottom = 0;
        while (bottom < height) {
            View child = getChildAt(i);
            if (child == null) {
                break;
            }
            bottom = child.getBottom();
            int displayedHeight = Math.min(bottom, height) - Math.max(0, child.getTop());
            if (displayedHeight > maxDisplayedHeight) {
                mostVisibleIndex = i;
                maxDisplayedHeight = displayedHeight;
            }
            i++;
        }
        return firstPosition + mostVisibleIndex;
    }

    public void onChange(){
        refreshAdapter();
    }

    private static String getMonthAndYearString(MonthAdapter.CalendarDay day) {
        Calendar cal = Calendar.getInstance();
        cal.set(day.year, day.month, day.day);

        String sbuf = "";
        sbuf += cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        sbuf += " ";
        sbuf += YEAR_FORMAT.format(cal.getTime());
        return sbuf;
    }

    @Override
    public void onDateChanged(){
        goTo(mController.getSelectedDay(), false, true, true);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        //mPreviousScrollState = mCurrentScrollState;
    }

    @Override
    public void onScrollStateChanged(int state) {
        // use a post to prevent re-entering onScrollStateChanged before it exits
        mScrollStateChangedRunnable.doScrollStateChange(state);
    }

    /**
     * This moves to the specified time in the view. If the time is not already
     * in range it will move the list so that the first of the month containing
     * the time is at the top of the view. If the new time is already in view
     * the list will not be scrolled unless forceScroll is true. This time may
     * optionally be highlighted as selected as well.
     *
     * @param day The day to move to
     * @param animate Whether to scroll to the given time or just redraw at the
     *            new location
     * @param setSelected Whether to set the given time as selected
     * @param forceScroll Whether to recenter even if the time is already
     *            visible
     * @return Whether or not the view animated to the new location
     */
    public boolean goTo(MonthAdapter.CalendarDay day, boolean animate, boolean setSelected, boolean forceScroll) {

        // Set the selected day
        if (setSelected) {
            mSelectedDay.set(day);
        }

        mTempDay.set(day);
        int minMonth = mController.getStartDate().get(Calendar.MONTH);
        final int position = (day.year - mController.getMinYear())
                * MonthAdapter.MONTHS_IN_YEAR + day.month - minMonth;

        View child;
        int i = 0;
        int toporleft;
        // Find a child that's completely in the view
        do {
            child = getChildAt(i++);
            if (child == null) {
                break;
            }
            toporleft = mScrollDirection == LinearLayoutManager.VERTICAL ? child.getTop() : child.getLeft();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "child at " + (i - 1) + " has top " + toporleft);
            }
        } while (toporleft < 0);

        // Compute the first and last position visible
        int selectedPosition;
        if (child != null) {
            selectedPosition = getLayoutManager().getPosition(child);
        } else {
            selectedPosition = 0;
        }

        if (setSelected) {
            mAdapter.setSelectedDay(mSelectedDay);
        }

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "GoTo position " + position);
        }
        // Check if the selected day is now outside of our visible range
        // and if so scroll to the month that contains it
        if (position != selectedPosition || forceScroll) {
            setMonthDisplayed(mTempDay);
            mPreviousScrollState = RecyclerView.SCROLL_STATE_SETTLING;
            if (animate) {
                smoothScrollToPosition(position);
                return true;
            } else {
                postSetSelection(position);
            }
        } else if (setSelected) {
            setMonthDisplayed(mSelectedDay);
        }
        return false;
    }

    public void postSetSelection(final int position) {
        clearFocus();
        post(new Runnable() {

            @Override
            public void run() {
                scrollToPosition(position);
                onScrollStateChanged(RecyclerView.SCROLL_STATE_IDLE);
            }
        });
    }

    /**
     * Sets the month displayed at the top of this view based on time. Override
     * to add custom events when the title is changed.
     */
    protected void setMonthDisplayed(MonthAdapter.CalendarDay date) {
        mCurrentMonthDisplayed = date.month;
        invalidate();
    }

    protected ScrollStateRunnable mScrollStateChangedRunnable = new ScrollStateRunnable();

    protected class ScrollStateRunnable implements Runnable {
        private int mNewState;

        /**
         * Sets up the runnable with a short delay in case the scroll state
         * immediately changes again.
         *
         * @param scrollState The new state it changed to
         */
        public void doScrollStateChange(int scrollState) {
            mHandler = new Handler();
            mHandler.removeCallbacks(this);
            mNewState = scrollState;
            mHandler.postDelayed(this, SCROLL_CHANGE_DELAY);
        }

        @Override
        public void run() {
            mCurrentScrollState = mNewState;
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG,
                        "new scroll state: " + mNewState + " old state: " + mPreviousScrollState);
            }
            // Fix the position after a scroll or a fling ends
            if (mNewState == RecyclerView.SCROLL_STATE_IDLE
                    && mPreviousScrollState != RecyclerView.SCROLL_STATE_IDLE
                    && mPreviousScrollState != RecyclerView.SCROLL_STATE_DRAGGING) {
                mPreviousScrollState = mNewState;

                int firstPosition = ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition();
                int lastPosition = ((LinearLayoutManager)getLayoutManager()).findLastVisibleItemPosition();
                boolean scroll = firstPosition != 0 && lastPosition != getLayoutManager().getChildCount() - 1;
                final int midpoint = getHeight() / 2;
                int toporleft;
                int bottomorright;

                int i = 0;
                View child = getChildAt(i);
                if(mScrollDirection == LinearLayoutManager.VERTICAL){
                    while (child != null && child.getBottom() <= 0) {
                        child = getChildAt(++i);
                    }
                    if (child == null) {return;} // The view is no longer visible, just return

                    toporleft = child.getTop();
                    bottomorright = child.getBottom();

                    if (scroll && toporleft < LIST_TOP_OFFSET) {
                        if (bottomorright > midpoint) {
                            smoothScrollBy(0, toporleft);
                        } else {
                            smoothScrollBy(0, bottomorright);
                        }
                    }

                }else{
                    while (child != null && child.getRight() <= 0) {
                        child = getChildAt(++i);
                    }
                    if (child == null) {return;} // The view is no longer visible, just return

                    toporleft = child.getLeft();
                    bottomorright = child.getRight();

                    if (scroll && toporleft < LIST_TOP_OFFSET) {
                        if (bottomorright > midpoint) {
                            smoothScrollBy(toporleft, 0);
                        } else {
                            smoothScrollBy(bottomorright, 0);
                        }
                    }
                }

            } else {
                mPreviousScrollState = mNewState;
            }
        }
    }

}
