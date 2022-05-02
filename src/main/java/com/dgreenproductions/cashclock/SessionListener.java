package com.dgreenproductions.cashclock;

import java.time.Instant;

public interface SessionListener {
    void sessionLogged(Instant start, Instant end);
}
