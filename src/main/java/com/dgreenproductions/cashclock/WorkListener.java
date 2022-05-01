package com.dgreenproductions.cashclock;

import java.time.Instant;

public interface WorkListener {
    void clockedIn(Instant clockInTime);
    void clockedOut(Instant clockOutTime);
    void session(Instant sessionStart, Instant sessionEnd);
}
