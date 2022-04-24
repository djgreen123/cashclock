package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class FileBasedTimeLogTest {

    private Path logFilePath;
    private FileBasedTimeLog timeLog;

    @BeforeEach
    public void beforeEach() throws IOException {
        logFilePath = Files.createTempFile("", "");
        timeLog = new FileBasedTimeLog(logFilePath);
    }

    @AfterEach
    public void afterEach() {
        logFilePath.toFile().delete();
    }

    @Test
    public void hasInMemoryListOfEntries() {
        timeLog.log(Instant.now(), Instant.now().plus(Duration.ofSeconds(1)));
        assertThat(timeLog.getEntries()).hasSize(1);
    }

    @Test
    public void entriesOnDiskAreLoadedOnConstruction() {
        TimeLog timeLog1 = new FileBasedTimeLog(logFilePath);
        timeLog1.log(Instant.now(), Instant.now().plus(Duration.ofMinutes(1)));

        FileBasedTimeLog timeLog2 = new FileBasedTimeLog(logFilePath);
        assertThat(timeLog2.getEntries()).hasSize(1);
    }

    @Test
    public void canLogInterval() throws IOException {
        Instant from = Instant.now();
        Instant to = from.plus(Duration.ofMinutes(1));
        timeLog.log(from, to);
        List<String> logLines = Files.readAllLines(logFilePath);
        assertThat(logLines).hasSize(1);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
        String expectedFromText = formatter.format(from.truncatedTo(ChronoUnit.SECONDS));
        String expectedToText = formatter.format(to.truncatedTo(ChronoUnit.SECONDS));
        String expectedLine = expectedFromText + ", " + expectedToText;

        String line = logLines.get(0);
        assertThat(line).isEqualTo(expectedLine);
    }

    @Test
    public void cannotLogZeroDuration() {
        Instant instant = Instant.now();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            timeLog.log(instant, instant);
        });
    }

    @Test
    public void cannotLogSubSecondDuration() {
        Instant from = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant to = from.plus(Duration.ofMillis(999));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            timeLog.log(from, to);
        });
    }

    @Test
    public void logTruncatesToSeconds() throws IOException {
        Instant from = Instant.parse("2018-05-12T20:59:59.123Z");
        Instant to = Instant.parse("2018-05-12T21:00:58.456Z");
        timeLog.log(from, to);
        List<String> logLines = Files.readAllLines(logFilePath);
        assertThat(logLines.get(0)).isEqualTo("2018-05-12T20:59:59Z, 2018-05-12T21:00:58Z");
    }

    @Test
    public void canLogTwoEntries() throws IOException {
        timeLog.log(Instant.now(), Instant.now().plus(Duration.ofSeconds(1)));
        timeLog.log(Instant.now(), Instant.now().plus(Duration.ofSeconds(1)));
        List<String> logLines = Files.readAllLines(logFilePath);
        assertThat(logLines).hasSize(2);
    }

    @Test
    public void creatingTimeLogOnExistingFileDoesNotDestroyPastEntries() throws IOException {
        TimeLog timeLog1 = new FileBasedTimeLog(logFilePath);
        timeLog1.log(Instant.now(), Instant.now().plus(Duration.ofMinutes(1)));

        TimeLog timeLog2 = new FileBasedTimeLog(logFilePath);
        timeLog2.log(Instant.now(), Instant.now().plus(Duration.ofMinutes(1)));

        List<String> logLines = Files.readAllLines(logFilePath);
        assertThat(logLines).hasSize(2);
    }
}
