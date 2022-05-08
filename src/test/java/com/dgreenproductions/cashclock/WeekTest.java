package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;

import static org.fest.assertions.Assertions.assertThat;

public class WeekTest {

    @Test
    public void canGetWeekFromTuesday() {
        Instant tuesday = Instant.parse("2022-05-10T09:00:00.000Z");
        Week week = Week.containing(tuesday);

        // weeks run Monday to Sunday
        assertThat(week.getStartOfWeek()).isEqualTo(Instant.parse("2022-05-09T09:00:00.000Z"));
        assertThat(week.getEndOfWeek()).isEqualTo(Instant.parse("2022-05-15T09:00:00.000Z"));
    }

    @Test
    public void canGetWeekFromSunday() {
        Instant tuesday = Instant.parse("2022-05-08T09:00:00.000Z");
        Week week = Week.containing(tuesday);

        // weeks run Monday to Sunday
        assertThat(week.getStartOfWeek()).isEqualTo(Instant.parse("2022-05-02T09:00:00.000Z"));
        assertThat(week.getEndOfWeek()).isEqualTo(Instant.parse("2022-05-08T09:00:00.000Z"));
    }

    @Test
    public void canGetWeekFromMonday() {
        Instant monday = Instant.parse("2022-05-09T09:00:00.000Z");
        Week week = Week.containing(monday);

        // weeks run Monday to Sunday
        assertThat(week.getStartOfWeek()).isEqualTo(Instant.parse("2022-05-09T09:00:00.000Z"));
        assertThat(week.getEndOfWeek()).isEqualTo(Instant.parse("2022-05-15T09:00:00.000Z"));
    }

    @Test
    public void canGetEachDayOfWeek() {
        Instant monday = Instant.parse("2022-05-09T09:00:00.000Z");
        Week week = Week.containing(monday);
        assertThat(week.getStartOf(DayOfWeek.MONDAY)).isEqualTo(monday);
        assertThat(week.getStartOf(DayOfWeek.TUESDAY)).isEqualTo(Instant.parse("2022-05-10T09:00:00.000Z"));
        assertThat(week.getStartOf(DayOfWeek.WEDNESDAY)).isEqualTo(Instant.parse("2022-05-11T09:00:00.000Z"));
        assertThat(week.getStartOf(DayOfWeek.THURSDAY)).isEqualTo(Instant.parse("2022-05-12T09:00:00.000Z"));
        assertThat(week.getStartOf(DayOfWeek.FRIDAY)).isEqualTo(Instant.parse("2022-05-13T09:00:00.000Z"));
        assertThat(week.getStartOf(DayOfWeek.SATURDAY)).isEqualTo(Instant.parse("2022-05-14T09:00:00.000Z"));
        assertThat(week.getStartOf(DayOfWeek.SUNDAY)).isEqualTo(Instant.parse("2022-05-15T09:00:00.000Z"));
    }
}
