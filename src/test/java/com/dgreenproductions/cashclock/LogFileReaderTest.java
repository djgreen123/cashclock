package com.dgreenproductions.cashclock;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.util.Collections.list;

public class LogFileReaderTest {

    private Path logFilePath;
    private List<WorkSession> readSessions;
    private WorkSessionHandler handler;
    private LogFileReader2 reader;

    @BeforeEach
    void setUp() throws IOException {
        logFilePath = Files.createTempFile("", "");
        readSessions = new ArrayList<>();
        handler = (from, to) -> readSessions.add(new WorkSession(from, to));
        reader = new LogFileReader2(logFilePath);
    }

    @AfterEach
    public void afterEach() {
        logFilePath.toFile().delete();
    }

    @Test
    public void enumerateOneLine() throws IOException {
        FileUtils.writeLines(logFilePath.toFile(), Collections.singleton("2018-05-12T20:59:59.123Z, 2018-05-12T21:00:59.123Z"), false);
        reader.readAll(handler);
        assertThat(readSessions).hasSize(1);
    }

    @Test
    public void ignoresBlankAndEmptyLines() throws IOException {
        FileUtils.writeLines(logFilePath.toFile(), list("", " "), false);
        reader.readAll(handler);
        assertThat(readSessions).isEmpty();
    }
}
