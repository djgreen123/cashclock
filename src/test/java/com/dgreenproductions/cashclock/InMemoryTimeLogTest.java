package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class InMemoryTimeLogTest {
    private InMemoryTimeLog timeLog;

    @BeforeEach
    public void beforeEach() {
        timeLog = new InMemoryTimeLog(new ArrayList<>());
    }

    @Test
    public void canGetTotalTimeOfSingleLogEntry() {
        Instant start = Instant.parse("2018-05-12T20:59:59.000Z");
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
        Instant start = Instant.parse("2018-05-12T20:59:59.000Z");
        timeLog.add(start, start.plus(Duration.ofSeconds(20)));
        timeLog.add(start.plus(Duration.ofSeconds(30)), start.plus(Duration.ofSeconds(34)));
        assertThat(timeLog.getTotalTime(start, start.plus(Duration.ofSeconds(34)))).isEqualTo(Duration.ofSeconds(24));
    }

    @Test
    public void cannotLogZeroDuration() {
        Instant instant = Instant.now();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            timeLog.add(instant, instant);
        });
    }

    @Test
    public void cannotLogNegativeDuration() {
        Instant from = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant to = from.plus(Duration.ofMillis(999));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            timeLog.add(to, from);
        });
    }

    @Test
    public void logHandlesMillioseconds() throws IOException {
        Instant from = Instant.parse("2018-05-12T20:59:59.123Z");
        Instant to = Instant.parse("2018-05-12T21:00:58.456Z");
        timeLog.add(from, to);
        assertThat(timeLog.getTotalTime(from, to)).isEqualTo(Duration.ofMillis(59333));
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
