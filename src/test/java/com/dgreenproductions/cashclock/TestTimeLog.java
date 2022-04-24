package com.dgreenproductions.cashclock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestTimeLog implements TimeLog {
    private List<TimeEntry> entries = new ArrayList<>();

    @Override
    public void log(Instant from, Instant to) {
        entries.add(new TimeEntry(from, to));
    }

    public List<TimeEntry> getEntries() {
        return entries;
    }

    public static class TimeEntry {
        private final Instant from;
        private final Instant to;

        public TimeEntry(Instant from, Instant to) {

            this.from = from;
            this.to = to;
        }

        public Instant getFrom() {
            return from;
        }

        public Instant getTo() {
            return to;
        }
    }
}
