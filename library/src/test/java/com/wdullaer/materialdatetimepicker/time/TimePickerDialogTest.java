package com.wdullaer.materialdatetimepicker.time;

import com.wdullaer.materialdatetimepicker.enums.CalendarType;

import org.junit.Assert;
import org.junit.Test;

public class TimePickerDialogTest {
    @Test
    public void getPickerResolutionShouldReturnSecondIfSecondsAreEnabled() {
        TimePickerDialog tpd = TimePickerDialog.newInstance(null, CalendarType.GREGORIAN, false);
        tpd.enableSeconds(true);
        Assert.assertEquals(tpd.getPickerResolution(), Timepoint.TYPE.SECOND);
    }

    @Test
    public void getPickerResolutionShouldReturnMinuteIfMinutesAreEnabled() {
        TimePickerDialog tpd = TimePickerDialog.newInstance(null, CalendarType.GREGORIAN, false);
        tpd.enableSeconds(false);
        tpd.enableMinutes(true);
        Assert.assertEquals(tpd.getPickerResolution(), Timepoint.TYPE.MINUTE);
    }

    @Test
    public void getPickerResolutionShouldReturnHourIfMinutesAndSecondsAreDisabled() {
        TimePickerDialog tpd = TimePickerDialog.newInstance(null, CalendarType.GREGORIAN, false);
        tpd.enableMinutes(false);
        Assert.assertEquals(tpd.getPickerResolution(), Timepoint.TYPE.HOUR);
    }
}
