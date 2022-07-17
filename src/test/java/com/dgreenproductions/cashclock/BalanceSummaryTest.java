package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

// A balance summary tells me throughout the month whether I am ontrack to meet my financial goals for the month.
// For example, during the Sky contract, I plan to save £4000 per month towards a home.  This goal could be derailed
// by uncontrolled spending in other areas, for example spending too much on food or failing to anticipate an
// upcoming bill or expenditure.
//
// Therefor I need a financial summary that can show me potential problems before they derail the monthly goals. For
// example, "at the current rate of spend on food, you will be able to save £199 less than your goal"
// Failures in a particular month would have to be corrected in following months - save harder
public class BalanceSummaryTest {

    @Test
    public void acceptance() {
        // I quite like the idea of buckets and flow of water as an analogy for month movement.  Money (or water) flows
        // from one bucket or account to another.  E.g I earn £100 which flows from the 'Sky Contract' bucket into the 'Cash'
        // bucket.  I spend £100 and the money flows from the 'Cash' bucket into the 'Entertainment' bucket.
    }
}
