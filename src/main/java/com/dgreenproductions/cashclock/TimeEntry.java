package com.dgreenproductions.cashclock;

import java.time.Instant;

public class TimeEntry {
    private final Instant from;
    private final Instant to;

    public TimeEntry(Instant from, Instant to) {

        this.from = from;
        this.to = to;
    }

    public Instant getFrom() {
        return from;
    }

    public Instant getTo() {
        return to;
    }
}
