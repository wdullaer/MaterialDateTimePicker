package com.wdullaer.materialdatetimepicker.date;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.R;

import java.util.Calendar;
import java.util.HashMap;

public class MonthRecyclerViewAdapter extends RecyclerView.Adapter<MonthRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "SimpleMonthAdapter";

    private final Context mContext;
    protected final DatePickerController mController;

    private MonthAdapter.CalendarDay mSelectedDay;

    protected static int WEEK_7_OVERHANG_HEIGHT = 7;
    protected static final int MONTHS_IN_YEAR = 12;


    public MonthRecyclerViewAdapter(Context context, DatePickerController controller) {
        mContext = context;
        mController = controller;
        mSelectedDay = new MonthAdapter.CalendarDay(System.currentTimeMillis());
        setSelectedDay(mController.getSelectedDay());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public HashMap<String, Integer> drawingParams = null;

        public ViewHolder(SimpleMonthView v) {
            super(v);

            if (drawingParams == null) {
                drawingParams = new HashMap<>();
            }
            drawingParams.clear();
        }
    }
    @Override
    public MonthRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        SimpleMonthView v = new SimpleMonthView(mContext, null, mController);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
        v.setLayoutParams(params);
        v.setClickable(true);
        v.setOnDayClickListener(new MonthView.OnDayClickListener() {
            @Override
            public void onDayClick(MonthView view, MonthAdapter.CalendarDay day) {
                if (day != null) {
                    onDayTapped(day);
                }
            }
        });

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final int month = (position + mController.getStartDate().get(Calendar.MONTH)) % MONTHS_IN_YEAR;
        final int year = (position + mController.getStartDate().get(Calendar.MONTH)) / MONTHS_IN_YEAR + mController.getMinYear();

        int selectedDay = -1;
        if (isSelectedDayInMonth(year, month)) {
            selectedDay = mSelectedDay.day;
        }

        // Invokes requestLayout() to ensure that the recycled view is set with the appropriate
        // height/number of weeks before being displayed.
        //((SimpleMonthView)holder.itemView).reuse();

        holder.drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
        holder.drawingParams.put(SimpleMonthView.VIEW_PARAMS_YEAR, year);
        holder.drawingParams.put(SimpleMonthView.VIEW_PARAMS_MONTH, month);
        holder.drawingParams.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, mController.getFirstDayOfWeek());
        ((SimpleMonthView)holder.itemView).setMonthParams(holder.drawingParams);
        holder.itemView.invalidate();
    }

    @Override
    public int getItemCount() {
        Calendar endDate = mController.getEndDate();
        Calendar startDate = mController.getStartDate();
        int endMonth = endDate.get(Calendar.YEAR) * MONTHS_IN_YEAR + endDate.get(Calendar.MONTH);
        int startMonth = startDate.get(Calendar.YEAR) * MONTHS_IN_YEAR + startDate.get(Calendar.MONTH);
        return endMonth - startMonth + 1;
        //return ((mController.getMaxYear() - mController.getMinYear()) + 1) * MONTHS_IN_YEAR;
    }

    private boolean isSelectedDayInMonth(int year, int month) {
        return mSelectedDay.year == year && mSelectedDay.month == month;
    }

    /**
     * Updates the selected day and related parameters.
     *
     * @param day The day to highlight
     */
    public void setSelectedDay(MonthAdapter.CalendarDay day) {
        mSelectedDay = day;
        notifyDataSetChanged();
    }

    /**
     * Maintains the same hour/min/sec but moves the day to the tapped day.
     *
     * @param day The day that was tapped
     */
    protected void onDayTapped(MonthAdapter.CalendarDay day) {
        mController.tryVibrate();
        mController.onDayOfMonthSelected(day.year, day.month, day.day);
        setSelectedDay(day);
    }

}
