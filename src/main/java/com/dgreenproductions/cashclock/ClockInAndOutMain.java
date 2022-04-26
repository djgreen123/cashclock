package com.dgreenproductions.cashclock;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClockInAndOutMain {

    public static void main(String[] args) {
        Path path = Path.of("/Users/duncangreen/WarehouseDocuments/By Company/Sky/timelog.txt");
        LogFileReader reader = new LogFileReader(path);
        LogFileWriter writer = new LogFileWriter(path);
        Timeline timeline = new Timeline(Instant.now());
        InMemoryTimeLog timeLog = new InMemoryTimeLog(reader.readEntries(), (from, to) -> writer.writeEntry(from, to));
        WorkTracker workTracker = new WorkTracker(timeline, timeLog);

        Runnable runnable = () -> timeline.advanceBySeconds(1);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);

        System.out.println("Work Tracker running...");
        Scanner keyboard = new Scanner(System.in);
        while (true) {
            String input = keyboard.nextLine();
            if (workTracker.isClockedIn()) {
                System.out.println("CLOCKED OUT " + Instant.now() + " (UTC)");
                workTracker.clockOut();
            } else {
                System.out.println("CLOCKED IN " + Instant.now() + " (UTC)");
                workTracker.clockIn();
            }
        }
    }

//    public static void main(String[] args) {
//        int dailyNet = 750;
//        double conversion = 0.554;
//        double dailyGross = dailyNet * conversion;
//        double hourlyGross = dailyGross / 8;
//        double secondlyGross = hourlyGross / (60.0 * 60.0);
//        double millisecondGross = secondlyGross / 1000.0;
//
//        long total = 235;
//        long startTime = System.currentTimeMillis();
//
//        for (int i = 1; i <= total; i = i + 3) {
//            try {
//                Thread.sleep(50);
//                printProgress(startTime, total, i);
//            } catch (InterruptedException e) {
//            }
//        }
//    }

    private static void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0 :
                (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A" :
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string
                .append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }
}