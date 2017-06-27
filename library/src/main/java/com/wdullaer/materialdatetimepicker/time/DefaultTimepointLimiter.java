package com.wdullaer.materialdatetimepicker.time;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.TreeSet;

import static com.wdullaer.materialdatetimepicker.time.TimePickerDialog.HOUR_INDEX;
import static com.wdullaer.materialdatetimepicker.time.TimePickerDialog.MINUTE_INDEX;

/**
 * An implementation of TimepointLimiter which implements the most common ways to restrict Timepoints
 * in a TimePickerDialog
 * Created by wdullaer on 20/06/17.
 */

class DefaultTimepointLimiter implements TimepointLimiter {
    private TreeSet<Timepoint> mSelectableTimes = new TreeSet<>();
    private Timepoint mMinTime;
    private Timepoint mMaxTime;

    DefaultTimepointLimiter() {}

    @SuppressWarnings("WeakerAccess")
    public DefaultTimepointLimiter(Parcel in) {
        mMinTime = in.readParcelable(Timepoint.class.getClassLoader());
        mMaxTime = in.readParcelable(Timepoint.class.getClassLoader());
        mSelectableTimes.addAll(Arrays.asList((Timepoint[]) in.readParcelableArray(Timepoint[].class.getClassLoader())));
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(mMinTime, flags);
        out.writeParcelable(mMaxTime, flags);
        out.writeParcelableArray((Timepoint[]) mSelectableTimes.toArray(), flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<DefaultTimepointLimiter> CREATOR
            = new Parcelable.Creator<DefaultTimepointLimiter>() {
        public DefaultTimepointLimiter createFromParcel(Parcel in) {
            return new DefaultTimepointLimiter(in);
        }

        public DefaultTimepointLimiter[] newArray(int size) {
            return new DefaultTimepointLimiter[size];
        }
    };

    void setMinTime(@NonNull Timepoint minTime) {
        if(mMaxTime != null && minTime.compareTo(mMaxTime) > 0)
            throw new IllegalArgumentException("Minimum time must be smaller than the maximum time");
        mMinTime = minTime;
    }

    void setMaxTime(@NonNull Timepoint maxTime) {
        if(mMinTime != null && maxTime.compareTo(mMinTime) < 0)
            throw new IllegalArgumentException("Maximum time must be greater than the minimum time");
        mMaxTime = maxTime;
    }

    void setSelectableTimes(@NonNull Timepoint[] selectableTimes) {
        mSelectableTimes.addAll(Arrays.asList(selectableTimes));
    }

    @Override
    public boolean isOutOfRange(@Nullable Timepoint current, int index) {
        if (current == null) return false;

        if (index == HOUR_INDEX) {
            if (mMinTime != null && mMinTime.getHour() > current.getHour()) return true;

            if (mMaxTime != null && mMaxTime.getHour()+1 <= current.getHour()) return true;

            if (!mSelectableTimes.isEmpty()) {
                Timepoint ceil = mSelectableTimes.ceiling(current);
                Timepoint floor = mSelectableTimes.floor(current);
                return !(ceil.getHour() == current.getHour() || floor.getHour() == current.getHour());
            }

            return false;
        }
        else if (index == MINUTE_INDEX) {
            if (mMinTime != null) {
                Timepoint roundedMin = new Timepoint(mMinTime.getHour(), mMinTime.getMinute());
                if (roundedMin.compareTo(current) > 0) return true;
            }

            if (mMaxTime != null) {
                Timepoint roundedMax = new Timepoint(mMaxTime.getHour(), mMaxTime.getMinute(), 59);
                if (roundedMax.compareTo(current) < 0) return true;
            }

            if (!mSelectableTimes.isEmpty()) {
                Timepoint ceil = mSelectableTimes.ceiling(current);
                Timepoint floor = mSelectableTimes.floor(current);
                if (ceil.getHour() == current.getHour() && ceil.getMinute() == current.getMinute()) return false;
                if (floor.getHour() == current.getHour() && ceil.getMinute() == current.getMinute()) return false;
                return true;
            }

            return false;
        }
        else return isOutOfRange(current);
    }

    public boolean isOutOfRange(@NonNull Timepoint current) {
        if (mMinTime != null && mMinTime.compareTo(current) > 0) return true;

        if (mMaxTime != null && mMaxTime.compareTo(current) < 0) return true;

        if (!mSelectableTimes.isEmpty()) return !mSelectableTimes.contains(current);

        return false;
    }

    @Override
    public boolean isAmDisabled() {
        Timepoint midday = new Timepoint(12);

        if (mMinTime != null && mMinTime.compareTo(midday) >= 0) return true;

        if (!mSelectableTimes.isEmpty()) return mSelectableTimes.first().compareTo(midday) >= 0;

        return false;
    }

    @Override
    public boolean isPmDisabled() {
        Timepoint midday = new Timepoint(12);

        if (mMaxTime != null && mMaxTime.compareTo(midday) < 0) return true;

        if (!mSelectableTimes.isEmpty()) return mSelectableTimes.last().compareTo(midday) < 0;

        return false;
    }

    @Override
    public @NonNull Timepoint roundToNearest(@NonNull Timepoint time,@Nullable Timepoint.TYPE type) {
        if (mMinTime != null && mMinTime.compareTo(time) > 0) return mMinTime;

        if (mMaxTime != null && mMaxTime.compareTo(time) < 0) return mMaxTime;

        if (!mSelectableTimes.isEmpty()) {
            // type == SECOND: cannot change anything, return input
            if (type == Timepoint.TYPE.SECOND) return time;

            Timepoint floor = mSelectableTimes.floor(time);
            Timepoint ceil = mSelectableTimes.ceiling(time);

            if (type == Timepoint.TYPE.HOUR) {
                if (floor.getHour() != time.getHour() && ceil.getHour() == time.getHour()) return ceil;
                if (floor.getHour() == time.getHour() && ceil.getHour() != time.getHour()) return floor;
                if (floor.getHour() != time.getHour() && ceil.getHour() != time.getHour()) return time;
            }

            if (type == Timepoint.TYPE.MINUTE) {
                if (floor.getHour() != time.getHour() && ceil.getHour() != time.getHour()) return time;
                if (floor.getHour() != time.getHour() && ceil.getHour() == time.getHour()) {
                    return ceil.getMinute() == time.getMinute() ? ceil : time;
                }
                if (floor.getHour() == time.getHour() && ceil.getHour() != time.getHour()) {
                    return floor.getMinute() == time.getMinute() ? floor : time;
                }
                if (floor.getMinute() != time.getMinute() && ceil.getMinute() == time.getMinute()) return ceil;
                if (floor.getMinute() == time.getMinute() && ceil.getMinute() != time.getMinute()) return floor;
                if (floor.getMinute() != time.getMinute() && ceil.getMinute() != time.getMinute()) return time;
            }

            int floorDist = floor == null ? Integer.MAX_VALUE : Math.abs(time.compareTo(floor));
            int ceilDist = ceil == null ? Integer.MAX_VALUE : Math.abs(time.compareTo(ceil));
            
            return floorDist < ceilDist ? floor : ceil;
        }

        return time;
    }
}
