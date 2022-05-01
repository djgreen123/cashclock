package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class WorkLoggerTest {

    @Test
    public void sessionGetsLogged() {
        InMemoryTimeLog log = new InMemoryTimeLog(new ArrayList<>(), new PassiveLogListener());
        WorkLogger workLogger = new WorkLogger(log);
        Instant start = Instant.now();
        Timeline timeline = new Timeline(start);
        WorkTracker2 tracker = new WorkTracker2(timeline, workLogger);
        tracker.clockIn();
        timeline.advanceBySeconds(1);
        tracker.clockOut();
        assertThat(log.getTotalTime(start, start.plus(Duration.ofHours(1)))).isEqualTo(Duration.ofSeconds(1));
    }
}
