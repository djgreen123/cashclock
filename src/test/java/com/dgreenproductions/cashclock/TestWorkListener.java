package com.dgreenproductions.cashclock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestWorkListener implements WorkListener {
    private List<Instant> clockedInTimes = new ArrayList<>();
    private List<Instant> clockedOutTimes = new ArrayList<>();
    private Optional<Instant> sessionStart = Optional.empty();
    private Optional<Instant> sessionEnd = Optional.empty();

    @Override
    public void clockedIn(Instant clockInTime) {
        clockedInTimes.add(clockInTime);
    }

    @Override
    public void clockedOut(Instant clockOutTime) {
        clockedOutTimes.add(clockOutTime);
    }

    @Override
    public void session(Instant sessionStart, Instant sessionEnd) {
        this.sessionStart = Optional.of(sessionStart);
        this.sessionEnd = Optional.of(sessionEnd);
    }

    public List<Instant> getClockedInTimes() {
        return clockedInTimes;
    }

    public List<Instant> getClockedOutTime() {
        return clockedOutTimes;
    }

    public Optional<Instant> getSessionStart() {
        return sessionStart;
    }

    public Optional<Instant> getSessionEnd() {
        return sessionEnd;
    }
}
