package com.dgreenproductions.cashclock;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FileBasedTimeLog implements TimeLog {

    private Path logFilePath;
    private List<TimeInterval> entries = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));

    public FileBasedTimeLog(String logFilePathString) {
        this(Path.of(logFilePathString));
    }

    public FileBasedTimeLog(Path logFilePath) {
        this.logFilePath = logFilePath;
        loadEntries();
    }

    @Override
    public void log(Instant from, Instant to) {
        Instant fromTruncedToSeconds = from.truncatedTo(ChronoUnit.SECONDS);
        Instant toTruncedToSeconds = to.truncatedTo(ChronoUnit.SECONDS);

        if (!fromTruncedToSeconds.isBefore(toTruncedToSeconds)) {
            throw new IllegalArgumentException("illegal time log entry");
        }
        try {
            String fromText = formatter.format(fromTruncedToSeconds);
            String toText = formatter.format(toTruncedToSeconds);

            TimeInterval interval = new TimeInterval(fromTruncedToSeconds, toTruncedToSeconds);
            entries.add(interval);

            FileUtils.writeLines(logFilePath.toFile(), List.of(fromText + ", " + toText), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TimeInterval> getEntries() {
        return List.copyOf(entries);
    }

    private void loadEntries() {
        try {
            List<String> logLines = FileUtils.readLines(logFilePath.toFile(), "UTF-8");
            for (String logLine : logLines) {
                if (!logLine.isBlank()) {
                    String[] parts = logLine.split(", ");
                    entries.add(new TimeInterval(parseInstant(parts[0]), parseInstant(parts[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Instant parseInstant(String text) {
        return Instant.from(formatter.parse(text.trim()));
    }

}
