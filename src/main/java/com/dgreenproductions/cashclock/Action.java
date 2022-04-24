package com.dgreenproductions.cashclock;

import java.time.Instant;

public abstract class Action {
    private Instant when;

    public Action(Instant when) {
        this.when = when;
    }

    public abstract void perform();

    public Instant getWhen() {
        return when;
    }
}