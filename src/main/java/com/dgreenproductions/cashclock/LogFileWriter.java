package com.dgreenproductions.cashclock;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LogFileWriter {
    private Path logFilePath;
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));

    public LogFileWriter(Path logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void writeEntry(Instant from, Instant to) {
        try {
            String fromText = formatter.format(from);
            String toText = formatter.format(to);

            FileUtils.writeLines(logFilePath.toFile(), List.of(fromText + ", " + toText), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
