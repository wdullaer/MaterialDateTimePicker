package com.wdullaer.materialdatetimepicker.time;

import android.os.Parcelable;

@SuppressWarnings("WeakerAccess")
public interface TimepointLimiter extends Parcelable {
    boolean isOutOfRange(Timepoint point, int index);

    boolean isAmDisabled();

    boolean isPmDisabled();

    Timepoint roundToNearest(Timepoint time, Timepoint.TYPE type);
}