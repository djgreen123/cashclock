package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.fest.assertions.Assertions.assertThat;

public class WorkTracker2Test {

    @Test
    public void canClockIn() {
        Instant start = Instant.now();
        Timeline timeline = new Timeline(start);
        TestWorkListener listener = new TestWorkListener();
        WorkTracker2 tracker = new WorkTracker2(timeline, listener);
        tracker.clockIn();
        assertThat(listener.isClockedIn()).isTrue();
        assertThat(listener.getClockedInTime()).isEqualTo(start);
    }

}
