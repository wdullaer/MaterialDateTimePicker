package com.wdullaer.materialdatetimepicker.time;

/**
 * A collection of methods which need to be shared with all components of the TimePicker
 *
 * Created by wdullaer on 6/10/15.
 */
public interface TimePickerController {
    /**
     * @return boolean - true if the dark theme should be used
     */
    boolean isThemeDark();

    /**
     * @return boolean - true if 24 hour mode is used / false if AM/PM is used
     */
    boolean is24HourMode();

    /**
     * @return int - the accent color currently in use
     */
    int getAccentColor();

    /**
     * Request the device to vibrate
     */
    void tryVibrate();

    /**
     * @param hour int - the selected hour
     * @param minute int - the selected minute
     * @param second int - selected second
     * @return boolean - true if this is not a selectable value
     */
    boolean isOutOfRange(int hour, int minute, int second);

    /**
     * @return boolean - true if AM times are outside the range of valid selections
     */
    boolean isAmDisabled();

    /**
     * @return boolean - true if PM times are outside the range of valid selections
     */
    boolean isPmDisabled();
}
