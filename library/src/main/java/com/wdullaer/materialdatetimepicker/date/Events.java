package com.wdullaer.materialdatetimepicker.date;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Events implements Parcelable{
    private int eventColor = -1;
    private boolean clickable;
    private ArrayList<CalendarwBadge> calList = null;

    public Events(boolean clickable){this.clickable = clickable;}

    public boolean isClickable() {return clickable;}

    /**
     * @param clickable Set true if user can select an event date. Set false, otherwise.
     */
    public void setClickable(boolean clickable) {this.clickable = clickable;}

    public ArrayList<CalendarwBadge> getCalList() {
        return calList;
    }

    /**
     * @param calList List of event details including dates and badges (if any).
     */
    public void setCalList(ArrayList<CalendarwBadge> calList) {
        this.calList = calList;
    }

    public int getEventColor(){return eventColor;}

    public void setEventColor(int eventColor){this.eventColor = eventColor;}

    //parcelling part
    public Events(Parcel in){
        eventColor = in.readInt();
        clickable = (Boolean) in.readValue( null );
        in.readTypedList(calList, CalendarwBadge.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(eventColor);
        dest.writeValue(clickable);
        dest.writeTypedList(calList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Events createFromParcel(Parcel in) {
            return new Events(in);
        }

        public Events[] newArray(int size) {return new Events[size];}
    };
}
