package com.dgreenproductions.cashclock;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Timeline {
    private Instant currentTime;
    private List<Action> actions = new ArrayList<>();

    public Timeline(Instant startTime) {
        currentTime = startTime;
    }

    public Instant getCurrentTime() {
        return currentTime;
    }

    public void advanceTimeTo(Instant newCurrentTime) {
        if (newCurrentTime.isBefore(currentTime)) {
            throw new IllegalArgumentException("Can only advance time into the future");
        }
        currentTime = newCurrentTime;

        performDueActions();
    }

    public void schedule(Action action) {
        if (action.getWhen().isBefore(currentTime)) {
            throw new IllegalArgumentException("Cannot schedule action in the past");
        }
        if (action.getWhen().equals(currentTime)) {
            action.perform();
        } else {
            actions.add(action);
        }
    }

    private void performDueActions() {
        List<Action> overdue = actions.stream().filter(action ->
                !action.getWhen().isAfter(currentTime)).collect(Collectors.toList());
        overdue.forEach(Action::perform);
        actions = actions.stream().filter(action -> !overdue.contains(action)).collect(Collectors.toList());
    }

    public void advanceBySeconds(long seconds) {
        advanceBy(Duration.ofSeconds(seconds));
    }

    public void advanceBy(Duration duration) {
        advanceTimeTo(currentTime.plus(duration));
    }

    public void cancelFutureActions() {
        actions = new ArrayList<>();
    }
}