package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.fest.assertions.Assertions.assertThat;

public class JavaTimeTest {

    @Test
    public void getDayOfWeek() {
        Instant tuesday = Instant.parse("2022-05-10T09:00:00.000Z");
        assertThat(LocalDateTime.ofInstant(tuesday, ZoneOffset.UTC).getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
    }

    @Test
    public void percentageDuration() {
        Duration totalTimeToday = Duration.ofHours(4).plus(Duration.ofMinutes(28));
        double percentTodayWorked = (double)totalTimeToday.toMillis() / Duration.ofHours(8).toMillis() * 100.0;
        assertThat(percentTodayWorked).isNotNull();
    }
}
