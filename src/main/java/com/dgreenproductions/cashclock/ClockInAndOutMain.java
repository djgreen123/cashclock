package com.dgreenproductions.cashclock;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClockInAndOutMain {

    public static void main(String[] args) throws InterruptedException {
        Path path = Path.of("/Users/duncangreen/WarehouseDocuments/By Company/Sky/timelog.txt");

        System.out.println("Work Tracker running...");

        WorkSessionLog workSessionLog = new WorkSessionLog();
        if (Files.exists(path)) {
            LogFileReader2 reader = new LogFileReader2(path);
            reader.readAll((from, to) -> workSessionLog.log(from, to));
        }

        WorkClock workClock = new WorkClock(new RealClock(), workSessionLog);

        LogFileWriter writer = new LogFileWriter(path);
        SessionListener listener = (start, end) -> writer.appendEntry(start, end);
        workClock.addListener(listener);

        JFrame frame = new JFrame("My First GUI");
        frame.setLayout(new GridLayout(5, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,350);
        JButton button1 = new JButton("Press");
        button1.setFont(new Font("Serif", Font.PLAIN, 72));
        button1.setSize(500, 100);
        button1.setText("Clock IN");
        button1.setBackground(Color.RED);
        button1.setOpaque(true);
        button1.addActionListener(e -> {
            if (workClock.isClockedIn()) {
                System.out.println("CLOCKED OUT " + Instant.now() + " (UTC)");
                workClock.clockOut();
                button1.setText("Clock IN");
                button1.setBackground(Color.RED);
            } else {
                System.out.println("CLOCKED IN " + Instant.now() + " (UTC)");
                workClock.clockIn();
                button1.setText("Clock OUT");
                button1.setBackground(Color.GREEN);
            }
        });
        frame.getContentPane().add(button1);

        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        frame.getContentPane().add(totalLabel);

        JLabel monthLabel = new JLabel("Month");
        monthLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        frame.getContentPane().add(monthLabel);

        JLabel todayLabel = new JLabel("Today");
        todayLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        frame.getContentPane().add(todayLabel);

        JLabel hourLabel = new JLabel("Hour");
        hourLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        frame.getContentPane().add(hourLabel);

        Runnable update = () -> {

            Duration totalTime = workClock.getTotalTime();
            totalLabel.setText(String.format("total: %s, (£%.2f)", formatTotalDuration(totalTime), asCash(totalTime)));

            Duration totalTimeThisMonth = workClock.getTotalTimeThisMonth();
            monthLabel.setText(String.format("month: %s, (£%.2f)", formatTotalDuration(totalTimeThisMonth), asCash(totalTimeThisMonth)));

            Duration totalTimeToday = workClock.getTotalTimeToday();
            todayLabel.setText(String.format("today: %s, (£%.2f)", formatDuration(totalTimeToday), asCash(totalTimeToday)));

            Duration totalTimeThisHour = workClock.getTotalTimeThisHour();
            hourLabel.setText(String.format("hour: %s, (£%.2f)", formatDuration(totalTimeThisHour), asCash(totalTimeThisHour)));
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(update, 1, 1, TimeUnit.SECONDS);

        frame.setVisible(true);

        while (true) {
        }
    }

    private static double asCash(Duration duration) {
        double grossPerDay = 750;
        double conversion = 0.5412;
        double netPerDay = grossPerDay * conversion;
        double netPerHour = netPerDay / 8.0;
        double netPerSecond = netPerHour / 3600.0;
        double cash = duration.toSeconds() * netPerSecond;
        return cash;
    }

    private static String formatTotalDuration(Duration duration) {
        long hours = duration.toHours();
        return String.format("%s days", hours / 8.0);
    }

    private static String formatDuration(Duration duration) {
        String asString = "";
        if (duration.compareTo(Duration.ofDays(1).minus(Duration.ofSeconds(1))) > 0) {
            asString = asString + String.format("%sd", duration.toDaysPart());
        }
        if (duration.compareTo(Duration.ofHours(1).minus(Duration.ofSeconds(1))) > 0) {
            asString = asString + String.format(" %sh", duration.toHoursPart());
        }
        if (duration.compareTo(Duration.ofMinutes(1).minus(Duration.ofSeconds(1))) > 0) {
            asString = asString + String.format(" %sm", duration.toMinutesPart());
        }
        asString = asString + String.format(" %ss",
                duration.toSecondsPart());

        return asString;
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
