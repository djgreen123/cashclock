package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class WorkClock {
    private Clock clock;
    private WorkSessionLog log;
    private Instant clockInTime;
    private boolean clockedIn;

    public WorkClock(Clock clock, WorkSessionLog workSessionLog) {
        this.clock = clock;
        this.log = workSessionLog;
    }

    public void clockIn() {
        clockInTime = clock.getCurrentTime();
        clockedIn = true;
    }

    public void clockOut() {
        log.log(clockInTime, clock.getCurrentTime());
        clockedIn = false;
    }

    public Duration getTotalTime() {
        Duration timeWorked = log.getTotalTime();
        if (clockedIn)
            return timeWorked.plus(Duration.between(clockInTime, clock.getCurrentTime()));
        else
            return timeWorked;
    }

    public Duration getTotalTime(Instant from, Instant to) {
        Duration timeWorked = log.getTotalTime(from , to);
        if (clockedIn)
            return timeWorked.plus(Duration.between(clockInTime, clock.getCurrentTime()));
        else
            return timeWorked;
    }

    public Duration getTotalTimeToday() {
        Instant startOfToday = clock.getCurrentTime().truncatedTo(ChronoUnit.DAYS);
        return getTotalTime(startOfToday, clock.getCurrentTime());
    }

    public Duration getTotalTimeThisHour() {
        Instant startOfThisHour = clock.getCurrentTime().truncatedTo(ChronoUnit.HOURS);
        return getTotalTime(startOfThisHour, clock.getCurrentTime());
    }

    public Duration getTotalTimeThisMinute() {
        Instant startOfThisMinute = clock.getCurrentTime().truncatedTo(ChronoUnit.MINUTES);
        return getTotalTime(startOfThisMinute, clock.getCurrentTime());
    }
}
