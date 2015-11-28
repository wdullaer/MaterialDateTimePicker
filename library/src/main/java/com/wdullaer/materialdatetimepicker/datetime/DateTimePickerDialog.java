package com.wdullaer.materialdatetimepicker.datetime;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wdullaer.materialdatetimepicker.HapticFeedbackController;
import com.wdullaer.materialdatetimepicker.R;
import com.wdullaer.materialdatetimepicker.TypefaceHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.Calendar;

/**
 * Dialog to set a date and a time using a viewpager
 * Created by wdullaer on 16/11/15.
 */
public class DateTimePickerDialog extends DialogFragment
{
    interface OnDateTimeListener {
        /**
         * @param view The view associated with this listener.
         * @param selection a Calendar object containing the select date and time
         */
        void onDateTimeSet(DateTimePickerDialog view, Calendar selection);
    }

    // Components
    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    private HapticFeedbackController mHapticFeedbackController;

    // Callbacks
    private OnDateTimeListener mCallback;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

    // Overall options
    private boolean mVibrate = true;
    private boolean mDismissOnPause = false;
    private int mOkResid = R.string.mdtp_ok;
    private String mOkString;
    private int mCancelResid = R.string.mdtp_cancel;
    private String mCancelString;

    private static final String KEY_VIBRATE = "vibrate";
    private static final String KEY_DISMISS = "dismiss";
    private static final String KEY_OK_RESID = "ok_resid";
    private static final String KEY_OK_STRING = "ok_string";
    private static final String KEY_CANCEL_RESID = "cancel_resid";
    private static final String KEY_CANCEL_STRING = "cancel_string";

    public DateTimePickerDialog() {}

    public static DateTimePickerDialog newInstance(
            OnDateTimeListener callback,
            Calendar defaultSelection,
            boolean is24HourMode
    ) {
        DateTimePickerDialog ret = new DateTimePickerDialog();
        ret.initialize(callback, defaultSelection, is24HourMode);
        return ret;
    }

    private void initialize(OnDateTimeListener callback, Calendar defaultSelection, boolean is24HourMode) {
        mCallback = callback;
        dpd = DatePickerDialog.newInstance(
                null,
                defaultSelection.get(Calendar.YEAR),
                defaultSelection.get(Calendar.MONTH),
                defaultSelection.get(Calendar.DAY_OF_MONTH)
        );
        tpd = TimePickerDialog.newInstance(
                null,
                defaultSelection.get(Calendar.HOUR_OF_DAY),
                defaultSelection.get(Calendar.MINUTE),
                defaultSelection.get(Calendar.SECOND),
                is24HourMode
        );
    }

    public DateTimePickerDialog setTitle(String title) {
        tpd.setTitle(title);
        dpd.setTitle(title);
        return this;
    }

    public DateTimePickerDialog setThemeDark(boolean themeDark) {
        tpd.setThemeDark(themeDark);
        dpd.setThemeDark(themeDark);
        return this;
    }

    public DateTimePickerDialog setAccentColor(int accentColor) {
        tpd.setAccentColor(accentColor);
        dpd.setAccentColor(accentColor);
        return this;
    }

    public DateTimePickerDialog Vibrate(boolean vibrate) {
        mVibrate = vibrate;
        tpd.vibrate(vibrate);
        dpd.vibrate(vibrate);
        return this;
    }

    public DateTimePickerDialog dismissOnPause(boolean dismissOnPause) {
        mDismissOnPause = dismissOnPause;
        return this;
    }

    public DateTimePickerDialog setOkText(int okResid) {
        mOkString = null;
        mOkResid = okResid;
        return this;
    }

    public DateTimePickerDialog setOkText(String okString) {
        mOkString = okString;
        return this;
    }

    public DateTimePickerDialog setCancelText(int cancelResid) {
        mCancelString = null;
        mCancelResid = cancelResid;
        return this;
    }

    public DateTimePickerDialog setCancelText(String cancelString) {
        mCancelString = cancelString;
        return this;
    }

    // DatePickerDialog options
    public DateTimePickerDialog setYearRange(int minYear, int maxYear) {
        dpd.setYearRange(minYear, maxYear);
        return this;
    }

    public DateTimePickerDialog setMinDate(Calendar minDate) {
        dpd.setMinDate(minDate);
        return this;
    }

    public DateTimePickerDialog setMaxDate(Calendar maxDate) {
        dpd.setMaxDate(maxDate);
        return this;
    }

    public DateTimePickerDialog setHighlightedDays(Calendar[] days) {
        dpd.setHighlightedDays(days);
        return this;
    }

    public DateTimePickerDialog setSelectableDays(Calendar[] days) {
        dpd.setSelectableDays(days);
        return this;
    }

    public DateTimePickerDialog showYearPickerFirst(boolean showYearFirst) {
        dpd.showYearPickerFirst(showYearFirst);
        return this;
    }

    // TimePickerDialog options
    public DateTimePickerDialog setSelectableTimes(Timepoint[] times) {
        tpd.setSelectableTimes(times);
        return this;
    }

    public DateTimePickerDialog setMinTime(Timepoint time) {
        tpd.setMinTime(time);
        return this;
    }

    public DateTimePickerDialog setMinTime(int hour, int minute, int second) {
        tpd.setMinTime(hour, minute, second);
        return this;
    }

    public DateTimePickerDialog setMaxTime(Timepoint time) {
        tpd.setMaxTime(time);
        return this;
    }

    public DateTimePickerDialog setMaxTime(int hour, int minute, int second) {
        tpd.setMaxTime(hour, minute, second);
        return this;
    }

    public DateTimePickerDialog enableSeconds(boolean enableSeconds) {
        tpd.enableSeconds(enableSeconds);
        return this;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }

    @SuppressWarnings("unused")
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VIBRATE, mVibrate);
        outState.putBoolean(KEY_DISMISS, mDismissOnPause);
        outState.putInt(KEY_OK_RESID, mOkResid);
        outState.putString(KEY_OK_STRING, mOkString);
        outState.putInt(KEY_CANCEL_RESID, mCancelResid);
        outState.putString(KEY_CANCEL_STRING, mCancelString);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mVibrate = savedInstanceState.getBoolean(KEY_VIBRATE);
            mDismissOnPause = savedInstanceState.getBoolean(KEY_DISMISS);
            mOkResid = savedInstanceState.getInt(KEY_OK_RESID);
            mOkString = savedInstanceState.getString(KEY_OK_STRING);
            mCancelResid = savedInstanceState.getInt(KEY_CANCEL_RESID);
            mCancelString = savedInstanceState.getString(KEY_CANCEL_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mdtp_date_time_picker_dialog, container, false);

        getFragmentManager().beginTransaction()
                .add(R.id.date_picker_dialog, dpd, "datepickerdialog")
                .add(R.id.time_picker_dialog, tpd, "timepickerdialog")
                .commit();

        Activity activity = getActivity();
        Button okButton = (Button) view.findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryVibrate();
                notifyOnDateListener();
                dismiss();
            }
        });
        okButton.setTypeface(TypefaceHelper.get(activity, "Roboto-Medium"));
        if(mOkString != null) okButton.setText(mOkString);
        else okButton.setText(mOkResid);

        Button cancelButton = (Button) view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryVibrate();
                if (getDialog() != null) getDialog().cancel();
            }
        });
        cancelButton.setTypeface(TypefaceHelper.get(activity, "Roboto-Medium"));
        if(mCancelString != null) cancelButton.setText(mCancelString);
        else cancelButton.setText(mCancelResid);
        cancelButton.setVisibility(isCancelable() ? View.VISIBLE : View.GONE);

        mHapticFeedbackController = new HapticFeedbackController(activity);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHapticFeedbackController.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHapticFeedbackController.stop();
        if(mDismissOnPause) dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(mOnCancelListener != null) mOnCancelListener.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mOnDismissListener != null) mOnDismissListener.onDismiss(dialog);
    }

    private void tryVibrate() {
        if(mVibrate) mHapticFeedbackController.tryVibrate();
    }

    private void notifyOnDateListener() {
        if (mCallback != null) {
            MonthAdapter.CalendarDay selectedDay = dpd.getSelectedDay();
            Timepoint selectedTime = tpd.getSelectedTime();
            Calendar selection = Calendar.getInstance();
            selection.set(Calendar.YEAR, selectedDay.getYear());
            selection.set(Calendar.MONTH, selectedDay.getMonth());
            selection.set(Calendar.DAY_OF_MONTH, selectedDay.getDay());
            selection.set(Calendar.HOUR_OF_DAY, selectedTime.getHour());
            selection.set(Calendar.MINUTE, selectedTime.getMinute());
            selection.set(Calendar.SECOND, selectedTime.getSecond());
            mCallback.onDateTimeSet(this, selection);
        }
    }
}
