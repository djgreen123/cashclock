package com.dgreenproductions.cashclock;

import java.time.Instant;

public class RealClock implements Clock {
    @Override
    public Instant getCurrentTime() {
        return Instant.now();
    }
}
