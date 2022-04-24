package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

public class HourTimeline {
    private Clock clock;
    private final double total;

    public HourTimeline(Clock clock, double total) {
        this.clock = clock;
        this.total = total;
    }

    public double get() {
        Instant currentTime = clock.getCurrentTime();
        double currentMinutes = currentTime.atZone(ZoneOffset.UTC).getMinute();
        double currentSeconds = currentTime.atZone(ZoneOffset.UTC).getSecond();
        double totalSeconds = currentMinutes * 60 + currentSeconds;
        double durationSeconds = Duration.ofHours(1).getSeconds();
        double elapsedPercent = totalSeconds / durationSeconds;
        return total * elapsedPercent;
    }

}
