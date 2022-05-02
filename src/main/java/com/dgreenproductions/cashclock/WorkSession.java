package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;

public class WorkSession {
    private final Instant start;
    private final Instant end;

    public WorkSession(Instant start, Instant end) {
        this.start = start;
        this.end = end;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public Duration getDuration() {
        return Duration.between(start, end);
    }
}
