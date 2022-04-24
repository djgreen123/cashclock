package com.dgreenproductions.cashclock;

import java.time.Instant;

public interface Clock {
    Instant getCurrentTime();
}
