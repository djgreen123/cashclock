package com.dgreenproductions.cashclock;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.fest.assertions.Assertions.assertThat;

public class InMemoryTimeLogTest {
    private InMemoryTimeLog timeLog;

    @BeforeEach
    public void beforeEach() {
        timeLog = new InMemoryTimeLog();
    }

    @Test
    public void canGetTotalTimeOfSingleLogEntry() {
        Instant start = Instant.now();
        Instant end = start.plus(Duration.ofMinutes(1));
        timeLog.add(start, end);
        assertThat(timeLog.getTotalTime(start, end)).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    public void canGetTotalTimeWhenWindowIsWiderThanSingleEntry() {
        Instant start = Instant.now();
        Instant end = start.plus(Duration.ofMinutes(1));
        timeLog.add(start, end);
        assertThat(timeLog.getTotalTime(start.minus(Duration.ofSeconds(1)), end.plus(Duration.ofSeconds(1)))).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    public void canGetTotalTimeWhenWindowIsNarrowerThanSingleEntry() {
        Instant start = Instant.now();
        Instant end = start.plus(Duration.ofMinutes(1));
        timeLog.add(start, end);
        assertThat(timeLog.getTotalTime(start.plus(Duration.ofSeconds(1)), end.minus(Duration.ofSeconds(1)))).isEqualTo(Duration.ofSeconds(58));
    }

    @Test
    public void canGetTotalOfMultipleEntries() {
        Instant start = Instant.now();
        timeLog.add(start, start.plus(Duration.ofSeconds(20)));
        timeLog.add(start.plus(Duration.ofSeconds(30)), start.plus(Duration.ofSeconds(34)));
        assertThat(timeLog.getTotalTime(start, start.plus(Duration.ofSeconds(34)))).isEqualTo(Duration.ofSeconds(24));
    }

//    Don't think I need this because time is moving forward so logging overlapping periods should not be possible
//    @Test
//    public void overlappingPeriodsAreConsolidated() {
//        Instant start = Instant.now();
//        timeLog.add(start, start.plus(Duration.ofSeconds(20)));
//        timeLog.add(start.plus(Duration.ofSeconds(10)), start.plus(Duration.ofSeconds(25)));
//        assertThat(timeLog.getTotalTime(start, start.plus(Duration.ofSeconds(25)))).isEqualTo(Duration.ofSeconds(25));
//    }
}
