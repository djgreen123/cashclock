package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class WorkTracker {
    private Timeline timeline;
    private TimeLog timeLog;
    private Optional<Instant> from = Optional.empty();
    private boolean clockedIn = false;

    public WorkTracker(Timeline timeline, TimeLog timeLog) {
        this.timeline = timeline;
        this.timeLog = timeLog;
    }

    public void clockIn() {
        if (clockedIn) return;
        clockedIn = true;
        scheduledNextLogEntry(timeline.getCurrentTime());
    }

    private void scheduledNextLogEntry(Instant fromTime) {
        from = Optional.of(fromTime);
        timeline.schedule(new Action(fromTime.plus(Duration.ofMinutes(1))) {
            @Override
            public void perform() {
                if (clockedIn) timeLog.log(fromTime, getWhen());
                scheduledNextLogEntry(getWhen());
            }
        });
    }

    public void clockOut() {
        if (!clockedIn) return;
        if (from.isPresent() && Duration.between(from.get(), timeline.getCurrentTime()).getSeconds() > 1) {
            timeLog.log(from.get(), timeline.getCurrentTime());
            from = Optional.empty();
        }
        clockedIn = false;
    }

    public boolean isClockedIn() {
        return clockedIn;
    }
}

