package com.dgreenproductions.cashclock;

import java.time.Instant;

public class WorkLogger implements WorkListener {
    private TimeLog log;

    public WorkLogger(TimeLog log) {
        this.log = log;
    }

    @Override
    public void clockedIn(Instant clockInTime) {

    }

    @Override
    public void clockedOut(Instant clockOutTime) {

    }

    @Override
    public void session(Instant sessionStart, Instant sessionEnd) {
        log.log(sessionStart, sessionEnd);
    }
}
