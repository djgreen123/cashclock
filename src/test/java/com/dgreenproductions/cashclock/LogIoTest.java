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

public class LogIoTest {
    private Path logFilePath;
    private LogFileReader reader;
    private LogFileWriter writer;

    @BeforeEach
    public void beforeEach() throws IOException {
        logFilePath = Files.createTempFile("", "");
        reader = new LogFileReader(logFilePath);
        writer = new LogFileWriter(logFilePath);
    }

    @AfterEach
    public void afterEach() {
        logFilePath.toFile().delete();
    }

    @Test
    public void canReadEntries() {
        writer.writeEntry(Instant.now(), Instant.now().plus(Duration.ofMinutes(1)));
        assertThat(reader.readEntries()).hasSize(1);
    }

    @Test
    public void canWriteTimeInterval() throws IOException {
        Instant from = Instant.parse("2018-05-12T20:59:59.123Z");
        Instant to = from.plus(Duration.ofMinutes(1));
        writer.writeEntry(from, to);
        List<String> logLines = Files.readAllLines(logFilePath);
        assertThat(logLines).hasSize(1);

        String line = logLines.get(0);
        assertThat(line).isEqualTo("2018-05-12T20:59:59.123Z, 2018-05-12T21:00:59.123Z");
    }

    @Test
    public void canWriteTwoEntries() throws IOException {
        writer.writeEntry(Instant.now(), Instant.now().plus(Duration.ofSeconds(1)));
        writer.writeEntry(Instant.now(), Instant.now().plus(Duration.ofSeconds(1)));
        List<String> logLines = Files.readAllLines(logFilePath);
        assertThat(logLines).hasSize(2);
    }

}
