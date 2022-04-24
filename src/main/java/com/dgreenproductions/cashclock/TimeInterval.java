package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;

public class TimeInterval {
    private final Instant from;
    private final Instant to;

    public TimeInterval(Instant from, Instant to) {
        this.from = from;
        this.to = to;
    }

    public Instant getFrom() {
        return from;
    }

    public Instant getTo() {
        return to;
    }

    public Duration getDuration() { return Duration.between(from, to); }
}
