package com.wdullaer.materialdatetimepicker.time;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Simple utility class that represents a time in the day up to second precision
 * The time input is expected to use 24 hour mode.
 * Fields are modulo'd into their correct ranges.
 * It does not handle timezones.
 *
 * Created by wdullaer on 13/10/15.
 */
@SuppressWarnings("WeakerAccess")
public class Timepoint implements Parcelable, Comparable<Timepoint> {
    private int hour;
    private int minute;
    private int second;

    /**
     * No type
     */
    public static final int NONE = 0;
    /**
     * Hour type
     */
    public static final int HOUR = 1;
    /**
     * Minute type
     */
    public static final int MINUTE = 2;
    /**
     * Second type
     */
    public static final int SECOND = 3;

    /**
     * Type of HOUR, MINUTE, SECOND
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HOUR, MINUTE, SECOND, NONE})
    public @interface type{}

    public Timepoint(Timepoint time) {
        this(time.hour, time.minute, time.second);
    }

    public Timepoint(@IntRange(from=0, to=23) int hour,
                     @IntRange(from=0, to=59) int minute,
                     @IntRange(from=0, to=59) int second) {
        this.hour = hour % 24;
        this.minute = minute % 60;
        this.second = second % 60;
    }

    public Timepoint(@IntRange(from=0, to=23) int hour,
                     @IntRange(from=0, to=59) int minute) {
        this(hour, minute, 0);
    }

    public Timepoint(@IntRange(from=0, to=23) int hour) {
        this(hour, 0);
    }

    public Timepoint(Parcel in) {
        hour = in.readInt();
        minute = in.readInt();
        second = in.readInt();
    }

    @IntRange(from=0, to=23)
    public int getHour() {
        return hour;
    }

    @IntRange(from=0, to=59)
    public int getMinute() {
        return minute;
    }

    @IntRange(from=0, to=59)
    public int getSecond() {
        return second;
    }

    public boolean isAM() {
        return hour < 12;
    }

    public boolean isPM() {
        return !isAM();
    }

    public void setAM() {
        if(hour >= 12) hour = hour % 12;
    }

    public void setPM() {
        if(hour < 12) hour = (hour + 12) % 24;
    }

    @Override
    public int hashCode() {
        return 3600 * hour + 60 * minute + second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timepoint timepoint = (Timepoint) o;

        return hashCode() == timepoint.hashCode();
    }

    @Override
    public int compareTo(@NonNull Timepoint t) {
        return hashCode() - t.hashCode();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(hour);
        out.writeInt(minute);
        out.writeInt(second);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Timepoint> CREATOR
            = new Parcelable.Creator<Timepoint>() {
        public Timepoint createFromParcel(Parcel in) {
            return new Timepoint(in);
        }

        public Timepoint[] newArray(int size) {
            return new Timepoint[size];
        }
    };

    @Override
    public String toString() {
        return "" + hour + "h " + minute + "m " + second + "s";
    }
}
