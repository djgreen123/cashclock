package com.dgreenproductions.cashclock;

import java.time.Instant;
import java.util.Optional;

public class TestWorkListener implements WorkListener {
    private Optional<Instant> clockedInTime = Optional.empty();
    private Optional<Instant> clockedOutTime = Optional.empty();
    private Optional<Instant> sessionStart = Optional.empty();
    private Optional<Instant> sessionEnd = Optional.empty();

    @Override
    public void clockedIn(Instant clockInTime) {
        clockedInTime = Optional.of(clockInTime);
    }

    @Override
    public void clockedOut(Instant clockOutTime) {
        clockedOutTime = Optional.of(clockOutTime);
    }

    @Override
    public void session(Instant sessionStart, Instant sessionEnd) {
        this.sessionStart = Optional.of(sessionStart);
        this.sessionEnd = Optional.of(sessionEnd);
    }

    public Optional<Instant> getClockedInTime() {
        return clockedInTime;
    }

    public Optional<Instant> getClockedOutTime() {
        return clockedOutTime;
    }

    public Optional<Instant> getSessionStart() {
        return sessionStart;
    }

    public Optional<Instant> getSessionEnd() {
        return sessionEnd;
    }
}
