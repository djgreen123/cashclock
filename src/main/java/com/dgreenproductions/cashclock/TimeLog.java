package com.dgreenproductions.cashclock;

import java.time.Instant;

public interface TimeLog {
    void log(Instant from, Instant to);
}
