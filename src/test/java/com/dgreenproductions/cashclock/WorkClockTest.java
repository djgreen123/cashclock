package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class WorkClockTest {

    private Instant start;
    private TestClock clock;
    private WorkClock workClock;
    private WorkSessionLog log;

    @BeforeEach
    void setUp() {
        start = Instant.now();
        clock = new TestClock(start);
        log = new WorkSessionLog();
        workClock = new WorkClock(clock, log);
    }

    @Test
    public void notifiedOfSessionOnClockOut() {
        List<Instant> startTimes = new ArrayList<>();
        List<Instant> endTimes = new ArrayList<>();
        SessionListener listener = (start, end) -> {
            startTimes.add(start);
            endTimes.add(end);
        };
        workClock.addListener(listener);

        Instant start = clock.getCurrentTime();
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(10));
        Instant end = clock.getCurrentTime();
        workClock.clockOut();

        assertThat(startTimes).containsOnly(start);
        assertThat(endTimes).containsOnly(end);
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
        assertThat(workClock.getTotalTimeToday()).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    public void getTotalTimeToday() {
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

        assertThat(workClock.getTotalTimeToday()).isEqualTo(Duration.ofHours(2));
    }

    @Test
    public void getTotalTimeThisHour() {
        Instant start = Instant.parse("2022-05-02T09:00:00.000Z");
        clock.setCurrentTime(start);
        workClock.clockIn();
        clock.advance(Duration.ofMinutes(30));
        workClock.clockOut();

        clock.advance(Duration.ofMinutes(20)); // clocked in 9:50
        workClock.clockIn();
        clock.advance(Duration.ofMinutes(30));
        workClock.clockOut();

        // total time within the hour should include 10 mins from previous session
        assertThat(workClock.getTotalTimeThisHour()).isEqualTo(Duration.ofMinutes(20));
    }

    @Test
    public void getTotalTimeThisMinute() {
        Instant start = Instant.parse("2022-05-02T09:00:00.000Z");
        clock.setCurrentTime(start);
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(30));
        workClock.clockOut();

        clock.advance(Duration.ofSeconds(20));
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(30));
        workClock.clockOut();

        assertThat(workClock.getTotalTimeThisMinute()).isEqualTo(Duration.ofSeconds(20));
    }
}
