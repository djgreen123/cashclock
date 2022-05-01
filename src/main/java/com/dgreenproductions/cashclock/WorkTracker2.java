package com.dgreenproductions.cashclock;

import java.time.Instant;

public class WorkTracker2 {
    private Timeline timeline;
    private WorkListener listener;
    private Instant clockedInTime;

    public WorkTracker2(Timeline timeline, WorkListener listener) {
        this.timeline = timeline;
        this.listener = listener;
    }

    public void clockIn() {
        clockedInTime = timeline.getCurrentTime();
        listener.clockedIn(clockedInTime);
    }

    public void clockOut() {
        Instant clockedOutTime = timeline.getCurrentTime();
        listener.clockedOut(clockedOutTime);
        listener.session(clockedInTime, clockedOutTime);
    }
}
