package com.wdullaer.materialdatetimepicker.time;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * Simple utility class that represents a time in the day up to second precision
 * The time input is expected to use 24 hour mode.
 * Fields are modulo'd into their correct ranges.
 * It does not handle timezones.
 *
 * Created by wdullaer on 13/10/15.
 */
public class Timepoint implements Parcelable, Comparable<Timepoint> {
    private int hour;
    private int minute;
    private int second;

    public enum TYPE {
        HOUR,
        MINUTE,
        SECOND
    }

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
        return hour >= 12 && hour < 24;
    }

    public void setAM() {
        if(hour >= 12) hour = hour % 12;
    }

    public void setPM() {
        if(hour < 12) hour = (hour + 12) % 24;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Timepoint other = (Timepoint) o;

            return other.getHour() == hour &&
                    other.getMinute() == minute &&
                    other.getSecond() == second;
        }
        catch(ClassCastException e) {
            return false;
        }
    }

    @Override
    public int compareTo(@NonNull Timepoint t) {
        return (this.hour - t.hour)*3600 + (this.minute - t.minute)*60 + (this.second - t.second);
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
