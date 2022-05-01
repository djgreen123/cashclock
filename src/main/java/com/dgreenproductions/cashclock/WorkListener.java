package com.dgreenproductions.cashclock;

import java.time.Instant;

public interface WorkListener {
    void clockedIn(Instant clockInTime);
}
