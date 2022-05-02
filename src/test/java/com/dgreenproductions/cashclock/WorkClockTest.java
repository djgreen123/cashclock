package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.fest.assertions.Assertions.assertThat;

public class WorkClockTest {

    private Instant start;
    private TestClock clock;
    private WorkClock workClock;

    @BeforeEach
    void setUp() {
        start = Instant.now();
        clock = new TestClock(start);
        workClock = new WorkClock(clock);
    }

    @Test
    public void totalTimeIncreasesWhenClockedIn() {
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(10));

        assertThat(workClock.getTotalTime()).isEqualTo(Duration.ofSeconds(10));

        clock.advance(Duration.ofMillis(20));

        assertThat(workClock.getTotalTime()).isEqualTo(Duration.ofSeconds(10).plus(Duration.ofMillis(20)));
    }

    @Test
    public void totalTimeStopsOnceClockedOut() {
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(10));
        workClock.clockOut();
        clock.advance(Duration.ofSeconds(5));
        assertThat(workClock.getTotalTime()).isEqualTo(Duration.ofSeconds(10));
        clock.advance(Duration.ofSeconds(2));
        assertThat(workClock.getTotalTime()).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    public void timeAccumulatesAcrossMultipleSessions() {
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(10));
        workClock.clockOut();
        clock.advance(Duration.ofSeconds(5));
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(5));
        workClock.clockOut();
        assertThat(workClock.getTotalTime()).isEqualTo(Duration.ofSeconds(15));
    }

    @Test
    public void hasNanosecondAccuracy() {
        workClock.clockIn();
        clock.advance(Duration.ofNanos(1));
        workClock.clockOut();
        assertThat(workClock.getTotalTime()).isEqualTo(Duration.ofNanos(1));
    }

    @Test
    public void getTotalTimeForToday() {
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(10));
        workClock.clockOut();
        assertThat(workClock.getTodaysTotalTime()).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    public void howMuchHaveIWorkedToday() {
        // clock in and out yesterday
        // and then clock in and out today
        // and check total time for today does not include yesterday's session

        Instant yesterdayStart = Instant.parse("2022-05-02T09:00:00.000Z");
        clock.setCurrentTime(yesterdayStart);
        workClock.clockIn();
        clock.advance(Duration.ofHours(1));
        workClock.clockOut();

        Instant todayStart = yesterdayStart.plus(Duration.ofDays(1));
        clock.setCurrentTime(todayStart);
        workClock.clockIn();
        clock.advance(Duration.ofHours(2));
        workClock.clockOut();

        assertThat(workClock.getTodaysTotalTime()).isEqualTo(Duration.ofHours(2));
    }
}
