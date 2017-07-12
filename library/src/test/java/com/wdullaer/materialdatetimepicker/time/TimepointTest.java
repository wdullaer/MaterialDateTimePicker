package com.wdullaer.materialdatetimepicker.time;


import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class TimepointTest {

    @Test
    public void timepointsWithSameFieldsShouldHaveSameHashCode() {
        Timepoint first = new Timepoint(12, 0, 0);
        Timepoint second = new Timepoint(12, 0, 0);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void timepointsWithSameFieldsShouldBeEquals() {
        Timepoint first = new Timepoint(12, 0, 0);
        Timepoint second = new Timepoint(12, 0, 0);
        assertEquals(first, second);
    }

    @Test
    public void timepointsWithSameFieldsShouldBeDistinctInHashSet() {
        HashSet<Timepoint> timepoints = new HashSet<>(4);
        timepoints.add(new Timepoint(12, 0, 0));
        timepoints.add(new Timepoint(12, 0, 0));
        timepoints.add(new Timepoint(12, 0, 0));
        timepoints.add(new Timepoint(12, 0, 0));
        assertEquals(timepoints.size(), 1);
    }

    @Test
    public void timepointsWithDifferentFieldsShouldNotBeDistinctInHashSet() {
        HashSet<Timepoint> timepoints = new HashSet<>(4);
        timepoints.add(new Timepoint(12, 1, 0));
        timepoints.add(new Timepoint(12, 2, 0));
        timepoints.add(new Timepoint(12, 3, 0));
        timepoints.add(new Timepoint(12, 4, 0));
        assertEquals(timepoints.size(), 4);
    }
}
