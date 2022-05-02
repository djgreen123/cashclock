package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class LogFileWriterTest {
    private Path logFilePath;
    private LogFileWriter writer;

    @BeforeEach
    public void beforeEach() throws IOException {
        logFilePath = Files.createTempFile("", "");
        writer = new LogFileWriter(logFilePath);
    }

    @AfterEach
    public void afterEach() {
        logFilePath.toFile().delete();
    }

    @Test
    public void canWrite() throws IOException {
        Instant from = Instant.parse("2018-05-12T20:59:59.123Z");
        Instant to = from.plus(Duration.ofMinutes(1));
        writer.appendEntry(from, to);
        List<String> logLines = Files.readAllLines(logFilePath);
        assertThat(logLines).hasSize(1);

        String line = logLines.get(0);
        assertThat(line).isEqualTo("2018-05-12T20:59:59.123Z, 2018-05-12T21:00:59.123Z");
    }

    @Test
    public void canWriteTwoEntries() throws IOException {
        writer.appendEntry(Instant.now(), Instant.now().plus(Duration.ofSeconds(1)));
        writer.appendEntry(Instant.now(), Instant.now().plus(Duration.ofSeconds(1)));
        List<String> logLines = Files.readAllLines(logFilePath);
        assertThat(logLines).hasSize(2);
    }

}
