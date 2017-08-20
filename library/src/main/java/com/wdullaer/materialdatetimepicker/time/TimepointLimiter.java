package com.wdullaer.materialdatetimepicker.time;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public interface TimepointLimiter extends Parcelable {
    boolean isOutOfRange(@Nullable Timepoint point, int index, @NonNull Timepoint.TYPE resolution);

    boolean isAmDisabled();

    boolean isPmDisabled();

    @NonNull Timepoint roundToNearest(
            @NonNull Timepoint time,
            @Nullable Timepoint.TYPE type,
            @NonNull Timepoint.TYPE resolution
    );
}