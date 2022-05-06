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
    public void dailyTotalCannotExceed8Hours() {
        Instant start = Instant.parse("2022-05-02T09:00:00.000Z");
        clock.setCurrentTime(start);
        workClock.clockIn();
        clock.advance(Duration.ofHours(9));
        workClock.clockOut();
        assertThat(workClock.getRunningTotalToday()).isEqualTo(Duration.ofHours(8));
    }

    @Test
    public void totalMustOnlyInclude8HoursMaxPerDay() {
        Instant start = Instant.parse("2022-05-02T09:00:00.000Z");
        clock.setCurrentTime(start);

        // day 1 - 9 hours logged
        workClock.clockIn();
        clock.advance(Duration.ofHours(9));
        workClock.clockOut();

        clock.advance(Duration.ofHours(12));
        workClock.clockIn();
        clock.advance(Duration.ofHours(10));
        workClock.clockOut();

        assertThat(workClock.getRunningTotalTime()).isEqualTo(Duration.ofHours(16));
    }

    @Test
    public void totalTimeIncreasesWhenClockedIn() {
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(10));

        assertThat(workClock.getRunningTotalTime()).isEqualTo(Duration.ofSeconds(10));

        clock.advance(Duration.ofMillis(20));

        assertThat(workClock.getRunningTotalTime()).isEqualTo(Duration.ofSeconds(10).plus(Duration.ofMillis(20)));
    }

    @Test
    public void totalTimeStopsOnceClockedOut() {
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(10));
        workClock.clockOut();
        clock.advance(Duration.ofSeconds(5));
        assertThat(workClock.getRunningTotalTime()).isEqualTo(Duration.ofSeconds(10));
        clock.advance(Duration.ofSeconds(2));
        assertThat(workClock.getRunningTotalTime()).isEqualTo(Duration.ofSeconds(10));
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
        assertThat(workClock.getRunningTotalTime()).isEqualTo(Duration.ofSeconds(15));
    }

    @Test
    public void hasNanosecondAccuracy() {
        workClock.clockIn();
        clock.advance(Duration.ofNanos(1));
        workClock.clockOut();
        assertThat(workClock.getRunningTotalTime()).isEqualTo(Duration.ofNanos(1));
    }

    @Test
    public void getTotalTimeForToday() {
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(10));
        workClock.clockOut();
        assertThat(workClock.getRunningTotalToday()).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    public void getRunningTotalThisHourWhenNothingLoggedToday() {
        Instant start = Instant.parse("2022-05-02T09:00:00.000Z");
        clock.setCurrentTime(start);
        workClock.clockIn();
        clock.advance(Duration.ofMinutes(10));
        assertThat(workClock.getRunningTimeThisHour()).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    public void getRunningTotalThisHourSpansMultipleSessions() {
        Instant start = Instant.parse("2022-05-02T09:55:00.000Z");
        clock.setCurrentTime(start);
        workClock.clockIn();
        clock.advance(Duration.ofMinutes(10));
        workClock.clockOut();
        clock.advance(Duration.ofSeconds(1));
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(19));
        assertThat(workClock.getRunningTimeThisHour()).isEqualTo(Duration.ofMinutes(5).plus(Duration.ofSeconds(19)));
    }

    @Test
    public void getRunningTotalThisHourWhen8HoursAlreadyLoggedBeforeCurrentHourStarts() {
        Instant start = Instant.parse("2022-05-02T09:00:00.000Z");
        clock.setCurrentTime(start);
        workClock.clockIn();
        clock.advance(Duration.ofHours(8));
        workClock.clockOut();

        clock.advance(Duration.ofSeconds(10));
        workClock.clockIn();
        clock.advance(Duration.ofSeconds(20));
        assertThat(workClock.getRunningTimeThisHour()).isEqualTo(Duration.ofSeconds(0));
    }

    @Test
    public void getRunningTotalThisHourWhen8HoursElapsesDuringThisHour() {
        Instant start = Instant.parse("2022-05-02T09:30:00.000Z");
        clock.setCurrentTime(start);
        workClock.clockIn();
        clock.advance(Duration.ofHours(7).plus(Duration.ofMinutes(50)));
        workClock.clockOut();

        // npw 5:20pm - worked 7.5 hours up to 5pm.  So 30 mins clockable time remains after 5pm
        clock.advance(Duration.ofSeconds(1));
        workClock.clockIn();
        clock.advance(Duration.ofMinutes(32));
        assertThat(workClock.getRunningTimeThisHour()).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    public void getRunningTotalThisHourClippedTo8HoursPerDay() {
        Instant start = Instant.parse("2022-05-02T09:00:00.000Z");
        clock.setCurrentTime(start);
        workClock.clockIn();
        clock.advance(Duration.ofHours(8));
        workClock.clockOut();
        clock.advance(Duration.ofMinutes(10));
        workClock.clockIn();
        clock.advance(Duration.ofMinutes(3));
        assertThat(workClock.getRunningTimeThisHour()).isEqualTo(Duration.ofMinutes(0));
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

        assertThat(workClock.getRunningTotalToday()).isEqualTo(Duration.ofHours(2));
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
        assertThat(workClock.getRunningTimeThisHour()).isEqualTo(Duration.ofMinutes(20));
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
