package com.dgreenproductions.cashclock.bookkeeping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class LedgerAccountTest {
    private LedgerAccount ledgerAccount;

    @BeforeEach
    public void beforeEach() {
        ledgerAccount = new LedgerAccount();
    }

    @Test
    public void addSingleDebitAndCheckBalance() {
        ledgerAccount.debit(Instant.now(), "Bought coat", 1000);
        assertThat(ledgerAccount.getBalance()).isEqualTo(-1000);
    }

    @Test
    public void addSingleCreditAndCheckBalance() {
        ledgerAccount.credit(Instant.now(), "Sold hat", 24.55);
        assertThat(ledgerAccount.getBalance()).isEqualTo(24.55);
    }

    @Test
    public void canGetSingleDebit() {
        Instant debitInstant = Instant.now();
        String debitDescription = "Bought coat";
        ledgerAccount.debit(debitInstant, debitDescription, 1000);

        List<MyLedgerEntry> debits = ledgerAccount.getDebits();
        assertThat(debits).hasSize(1);
        MyLedgerEntry myLedgerEntry = debits.get(0);
        assertThat(myLedgerEntry.isDebit()).isTrue();
        assertThat(myLedgerEntry.getInstant()).isEqualTo(debitInstant);
        assertThat(myLedgerEntry.getDescription()).isEqualTo(debitDescription);
        assertThat(myLedgerEntry.getAmount()).isEqualTo(1000.0);
    }

    @Test
    public void canGetMultipleDebits() {

    }

}
