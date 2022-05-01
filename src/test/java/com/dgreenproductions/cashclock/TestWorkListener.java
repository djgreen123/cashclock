package com.dgreenproductions.cashclock;

import java.time.Instant;

public class TestWorkListener implements WorkListener {
    private boolean clockedIn;
    private Instant clockedInTime;

    public boolean isClockedIn() {
        return clockedIn;
    }

    @Override
    public void clockedIn(Instant clockInTime) {
        clockedIn = true;
        clockedInTime = clockInTime;
    }

    public Instant getClockedInTime() {
        return clockedInTime;
    }

}
