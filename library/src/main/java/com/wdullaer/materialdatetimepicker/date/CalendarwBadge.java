package com.wdullaer.materialdatetimepicker.date;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarwBadge extends MonthAdapter.CalendarDay implements Parcelable{
    private ArrayList<Badge> badgeArrayList;

    public CalendarwBadge() {super();}

    public CalendarwBadge(long timeInMillis) {super(timeInMillis);}

    public CalendarwBadge(Calendar calendar) {super(calendar);}

    public CalendarwBadge(int year, int month, int day) {
        super(year, month, day);
    }

    public ArrayList<Badge> getBadgeArrayList() {
        return badgeArrayList;
    }

    /**
     * Set list of badges to be displayed.
     *
     * @param badgeArrayList List of badges. Maximum number of badges are 4.
     */
    public void setBadgeArrayList(ArrayList<Badge> badgeArrayList) {
        this.badgeArrayList = badgeArrayList;
    }

    //parcelling part
    public CalendarwBadge(Parcel in){
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        in.readTypedList(badgeArrayList, Badge.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeTypedList(badgeArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CalendarwBadge createFromParcel(Parcel in) {
            return new CalendarwBadge(in);
        }

        public CalendarwBadge[] newArray(int size) {
            return new CalendarwBadge[size];
        }
    };
}
