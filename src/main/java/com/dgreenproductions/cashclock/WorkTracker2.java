package com.dgreenproductions.cashclock;

import java.time.Instant;
import java.util.Optional;

public class WorkTracker2 {
    private Timeline timeline;
    private WorkListener listener;
    private Optional<Instant> clockedInTime = Optional.empty();

    public WorkTracker2(Timeline timeline, WorkListener listener) {
        this.timeline = timeline;
        this.listener = listener;
    }

    public void clockIn() {
        if (clockedInTime.isPresent()) throw new IllegalStateException();
        clockedInTime = Optional.of(timeline.getCurrentTime());
        listener.clockedIn(clockedInTime.get());
    }

    public void clockOut() {
        if (clockedInTime.isEmpty()) throw new IllegalStateException();
        Instant clockedOutTime = timeline.getCurrentTime();
        listener.clockedOut(clockedOutTime);
        listener.session(clockedInTime.get(), clockedOutTime);
    }
}
