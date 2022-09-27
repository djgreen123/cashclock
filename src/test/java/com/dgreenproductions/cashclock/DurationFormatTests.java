package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.fest.assertions.Assertions.assertThat;

public class DurationFormatTests {

    @Test
    public void difference() {
        Duration d1 = Duration.ofSeconds(10);
        Duration d2 = Duration.ofSeconds(5);

        String formatted = DurationFormats.formatDuration(d1.minus(d2));
        assertThat(formatted).isEqualTo("5s");
    }

    @Test
    public void negativeSeconds() {
        assertThat(DurationFormats.formatDuration(Duration.ofSeconds(10).negated())).isEqualTo("-(10s)");
    }

    @Test
    public void negativeMinsAndSeconds() {
        assertThat(DurationFormats.formatDuration(Duration.ofSeconds(61).negated())).isEqualTo("-(1m 1s)");
    }

    @Test
    public void negativeHoursMinsAndSeconds() {
        assertThat(DurationFormats.formatDuration(Duration.ofHours(2).plus(Duration.ofMinutes(32)).plus(Duration.ofSeconds(12)).negated())).isEqualTo("-(2h 32m 12s)");
    }

}
