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
 * limitations under the License
 */

package com.wdullaer.materialdatetimepicker.time;

import android.animation.ObjectAnimator;
import android.app.ActionBar.LayoutParams;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.HapticFeedbackController;
import com.wdullaer.materialdatetimepicker.R;
import com.wdullaer.materialdatetimepicker.TypefaceHelper;
import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout.OnValueSelectedListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Dialog to set a time.
 */
public class TimePickerDialog extends DialogFragment implements
        OnValueSelectedListener, TimePickerController {
    private static final String TAG = "TimePickerDialog";

    private static final String KEY_HOUR_OF_DAY = "hour_of_day";
    private static final String KEY_MINUTE = "minute";
    private static final String KEY_SECOND = "second";
    private static final String KEY_IS_24_HOUR_VIEW = "is_24_hour_view";
    private static final String KEY_TITLE = "dialog_title";
    private static final String KEY_CURRENT_ITEM_SHOWING = "current_item_showing";
    private static final String KEY_IN_KB_MODE = "in_kb_mode";
    private static final String KEY_TYPED_TIMES = "typed_times";
    private static final String KEY_DARK_THEME = "dark_theme";
    private static final String KEY_ACCENT = "accent";
    private static final String KEY_VIBRATE = "vibrate";
    private static final String KEY_DISMISS = "dismiss";
    private static final String KEY_ENABLE_SECONDS = "enable_seconds";

    public static final int HOUR_INDEX = 0;
    public static final int MINUTE_INDEX = 1;
    public static final int SECOND_INDEX = 2;
    // NOT a real index for the purpose of what's showing.
    public static final int AMPM_INDEX = 3;
    // Also NOT a real index, just used for keyboard mode.
    public static final int ENABLE_PICKER_INDEX = 4;
    public static final int AM = 0;
    public static final int PM = 1;

    // Delay before starting the pulse animation, in ms.
    private static final int PULSE_ANIMATOR_DELAY = 300;

    private OnTimeSetListener mCallback;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

    private HapticFeedbackController mHapticFeedbackController;

    private Button mCancelButton;
    private Button mOkButton;
    private TextView mHourView;
    private TextView mHourSpaceView;
    private TextView mMinuteView;
    private TextView mMinuteSpaceView;
    private TextView mSecondView;
    private TextView mSecondSpaceView;
    private TextView mAmPmTextView;
    private View mAmPmHitspace;
    private RadialPickerLayout mTimePicker;

    private int mSelectedColor;
    private int mUnselectedColor;
    private String mAmText;
    private String mPmText;

    private boolean mAllowAutoAdvance;
    private int mInitialHourOfDay;
    private int mInitialMinute;
    private int mInitialSecond;
    private boolean mIs24HourMode;
    private String mTitle;
    private boolean mThemeDark;
    private boolean mVibrate;
    private int mAccentColor = -1;
    private boolean mDismissOnPause;
    private boolean mEnableSeconds;

    // For hardware IME input.
    private char mPlaceholderText;
    private String mDoublePlaceholderText;
    private String mDeletedKeyFormat;
    private boolean mInKbMode;
    private ArrayList<Integer> mTypedTimes;
    private Node mLegalTimesTree;
    private int mAmKeyCode;
    private int mPmKeyCode;

    // Accessibility strings.
    private String mHourPickerDescription;
    private String mSelectHours;
    private String mMinutePickerDescription;
    private String mSecondPickerDescription;
    private String mSelectMinutes;
    private String mSelectSeconds;

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
         * @param second The second that was set.
         */
        void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second);
    }

    public TimePickerDialog() {
        // Empty constructor required for dialog fragment.
    }

    /**
    public TimePickerDialog(Context context, int theme, OnTimeSetListener callback,
            int hourOfDay, int minute, boolean is24HourMode) {
        // Empty constructor required for dialog fragment.
    }
     **/

    public static TimePickerDialog newInstance(OnTimeSetListener callback,
            int hourOfDay, int minute, boolean is24HourMode) {
        TimePickerDialog ret = new TimePickerDialog();
        ret.initialize(callback, hourOfDay, minute, is24HourMode);
        return ret;
    }

    public static TimePickerDialog newInstance(OnTimeSetListener callback,
            int hourOfDay, int minute, int second, boolean is24HourMode) {
        TimePickerDialog ret = new TimePickerDialog();
        ret.initialize(callback, hourOfDay, minute, second, is24HourMode);
        return ret;
    }

    public void initialize(OnTimeSetListener callback,
            int hourOfDay, int minute, boolean is24HourMode) {
        mCallback = callback;

        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mIs24HourMode = is24HourMode;
        mInKbMode = false;
        mTitle = "";
        mThemeDark = false;
        mAccentColor = -1;
        mVibrate = true;
        mDismissOnPause = false;
        mEnableSeconds = false;
    }

    public void initialize(OnTimeSetListener callback,
                           int hourOfDay, int minute, int second, boolean is24HourMode) {
        initialize(callback, hourOfDay, minute, is24HourMode);
        mInitialSecond = second;
        mEnableSeconds = true;
    }

    /**
     * Set a title. NOTE: this will only take effect with the next onCreateView
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    /**
     * Set a dark or light theme. NOTE: this will only take effect for the next onCreateView.
     */
    public void setThemeDark(boolean dark) {
        mThemeDark = dark;
    }

    public void setAccentColor(int color) {
        mAccentColor = color;
    }

    @Override
    public boolean isThemeDark() {
        return mThemeDark;
    }

    @Override
    public boolean is24HourMode() {
        return mIs24HourMode;
    }

    @Override
    public int getAccentColor() {
        return mAccentColor;
    }

    public void setSecondsEnabled(boolean enabled) {
        mEnableSeconds = enabled;
    }

    /**
     * Set whether the device should vibrate when touching fields
     * @param vibrate true if the device should vibrate when touching a field
     */
    public void vibrate(boolean vibrate) {
        mVibrate = vibrate;
    }

    /**
     * Set whether the picker should dismiss itself when it's pausing or whether it should try to survive an orientation change
     * @param dismissOnPause true if the picker should dismiss itself
     */
    public void dismissOnPause(boolean dismissOnPause) {
        mDismissOnPause = dismissOnPause;
    }

    public void setOnTimeSetListener(OnTimeSetListener callback) {
        mCallback = callback;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public void setStartTime(int hourOfDay, int minute) {
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mInKbMode = false;
    }

    public void setStartTime(int hourOfDay, int minute, int second) {
        setStartTime(hourOfDay, minute);
        mInitialSecond = second;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_HOUR_OF_DAY)
                    && savedInstanceState.containsKey(KEY_MINUTE)
                    && savedInstanceState.containsKey(KEY_IS_24_HOUR_VIEW)) {
            mInitialHourOfDay = savedInstanceState.getInt(KEY_HOUR_OF_DAY);
            mInitialMinute = savedInstanceState.getInt(KEY_MINUTE);
            mInitialSecond = savedInstanceState.getInt(KEY_SECOND);
            mIs24HourMode = savedInstanceState.getBoolean(KEY_IS_24_HOUR_VIEW);
            mInKbMode = savedInstanceState.getBoolean(KEY_IN_KB_MODE);
            mTitle = savedInstanceState.getString(KEY_TITLE);
            mThemeDark = savedInstanceState.getBoolean(KEY_DARK_THEME);
            mAccentColor = savedInstanceState.getInt(KEY_ACCENT);
            mVibrate = savedInstanceState.getBoolean(KEY_VIBRATE);
            mDismissOnPause = savedInstanceState.getBoolean(KEY_DISMISS);
            mEnableSeconds = savedInstanceState.getBoolean(KEY_ENABLE_SECONDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.mdtp_time_picker_dialog, container);
        KeyboardListener keyboardListener = new KeyboardListener();
        view.findViewById(R.id.time_picker_dialog).setOnKeyListener(keyboardListener);

        // If an accent color has not been set manually, get it from the context
        if (mAccentColor == -1) {
            mAccentColor = Utils.getAccentColorFromThemeIfAvailable(getActivity());
        }

        Resources res = getResources();
        mHourPickerDescription = res.getString(R.string.mdtp_hour_picker_description);
        mSelectHours = res.getString(R.string.mdtp_select_hours);
        mMinutePickerDescription = res.getString(R.string.mdtp_minute_picker_description);
        mSelectMinutes = res.getString(R.string.mdtp_select_minutes);
        mSecondPickerDescription = res.getString(R.string.mdtp_second_picker_description);
        mSelectSeconds = res.getString(R.string.mdtp_select_seconds);
        mSelectedColor = res.getColor(R.color.mdtp_white);
        mUnselectedColor = res.getColor(R.color.mdtp_accent_color_focused);

        mHourView = (TextView) view.findViewById(R.id.hours);
        mHourView.setOnKeyListener(keyboardListener);
        mHourSpaceView = (TextView) view.findViewById(R.id.hour_space);
        mMinuteSpaceView = (TextView) view.findViewById(R.id.minutes_space);
        mMinuteView = (TextView) view.findViewById(R.id.minutes);
        mMinuteView.setOnKeyListener(keyboardListener);
        mSecondSpaceView = (TextView) view.findViewById(R.id.seconds_space);
        mSecondView = (TextView) view.findViewById(R.id.seconds);
        mSecondView.setOnKeyListener(keyboardListener);
        mAmPmTextView = (TextView) view.findViewById(R.id.ampm_label);
        mAmPmTextView.setOnKeyListener(keyboardListener);
        String[] amPmTexts = new DateFormatSymbols().getAmPmStrings();
        mAmText = amPmTexts[0];
        mPmText = amPmTexts[1];

        mHapticFeedbackController = new HapticFeedbackController(getActivity());

        mTimePicker = (RadialPickerLayout) view.findViewById(R.id.time_picker);
        mTimePicker.setOnValueSelectedListener(this);
        mTimePicker.setOnKeyListener(keyboardListener);
        mTimePicker.initialize(getActivity(), this, mInitialHourOfDay,
            mInitialMinute, mInitialSecond, mIs24HourMode);

        int currentItemShowing = HOUR_INDEX;
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_CURRENT_ITEM_SHOWING)) {
            currentItemShowing = savedInstanceState.getInt(KEY_CURRENT_ITEM_SHOWING);
        }
        setCurrentItemShowing(currentItemShowing, false, true, true);
        mTimePicker.invalidate();

        mHourView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItemShowing(HOUR_INDEX, true, false, true);
                tryVibrate();
            }
        });
        mMinuteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItemShowing(MINUTE_INDEX, true, false, true);
                tryVibrate();
            }
        });
        mSecondView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItemShowing(SECOND_INDEX, true, false, true);
                tryVibrate();
            }
        });

        mOkButton = (Button) view.findViewById(R.id.ok);
        mOkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInKbMode && isTypedTimeFullyLegal()) {
                    finishKbMode(false);
                } else {
                    tryVibrate();
                }
                if (mCallback != null) {
                    mCallback.onTimeSet(mTimePicker,
                            mTimePicker.getHours(), mTimePicker.getMinutes(), mTimePicker.getSeconds());
                }
                dismiss();
            }
        });
        mOkButton.setOnKeyListener(keyboardListener);
        mOkButton.setTypeface(TypefaceHelper.get(getDialog().getContext(), "Roboto-Medium"));

        mCancelButton = (Button) view.findViewById(R.id.cancel);
        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tryVibrate();
                if(getDialog() != null) getDialog().cancel();
            }
        });
        mCancelButton.setTypeface(TypefaceHelper.get(getDialog().getContext(),"Roboto-Medium"));
        mCancelButton.setVisibility(isCancelable() ? View.VISIBLE : View.GONE);

        // Enable or disable the AM/PM view.
        mAmPmHitspace = view.findViewById(R.id.ampm_hitspace);
        if (mIs24HourMode) {
            mAmPmTextView.setVisibility(View.GONE);

        } else {

            mAmPmTextView.setVisibility(View.VISIBLE);
            updateAmPmDisplay(mInitialHourOfDay < 12 ? AM : PM);
            mAmPmHitspace.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tryVibrate();
                    int amOrPm = mTimePicker.getIsCurrentlyAmOrPm();
                    if (amOrPm == AM) {
                        amOrPm = PM;
                    } else if (amOrPm == PM) {
                        amOrPm = AM;
                    }
                    updateAmPmDisplay(amOrPm);
                    mTimePicker.setAmOrPm(amOrPm);
                }
            });
        }

        // Hide seconds if disabled
        if (!mEnableSeconds) {
            mSecondSpaceView.setVisibility(View.GONE);
            view.findViewById(R.id.separator_seconds).setVisibility(View.GONE);
        }

        // Center stuff depending on what's visible
        if (mIs24HourMode && !mEnableSeconds) {

            // center first separator
            RelativeLayout.LayoutParams paramsSeparator = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            paramsSeparator.addRule(RelativeLayout.CENTER_IN_PARENT);
            TextView separatorView = (TextView) view.findViewById(R.id.separator);
            separatorView.setLayoutParams(paramsSeparator);

        } else {
            if (mEnableSeconds) {

                // link separator to minutes
                final View separator = view.findViewById(R.id.separator);
                RelativeLayout.LayoutParams paramsSeparator = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                paramsSeparator.addRule(RelativeLayout.LEFT_OF, R.id.minutes_space);
                paramsSeparator.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                separator.setLayoutParams(paramsSeparator);

                if (!mIs24HourMode) {

                    // center minutes
                    RelativeLayout.LayoutParams paramsMinutes = new RelativeLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    paramsMinutes.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mMinuteSpaceView.setLayoutParams(paramsMinutes);

                } else {

                    // move minutes to right of center
                    RelativeLayout.LayoutParams paramsMinutes = new RelativeLayout.LayoutParams(
                                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    paramsMinutes.addRule(RelativeLayout.RIGHT_OF, R.id.center_view);
                    mMinuteSpaceView.setLayoutParams(paramsMinutes);
                }
            }
        }

        mAllowAutoAdvance = true;
        setHour(mInitialHourOfDay, true);
        setMinute(mInitialMinute);
        setSecond(mInitialSecond);

        // Set up for keyboard mode.
        mDoublePlaceholderText = res.getString(R.string.mdtp_time_placeholder);
        mDeletedKeyFormat = res.getString(R.string.mdtp_deleted_key);
        mPlaceholderText = mDoublePlaceholderText.charAt(0);
        mAmKeyCode = mPmKeyCode = -1;
        generateLegalTimesTree();
        if (mInKbMode) {
            mTypedTimes = savedInstanceState.getIntegerArrayList(KEY_TYPED_TIMES);
            tryStartingKbMode(-1);
            mHourView.invalidate();
        } else if (mTypedTimes == null) {
            mTypedTimes = new ArrayList<>();
        }

        // Set the title (if any)
        TextView timePickerHeader = (TextView) view.findViewById(R.id.time_picker_header);
        if (!mTitle.isEmpty()) {
            timePickerHeader.setVisibility(TextView.VISIBLE);
            timePickerHeader.setText(mTitle);
        }

        // Set the theme at the end so that the initialize()s above don't counteract the theme.
        mOkButton.setTextColor(mAccentColor);
        mCancelButton.setTextColor(mAccentColor);
        timePickerHeader.setBackgroundColor(Utils.darkenColor(mAccentColor));
        view.findViewById(R.id.time_display_background).setBackgroundColor(mAccentColor);
        view.findViewById(R.id.time_display).setBackgroundColor(mAccentColor);

        int circleBackground = res.getColor(R.color.mdtp_circle_background);
        int backgroundColor = res.getColor(R.color.mdtp_background_color);
        int darkBackgroundColor = res.getColor(R.color.mdtp_light_gray);
        int lightGray = res.getColor(R.color.mdtp_light_gray);

        mTimePicker.setBackgroundColor(mThemeDark? lightGray : circleBackground);
        view.findViewById(R.id.time_picker_dialog).setBackgroundColor(mThemeDark ? darkBackgroundColor : backgroundColor);
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

    @Override
    public void tryVibrate() {
        if(mVibrate) mHapticFeedbackController.tryVibrate();
    }

    private void updateAmPmDisplay(int amOrPm) {
        if (amOrPm == AM) {
            mAmPmTextView.setText(mAmText);
            Utils.tryAccessibilityAnnounce(mTimePicker, mAmText);
            mAmPmHitspace.setContentDescription(mAmText);
        } else if (amOrPm == PM){
            mAmPmTextView.setText(mPmText);
            Utils.tryAccessibilityAnnounce(mTimePicker, mPmText);
            mAmPmHitspace.setContentDescription(mPmText);
        } else {
            mAmPmTextView.setText(mDoublePlaceholderText);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mTimePicker != null) {
            outState.putInt(KEY_HOUR_OF_DAY, mTimePicker.getHours());
            outState.putInt(KEY_MINUTE, mTimePicker.getMinutes());
            outState.putInt(KEY_SECOND, mTimePicker.getSeconds());
            outState.putBoolean(KEY_IS_24_HOUR_VIEW, mIs24HourMode);
            outState.putInt(KEY_CURRENT_ITEM_SHOWING, mTimePicker.getCurrentItemShowing());
            outState.putBoolean(KEY_IN_KB_MODE, mInKbMode);
            if (mInKbMode) {
                outState.putIntegerArrayList(KEY_TYPED_TIMES, mTypedTimes);
            }
            outState.putString(KEY_TITLE, mTitle);
            outState.putBoolean(KEY_DARK_THEME, mThemeDark);
            outState.putInt(KEY_ACCENT, mAccentColor);
            outState.putBoolean(KEY_VIBRATE, mVibrate);
            outState.putBoolean(KEY_DISMISS, mDismissOnPause);
            outState.putBoolean(KEY_ENABLE_SECONDS, mEnableSeconds);
        }
    }

    /**
     * Called by the picker for updating the header display.
     */
    @Override
    public void onValueSelected(int pickerIndex, int newValue, boolean autoAdvance) {
        if (pickerIndex == HOUR_INDEX) {
            setHour(newValue, false);
            String announcement = String.format("%d", newValue);
            if (mAllowAutoAdvance && autoAdvance) {
                setCurrentItemShowing(MINUTE_INDEX, true, true, false);
                announcement += ". " + mSelectMinutes;
            } else {
                mTimePicker.setContentDescription(mHourPickerDescription + ": " + newValue);
            }

            Utils.tryAccessibilityAnnounce(mTimePicker, announcement);
        } else if (pickerIndex == MINUTE_INDEX) {
            setMinute(newValue);
            if (mEnableSeconds && mAllowAutoAdvance && autoAdvance) {
                setCurrentItemShowing(SECOND_INDEX, true, true, false);
                Utils.tryAccessibilityAnnounce(mTimePicker, String.format("%02d", newValue));
            } else {
                mTimePicker.setContentDescription(mMinutePickerDescription + ": " + newValue);
            }

        } else if (pickerIndex == SECOND_INDEX) {
            setSecond(newValue);
            mTimePicker.setContentDescription(mSecondPickerDescription + ": " + newValue);
        } else if (pickerIndex == AMPM_INDEX) {
            updateAmPmDisplay(newValue);
        } else if (pickerIndex == ENABLE_PICKER_INDEX) {
            if (!isTypedTimeFullyLegal()) {
                mTypedTimes.clear();
            }
            finishKbMode(true);
        }
    }

    private void setHour(int value, boolean announce) {
        String format;
        if (mIs24HourMode) {
            format = "%02d";
        } else {
            format = "%d";
            value = value % 12;
            if (value == 0) {
                value = 12;
            }
        }

        CharSequence text = String.format(format, value);
        mHourView.setText(text);
        mHourSpaceView.setText(text);
        if (announce) {
            Utils.tryAccessibilityAnnounce(mTimePicker, text);
        }
    }

    private void setMinute(int value) {
        if (value == 60) {
            value = 0;
        }
        CharSequence text = String.format(Locale.getDefault(), "%02d", value);
        Utils.tryAccessibilityAnnounce(mTimePicker, text);
        mMinuteView.setText(text);
        mMinuteSpaceView.setText(text);
    }

    private void setSecond(int value) {
        if (value == 60) {
            value = 0;
        }
        CharSequence text = String.format(Locale.getDefault(), "%02d", value);
        Utils.tryAccessibilityAnnounce(mTimePicker, text);
        mSecondView.setText(text);
        mSecondSpaceView.setText(text);
    }

    // Show either Hours, Minutes or Seconds.
    private void setCurrentItemShowing(int index, boolean animateCircle, boolean delayLabelAnimate,
            boolean announce) {
        mTimePicker.setCurrentItemShowing(index, animateCircle);

        TextView labelToAnimate;
        if (index == HOUR_INDEX) {
            int hours = mTimePicker.getHours();
            if (!mIs24HourMode) {
                hours = hours % 12;
            }
            mTimePicker.setContentDescription(mHourPickerDescription + ": " + hours);
            if (announce) {
                Utils.tryAccessibilityAnnounce(mTimePicker, mSelectHours);
            }
            labelToAnimate = mHourView;
        } else if (index == MINUTE_INDEX) {
            int minutes = mTimePicker.getMinutes();
            mTimePicker.setContentDescription(mMinutePickerDescription + ": " + minutes);
            if (announce) {
                Utils.tryAccessibilityAnnounce(mTimePicker, mSelectMinutes);
            }
            labelToAnimate = mMinuteView;
        } else {
            int seconds = mTimePicker.getSeconds();
            mTimePicker.setContentDescription(mSecondPickerDescription + ": " + seconds);
            if (announce) {
                Utils.tryAccessibilityAnnounce(mTimePicker, mSelectSeconds);
            }
            labelToAnimate = mSecondView;
        }

        int hourColor = (index == HOUR_INDEX)? mSelectedColor : mUnselectedColor;
        int minuteColor = (index == MINUTE_INDEX)? mSelectedColor : mUnselectedColor;
        int secondColor = (index == SECOND_INDEX)? mSelectedColor : mUnselectedColor;
        mHourView.setTextColor(hourColor);
        mMinuteView.setTextColor(minuteColor);
        mSecondView.setTextColor(secondColor);

        ObjectAnimator pulseAnimator = Utils.getPulseAnimator(labelToAnimate, 0.85f, 1.1f);
        if (delayLabelAnimate) {
            pulseAnimator.setStartDelay(PULSE_ANIMATOR_DELAY);
        }
        pulseAnimator.start();
    }

    /**
     * For keyboard mode, processes key events.
     * @param keyCode the pressed key.
     * @return true if the key was successfully processed, false otherwise.
     */
    private boolean processKeyUp(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_BACK) {
            if(isCancelable()) dismiss();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_TAB) {
            if(mInKbMode) {
                if (isTypedTimeFullyLegal()) {
                    finishKbMode(true);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mInKbMode) {
                if (!isTypedTimeFullyLegal()) {
                    return true;
                }
                finishKbMode(false);
            }
            if (mCallback != null) {
                mCallback.onTimeSet(mTimePicker,
                        mTimePicker.getHours(), mTimePicker.getMinutes(), mTimePicker.getSeconds());
            }
            dismiss();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (mInKbMode) {
                if (!mTypedTimes.isEmpty()) {
                    int deleted = deleteLastTypedKey();
                    String deletedKeyStr;
                    if (deleted == getAmOrPmKeyCode(AM)) {
                        deletedKeyStr = mAmText;
                    } else if (deleted == getAmOrPmKeyCode(PM)) {
                        deletedKeyStr = mPmText;
                    } else {
                        deletedKeyStr = String.format("%d", getValFromKeyCode(deleted));
                    }
                    Utils.tryAccessibilityAnnounce(mTimePicker,
                            String.format(mDeletedKeyFormat, deletedKeyStr));
                    updateDisplay(true);
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_0 || keyCode == KeyEvent.KEYCODE_1
                || keyCode == KeyEvent.KEYCODE_2 || keyCode == KeyEvent.KEYCODE_3
                || keyCode == KeyEvent.KEYCODE_4 || keyCode == KeyEvent.KEYCODE_5
                || keyCode == KeyEvent.KEYCODE_6 || keyCode == KeyEvent.KEYCODE_7
                || keyCode == KeyEvent.KEYCODE_8 || keyCode == KeyEvent.KEYCODE_9
                || (!mIs24HourMode &&
                        (keyCode == getAmOrPmKeyCode(AM) || keyCode == getAmOrPmKeyCode(PM)))) {
            if (!mInKbMode) {
                if (mTimePicker == null) {
                    // Something's wrong, because time picker should definitely not be null.
                    Log.e(TAG, "Unable to initiate keyboard mode, TimePicker was null.");
                    return true;
                }
                mTypedTimes.clear();
                tryStartingKbMode(keyCode);
                return true;
            }
            // We're already in keyboard mode.
            if (addKeyIfLegal(keyCode)) {
                updateDisplay(false);
            }
            return true;
        }
        return false;
    }

    /**
     * Try to start keyboard mode with the specified key, as long as the timepicker is not in the
     * middle of a touch-event.
     * @param keyCode The key to use as the first press. Keyboard mode will not be started if the
     * key is not legal to start with. Or, pass in -1 to get into keyboard mode without a starting
     * key.
     */
    private void tryStartingKbMode(int keyCode) {
        if (mTimePicker.trySettingInputEnabled(false) &&
                (keyCode == -1 || addKeyIfLegal(keyCode))) {
            mInKbMode = true;
            mOkButton.setEnabled(false);
            updateDisplay(false);
        }
    }

    private boolean addKeyIfLegal(int keyCode) {
        // If we're in 24hour mode, we'll need to check if the input is full. If in AM/PM mode,
        // we'll need to see if AM/PM have been typed.
        if ((mIs24HourMode && mTypedTimes.size() == (mEnableSeconds ? 6 : 4)) ||
                (!mIs24HourMode && isTypedTimeFullyLegal())) {
            return false;
        }

        mTypedTimes.add(keyCode);
        if (!isTypedTimeLegalSoFar()) {
            deleteLastTypedKey();
            return false;
        }

        int val = getValFromKeyCode(keyCode);
        Utils.tryAccessibilityAnnounce(mTimePicker, String.format("%d", val));
        // Automatically fill in 0's if AM or PM was legally entered.
        if (isTypedTimeFullyLegal()) {
            if (!mIs24HourMode && mTypedTimes.size() <= (mEnableSeconds ? 5 : 3)) {
                mTypedTimes.add(mTypedTimes.size() - 1, KeyEvent.KEYCODE_0);
                mTypedTimes.add(mTypedTimes.size() - 1, KeyEvent.KEYCODE_0);
            }
            mOkButton.setEnabled(true);
        }

        return true;
    }

    /**
     * Traverse the tree to see if the keys that have been typed so far are legal as is,
     * or may become legal as more keys are typed (excluding backspace).
     */
    private boolean isTypedTimeLegalSoFar() {
        Node node = mLegalTimesTree;
        for (int keyCode : mTypedTimes) {
            node = node.canReach(keyCode);
            if (node == null) {
                return false;
            }
        }
        return true;
    }

    /**s
     * Check if the time that has been typed so far is completely legal, as is.
     */
    private boolean isTypedTimeFullyLegal() {
        if (mIs24HourMode) {
            // For 24-hour mode, the time is legal if the hours and minutes are each legal. Note:
            // getEnteredTime() will ONLY call isTypedTimeFullyLegal() when NOT in 24hour mode.
            int[] values = getEnteredTime(null);
            return mEnableSeconds ?
                    (values[0] >= 0 && values[1] >= 0 && values[1] < 60 && values[2] >= 0 && values[2] < 60) :
                    (values[0] >= 0 && values[1] >= 0 && values[1] < 60);
        } else {
            // For AM/PM mode, the time is legal if it contains an AM or PM, as those can only be
            // legally added at specific times based on the tree's algorithm.
            return (mTypedTimes.contains(getAmOrPmKeyCode(AM)) ||
                    mTypedTimes.contains(getAmOrPmKeyCode(PM)));
        }
    }

    private int deleteLastTypedKey() {
        int deleted = mTypedTimes.remove(mTypedTimes.size() - 1);
        if (!isTypedTimeFullyLegal()) {
            mOkButton.setEnabled(false);
        }
        return deleted;
    }

    /**
     * Get out of keyboard mode. If there is nothing in typedTimes, revert to TimePicker's time.
     * @param updateDisplays If true, update the displays with the relevant time.
     */
    private void finishKbMode(boolean updateDisplays) {
        mInKbMode = false;
        if (!mTypedTimes.isEmpty()) {
            int values[] = getEnteredTime(null);
            if (mEnableSeconds) {
                mTimePicker.setTime(values[0], values[1], values[2]);
            } else {
                mTimePicker.setTime(values[0], values[1]);
            }
            if (!mIs24HourMode) {
                mTimePicker.setAmOrPm(values[3]);
            }
            mTypedTimes.clear();
        }
        if (updateDisplays) {
            updateDisplay(false);
            mTimePicker.trySettingInputEnabled(true);
        }
    }

    /**
     * Update the hours, minutes, seconds and AM/PM displays with the typed times. If the typedTimes is
     * empty, either show an empty display (filled with the placeholder text), or update from the
     * timepicker's values.
     * @param allowEmptyDisplay if true, then if the typedTimes is empty, use the placeholder text.
     * Otherwise, revert to the timepicker's values.
     */
    private void updateDisplay(boolean allowEmptyDisplay) {
        if (!allowEmptyDisplay && mTypedTimes.isEmpty()) {
            int hour = mTimePicker.getHours();
            int minute = mTimePicker.getMinutes();
            int second = mTimePicker.getSeconds();
            setHour(hour, true);
            setMinute(minute);
            setSecond(second);
            if (!mIs24HourMode) {
                updateAmPmDisplay(hour < 12? AM : PM);
            }
            setCurrentItemShowing(mTimePicker.getCurrentItemShowing(), true, true, true);
            mOkButton.setEnabled(true);
        } else {
            Boolean[] enteredZeros = {false, false, false};
            int[] values = getEnteredTime(enteredZeros);
            String hourFormat = enteredZeros[0] ? "%02d" : "%2d";
            String minuteFormat = enteredZeros[1] ? "%02d" : "%2d";
            String secondFormat = enteredZeros[2] ? "%02d" : "%2d";
            String hourStr = (values[0] == -1) ? mDoublePlaceholderText :
                String.format(hourFormat, values[0]).replace(' ', mPlaceholderText);
            String minuteStr = (values[1] == -1)? mDoublePlaceholderText :
                String.format(minuteFormat, values[1]).replace(' ', mPlaceholderText);
            String secondStr = (values[2] == -1)? mDoublePlaceholderText :
                String.format(secondFormat, values[2]).replace(' ', mPlaceholderText);
            mHourView.setText(hourStr);
            mHourSpaceView.setText(hourStr);
            mHourView.setTextColor(mUnselectedColor);
            mMinuteView.setText(minuteStr);
            mMinuteSpaceView.setText(minuteStr);
            mMinuteView.setTextColor(mUnselectedColor);
            mSecondView.setText(secondStr);
            mSecondSpaceView.setText(secondStr);
            mSecondView.setTextColor(mUnselectedColor);
            if (!mIs24HourMode) {
                updateAmPmDisplay(values[3]);
            }
        }
    }

    private static int getValFromKeyCode(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                return 0;
            case KeyEvent.KEYCODE_1:
                return 1;
            case KeyEvent.KEYCODE_2:
                return 2;
            case KeyEvent.KEYCODE_3:
                return 3;
            case KeyEvent.KEYCODE_4:
                return 4;
            case KeyEvent.KEYCODE_5:
                return 5;
            case KeyEvent.KEYCODE_6:
                return 6;
            case KeyEvent.KEYCODE_7:
                return 7;
            case KeyEvent.KEYCODE_8:
                return 8;
            case KeyEvent.KEYCODE_9:
                return 9;
            default:
                return -1;
        }
    }

    /**
     * Get the currently-entered time, as integer values of the hours, minutes and seconds typed.
     * @param enteredZeros A size-4 boolean array, which the caller should initialize, and which
     * may then be used for the caller to know whether zeros had been explicitly entered as either
     * hours of minutes. This is helpful for deciding whether to show the dashes, or actual 0's.
     * @return A size-3 int array. The first value will be the hours, the second value will be the
     * minutes, the third will be the seconds and the fourth will be either TimePickerDialog.AM
     * or TimePickerDialog.PM.
     */
    private int[] getEnteredTime(Boolean[] enteredZeros) {
        int amOrPm = -1;
        int startIndex = 1;
        if (!mIs24HourMode && isTypedTimeFullyLegal()) {
            int keyCode = mTypedTimes.get(mTypedTimes.size() - 1);
            if (keyCode == getAmOrPmKeyCode(AM)) {
                amOrPm = AM;
            } else if (keyCode == getAmOrPmKeyCode(PM)){
                amOrPm = PM;
            }
            startIndex = 2;
        }
        int minute = -1;
        int hour = -1;
        int second = -1;
        int shift = mEnableSeconds ? 2 : 0;
        for (int i = startIndex; i <= mTypedTimes.size(); i++) {
            int val = getValFromKeyCode(mTypedTimes.get(mTypedTimes.size() - i));
            if (mEnableSeconds) {
                if (i == startIndex) {
                    second = val;
                } else if (i == startIndex + 1) {
                    second += 10*val;
                    if (enteredZeros != null && val == 0) {
                        enteredZeros[2] = true;
                    }
                }
            }
            if (i == startIndex + shift) {
                minute = val;
            } else if (i == startIndex + shift + 1) {
                minute += 10*val;
                if (enteredZeros != null && val == 0) {
                    enteredZeros[1] = true;
                }
            } else if (i == startIndex + shift + 2) {
                hour = val;
            } else if (i == startIndex + shift + 3) {
                hour += 10*val;
                if (enteredZeros != null && val == 0) {
                    enteredZeros[0] = true;
                }
            }
        }

        return new int[] {hour, minute, second, amOrPm};
    }


    /**
     * Get the keycode value for AM and PM in the current language.
     */
    private int getAmOrPmKeyCode(int amOrPm) {
        // Cache the codes.
        if (mAmKeyCode == -1 || mPmKeyCode == -1) {
            // Find the first character in the AM/PM text that is unique.
            KeyCharacterMap kcm = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
            char amChar;
            char pmChar;
            for (int i = 0; i < Math.max(mAmText.length(), mPmText.length()); i++) {
                amChar = mAmText.toLowerCase(Locale.getDefault()).charAt(i);
                pmChar = mPmText.toLowerCase(Locale.getDefault()).charAt(i);
                if (amChar != pmChar) {
                    KeyEvent[] events = kcm.getEvents(new char[]{amChar, pmChar});
                    // There should be 4 events: a down and up for both AM and PM.
                    if (events != null && events.length == 4) {
                        mAmKeyCode = events[0].getKeyCode();
                        mPmKeyCode = events[2].getKeyCode();
                    } else {
                        Log.e(TAG, "Unable to find keycodes for AM and PM.");
                    }
                    break;
                }
            }
        }
        if (amOrPm == AM) {
            return mAmKeyCode;
        } else if (amOrPm == PM) {
            return mPmKeyCode;
        }

        return -1;
    }

    /**
     * Create a tree for deciding what keys can legally be typed.
     */
    private void generateLegalTimesTree() {
        // Create a quick cache of numbers to their keycodes.
        int k0 = KeyEvent.KEYCODE_0;
        int k1 = KeyEvent.KEYCODE_1;
        int k2 = KeyEvent.KEYCODE_2;
        int k3 = KeyEvent.KEYCODE_3;
        int k4 = KeyEvent.KEYCODE_4;
        int k5 = KeyEvent.KEYCODE_5;
        int k6 = KeyEvent.KEYCODE_6;
        int k7 = KeyEvent.KEYCODE_7;
        int k8 = KeyEvent.KEYCODE_8;
        int k9 = KeyEvent.KEYCODE_9;

        // The root of the tree doesn't contain any numbers.
        mLegalTimesTree = new Node();
        if (mIs24HourMode) {
            // We'll be re-using these nodes, so we'll save them.
            Node minuteFirstDigit = new Node(k0, k1, k2, k3, k4, k5);
            Node minuteSecondDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
            // The first digit must be followed by the second digit.
            minuteFirstDigit.addChild(minuteSecondDigit);

            if (mEnableSeconds) {
                Node secondsFirstDigit = new Node(k0, k1, k2, k3, k4, k5);
                Node secondsSecondDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
                secondsFirstDigit.addChild(secondsSecondDigit);

                // Minutes can be followed by seconds.
                minuteSecondDigit.addChild(secondsFirstDigit);
            }

            // The first digit may be 0-1.
            Node firstDigit = new Node(k0, k1);
            mLegalTimesTree.addChild(firstDigit);

            // When the first digit is 0-1, the second digit may be 0-5.
            Node secondDigit = new Node(k0, k1, k2, k3, k4, k5);
            firstDigit.addChild(secondDigit);
            // We may now be followed by the first minute digit. E.g. 00:09, 15:58.
            secondDigit.addChild(minuteFirstDigit);

            // When the first digit is 0-1, and the second digit is 0-5, the third digit may be 6-9.
            Node thirdDigit = new Node(k6, k7, k8, k9);
            // The time must now be finished. E.g. 0:55, 1:08.
            secondDigit.addChild(thirdDigit);

            // When the first digit is 0-1, the second digit may be 6-9.
            secondDigit = new Node(k6, k7, k8, k9);
            firstDigit.addChild(secondDigit);
            // We must now be followed by the first minute digit. E.g. 06:50, 18:20.
            secondDigit.addChild(minuteFirstDigit);

            // The first digit may be 2.
            firstDigit = new Node(k2);
            mLegalTimesTree.addChild(firstDigit);

            // When the first digit is 2, the second digit may be 0-3.
            secondDigit = new Node(k0, k1, k2, k3);
            firstDigit.addChild(secondDigit);
            // We must now be followed by the first minute digit. E.g. 20:50, 23:09.
            secondDigit.addChild(minuteFirstDigit);

            // When the first digit is 2, the second digit may be 4-5.
            secondDigit = new Node(k4, k5);
            firstDigit.addChild(secondDigit);
            // We must now be followd by the last minute digit. E.g. 2:40, 2:53.
            secondDigit.addChild(minuteSecondDigit);

            // The first digit may be 3-9.
            firstDigit = new Node(k3, k4, k5, k6, k7, k8, k9);
            mLegalTimesTree.addChild(firstDigit);
            // We must now be followed by the first minute digit. E.g. 3:57, 8:12.
            firstDigit.addChild(minuteFirstDigit);
        } else {
            // We'll need to use the AM/PM node a lot.
            // Set up AM and PM to respond to "a" and "p".
            Node ampm = new Node(getAmOrPmKeyCode(AM), getAmOrPmKeyCode(PM));

            // Seconds will be used a few times as well, if enabled.
            Node secondsFirstDigit = new Node(k0, k1, k2, k3, k4, k5);
            Node secondsSecondDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
            secondsSecondDigit.addChild(ampm);
            secondsFirstDigit.addChild(secondsSecondDigit);

            // The first hour digit may be 1.
            Node firstDigit = new Node(k1);
            mLegalTimesTree.addChild(firstDigit);
            // We'll allow quick input of on-the-hour times. E.g. 1pm.
            firstDigit.addChild(ampm);

            // When the first digit is 1, the second digit may be 0-2.
            Node secondDigit = new Node(k0, k1, k2);
            firstDigit.addChild(secondDigit);
            // Also for quick input of on-the-hour times. E.g. 10pm, 12am.
            secondDigit.addChild(ampm);

            // When the first digit is 1, and the second digit is 0-2, the third digit may be 0-5.
            Node thirdDigit = new Node(k0, k1, k2, k3, k4, k5);
            secondDigit.addChild(thirdDigit);
            // The time may be finished now. E.g. 1:02pm, 1:25am.
            thirdDigit.addChild(ampm);

            // When the first digit is 1, the second digit is 0-2, and the third digit is 0-5,
            // the fourth digit may be 0-9.
            Node fourthDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
            thirdDigit.addChild(fourthDigit);
            // The time must be finished now if seconds disabled. E.g. 10:49am, 12:40pm.
            fourthDigit.addChild(ampm);

            // When the first digit is 1, the second digit is 0-2, and the third digit is 0-5,
            // and fourth digit is 0-9, we may add seconds if enabled.
            if (mEnableSeconds) {
                // The time must be finished now. E.g. 10:49:01am, 12:40:59pm.
                fourthDigit.addChild(secondsFirstDigit);
            }

            // When the first digit is 1, and the second digit is 0-2, the third digit may be 6-9.
            thirdDigit = new Node(k6, k7, k8, k9);
            secondDigit.addChild(thirdDigit);
            // The time must be finished now. E.g. 1:08am, 1:26pm.
            thirdDigit.addChild(ampm);

            // When the first digit is 1, and the second digit is 0-2, and the third digit is 6-9,
            // we may add seconds is enabled.
            if (mEnableSeconds) {
                // The time must be finished now. E.g. 1:08:01am, 1:26:59pm.
                thirdDigit.addChild(secondsFirstDigit);
            }

            // When the first digit is 1, the second digit may be 3-5.
            secondDigit = new Node(k3, k4, k5);
            firstDigit.addChild(secondDigit);

            // When the first digit is 1, and the second digit is 3-5, the third digit may be 0-9.
            thirdDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
            secondDigit.addChild(thirdDigit);
            // The time must be finished now if seconds disabled. E.g. 1:39am, 1:50pm.
            thirdDigit.addChild(ampm);

            // When the first digit is 1, and the second digit is 3-5, and the third digit is 0-9,
            // we may add seconds if enabled.
            if (mEnableSeconds) {
                // The time must be finished now. E.g. 1:39:01am, 1:50:59pm.
                thirdDigit.addChild(secondsFirstDigit);
            }

            // The hour digit may be 2-9.
            firstDigit = new Node(k2, k3, k4, k5, k6, k7, k8, k9);
            mLegalTimesTree.addChild(firstDigit);
            // We'll allow quick input of on-the-hour-times. E.g. 2am, 5pm.
            firstDigit.addChild(ampm);

            // When the first digit is 2-9, the second digit may be 0-5.
            secondDigit = new Node(k0, k1, k2, k3, k4, k5);
            firstDigit.addChild(secondDigit);

            // When the first digit is 2-9, and the second digit is 0-5, the third digit may be 0-9.
            thirdDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
            secondDigit.addChild(thirdDigit);
            // The time must be finished now. E.g. 2:57am, 9:30pm.
            thirdDigit.addChild(ampm);

            // When the first digit is 2-9, and the second digit is 0-5, and third digit is 0-9, we
            // may add seconds if enabled.
            if (mEnableSeconds) {
                // The time must be finished now. E.g. 2:57:01am, 9:30:59pm.
                thirdDigit.addChild(secondsFirstDigit);
            }
        }
    }

    /**
     * Simple node class to be used for traversal to check for legal times.
     * mLegalKeys represents the keys that can be typed to get to the node.
     * mChildren are the children that can be reached from this node.
     */
    private static class Node {
        private int[] mLegalKeys;
        private ArrayList<Node> mChildren;

        public Node(int... legalKeys) {
            mLegalKeys = legalKeys;
            mChildren = new ArrayList<>();
        }

        public void addChild(Node child) {
            mChildren.add(child);
        }

        public boolean containsKey(int key) {
            for (int i = 0; i < mLegalKeys.length; i++) {
                if (mLegalKeys[i] == key) {
                    return true;
                }
            }
            return false;
        }

        public Node canReach(int key) {
            if (mChildren == null) {
                return null;
            }
            for (Node child : mChildren) {
                if (child.containsKey(key)) {
                    return child;
                }
            }
            return null;
        }
    }

    private class KeyboardListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return processKeyUp(keyCode);
            }
            return false;
        }
    }
}
