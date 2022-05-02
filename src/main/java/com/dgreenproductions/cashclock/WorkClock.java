package com.dgreenproductions.cashclock;

import java.time.*;
import java.time.temporal.*;
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
            if (from.isAfter(clockInTime)) {
                return timeWorked.plus(Duration.between(from, clock.getCurrentTime()));
            } else {
                return timeWorked.plus(Duration.between(clockInTime, clock.getCurrentTime()));
            }
        else
            return timeWorked;
    }

    public Duration getTotalTimeThisMonth() {
        try {
            Instant now = clock.getCurrentTime();
            Instant firstDayOfMonth = LocalDateTime.ofInstant(now, ZoneOffset.UTC).with(TemporalAdjusters.firstDayOfMonth()).toInstant(ZoneOffset.UTC);
            return getTotalTime(firstDayOfMonth.truncatedTo(ChronoUnit.DAYS), clock.getCurrentTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Duration.ZERO;
    }


    public Duration getTotalTimeToday() {
        Instant startOfToday = clock.getCurrentTime().truncatedTo(ChronoUnit.DAYS);
        return minOf(Duration.ofHours(8), getTotalTime(startOfToday, clock.getCurrentTime()));
    }

    private Duration minOf(Duration d1, Duration d2) {
        if (d1.compareTo(d2) > 0)
            return d2;
        else
            return d1;
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
