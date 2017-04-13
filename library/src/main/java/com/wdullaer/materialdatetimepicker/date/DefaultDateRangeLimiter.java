/*
 * Copyright (C) 2017 Wouter Dullaert
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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wdullaer.materialdatetimepicker.Utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.TreeSet;

class DefaultDateRangeLimiter implements DateRangeLimiter {
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;

    private DatePickerController mController;
    private int mMinYear = DEFAULT_START_YEAR;
    private int mMaxYear = DEFAULT_END_YEAR;
    private Calendar mMinDate;
    private Calendar mMaxDate;
    private TreeSet<Calendar> selectableDays = new TreeSet<>();
    private HashSet<Calendar> disabledDays = new HashSet<>();

    DefaultDateRangeLimiter(@NonNull DatePickerController controller) {
        mController = controller;
    }

    void setSelectedDays(@NonNull Calendar[] days) {
        for (Calendar selectableDay : days) Utils.trimToMidnight(selectableDay);
        this.selectableDays.addAll(Arrays.asList(days));
    }

    void setDisabledDays(@NonNull Calendar[] days) {
        for (Calendar disabledDay : days) Utils.trimToMidnight(disabledDay);
        this.disabledDays.addAll(Arrays.asList(days));
    }

    void setMinDate(@NonNull Calendar calendar) {
        mMinDate = Utils.trimToMidnight((Calendar) calendar.clone());
    }

    void setMaxDate(@NonNull Calendar calendar) {
        mMaxDate = Utils.trimToMidnight((Calendar) calendar.clone());
    }

    void setYearRange(int startYear, int endYear) {
        if (endYear < startYear) {
            throw new IllegalArgumentException("Year end must be larger than or equal to year start");
        }

        mMinYear = startYear;
        mMaxYear = endYear;
    }

    @Nullable Calendar getMinDate() {
        return mMinDate;
    }

    @Nullable Calendar getMaxDate() {
        return mMaxDate;
    }

    @Nullable Calendar[] getSelectableDays() {
         return selectableDays.isEmpty() ? null : selectableDays.toArray(new Calendar[0]);
    }

    @Nullable Calendar[] getDisabledDays() {
        return disabledDays.isEmpty() ? null : disabledDays.toArray(new Calendar[0]);
    }

    @Override
    public int getMinYear() {
        if (!selectableDays.isEmpty()) return selectableDays.first().get(Calendar.YEAR);
        // Ensure no years can be selected outside of the given minimum date
        return mMinDate != null && mMinDate.get(Calendar.YEAR) > mMinYear ? mMinDate.get(Calendar.YEAR) : mMinYear;
    }

    @Override
    public int getMaxYear() {
        if (!selectableDays.isEmpty()) return selectableDays.last().get(Calendar.YEAR);
        // Ensure no years can be selected outside of the given maximum date
        return mMaxDate != null && mMaxDate.get(Calendar.YEAR) < mMaxYear ? mMaxDate.get(Calendar.YEAR) : mMaxYear;
    }

    @Override
    public @NonNull Calendar getStartDate() {
        if (!selectableDays.isEmpty()) return selectableDays.first();
        if (mMinDate != null) return mMinDate;
        Calendar output = Calendar.getInstance(mController.getTimeZone());
        output.set(Calendar.YEAR, mMinYear);
        output.set(Calendar.DAY_OF_MONTH, 1);
        output.set(Calendar.MONTH, Calendar.JANUARY);
        return output;
    }

    @Override
    public @NonNull Calendar getEndDate() {
        if (!selectableDays.isEmpty()) return selectableDays.last();
        if (mMaxDate != null) return mMaxDate;
        Calendar output = Calendar.getInstance(mController.getTimeZone());
        output.set(Calendar.YEAR, mMaxYear);
        output.set(Calendar.DAY_OF_MONTH, 31);
        output.set(Calendar.MONTH, Calendar.DECEMBER);
        return output;
    }

    /**
     * @return true if the specified year/month/day are within the selectable days or the range set by minDate and maxDate.
     * If one or either have not been set, they are considered as Integer.MIN_VALUE and
     * Integer.MAX_VALUE.
     */
    @Override
    public boolean isOutOfRange(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);
        return isOutOfRange(date);
    }

    private boolean isOutOfRange(Calendar calendar) {
        Utils.trimToMidnight(calendar);
        return isDisabled(calendar) || !isSelectable(calendar);
    }

    private boolean isDisabled(Calendar c) {
        return disabledDays.contains(Utils.trimToMidnight(c)) || isBeforeMin(c) || isAfterMax(c);
    }

    private boolean isSelectable(Calendar c) {
        return selectableDays.isEmpty() || selectableDays.contains(Utils.trimToMidnight(c));
    }

    private boolean isBeforeMin(Calendar calendar) {
        return mMinDate != null && calendar.before(mMinDate);
    }

    private boolean isAfterMax(Calendar calendar) {
        return mMaxDate != null && calendar.after(mMaxDate);
    }

    @SuppressWarnings("UnnecessaryReturnStatement")
    public void setToNearestDate(Calendar calendar) {
        if (!selectableDays.isEmpty()) {
            Calendar newCalendar = null;
            Calendar higher = selectableDays.ceiling(calendar);
            Calendar lower = selectableDays.lower(calendar);

            if (higher == null && lower != null) newCalendar = lower;
            else if (lower == null && higher != null) newCalendar = higher;

            if (newCalendar != null || higher == null) {
                newCalendar = newCalendar == null ? calendar : newCalendar;
                newCalendar.setTimeZone(mController.getTimeZone());
                calendar.setTimeInMillis(newCalendar.getTimeInMillis());
                return;
            }

            long highDistance = Math.abs(higher.getTimeInMillis() - calendar.getTimeInMillis());
            long lowDistance = Math.abs(calendar.getTimeInMillis() - lower.getTimeInMillis());

            if (lowDistance < highDistance) calendar.setTimeInMillis(lower.getTimeInMillis());
            else calendar.setTimeInMillis(higher.getTimeInMillis());

            return;
        }

        if (!disabledDays.isEmpty()) {
            Calendar forwardDate = (Calendar) calendar.clone();
            Calendar backwardDate = (Calendar) calendar.clone();
            while (isDisabled(forwardDate) && isDisabled(backwardDate)) {
                forwardDate.add(Calendar.DAY_OF_MONTH, 1);
                backwardDate.add(Calendar.DAY_OF_MONTH, -1);
            }
            if (!isDisabled(backwardDate)) {
                calendar.setTimeInMillis(backwardDate.getTimeInMillis());
                return;
            }
            if (!isDisabled(forwardDate)) {
                calendar.setTimeInMillis(forwardDate.getTimeInMillis());
                return;
            }
        }


        if (isBeforeMin(calendar)) {
            calendar.setTimeInMillis(mMinDate.getTimeInMillis());
            return;
        }

        if (isAfterMax(calendar)) {
            calendar.setTimeInMillis(mMaxDate.getTimeInMillis());
            return;
        }
    }
}