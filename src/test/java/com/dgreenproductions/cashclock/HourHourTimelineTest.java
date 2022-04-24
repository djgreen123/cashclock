package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

import static org.fest.assertions.Assertions.assertThat;

public class HourHourTimelineTest {

    @Test
    public void atZeroSeconds() {
        int anyInt = 60 * 60;
        HourTimeline timeline = new HourTimeline(new TestClock(Instant.parse("2018-05-12T20:00:00.000Z")), anyInt);
        assertThat(timeline.get()).isEqualTo(0);
    }

    @Test
    public void atOneSecond() {
        HourTimeline timeline = new HourTimeline(new TestClock(Instant.parse("2018-05-12T20:00:01.000Z")), Duration.ofHours(1).getSeconds());
        assertThat(timeline.get()).isEqualTo(1);
    }

    @Test
    public void atHourPastTheHoud() {
        long total = Duration.ofHours(1).getSeconds();
        HourTimeline timeline = new HourTimeline(new TestClock(Instant.parse("2018-05-12T20:30:00.000Z")), total);
        assertThat(timeline.get()).isEqualTo(total / 2.0);
    }

    @Test
    public void atLastSecondOfTheHour() {
        long total = Duration.ofHours(1).getSeconds();
        HourTimeline timeline = new HourTimeline(new TestClock(Instant.parse("2018-05-12T20:59:59.000Z")), total);
        assertThat(timeline.get()).isEqualTo(total -1);
    }

    @Test
    public void rolloverToNextHour() {
        long total = Duration.ofHours(1).getSeconds();
        TestClock clock = new TestClock(Instant.parse("2018-05-12T20:59:59.000Z"));
        HourTimeline timeline = new HourTimeline(clock, total);
        clock.advance(Duration.ofSeconds(1));
        assertThat(timeline.get()).isEqualTo(0);
    }

//    @Test
//    public void beforeEarningsWindow() {
//        long total = Duration.ofHours(1).getSeconds();
//        TestClock clock = new TestClock(Instant.parse("2018-05-12T09:29:59.000Z"));
//        EarningsWindow earningsWindow = new EarningsWindow(LocalTime.of(9, 30), LocalTime.of(18, 30));
//        HourTimeline timeline = new HourTimeline(earningsWindow, clock, total);
//        assertThat(timeline.get()).isEqualTo(0);
//    }

    // TimeLog.txt
    // 2022-04-25T09:29:00.000Z, START, SKY
    // 2022-04-25T09:45:00.000Z, STOP, SKY
}
