package com.dgreenproductions.cashclock;

import java.time.Duration;

public class DurationFormats {
    public static String formatDuration(Duration duration) {
        String asString = "";
        Duration absDuration = duration.abs();

        if (absDuration.compareTo(Duration.ofDays(1).minus(Duration.ofSeconds(1))) > 0) {
            asString = asString + String.format("%sd", absDuration.toDaysPart());
        }
        if (absDuration.compareTo(Duration.ofHours(1).minus(Duration.ofSeconds(1))) > 0) {
            asString = asString + String.format(" %sh", absDuration.toHoursPart());
        }
        if (absDuration.compareTo(Duration.ofMinutes(1).minus(Duration.ofSeconds(1))) > 0) {
            asString = asString + String.format(" %sm", absDuration.toMinutesPart());
        }

        asString = asString + String.format(" %ss", absDuration.toSecondsPart());

        if (asString.startsWith(" ")) {
            asString = asString.substring(1);
        }
        if (duration.compareTo(Duration.ZERO) > 0) {
            return asString;
        } else {
            return "-(" + asString + ")";
        }
    }

    public static String formatTotalDuration(Duration duration) {
        double percentTodayWorked = (double)duration.toMillis() / Duration.ofHours(8).toMillis();
        return String.format("%.2f days", percentTodayWorked);
    }

    public static String formatHourlyDuration(Duration duration) {
        String asString = "";
        if (duration.compareTo(Duration.ofMinutes(1).minus(Duration.ofSeconds(1))) > 0) {
            asString = asString + String.format(" %sm", duration.toMinutesPart());
        }
        asString = asString + String.format(" %ss %03dms",
                duration.toSecondsPart(), duration.toMillisPart());

        return asString;
    }
}
