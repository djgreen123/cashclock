package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class DailySummaryTest {

    @Test
    public void reportsFirstTimeLogged() {
        InMemoryTimeLog timeLog = new InMemoryTimeLog(new ArrayList<>(), new PassiveLogListener());
        Instant start = Instant.parse("2018-05-12T20:59:59.123Z");
        Timeline timeline = new Timeline(start);

        timeLog.log(start, start.plus(Duration.ofMinutes(2)));

        final Duration[] capturedDuration = new Duration[1];
        DailySummary reporter = new DailySummary(timeline, timeLog, (duration) -> capturedDuration[0] = duration);

        // daily summary is reported every minute
        timeline.advanceBy(Duration.ofMinutes(1));

        assertThat(capturedDuration[0]).isEqualTo(Duration.ofMinutes(2));
    }
}
