package com.dgreenproductions.cashclock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestTimeLog implements TimeLog {
    private List<TimeInterval> entries = new ArrayList<>();

    @Override
    public void log(Instant from, Instant to) {
        entries.add(new TimeInterval(from, to));
    }

    public List<TimeInterval> getEntries() {
        return entries;
    }

}
