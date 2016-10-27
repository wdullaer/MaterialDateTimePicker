package com.wdullaer.materialdatetimepicker.date;

import android.os.Parcel;
import android.os.Parcelable;

public class Badge implements Parcelable {
    public static final int TOP_RIGHT = 1;
    public static final int TOP_LEFT = 2;
    public static final int BOTTOM_RIGHT = 3;
    public static final int BOTTOM_LEFT = 4;

    private int location = TOP_LEFT;
    private int color = -1;
    private int displayInt = 0;

    public Badge(int displayInt){
        this.displayInt = displayInt;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getDisplayInt() {
        return displayInt;
    }

    public void setDisplayInt(int displayInt) {
        this.displayInt = displayInt;
    }

    //parcelling part
    public Badge(Parcel in){
        location = in.readInt();
        color = in.readInt();
        displayInt = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(location);
        dest.writeInt(color);
        dest.writeInt(displayInt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Badge createFromParcel(Parcel in) {
            return new Badge(in);
        }

        public Badge[] newArray(int size) {
            return new Badge[size];
        }
    };
}
