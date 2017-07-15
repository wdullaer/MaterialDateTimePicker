/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wdullaer.materialdatetimepicker.date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.wdullaer.materialdatetimepicker.GravitySnapHelper;
import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateChangedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * This displays a list of months in a calendar format with selectable days.
 */
public abstract class DayPickerView extends RecyclerView implements OnDateChangedListener {

    private static final String TAG = "MonthFragment";

    // Affects when the month selection will change while scrolling up
    protected static final int SCROLL_HYST_WEEKS = 2;

    // The number of days to display in each week
    public static final int DAYS_PER_WEEK = 7;

    protected int mNumWeeks = 6;
    protected boolean mShowWeekNumber = false;
    protected int mDaysPerWeek = 7;
    private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());

    protected Context mContext;
    protected Handler mHandler;

    // highlighted time
    protected MonthAdapter.CalendarDay mSelectedDay;
    protected MonthAdapter mAdapter;

    protected MonthAdapter.CalendarDay mTempDay;

    // When the week starts; numbered like Time.<WEEKDAY> (e.g. SUNDAY=0).
    protected int mFirstDayOfWeek;
    // The last name announced by accessibility
    protected CharSequence mPrevMonthName;
    // which month should be displayed/highlighted [0-11]
    protected int mCurrentMonthDisplayed;
    // used for tracking during a scroll
    protected long mPreviousScrollPosition;
    // used for tracking what state listview is in
    protected int mPreviousScrollState = RecyclerView.SCROLL_STATE_IDLE;

    private DatePickerController mController;
    private LinearLayoutManager linearLayoutManager;

    public DayPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DayPickerView(Context context, DatePickerController controller) {
        super(context);
        init(context);
        setController(controller);
    }

    public void setController(DatePickerController controller) {
        mController = controller;
        mController.registerOnDateChangedListener(this);
        mSelectedDay = new MonthAdapter.CalendarDay(mController.getTimeZone());
        mTempDay = new MonthAdapter.CalendarDay(mController.getTimeZone());
        refreshAdapter();
        onDateChanged();
    }

    public void init(Context context) {
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        setLayoutManager(linearLayoutManager);
        mHandler = new Handler();
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);

        mContext = context;
        setUpRecyclerView();
    }

    public void setScrollOrientation(int orientation) {
        linearLayoutManager.setOrientation(orientation);
    }

    /**
     * Sets all the required fields for the list view. Override this method to
     * set a different list view behavior.
     */
    protected void setUpRecyclerView() {
        setVerticalScrollBarEnabled(false);
        setFadingEdgeLength(0);
        GravitySnapHelper helper = new GravitySnapHelper(Gravity.TOP);
        helper.attachToRecyclerView(this);
    }

    public void onChange() {
        refreshAdapter();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        final MonthAdapter.CalendarDay focusedDay = findAccessibilityFocus();
        restoreAccessibilityFocus(focusedDay);
    }

    /**
     * Creates a new adapter if necessary and sets up its parameters. Override
     * this method to provide a custom adapter.
     */
    protected void refreshAdapter() {
        if (mAdapter == null) {
            mAdapter = createMonthAdapter(mController);
        } else {
            mAdapter.setSelectedDay(mSelectedDay);
        }
        // refresh the view with the new parameters
        setAdapter(mAdapter);
    }

    public abstract MonthAdapter createMonthAdapter(DatePickerController controller);

    /**
     * This moves to the specified time in the view. If the time is not already
     * in range it will move the list so that the first of the month containing
     * the time is at the top of the view. If the new time is already in view
     * the list will not be scrolled unless forceScroll is true. This time may
     * optionally be highlighted as selected as well.
     *
     * @param day         The day to move to
     * @param animate     Whether to scroll to the given time or just redraw at the
     *                    new location
     * @param setSelected Whether to set the given time as selected
     * @param forceScroll Whether to recenter even if the time is already
     *                    visible
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
        int top = 0;
        // Find a child that's completely in the view
        do {
            child = getChildAt(i++);
            if (child == null) {
                break;
            }
            top = child.getTop();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "child at " + (i - 1) + " has top " + top);
            }
        } while (top < 0);

        // Compute the first and last position visible
        int selectedPosition = child != null ? getChildAdapterPosition(child) : 0;

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
            mPreviousScrollState = RecyclerView.SCROLL_STATE_DRAGGING;
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
                ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(position, 0);
            }
        });
    }

    /**
     * Sets the month displayed at the top of this view based on time. Override
     * to add custom events when the title is changed.
     */
    protected void setMonthDisplayed(MonthAdapter.CalendarDay date) {
        mCurrentMonthDisplayed = date.month;
    }

    /**
     * Gets the position of the view that is most prominently displayed within the list.
     */
    public int getMostVisiblePosition() {
        return getChildAdapterPosition(getMostVisibleMonth());
    }

    public MonthView getMostVisibleMonth() {
        boolean verticalScroll = ((LinearLayoutManager) getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL;
        final int maxSize = verticalScroll ? getHeight() : getWidth();
        int maxDisplayedSize = 0;
        int i = 0;
        int size = 0;
        MonthView mostVisibleMonth = null;

        while (size < maxSize) {
            View child = getChildAt(i);
            if (child == null) {
                break;
            }
            size = verticalScroll ? child.getBottom() : getRight();
            int displayedSize = Math.min(size, maxSize) - Math.max(0, child.getTop());
            if (displayedSize > maxDisplayedSize) {
                mostVisibleMonth = (MonthView) child;
                maxDisplayedSize = displayedSize;
            }
            i++;
        }
        return mostVisibleMonth;
    }

    @Override
    public void onDateChanged() {
        goTo(mController.getSelectedDay(), false, true, true);
    }

    /**
     * Attempts to return the date that has accessibility focus.
     *
     * @return The date that has accessibility focus, or {@code null} if no date
     * has focus.
     */
    private MonthAdapter.CalendarDay findAccessibilityFocus() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child instanceof MonthView) {
                final MonthAdapter.CalendarDay focus = ((MonthView) child).getAccessibilityFocus();
                if (focus != null) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        // Clear focus to avoid ListView bug in Jelly Bean MR1.
                        ((MonthView) child).clearAccessibilityFocus();
                    }
                    return focus;
                }
            }
        }

        return null;
    }

    /**
     * Attempts to restore accessibility focus to a given date. No-op if
     * {@code day} is {@code null}.
     *
     * @param day The date that should receive accessibility focus
     * @return {@code true} if focus was restored
     */
    private boolean restoreAccessibilityFocus(MonthAdapter.CalendarDay day) {
        if (day == null) {
            return false;
        }

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child instanceof MonthView) {
                if (((MonthView) child).restoreAccessibilityFocus(day)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setItemCount(-1);
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

    /**
     * Necessary for accessibility, to ensure we support "scrolling" forward and backward
     * in the month list.
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT >= 21) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
        } else {
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        }
    }

    /**
     * When scroll forward/backward events are received, announce the newly scrolled-to month.
     */
    @SuppressLint("NewApi")
    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (action != AccessibilityNodeInfo.ACTION_SCROLL_FORWARD &&
                action != AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
            return super.performAccessibilityAction(action, arguments);
        }
        // Figure out what month is showing.
        int firstVisiblePosition = getFirstVisiblePosition();
        int minMonth = mController.getStartDate().get(Calendar.MONTH);
        int month = (firstVisiblePosition + minMonth) % MonthAdapter.MONTHS_IN_YEAR;
        int year = (firstVisiblePosition + minMonth) / MonthAdapter.MONTHS_IN_YEAR + mController.getMinYear();
        MonthAdapter.CalendarDay day = new MonthAdapter.CalendarDay(year, month, 1);

        // Scroll either forward or backward one month.
        if (action == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
            day.month++;
            if (day.month == 12) {
                day.month = 0;
                day.year++;
            }
        } else if (action == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
            View firstVisibleView = getChildAt(0);
            // If the view is fully visible, jump one month back. Otherwise, we'll just jump
            // to the first day of first visible month.
            if (firstVisibleView != null && firstVisibleView.getTop() >= -1) {
                // There's an off-by-one somewhere, so the top of the first visible item will
                // actually be -1 when it's at the exact top.
                day.month--;
                if (day.month == -1) {
                    day.month = 11;
                    day.year--;
                }
            }
        }

        // Go to that month.
        Utils.tryAccessibilityAnnounce(this, getMonthAndYearString(day));
        goTo(day, true, false, true);
        return true;
    }

    private int getFirstVisiblePosition() {
        return getChildAdapterPosition(getChildAt(0));
    }

}
