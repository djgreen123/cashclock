package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;

public class TestClock implements Clock {
    private Instant currentTime;

    public TestClock(Instant time) {
        currentTime = time;
    }

    @Override
    public Instant getCurrentTime() {
        return currentTime;
    }

    public void advance(Duration duration) {
        currentTime = currentTime.plus(duration);
    }
}
