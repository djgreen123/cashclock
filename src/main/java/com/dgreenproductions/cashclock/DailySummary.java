package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DailySummary {

    private final Timeline timeline;
    private final InMemoryTimeLog timeLog;
    private final DurationReporter reporter;

    public DailySummary(Timeline timeline, InMemoryTimeLog timeLog, DurationReporter reporter) {
        this.timeline = timeline;
        this.timeLog = timeLog;
        this.reporter = reporter;
        scheduleNextReport(timeline.getCurrentTime());
    }

    private void scheduleNextReport(Instant current) {
        timeline.schedule(new Action(current.plus(Duration.ofMinutes(1))) {
            @Override
            public void perform() {
                Instant startOfToday = getWhen().truncatedTo(ChronoUnit.DAYS);
                Instant endOfToday = startOfToday.plus(Duration.ofDays(1)).minus(Duration.ofSeconds(1));
                reporter.report(timeLog.getTotalTime(startOfToday, endOfToday));

                scheduleNextReport(getWhen());
            }
        });
    }

}
