package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class DailySummaryTest {

    @Test
    public void reportsFirstTimeLogged() {
        InMemoryTimeLog timeLog = new InMemoryTimeLog(new ArrayList<>(), new PassiveLogListener());
        Instant start = Instant.parse("2018-05-12T20:59:59.123Z");
        Timeline timeline = new Timeline(start);

        timeLog.log(start, start.plus(Duration.ofMinutes(2)));

        List<Duration> capturedDurations = new ArrayList<>();
        new DailySummary(timeline, timeLog, capturedDurations::add);

        // daily summary is reported every minute
        timeline.advanceBy(Duration.ofMinutes(1));

        assertThat(capturedDurations.get(0)).isEqualTo(Duration.ofMinutes(2));
    }

    @Test
    public void continuesToReportDailyTotal() {
        InMemoryTimeLog timeLog = new InMemoryTimeLog(new ArrayList<>(), new PassiveLogListener());
        Instant start = Instant.parse("2018-05-12T20:59:59.123Z");
        Timeline timeline = new Timeline(start);

        timeLog.log(start, start.plus(Duration.ofMinutes(2)));

        List<Duration> capturedDurations = new ArrayList<>();
        new DailySummary(timeline, timeLog, capturedDurations::add);

        timeline.advanceBy(Duration.ofMinutes(1));
        assertThat(capturedDurations).hasSize(1);

        timeline.advanceBy(Duration.ofMinutes(1));
        assertThat(capturedDurations).hasSize(2);
    }
}
