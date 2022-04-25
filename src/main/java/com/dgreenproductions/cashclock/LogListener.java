package com.dgreenproductions.cashclock;

import java.time.Instant;

public interface LogListener {
    void timeLogged(Instant from, Instant to);
}
