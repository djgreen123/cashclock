package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.fest.assertions.Assertions.assertThat;

public class JavaTimeTest {

    @Test
    public void getDayOfWeek() {
        Instant tuesday = Instant.parse("2022-05-10T09:00:00.000Z");
        assertThat(LocalDateTime.ofInstant(tuesday, ZoneOffset.UTC).getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
    }

}
