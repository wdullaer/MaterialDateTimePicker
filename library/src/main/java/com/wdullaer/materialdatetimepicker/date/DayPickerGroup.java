package com.wdullaer.materialdatetimepicker.date;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.wdullaer.materialdatetimepicker.R;
import com.wdullaer.materialdatetimepicker.Utils;

public class DayPickerGroup extends ViewGroup
        implements View.OnClickListener, DayPickerView.OnPageListener {
    private ImageButton prevButton;
    private ImageButton nextButton;
    private DayPickerView dayPickerView;
    private DatePickerController controller;
    private OnMonthChangedListener monthChangedListener;

    /**
     * The callback used to notify a listener the month has changed.
     */
    public interface OnMonthChangedListener {

        void onMonthIncremented();

        void onMonthDecremented();
    }

    public DayPickerGroup(Context context) {
        super(context);
        init();
    }

    public DayPickerGroup(Context context, @NonNull DatePickerController controller) {
        super(context);
        this.controller = controller;
        init();
    }

    public DayPickerGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DayPickerGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dayPickerView = new SimpleDayPickerView(getContext(), controller);
        addView(dayPickerView);

        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final ViewGroup content = (ViewGroup) inflater.inflate(R.layout.mdtp_daypicker_group, this, false);

        // Transfer all children from the content to this
        while (content.getChildCount() > 0) {
            final View view = content.getChildAt(0);
            content.removeViewAt(0);
            addView(view);
        }

        prevButton = findViewById(R.id.mdtp_previous_month_arrow);
        nextButton = findViewById(R.id.mdtp_next_month_arrow);

        if (controller.getVersion() == DatePickerDialog.Version.VERSION_1) {
            int size = Utils.dpToPx(16f, getResources());
            prevButton.setMinimumHeight(size);
            prevButton.setMinimumWidth(size);
            nextButton.setMinimumHeight(size);
            nextButton.setMinimumWidth(size);
        }

        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        dayPickerView.setOnPageListener(this);
    }

    private void updateButtonVisibility(int position) {
        final boolean isHorizontal = controller.getScrollOrientation() == DatePickerDialog.ScrollOrientation.HORIZONTAL;
        final boolean hasPrev = position > 0;
        final boolean hasNext = position < (dayPickerView.getCount() - 1);
        prevButton.setVisibility(isHorizontal && hasPrev ? View.VISIBLE : View.INVISIBLE);
        nextButton.setVisibility(isHorizontal && hasNext ? View.VISIBLE : View.INVISIBLE);
    }

    public void onChange() {
        dayPickerView.onChange();
    }

    public void onDateChanged() {
        dayPickerView.onDateChanged();
    }

    public void postSetSelection(int position) {
        dayPickerView.postSetSelection(position);
    }

    public int getMostVisiblePosition() {
        return dayPickerView.getMostVisiblePosition();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(dayPickerView, widthMeasureSpec, heightMeasureSpec);

        final int measuredWidthAndState = dayPickerView.getMeasuredWidthAndState();
        final int measuredHeightAndState = dayPickerView.getMeasuredHeightAndState();
        setMeasuredDimension(measuredWidthAndState, measuredHeightAndState);

        final int pagerWidth = dayPickerView.getMeasuredWidth();
        final int pagerHeight = dayPickerView.getMeasuredHeight();
        final int buttonWidthSpec = MeasureSpec.makeMeasureSpec(pagerWidth, MeasureSpec.AT_MOST);
        final int buttonHeightSpec = MeasureSpec.makeMeasureSpec(pagerHeight, MeasureSpec.AT_MOST);
        prevButton.measure(buttonWidthSpec, buttonHeightSpec);
        nextButton.measure(buttonWidthSpec, buttonHeightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final ImageButton leftButton;
        final ImageButton rightButton;
        if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            leftButton = nextButton;
            rightButton = prevButton;
        } else {
            leftButton = prevButton;
            rightButton = nextButton;
        }

        final int width = right - left;
        final int height = bottom - top;
        dayPickerView.layout(0, 0, width, height);

        final SimpleMonthView monthView = (SimpleMonthView) dayPickerView.getChildAt(0);
        final int monthHeight = monthView.getMonthHeight();
        final int cellWidth = monthView.getCellWidth();
        final int edgePadding = monthView.getEdgePadding();

        // Vertically center the previous/next buttons within the month
        // header, horizontally center within the day cell.
        final int leftDW = leftButton.getMeasuredWidth();
        final int leftDH = leftButton.getMeasuredHeight();
        final int leftIconTop = monthView.getPaddingTop() + (monthHeight - leftDH) / 2;
        final int leftIconLeft = edgePadding + (cellWidth - leftDW) / 2;
        leftButton.layout(leftIconLeft, leftIconTop, leftIconLeft + leftDW, leftIconTop + leftDH);

        final int rightDW = rightButton.getMeasuredWidth();
        final int rightDH = rightButton.getMeasuredHeight();
        final int rightIconTop = monthView.getPaddingTop() + (monthHeight - rightDH) / 2;
        final int rightIconRight = width - edgePadding - (cellWidth - rightDW) / 2 - 2;
        rightButton.layout(rightIconRight - rightDW, rightIconTop,
                rightIconRight, rightIconTop + rightDH);
    }

    @Override
    public void onPageChanged(int position) {
        updateButtonVisibility(position);
    }

    @Override
    public void onClick(@NonNull View v) {
        int offset;
        if (nextButton == v) {
            offset = 1;
            notifyMonthIncremented();
        } else if (prevButton == v) {
            offset = -1;
            notifyMonthDecremented();
        } else {
            return;
        }
        int position = dayPickerView.getMostVisiblePosition() + offset;
        dayPickerView.smoothScrollToPosition(position);
        updateButtonVisibility(position);
    }

    public void setOnMonthChangedListener(OnMonthChangedListener listener) {
        monthChangedListener = listener;
    }

    private void notifyMonthIncremented() {
        monthChangedListener.onMonthIncremented();
    }

    private void notifyMonthDecremented() {
        monthChangedListener.onMonthDecremented();
    }
}
