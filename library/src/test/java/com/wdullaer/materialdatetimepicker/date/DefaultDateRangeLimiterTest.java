package com.wdullaer.materialdatetimepicker.date;

import com.wdullaer.materialdatetimepicker.Utils;

import org.junit.Test;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Unit tests for the default DateRangeLimiter implementation
 * Primarily used to assert that the rounding logic functions properly
 * 
 * Created by wdullaer on 14/04/17.
 */
public class DefaultDateRangeLimiterTest {
    final private DatePickerController controller = new DatePickerController() {
        @Override
        public void onYearSelected(int year) {}

        @Override
        public void onDayOfMonthSelected(int year, int month, int day) {}

        @Override
        public void registerOnDateChangedListener(DatePickerDialog.OnDateChangedListener listener) {}

        @Override
        public void unregisterOnDateChangedListener(DatePickerDialog.OnDateChangedListener listener) {}

        @Override
        public MonthAdapter.CalendarDay getSelectedDay() {
            return new MonthAdapter.CalendarDay(Calendar.getInstance(), TimeZone.getDefault());
        }

        @Override
        public boolean isThemeDark() {
            return false;
        }

        @Override
        public int getAccentColor() {
            return 0;
        }

        @Override
        public boolean isHighlighted(int year, int month, int day) {
            return false;
        }

        @Override
        public int getFirstDayOfWeek() {
            return 0;
        }

        @Override
        public int getMinYear() {
            return 0;
        }

        @Override
        public int getMaxYear() {
            return 0;
        }

        @Override
        public Calendar getStartDate() {
            return Calendar.getInstance();
        }

        @Override
        public Calendar getEndDate() {
            return Calendar.getInstance();
        }

        @Override
        public boolean isOutOfRange(int year, int month, int day) {
            return false;
        }

        @Override
        public void tryVibrate() {}

        @Override
        public TimeZone getTimeZone() {
            return TimeZone.getDefault();
        }
    };

    // getters
    @Test
    public void getSelectableDaysShouldHaveDatesTrimmedToMidnight() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[3];
        for (int i = 0;i < days.length; i++) {
            Calendar day = Calendar.getInstance();
            day.set(Calendar.YEAR, 1999 + i);
            day.set(Calendar.HOUR_OF_DAY, 2);
            day.set(Calendar.MINUTE, 10);
            day.set(Calendar.SECOND, 30);
            day.set(Calendar.MILLISECOND, 25);
            days[i] = day;
        }

        limiter.setSelectableDays(days);
        Calendar[] selectableDays = limiter.getSelectableDays();

        Assert.assertNotNull(selectableDays);
        Assert.assertEquals(days.length, selectableDays.length);
        for (Calendar selectableDay : selectableDays) {
            Assert.assertEquals(selectableDay.get(Calendar.HOUR_OF_DAY), 0);
            Assert.assertEquals(selectableDay.get(Calendar.MINUTE), 0);
            Assert.assertEquals(selectableDay.get(Calendar.SECOND), 0);
            Assert.assertEquals(selectableDay.get(Calendar.MILLISECOND), 0);
        }
    }

    @Test
    public void getDisabledDaysShouldHaveDatesTrimmedToMidnight() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[3];
        for (int i = 0;i < days.length; i++) {
            Calendar day = Calendar.getInstance();
            day.set(Calendar.YEAR, 1999 + i);
            day.set(Calendar.HOUR_OF_DAY, 2);
            day.set(Calendar.MINUTE, 10);
            day.set(Calendar.SECOND, 30);
            day.set(Calendar.MILLISECOND, 25);
            days[i] = day;
        }

        limiter.setDisabledDays(days);
        Calendar[] disabledDays = limiter.getDisabledDays();

        Assert.assertNotNull(disabledDays);
        Assert.assertEquals(days.length, disabledDays.length);
        for (Calendar selectableDay : disabledDays) {
            Assert.assertEquals(selectableDay.get(Calendar.HOUR_OF_DAY), 0);
            Assert.assertEquals(selectableDay.get(Calendar.MINUTE), 0);
            Assert.assertEquals(selectableDay.get(Calendar.SECOND), 0);
            Assert.assertEquals(selectableDay.get(Calendar.MILLISECOND), 0);
        }
    }

    @Test
    public void getMinDateShouldHaveDateTrimmedToMidnight() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 1999);
        day.set(Calendar.HOUR_OF_DAY, 2);
        day.set(Calendar.MINUTE, 10);
        day.set(Calendar.SECOND, 30);
        day.set(Calendar.MILLISECOND, 25);

        limiter.setMinDate(day);
        Calendar minDate = limiter.getMinDate();

        Assert.assertNotNull(minDate);
        Assert.assertEquals(minDate.get(Calendar.HOUR_OF_DAY), 0);
        Assert.assertEquals(minDate.get(Calendar.MINUTE), 0);
        Assert.assertEquals(minDate.get(Calendar.SECOND), 0);
        Assert.assertEquals(minDate.get(Calendar.MILLISECOND), 0);
    }

    @Test
    public void getMaxDateShouldHaveDateTrimmedToMidnight() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 1999);
        day.set(Calendar.HOUR_OF_DAY, 2);
        day.set(Calendar.MINUTE, 10);
        day.set(Calendar.SECOND, 30);
        day.set(Calendar.MILLISECOND, 25);

        limiter.setMaxDate(day);
        Calendar maxDate = limiter.getMaxDate();

        Assert.assertNotNull(maxDate);
        Assert.assertEquals(maxDate.get(Calendar.HOUR_OF_DAY), 0);
        Assert.assertEquals(maxDate.get(Calendar.MINUTE), 0);
        Assert.assertEquals(maxDate.get(Calendar.SECOND), 0);
        Assert.assertEquals(maxDate.get(Calendar.MILLISECOND), 0);
    }

    // getStartDate()
    @Test
    public void getStartDateShouldReturnFirstSelectableDay() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[3];
        for (int i = 0; i < days.length; i++) {
            days[i] = Calendar.getInstance();
            days[i].set(Calendar.YEAR, 1999 + i);
        }

        limiter.setSelectableDays(days);

        Assert.assertEquals(limiter.getStartDate().getTimeInMillis(), days[0].getTimeInMillis());
    }

    @Test
    public void getStartDateShouldReturnMinDate() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar minDate = Calendar.getInstance();

        limiter.setMinDate(minDate);
        minDate = Utils.trimToMidnight(minDate);

        Assert.assertEquals(limiter.getStartDate().getTimeInMillis(), minDate.getTimeInMillis());
    }

    @Test
    public void getStartDateShouldReturnMinDateWhenAControllerIsSet() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        limiter.setController(controller);
        Calendar minDate = Calendar.getInstance();

        limiter.setMinDate(minDate);
        minDate = Utils.trimToMidnight(minDate);

        Assert.assertEquals(limiter.getStartDate().getTimeInMillis(), minDate.getTimeInMillis());
    }

    @Test
    public void getStartDateShouldPreferSelectableOverMinDate() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[3];
        for (int i = 0; i < days.length; i++) {
            days[i] = Calendar.getInstance();
            days[i].set(Calendar.YEAR, 1999 + i);
        }
        Calendar minDate = Calendar.getInstance();

        limiter.setSelectableDays(days);
        limiter.setMinDate(minDate);

        Assert.assertEquals(limiter.getStartDate().getTimeInMillis(), days[0].getTimeInMillis());
    }

    // getEndDate()
    @Test
    public void getEndDateShouldReturnLastSelectableDay() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[3];
        for (int i = 0; i < days.length; i++) {
            days[i] = Calendar.getInstance();
            days[i].set(Calendar.YEAR, 1999 + i);
        }

        limiter.setSelectableDays(days);

        Assert.assertEquals(limiter.getEndDate().getTimeInMillis(), days[days.length - 1].getTimeInMillis());
    }

    @Test
    public void getEndDateShouldReturnMaxDate() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar maxDate = Calendar.getInstance();

        limiter.setMaxDate(maxDate);
        maxDate = Utils.trimToMidnight(maxDate);

        Assert.assertEquals(limiter.getEndDate().getTimeInMillis(), maxDate.getTimeInMillis());
    }

    @Test
    public void getEndDateShouldReturnMaxDateWhenAControllerIsSet() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        limiter.setController(controller);
        Calendar maxDate = Calendar.getInstance();

        limiter.setMaxDate(maxDate);
        maxDate = Utils.trimToMidnight(maxDate);

        Assert.assertEquals(limiter.getEndDate().getTimeInMillis(), maxDate.getTimeInMillis());
    }

    @Test
    public void getEndDateShouldPreferSelectableOverMaxDate() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[3];
        for (int i = 0; i < days.length; i++) {
            days[i] = Calendar.getInstance();
            days[i].set(Calendar.YEAR, 1999 + i);
        }
        Calendar maxDate = Calendar.getInstance();

        limiter.setSelectableDays(days);
        limiter.setMinDate(maxDate);

        Assert.assertEquals(limiter.getEndDate().getTimeInMillis(), days[days.length - 1].getTimeInMillis());
    }

    // isOutOfRange()
    @Test
    public void isOutOfRangeShouldReturnTrueForDisabledDates() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[1];
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 1999);
        days[0] = day;

        limiter.setDisabledDays(days);
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        int dayNumber = day.get(Calendar.DAY_OF_MONTH);

        Assert.assertTrue(limiter.isOutOfRange(year, month, dayNumber));
    }

    @Test
    public void isOutOfRangeShouldReturnFalseForEnabledDates() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[1];
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 1999);
        days[0] = day;

        limiter.setSelectableDays(days);
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        int dayNumber = day.get(Calendar.DAY_OF_MONTH);

        Assert.assertFalse(limiter.isOutOfRange(year, month, dayNumber));
    }

    @Test
    public void isOutOfRangeShouldReturnTrueIfDateIsBeforeMin() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 1999);

        limiter.setMinDate(day);
        day.add(Calendar.DAY_OF_MONTH, -1);
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        int dayNumber = day.get(Calendar.DAY_OF_MONTH);

        Assert.assertTrue(limiter.isOutOfRange(year, month, dayNumber));
    }

    @Test
    public void isOutOfRangeShouldReturnTrueIfDateIsBeforeMinYear() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        int minYear = 1999;

        limiter.setYearRange(minYear, minYear + 1);

        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, minYear - 1);
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        int dayNumber = day.get(Calendar.DAY_OF_MONTH);

        Assert.assertTrue(limiter.isOutOfRange(year, month, dayNumber));
    }

    @Test
    public void isOutOfRangeShouldReturnTrueIfDateIsAfterMax() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 1999);

        limiter.setMaxDate(day);
        day.add(Calendar.DAY_OF_MONTH, 1);
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        int dayNumber = day.get(Calendar.DAY_OF_MONTH);

        Assert.assertTrue(limiter.isOutOfRange(year, month, dayNumber));
    }

    @Test
    public void isOutOfRangeShouldReturnTrueIfDateIsAfterMaxYear() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        int maxYear = 1999;

        limiter.setYearRange(maxYear - 1, maxYear);

        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, maxYear + 1);
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        int dayNumber = day.get(Calendar.DAY_OF_MONTH);

        Assert.assertTrue(limiter.isOutOfRange(year, month, dayNumber));
    }

    @Test
    public void isOutOfRangeShouldPreferDisabledOverEnabled() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[1];
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 1999);
        days[0] = day;

        limiter.setSelectableDays(days);
        limiter.setDisabledDays(days);
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        int dayNumber = day.get(Calendar.DAY_OF_MONTH);

        Assert.assertTrue(limiter.isOutOfRange(year, month, dayNumber));
    }

    // setToNearestDate()
    @Test
    public void setToNearestShouldReturnTheInputWhenValid() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar day = Calendar.getInstance();
        Calendar expected = (Calendar) day.clone();

        Assert.assertEquals(limiter.setToNearestDate(day).getTimeInMillis(), expected.getTimeInMillis());
    }

    @Test
    public void setToNearestShouldRoundDisabledDates() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[3];
        for (int i = 0;i < days.length; i++) {
            Calendar day = Calendar.getInstance();
            day.set(Calendar.YEAR, 1999 + i);
            day.set(Calendar.HOUR_OF_DAY, 2);
            day.set(Calendar.MINUTE, 10);
            day.set(Calendar.SECOND, 30);
            day.set(Calendar.MILLISECOND, 25);
            days[i] = day;
        }

        limiter.setDisabledDays(days);
        Calendar day = (Calendar) days[0].clone();

        Assert.assertNotSame(limiter.setToNearestDate(day).getTimeInMillis(), days[0].getTimeInMillis());
    }

    @Test
    public void setToNearestShouldRoundToMinDate() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.YEAR, 1999);

        limiter.setMinDate(minDate);

        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 1998);

        Assert.assertEquals(
                limiter.setToNearestDate(day).getTimeInMillis(),
                Utils.trimToMidnight(minDate).getTimeInMillis()
        );
    }

    @Test
    public void setToNearestShouldRoundToMaxDate() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.YEAR, 1999);

        limiter.setMaxDate(maxDate);

        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2000);

        Assert.assertEquals(
                limiter.setToNearestDate(day).getTimeInMillis(),
                Utils.trimToMidnight(maxDate).getTimeInMillis()
        );
    }

    @Test
    public void setToNearestShouldRoundToASelectableDay() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        Calendar[] days = new Calendar[3];
        for (int i = 0;i < days.length; i++) {
            Calendar day = Calendar.getInstance();
            day.set(Calendar.YEAR, 1999 + i);
            day.set(Calendar.HOUR_OF_DAY, 2);
            day.set(Calendar.MINUTE, 10);
            day.set(Calendar.SECOND, 30);
            day.set(Calendar.MILLISECOND, 25);
            days[i] = day;
        }

        limiter.setSelectableDays(days);
        Calendar day = Calendar.getInstance();

        Assert.assertTrue(Arrays.asList(days).contains(limiter.setToNearestDate(day)));
    }

    @Test
    public void setToNearestShouldRoundToASelectableDayWhenAControllerIsSet() {
        DefaultDateRangeLimiter limiter = new DefaultDateRangeLimiter();
        limiter.setController(controller);
        Calendar[] days = new Calendar[3];
        for (int i = 0;i < days.length; i++) {
            Calendar day = Calendar.getInstance();
            day.set(Calendar.YEAR, 1999 + i);
            day.set(Calendar.HOUR_OF_DAY, 2);
            day.set(Calendar.MINUTE, 10);
            day.set(Calendar.SECOND, 30);
            day.set(Calendar.MILLISECOND, 25);
            days[i] = day;
        }

        limiter.setSelectableDays(days);
        Calendar day = Calendar.getInstance();

        Assert.assertTrue(Arrays.asList(days).contains(limiter.setToNearestDate(day)));
    }
}