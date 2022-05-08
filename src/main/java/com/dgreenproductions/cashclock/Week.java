package com.dgreenproductions.cashclock;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

public class Week {
    private Map<DayOfWeek, Instant> dayInstants = new HashMap<>();

    public static Week containing(Instant instant) {
        return new Week(startOfPreceedingMonday(instant));
    }

    private Week(Instant startOfWeek) {
        dayInstants.put(DayOfWeek.MONDAY, startOfWeek);
        dayInstants.put(DayOfWeek.TUESDAY, startOfWeek.plus(Duration.ofDays(1)));
        dayInstants.put(DayOfWeek.WEDNESDAY, startOfWeek.plus(Duration.ofDays(2)));
        dayInstants.put(DayOfWeek.THURSDAY, startOfWeek.plus(Duration.ofDays(3)));
        dayInstants.put(DayOfWeek.FRIDAY, startOfWeek.plus(Duration.ofDays(4)));
        dayInstants.put(DayOfWeek.SATURDAY, startOfWeek.plus(Duration.ofDays(5)));
        dayInstants.put(DayOfWeek.SUNDAY, startOfWeek.plus(Duration.ofDays(6)));
    }

    private static Instant startOfPreceedingMonday(Instant instant) {
        Instant current = instant;
        while (dayOfWeek(current) != DayOfWeek.MONDAY) current = current.minus(Duration.ofDays(1));
        return current;
    }

    private static DayOfWeek dayOfWeek(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).getDayOfWeek();
    }
    public Instant getStartOfWeek() {
        return dayInstants.get(DayOfWeek.MONDAY);
    }

    public Instant getEndOfWeek() {
        return dayInstants.get(DayOfWeek.SUNDAY);
    }

    public Instant getStartOf(DayOfWeek dayOfWeek) {
        return dayInstants.get(dayOfWeek);
    }
}