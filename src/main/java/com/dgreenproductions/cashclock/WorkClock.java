package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class WorkClock {
    private Clock clock;
    private WorkSessionLog log;
    private Instant clockInTime;
    private List<SessionListener> listeners = new ArrayList<>();

    public boolean isClockedIn() {
        return clockedIn;
    }

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
        Instant start = clockInTime;
        Instant end = clock.getCurrentTime();
        log.log(start, end);
        listeners.stream().forEach(listener -> listener.sessionLogged(start, end));
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

    public void addListener(SessionListener listener) {
        listeners.add(listener);
    }
}
