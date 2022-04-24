package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.fest.assertions.Assertions.assertThat;

public class WorkTrackerTest {
    private TestTimeLog timeLog;
    private Instant startTime;
    private Timeline timeline;
    private WorkTracker tracker;

    @BeforeEach
    public void before() {
        timeLog = new TestTimeLog();
        startTime = Instant.now();
        timeline = new Timeline(startTime);
        tracker = new WorkTracker(timeline, timeLog);
    }

    @Test
    public void canClockIn() {
        tracker.clockIn();

        timeline.advanceBy(Duration.ofMinutes(1));

        assertThat(timeLog.getEntries()).hasSize(1);

        TimeInterval entry = timeLog.getEntries().get(0);
        assertThat(entry.getFrom()).isEqualTo(startTime);
        assertThat(entry.getTo()).isEqualTo(startTime.plus(Duration.ofMinutes(1)));
    }

    @Test
    public void whilstClockedInLogsEveryMinute() {
        tracker.clockIn();
        timeline.advanceBy(Duration.ofMinutes(2));
        assertThat(timeLog.getEntries()).hasSize(2);
    }

    @Test
    public void stopsLoggingAfterClockOut() {
        tracker.clockIn();
        timeline.advanceBy(Duration.ofMinutes(1));
        tracker.clockOut();
        timeline.advanceBy(Duration.ofMinutes(1));
        assertThat(timeLog.getEntries()).hasSize(1);
    }

    @Test
    public void clockOutLogsFractionOfMostRecentMinutePeriod() {
        tracker.clockIn();
        timeline.advanceBy(Duration.ofSeconds(30));
        tracker.clockOut();
        assertThat(timeLog.getEntries()).hasSize(1);
    }

    @Test
    public void multipleClockOutsDoNotLogAdditionalTime() {
        tracker.clockIn();
        timeline.advanceBy(Duration.ofSeconds(10));
        tracker.clockOut();
        timeline.advanceBy(Duration.ofSeconds(10));
        tracker.clockOut();
        timeline.advanceBy(Duration.ofSeconds(10));
        tracker.clockOut();
        assertThat(timeLog.getEntries()).hasSize(1);
    }
}
