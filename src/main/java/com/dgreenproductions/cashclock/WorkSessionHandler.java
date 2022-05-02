package com.dgreenproductions.cashclock;

import java.time.Instant;

public interface WorkSessionHandler {
    void handleSession(Instant from, Instant to);
}
