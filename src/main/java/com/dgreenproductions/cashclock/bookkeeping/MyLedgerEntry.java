package com.dgreenproductions.cashclock.bookkeeping;

import java.time.Instant;

public interface MyLedgerEntry {
    Instant getInstant();

    String getDescription();

    boolean isDebit();

    Double getAmount();
}
