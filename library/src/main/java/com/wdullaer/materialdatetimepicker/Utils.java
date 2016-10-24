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

package com.wdullaer.materialdatetimepicker;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

/**
 * Utility helper functions for time and date pickers.
 */
public class Utils {

    //public static final int MONDAY_BEFORE_JULIAN_EPOCH = Time.EPOCH_JULIAN_DAY - 3;
    public static final int PULSE_ANIMATOR_DURATION = 544;

    // Alpha level for time picker selection.
    public static final int SELECTED_ALPHA = 255;
    public static final int SELECTED_ALPHA_THEME_DARK = 255;
    // Alpha level for fully opaque.
    public static final int FULL_ALPHA = 255;

    public static boolean isJellybeanOrLater() {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Try to speak the specified text, for accessibility. Only available on JB or later.
     * @param text Text to announce.
     */
    @SuppressLint("NewApi")
    public static void tryAccessibilityAnnounce(View view, CharSequence text) {
        if (isJellybeanOrLater() && view != null && text != null) {
            view.announceForAccessibility(text);
        }
    }

    /**
     * Takes a number of weeks since the epoch and calculates the Julian day of
     * the Monday for that week.
     *
     * This assumes that the week containing the {@link Time#EPOCH_JULIAN_DAY}
     * is considered week 0. It returns the Julian day for the Monday
     * {@code week} weeks after the Monday of the week containing the epoch.
     *
     * @param week Number of weeks since the epoch
     * @return The julian day for the Monday of the given week since the epoch
     */
    /**
    public static int getJulianMondayFromWeeksSinceEpoch(int week) {
        return MONDAY_BEFORE_JULIAN_EPOCH + week * 7;
    }
     */

    /**
     * Returns the week since {@link Time#EPOCH_JULIAN_DAY} (Jan 1, 1970)
     * adjusted for first day of week.
     *
     * This takes a julian day and the week start day and calculates which
     * week since {@link Time#EPOCH_JULIAN_DAY} that day occurs in, starting
     * at 0. *Do not* use this to compute the ISO week number for the year.
     *
     * @param julianDay The julian day to calculate the week number for
     * @param firstDayOfWeek Which week day is the first day of the week,
     *          see {@link Time#SUNDAY}
     * @return Weeks since the epoch
     */
    /**
    public static int getWeeksSinceEpochFromJulianDay(int julianDay, int firstDayOfWeek) {
        int diff = Time.THURSDAY - firstDayOfWeek;
        if (diff < 0) {
            diff += 7;
        }
        int refDay = Time.EPOCH_JULIAN_DAY - diff;
        return (julianDay - refDay) / 7;
    }
     */

    /**
     * Render an animator to pulsate a view in place.
     * @param labelToAnimate the view to pulsate.
     * @return The animator object. Use .start() to begin.
     */
    public static ObjectAnimator getPulseAnimator(View labelToAnimate, float decreaseRatio,
            float increaseRatio) {
        Keyframe k0 = Keyframe.ofFloat(0f, 1f);
        Keyframe k1 = Keyframe.ofFloat(0.275f, decreaseRatio);
        Keyframe k2 = Keyframe.ofFloat(0.69f, increaseRatio);
        Keyframe k3 = Keyframe.ofFloat(1f, 1f);

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofKeyframe("scaleX", k0, k1, k2, k3);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofKeyframe("scaleY", k0, k1, k2, k3);
        ObjectAnimator pulseAnimator =
                ObjectAnimator.ofPropertyValuesHolder(labelToAnimate, scaleX, scaleY);
        pulseAnimator.setDuration(PULSE_ANIMATOR_DURATION);

        return pulseAnimator;
    }

    /**
     * Convert Dp to Pixel
     */
    @SuppressWarnings("unused")
    public static int dpToPx(float dp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    /**
     * Gets the colorAccent from the current context, if possible/available
     * @param context The context to use as reference for the color
     * @return the accent color of the current context
     */
    public static int getAccentColorFromThemeIfAvailable(Context context) {
        TypedValue typedValue = new TypedValue();
        // First, try the android:colorAccent
        if (Build.VERSION.SDK_INT >= 21) {
            context.getTheme().resolveAttribute(android.R.attr.colorAccent, typedValue, true);
            return typedValue.data;
        }
        // Next, try colorAccent from support lib
        int colorAccentResId = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        if (colorAccentResId != 0 && context.getTheme().resolveAttribute(colorAccentResId, typedValue, true)) {
            return typedValue.data;
        }
        // Return the value in mdtp_accent_color
        return ContextCompat.getColor(context, R.color.mdtp_accent_color);
    }

    /**
     * Gets dialog type (Light/Dark) from current theme
     * @param context The context to use as reference for the boolean
     * @param current Default value to return if cannot resolve the attribute
     * @return true if dark mode, false if light.
     */
    public static boolean isDarkTheme(Context context, boolean current) {
        return resolveBoolean(context, R.attr.mdtp_theme_dark, current);
    }

    /**
     * Gets the required boolean value from the current context, if possible/available
     * @param context The context to use as reference for the boolean
     * @param attr Attribute id to resolve
     * @param fallback Default value to return if no value is specified in theme
     * @return the boolean value from current theme
     */
    private static boolean resolveBoolean(Context context, @AttrRes int attr, boolean fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getBoolean(0, fallback);
        } finally {
            a.recycle();
        }
    }

    /**
     * Indicates if app is running on Android TV device
     * @param context The context to use as reference for the test
     * @return true if device is Android TV, false if other device type
     */
    public static boolean isTv(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }
}
