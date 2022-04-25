package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTimeLog implements TimeLog {
    private List<TimeInterval> entries;
    private LogListener logListener;

    public InMemoryTimeLog(List<TimeInterval> entries, LogListener logListener) {
        this.entries = new ArrayList<>(entries);
        this.logListener = logListener;
    }

    @Override
    public void log(Instant from, Instant to) {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("illegal time log entry");
        }

        entries.add(new TimeInterval(from, to));
        logListener.timeLogged(from, to);
    }

    public Duration getTotalTime(Instant from, Instant to) {
        List<TimeInterval> clippedEntries = entries.stream().map(e -> clip(e, from, to)).collect(Collectors.toList());
        return sumOf(clippedEntries);
    }

    private Duration sumOf(List<TimeInterval> entries) {
        Duration total = Duration.ZERO;
        for (TimeInterval entry : entries) {
            total = total.plus(entry.getDuration());
        }
        return total;
    }

    private TimeInterval clip(TimeInterval timeInterval, Instant from, Instant to) {
        return new TimeInterval(laterOf(from, timeInterval.getFrom()), earlierOf(to, timeInterval.getTo()));
    }

    private Instant laterOf(Instant i1, Instant i2) {
        if (i1.isAfter(i2)) return i1; else return i2;
    }

    private Instant earlierOf(Instant i1, Instant i2) {
        if (i1.isBefore(i2)) return i1; else return i2;
    }

}
