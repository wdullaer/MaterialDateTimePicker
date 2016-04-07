package com.wdullaer.materialdatetimepicker.date;

/**
 * Created by DroidGreen on 4/6/16.
 */
public interface DayPickerViewInterface {
    int getMostVisiblePosition();
    void postSetSelection(final int position);
    void onDateChanged();
    void onChange();
}
