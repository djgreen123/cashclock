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

    private boolean clockedIn = false;

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

    public Duration getRunningTotalTime() {
        return getRunningTotalTime(Instant.EPOCH);
    }

    public Duration getTotalTime(Instant from, Instant to) {
        return log.getTotalTime(from , to);
    }

    public Duration getRunningTotalTime(Instant from) {
        Instant to = clock.getCurrentTime();
        Instant startOfToday = to.truncatedTo(ChronoUnit.DAYS);
        Duration timeWorkedUpToToday = log.getTotalTime(from , startOfToday.minus(Duration.ofSeconds(1)));
        Duration timeWorkedSoFarToday = log.getTotalTime(latestOf(startOfToday, from), to);
        if (clockedIn)
            if (from.isAfter(clockInTime)) {
                Duration runningDuration = Duration.between(from, clock.getCurrentTime());
                Duration totalToday = minOf(timeWorkedSoFarToday.plus(runningDuration), Duration.ofHours(8));
                return timeWorkedUpToToday.plus(totalToday);
            } else {
                Duration runningDuration = Duration.between(clockInTime, clock.getCurrentTime());
                Duration totalToday = minOf(timeWorkedSoFarToday.plus(runningDuration), Duration.ofHours(8));
                return timeWorkedUpToToday.plus(totalToday);
            }
        else
            return timeWorkedUpToToday.plus(timeWorkedSoFarToday);
    }

    private Instant latestOf(Instant i1, Instant i2) {
        if (i1.isAfter(i2)) {
            return i1;
        } else {
            return i2;
        }
    }

    public Duration getRunningTotalTimeThisMonth() {
        try {
            Instant startOfToday = clock.getCurrentTime().truncatedTo(ChronoUnit.DAYS);
            Instant firstDayOfMonth = LocalDateTime.ofInstant(startOfToday, ZoneOffset.UTC).with(TemporalAdjusters.firstDayOfMonth()).toInstant(ZoneOffset.UTC);
            return getRunningTotalTime(firstDayOfMonth.truncatedTo(ChronoUnit.DAYS));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Duration.ZERO;
    }

    public Duration getTotalTimePreviousMonth() {
        try {
            Instant startOfToday = clock.getCurrentTime().truncatedTo(ChronoUnit.DAYS);
            Instant firstDayOfMonth = LocalDateTime.ofInstant(startOfToday, ZoneOffset.UTC).with(TemporalAdjusters.firstDayOfMonth()).toInstant(ZoneOffset.UTC);
            Instant finalDayOfPreviousMonth = firstDayOfMonth.minus(Duration.ofDays(1));
            Instant firstDatOfPreviousMonth = LocalDateTime.ofInstant(finalDayOfPreviousMonth, ZoneOffset.UTC).with(TemporalAdjusters.firstDayOfMonth()).toInstant(ZoneOffset.UTC);
            return getTotalTime(firstDatOfPreviousMonth.truncatedTo(ChronoUnit.DAYS),
                    finalDayOfPreviousMonth.truncatedTo(ChronoUnit.DAYS).plus(Duration.ofHours(23)).plus(Duration.ofMinutes(59)).plus(Duration.ofSeconds(59)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Duration.ZERO;
    }

    public Duration getRunningTotalToday() {
        Duration duration = log.getTimeLoggedOnDayContaining(clock.getCurrentTime());
        if (clockedIn) {
            duration = duration.plus(Duration.between(clockInTime, clock.getCurrentTime()));
        }
        return minOf(Duration.ofHours(8), duration);
    }

    private Duration minOf(Duration d1, Duration d2) {
        if (d1.compareTo(d2) > 0)
            return d2;
        else
            return d1;
    }

    public Duration getRunningTimeThisHour() {
        Instant startOfToday = clock.getCurrentTime().truncatedTo(ChronoUnit.DAYS);
        Instant startOfCurrentHour = clock.getCurrentTime().truncatedTo(ChronoUnit.HOURS);
        if (clockedIn) {
            Duration loggedUpToStartOfCurrentHour = log.getTotalTime(startOfToday, startOfCurrentHour);
            Duration remainingClockableTimeInCurrentHour = Duration.ofHours(8).minus(loggedUpToStartOfCurrentHour);
            Duration runningTotalForCurrentHour = log.getTotalTime(startOfCurrentHour, clock.getCurrentTime());
            runningTotalForCurrentHour = runningTotalForCurrentHour.plus(Duration.between(clockInTime, clock.getCurrentTime()));
            return minOf(remainingClockableTimeInCurrentHour, runningTotalForCurrentHour);
        } else {
            return log.getTotalTime(startOfCurrentHour, clock.getCurrentTime());
        }
    }

    public Duration getTotalTimeThisMinute() {
        Instant startOfThisMinute = clock.getCurrentTime().truncatedTo(ChronoUnit.MINUTES);
        return getRunningTotalTime(startOfThisMinute);
    }

    public void addListener(SessionListener listener) {
        listeners.add(listener);
    }

    public Duration getThisWeeksRunningTotal(DayOfWeek dayOfWeek) {
        Instant now = clock.getCurrentTime();
        Week currentWeek = Week.containing(now);
        Instant startOfDay = currentWeek.getStartOf(dayOfWeek).truncatedTo(ChronoUnit.DAYS);
        Instant endOfDay = startOfDay.plus(Duration.ofHours(23).plus(Duration.ofMinutes(59)).plus(Duration.ofSeconds(59)));
        Duration total = log.getTotalTime(startOfDay, endOfDay);
        if (now.compareTo(startOfDay) >= 0 && now.compareTo(endOfDay) <= 0) {
            if (clockedIn) {
                total = total.plus(Duration.between(clockInTime, now));
            }
        }
        return total;
    }
}
