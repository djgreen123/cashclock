package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class WorkClock {
    private Clock clock;
    private List<WorkSession> sessions = new ArrayList<>();
    private Instant clockInTime;
    private boolean clockedIn;

    public WorkClock(Clock clock) {
        this.clock = clock;
    }

    public void clockIn() {
        clockInTime = clock.getCurrentTime();
        clockedIn = true;
    }

    public void clockOut() {
        sessions.add(new WorkSession(clockInTime, clock.getCurrentTime()));
        clockedIn = false;
    }

    public Duration getTotalTime() {
        Duration timeWorked = sumSessions();
        if (clockedIn)
            return timeWorked.plus(Duration.between(clockInTime, clock.getCurrentTime()));
        else
            return timeWorked;
    }

    private Duration sumSessions() {
        Duration duration = Duration.ZERO;
        for (WorkSession session : sessions) {
            duration = duration.plus(session.getDuration());
        }
        return duration;
    }

    public Duration getTodaysTotalTime() {
        return getTotalTime();
    }
}
