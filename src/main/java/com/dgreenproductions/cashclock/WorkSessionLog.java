package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

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
        Map<Instant, List<WorkSession>> byDay = entries.stream().collect(groupingBy(s -> s.getStart().truncatedTo(ChronoUnit.DAYS)));
        for (Instant startOfDay : byDay.keySet()) {
            Duration dailyTotal = Duration.ZERO;
            List<WorkSession> dailySessions = byDay.get(startOfDay);
            for (WorkSession dailySession : dailySessions) {
                dailyTotal = dailyTotal.plus(dailySession.getDuration());
            }
            total = total.plus(minOf(dailyTotal, Duration.ofHours(8)));
        }
        return total;
    }

    private Duration minOf(Duration d1, Duration d2) {
        if (d1.compareTo(d2) > 0)
            return d2;
        else
            return d1;
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

    private Duration sum(List<WorkSession> sessions) {
        Duration total = Duration.ZERO;
        for (WorkSession session : sessions) {
            total = total.plus(session.getDuration());
        }
        return total;
    }

    public Duration getTimeLoggedOnDayContaining(Instant instant) {
        Map<Instant, List<WorkSession>> byDay = sessions.stream().collect(groupingBy(s -> s.getStart().truncatedTo(ChronoUnit.DAYS)));
        List<WorkSession> workSessions = byDay.get(instant.truncatedTo(ChronoUnit.DAYS));
        return sum(workSessions);
    }
}
