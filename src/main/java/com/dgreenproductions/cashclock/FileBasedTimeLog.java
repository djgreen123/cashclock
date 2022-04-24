package com.dgreenproductions.cashclock;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class FileBasedTimeLog implements TimeLog {

    private Path logFilePath;

    public FileBasedTimeLog(Path logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public void log(Instant from, Instant to) {
        Instant fromTruncedToSeconds = from.truncatedTo(ChronoUnit.SECONDS);
        Instant toTruncedToSeconds = to.truncatedTo(ChronoUnit.SECONDS);

        if (!fromTruncedToSeconds.isBefore(toTruncedToSeconds)) {
            throw new IllegalArgumentException("illegal time log entry");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
            String fromText = formatter.format(fromTruncedToSeconds);
            String toText = formatter.format(toTruncedToSeconds);

            FileUtils.writeLines(logFilePath.toFile(), List.of(fromText + ", " + toText), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
