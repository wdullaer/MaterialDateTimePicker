package com.wdullaer.materialdatetimepicker.time;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

import static com.wdullaer.materialdatetimepicker.time.TimePickerDialog.HOUR_INDEX;
import static com.wdullaer.materialdatetimepicker.time.TimePickerDialog.MINUTE_INDEX;

/**
 * An implementation of TimepointLimiter which implements the most common ways to restrict Timepoints
 * in a TimePickerDialog
 * Created by wdullaer on 20/06/17.
 */

class DefaultTimepointLimiter implements TimepointLimiter {
    private Timepoint[] mSelectableTimes;
    private Timepoint mMinTime;
    private Timepoint mMaxTime;

    DefaultTimepointLimiter() {}

    @SuppressWarnings("WeakerAccess")
    public DefaultTimepointLimiter(Parcel in) {
        mMinTime = in.readParcelable(Timepoint.class.getClassLoader());
        mMaxTime = in.readParcelable(Timepoint.class.getClassLoader());
        mSelectableTimes = (Timepoint[]) in.readParcelableArray(Timepoint[].class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(mMinTime, flags);
        out.writeParcelable(mMaxTime, flags);
        out.writeParcelableArray(mSelectableTimes, flags);
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
        mSelectableTimes = selectableTimes;
        Arrays.sort(mSelectableTimes);
    }

    @Override
    public boolean isOutOfRange(@Nullable Timepoint current, int index) {
        if(current == null) return false;

        if(index == HOUR_INDEX) {
            if(mMinTime != null && mMinTime.getHour() > current.getHour()) return true;

            if(mMaxTime != null && mMaxTime.getHour()+1 <= current.getHour()) return true;

            if(mSelectableTimes != null) {
                for(Timepoint t : mSelectableTimes) {
                    if(t.getHour() == current.getHour()) return false;
                }
                return true;
            }

            return false;
        }
        else if(index == MINUTE_INDEX) {
            if(mMinTime != null) {
                Timepoint roundedMin = new Timepoint(mMinTime.getHour(), mMinTime.getMinute());
                if (roundedMin.compareTo(current) > 0) return true;
            }

            if(mMaxTime != null) {
                Timepoint roundedMax = new Timepoint(mMaxTime.getHour(), mMaxTime.getMinute(), 59);
                if (roundedMax.compareTo(current) < 0) return true;
            }

            if(mSelectableTimes != null) {
                for(Timepoint t : mSelectableTimes) {
                    if(t.getHour() == current.getHour() && t.getMinute() == current.getMinute()) return false;
                }
                return true;
            }

            return false;
        }
        else return isOutOfRange(current);
    }

    public boolean isOutOfRange(@NonNull Timepoint current) {
        if(mMinTime != null && mMinTime.compareTo(current) > 0) return true;

        if(mMaxTime != null && mMaxTime.compareTo(current) < 0) return true;

        if(mSelectableTimes != null) return !Arrays.asList(mSelectableTimes).contains(current);

        return false;
    }

    @Override
    public boolean isAmDisabled() {
        Timepoint midday = new Timepoint(12);

        if(mMinTime != null && mMinTime.compareTo(midday) > 0) return true;

        if(mSelectableTimes != null) {
            for(Timepoint t : mSelectableTimes) if(t.compareTo(midday) < 0) return false;
            return true;
        }

        return false;
    }

    @Override
    public boolean isPmDisabled() {
        Timepoint midday = new Timepoint(12);

        if(mMaxTime != null && mMaxTime.compareTo(midday) < 0) return true;

        if(mSelectableTimes != null) {
            for(Timepoint t : mSelectableTimes) if(t.compareTo(midday) >= 0) return false;
            return true;
        }

        return false;
    }

    @Override
    public @NonNull Timepoint roundToNearest(@NonNull Timepoint time,@Nullable Timepoint.TYPE type) {
        if(mMinTime != null && mMinTime.compareTo(time) > 0) return mMinTime;

        if(mMaxTime != null && mMaxTime.compareTo(time) < 0) return mMaxTime;
        if(mSelectableTimes != null) {
            int currentDistance = Integer.MAX_VALUE;
            Timepoint output = time;
            for(Timepoint t : mSelectableTimes) {
                // type == null: no restrictions
                // type == HOUR: do not change the hour
                if (type == Timepoint.TYPE.HOUR && t.getHour() != time.getHour()) continue;
                // type == MINUTE: do not change hour or minute
                if (type == Timepoint.TYPE.MINUTE  && t.getHour() != time.getHour() && t.getMinute() != time.getMinute()) continue;
                // type == SECOND: cannot change anything, return input
                if (type == Timepoint.TYPE.SECOND) return time;
                int newDistance = Math.abs(t.compareTo(time));
                if (newDistance < currentDistance) {
                    currentDistance = newDistance;
                    output = t;
                }
                else break;
            }
            return output;
        }

        return time;
    }
}
