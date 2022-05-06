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

    public static void main(String[] args) throws InterruptedException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Path path = Path.of("/Users/duncangreen/WarehouseDocuments/By Company/Sky/timelog.txt");

        System.out.println("Work Tracker running...");

        WorkSessionLog workSessionLog = new WorkSessionLog();
        if (Files.exists(path)) {
            LogFileReader reader = new LogFileReader(path);
            reader.readAll((from, to) -> workSessionLog.log(from, to));
        }

        WorkClock workClock = new WorkClock(new RealClock(), workSessionLog);

        LogFileWriter writer = new LogFileWriter(path);
        SessionListener listener = (start, end) -> writer.appendEntry(start, end);
        workClock.addListener(listener);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame("Work Clock - DG, Sky");
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550,400);
        JButton checkInOutButton = new JButton("Press");
        frame.add(checkInOutButton);
        checkInOutButton.setFont(new Font("Serif", Font.PLAIN, 50));
        checkInOutButton.setSize(800, 200);
        checkInOutButton.setText("Clock IN");
        checkInOutButton.setBorderPainted(false);
        checkInOutButton.setBackground(Color.RED);
        checkInOutButton.setOpaque(true);
        checkInOutButton.addActionListener(e -> {
            if (workClock.isClockedIn()) {
                System.out.println("CLOCKED OUT " + Instant.now() + " (UTC)");
                workClock.clockOut();
                checkInOutButton.setText("Clock IN");
                checkInOutButton.setBackground(Color.RED);
            } else {
                System.out.println("CLOCKED IN " + Instant.now() + " (UTC)");
                workClock.clockIn();
                checkInOutButton.setText("Clock OUT");
                checkInOutButton.setBackground(Color.GREEN);
            }
        });

        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        frame.add(totalLabel);

        JLabel monthLabel = new JLabel("This Month");
        monthLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        frame.add(monthLabel);

        JLabel previousMonthLabel = new JLabel("This Month");
        previousMonthLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        frame.add(previousMonthLabel);

        JLabel todayLabel = new JLabel("Today");
        todayLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        todayLabel.setOpaque(true);
        frame.add(todayLabel);

        JLabel hourLabel = new JLabel("Hour");
        hourLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        frame.add(hourLabel);

        Runnable update = () -> {

            Duration totalTime = workClock.getRunningTotalTime();
            totalLabel.setText(String.format("total: %s, (£%.2f)", formatTotalDuration(totalTime), asCash(totalTime)));

            Duration totalTimeThisMonth = workClock.getRunningTotalTimeThisMonth();
            monthLabel.setText(String.format("this month: %s, (£%.2f)", formatTotalDuration(totalTimeThisMonth), asCash(totalTimeThisMonth)));

            Duration totalTimePreviousMonth = workClock.getTotalTimePreviousMonth();
            previousMonthLabel.setText(String.format("last month: %s, (£%.2f)", formatTotalDuration(totalTimePreviousMonth), asCash(totalTimePreviousMonth)));

            Duration totalTimeToday = workClock.getRunningTotalToday();
            if (Duration.ofHours(8).compareTo(totalTimeToday) <= 0) {
                todayLabel.setBackground(Color.GREEN);
            }
            todayLabel.setText(String.format("today: %s, (£%.2f)", formatDuration(totalTimeToday), asCash(totalTimeToday)));

            Duration totalTimeThisHour = workClock.getRunningTimeThisHour();
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
        return String.format("%.2f days", hours / 8.0);
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
