package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorkSessionLog {
    private List<WorkSession> sessions = new ArrayList<>();
    
    public void log(Instant start, Instant end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("illegal time log");
        }

        sessions.add(new WorkSession(start, end));
    }

    public Duration getTotalTime(Instant from, Instant to) {
        List<WorkSession> intersectingEntries = sessions.stream().filter(e -> intervalIntersectsWindow(e, from, to)).collect(Collectors.toList());
        List<WorkSession> clippedEntries = intersectingEntries.stream().map(e -> clip(e, from, to)).collect(Collectors.toList());
        return sumOf(clippedEntries);
    }

    public Duration getTotalTime() {
        return sumOf(sessions);
    }

    private boolean intervalIntersectsWindow(WorkSession WorkSession, Instant windowStart, Instant windowEnd) {
        return !intervalIsOutsideWindow(WorkSession, windowStart, windowEnd);
    }

    private boolean intervalIsOutsideWindow(WorkSession i, Instant windowStart, Instant windowEnd) {
        // both from and to are before the start OR both from and to are after the end
        return (i.getStart().isBefore(windowStart) && i.getEnd().isBefore(windowStart)) ||
                (i.getStart().isAfter(windowEnd) && i.getEnd().isAfter(windowEnd));
    }

    private Duration sumOf(List<WorkSession> entries) {
        Duration total = Duration.ZERO;
        for (WorkSession entry : entries) {
            total = total.plus(entry.getDuration());
        }
        return total;
    }

    private WorkSession clip(WorkSession WorkSession, Instant from, Instant to) {
        return new WorkSession(laterOf(from, WorkSession.getStart()), earlierOf(to, WorkSession.getEnd()));
    }

    private Instant laterOf(Instant i1, Instant i2) {
        if (i1.isAfter(i2)) return i1; else return i2;
    }

    private Instant earlierOf(Instant i1, Instant i2) {
        if (i1.isBefore(i2)) return i1; else return i2;
    }
}
