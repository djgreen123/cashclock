package com.dgreenproductions.cashclock.bookkeeping;

import java.time.Instant;
import java.util.List;

import static com.dgreenproductions.cashclock.bookkeeping.EntryType.CREDIT;
import static com.dgreenproductions.cashclock.bookkeeping.EntryType.DEBIT;

public class LedgerAccount {
    private EntryType type;
    private Instant when;
    private String description;
    private double amount;

    public void debit(Instant when, String description, double amount) {
        this.type = DEBIT;
        this.when = when;
        this.description = description;
        this.amount = amount;
    }

    public void credit(Instant when, String description, double amount) {
        this.type = CREDIT;
        this.when = when;
        this.description = description;
        this.amount = amount;
    }

    public Double getBalance() {
        if (type == DEBIT) {
            return -1 * amount;
        }
        return amount;
    }

    public List<MyLedgerEntry> getDebits() {
        return List.of(new MyLedgerEntry() {
            @Override
            public Instant getInstant() {
                return when;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public boolean isDebit() {
                return type == DEBIT;
            }

            @Override
            public Double getAmount() {
                return amount;
            }
        });
    }


}
