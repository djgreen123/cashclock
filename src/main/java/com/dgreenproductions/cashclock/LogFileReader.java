package com.dgreenproductions.cashclock;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LogFileReader {
    private Path logFilePath;
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));

    public LogFileReader(Path logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void readAll(WorkSessionHandler handler) {
        try {
            List<String> logLines = FileUtils.readLines(logFilePath.toFile(), "UTF-8");
            for (String logLine : logLines) {
                if (!logLine.isBlank()) {
                    String[] parts = logLine.split(", ");
                    handler.handleSession(parseInstant(parts[0]), parseInstant(parts[1]));
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
