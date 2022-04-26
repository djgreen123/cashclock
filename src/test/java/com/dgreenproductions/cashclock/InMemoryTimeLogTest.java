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
        timeLog = new InMemoryTimeLog(new ArrayList<>(), (from, to) -> {

        });
    }

    @Test
    public void canGetTotalTimeWhenOnlyTimeLoggedIsOutsideTheWindow() {
        Instant start = Instant.parse("2018-05-12T20:59:59.123Z");
        Instant end = start.plus(Duration.ofMinutes(2));
        timeLog.log(start, end);

        Instant windowStart = Instant.parse("2022-04-26T00:00:00Z");
        Instant windowEnd = Instant.parse("2022-04-26T23:59:59Z");
        assertThat(timeLog.getTotalTime(windowStart, windowEnd)).isEqualTo(Duration.ZERO);
    }

    @Test
    public void canGetTotalTimeOfSingleLogEntry() {
        Instant start = Instant.parse("2018-05-12T20:59:59.000Z");
        Instant end = start.plus(Duration.ofMinutes(1));
        timeLog.log(start, end);
        assertThat(timeLog.getTotalTime(start, end)).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    public void canGetTotalTimeWhenWindowIsWiderThanSingleEntry() {
        Instant start = Instant.now();
        Instant end = start.plus(Duration.ofMinutes(1));
        timeLog.log(start, end);
        assertThat(timeLog.getTotalTime(start.minus(Duration.ofSeconds(1)), end.plus(Duration.ofSeconds(1)))).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    public void canGetTotalTimeWhenWindowIsNarrowerThanSingleEntry() {
        Instant start = Instant.now();
        Instant end = start.plus(Duration.ofMinutes(1));
        timeLog.log(start, end);
        assertThat(timeLog.getTotalTime(start.plus(Duration.ofSeconds(1)), end.minus(Duration.ofSeconds(1)))).isEqualTo(Duration.ofSeconds(58));
    }

    @Test
    public void canGetTotalOfMultipleEntries() {
        Instant start = Instant.parse("2018-05-12T20:59:59.000Z");
        timeLog.log(start, start.plus(Duration.ofSeconds(20)));
        timeLog.log(start.plus(Duration.ofSeconds(30)), start.plus(Duration.ofSeconds(34)));
        assertThat(timeLog.getTotalTime(start, start.plus(Duration.ofSeconds(34)))).isEqualTo(Duration.ofSeconds(24));
    }

    @Test
    public void cannotLogZeroDuration() {
        Instant instant = Instant.now();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            timeLog.log(instant, instant);
        });
    }

    @Test
    public void cannotLogNegativeDuration() {
        Instant from = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant to = from.plus(Duration.ofMillis(999));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            timeLog.log(to, from);
        });
    }

    @Test
    public void logHandlesMillioseconds() throws IOException {
        Instant from = Instant.parse("2018-05-12T20:59:59.123Z");
        Instant to = Instant.parse("2018-05-12T21:00:58.456Z");
        timeLog.log(from, to);
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
