package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;

public class WorkTracker2Test {
    private Instant start;
    private Timeline timeline;
    private WorkTracker2 tracker;
    private TestWorkListener listener;

    @BeforeEach
    void setUp() {
        start = Instant.now();
        timeline = new Timeline(start);
        listener = new TestWorkListener();
        tracker = new WorkTracker2(timeline, listener);
    }

    @Test
    public void canClockIn() {
        tracker.clockIn();
        assertThat(listener.getClockedInTime()).isEqualTo(Optional.of(start));
    }

    @Test
    public void canClockOut() {
        tracker.clockIn();
        timeline.advanceBySeconds(10);
        tracker.clockOut();
        assertThat(listener.getClockedOutTime()).isEqualTo(Optional.of(start.plus(Duration.ofSeconds(10))));
    }

    @Test
    public void listenerNotifiedOfSession() {
        tracker.clockIn();
        timeline.advanceBySeconds(1);
        tracker.clockOut();
        assertThat(listener.getSessionStart()).isEqualTo(Optional.of(start));
        assertThat(listener.getSessionEnd()).isEqualTo(Optional.of(start.plus(Duration.ofSeconds(1))));
    }
}
