package com.dgreenproductions.cashclock;

public class WorkTracker2 {
    private Timeline timeline;
    private WorkListener listener;

    public WorkTracker2(Timeline timeline, WorkListener listener) {
        this.timeline = timeline;
        this.listener = listener;
    }

    public void clockIn() {
        listener.clockedIn(timeline.getCurrentTime());
    }
}
