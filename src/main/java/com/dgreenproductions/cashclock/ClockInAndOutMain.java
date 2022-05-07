package com.dgreenproductions.cashclock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;

public class ClockInAndOutMain {
    private static final String CLOCK_IN_LABEL = " Clock IN ";
    private static final String CLOCK_OUT_LABEL = " Clock OUT ";
    private static Duration totalTimeThisHour;
    private static Optional<Instant> lastHourlyUpdate = Optional.empty();
    private static Optional<Double> cashThisHour = Optional.empty();


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
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1800,240);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                if (workClock.isClockedIn()) {
                    JOptionPane.showMessageDialog(frame, "You are Clocked In");
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                } else {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setLayout(new GridBagLayout());
        frame.add(outer, NORTH);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = c.weighty = 1.0;

        JPanel topPanel = new JPanel();
        c.gridy = 1;
        outer.add(topPanel, c);

        JButton checkInOutButton = new JButton("Press");
        topPanel.add(checkInOutButton);
        checkInOutButton.setSize(400, 20);
        checkInOutButton.setFont(new Font("Serif", Font.PLAIN, 50));
        checkInOutButton.setText(CLOCK_IN_LABEL);
        checkInOutButton.setBorderPainted(false);
        checkInOutButton.setBackground(Color.RED);
        checkInOutButton.setOpaque(true);
        checkInOutButton.addActionListener(e -> {
            if (workClock.isClockedIn()) {
                System.out.println("CLOCKED OUT " + Instant.now() + " (UTC)");
                workClock.clockOut();
                checkInOutButton.setText(CLOCK_IN_LABEL);
                checkInOutButton.setBackground(Color.RED);
            } else {
                System.out.println("CLOCKED IN " + Instant.now() + " (UTC)");
                workClock.clockIn();
                checkInOutButton.setText(CLOCK_OUT_LABEL);
                checkInOutButton.setBackground(Color.GREEN);
            }
        });

        JLabel hourLabel = new JLabel("Hour");
        hourLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        topPanel.add(hourLabel);

        JLabel todayLabel = new JLabel("Today");
        todayLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        todayLabel.setOpaque(true);
        topPanel.add(todayLabel);

        JLabel monthLabel = new JLabel("This Month");
        monthLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        topPanel.add(monthLabel);

        JPanel bucketSummaryPanel1 = new JPanel();
        bucketSummaryPanel1.setSize(550, 300);
        bucketSummaryPanel1.setFont(new Font("Serif", Font.PLAIN, 15));
        bucketSummaryPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        c.gridy = 2;
        outer.add(bucketSummaryPanel1, c);

        JPanel bucketSummaryPanel2 = new JPanel();
        bucketSummaryPanel2.setSize(550, 300);
        bucketSummaryPanel2.setFont(new Font("Serif", Font.PLAIN, 15));
        bucketSummaryPanel2.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        c.gridy = 3;
        outer.add(bucketSummaryPanel2, c);

        JPanel bottomPanel = new JPanel();
        frame.add(bottomPanel, SOUTH);

        JLabel previousMonthLabel = new JLabel("This Month");
        previousMonthLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        bottomPanel.add(previousMonthLabel);

        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        bottomPanel.add(totalLabel);

        Buckets buckets = new Buckets();
        buckets.add("Rent", 925);
        buckets.addHighlight("After Eight Dark Chocolate Mints", 1.99);
        buckets.add("Food", 250);
        buckets.add("Gas & Electric", 60);
        buckets.add("Water", 20);
        buckets.add("Council Tax", 116);
        buckets.add("Storage", 112.20);
        buckets.add("Pension", 1500);
        buckets.addHighlight("Hue Play Bar", 119.99);
        buckets.add("Holiday", 200);
        buckets.add("Car tax", 200 / 12.0);
        buckets.addHighlight("Carol Anne Dark Chocolate Brazil Nuts", 8.99);
        buckets.add("Car wash", 17);
        buckets.add("Car servicing", 500 / 12.0);
        buckets.add("NOW TV", 35);
        buckets.add("Netflix", 9.99);
        buckets.add("Savings", 4000);
        buckets.addHighlight("Kampa Kielder 4 Air Tent", 599.99);
        buckets.add("Entertainment", 10000);

        Map<String, JLabel> bucketLabelMap = new HashMap<>();
        List<Bucket> bucketList = buckets.getBuckets();
        int bucketCount = 0;
        for (Bucket bucket : bucketList) {
            JLabel bucketLabel = new JLabel(bucket.getName());
            bucketLabel.setOpaque(true);
            bucketLabel.setFont(new Font("Default", Font.PLAIN, 12));
            if (bucketCount < bucketList.size() / 2) {
                bucketSummaryPanel1.add(bucketLabel);
            } else {
                bucketSummaryPanel2.add(bucketLabel);
            }
            bucketCount++;
            bucketLabelMap.put(bucket.getName(), bucketLabel);
        }

        Runnable updateEverySecond = () -> {
            Duration totalTime = workClock.getRunningTotalTime();
            totalLabel.setText(String.format("total: %s, (£%.2f)", formatTotalDuration(totalTime), asCash(totalTime)));

            Duration totalTimeThisMonth = workClock.getRunningTotalTimeThisMonth();
            double totalThisMonthCash = asCash(totalTimeThisMonth);
            monthLabel.setText(String.format("month: %s, (£%.2f)", formatTotalDuration(totalTimeThisMonth), totalThisMonthCash));
            buckets.setTotalCash(totalThisMonthCash);

            List<Bucket> listOfBuckets = buckets.getBuckets();
            for (Bucket bucket : listOfBuckets) {
                JLabel label = bucketLabelMap.get(bucket.getName());
                label.setText(bucket.asText());
                if (bucket.isHighlight()) {
                    label.setFont(new Font("Default", Font.BOLD, 14));
                }
                if (bucket.isFull()) {
                    label.setForeground(new Color(4, 120, 35));
                }
                if (bucket.isEmpty()) {
                    label.setForeground(Color.GRAY);
                }
                if (!bucket.isFull() && !bucket.isEmpty()) {
                    label.setForeground(Color.BLUE);
                }
            }

            Duration totalTimePreviousMonth = workClock.getTotalTimePreviousMonth();
            previousMonthLabel.setText(String.format(" last month: %s, (£%.2f)  ", formatTotalDuration(totalTimePreviousMonth), asCash(totalTimePreviousMonth)));

            try {
                Duration totalTimeToday = workClock.getRunningTotalToday();
                if (Duration.ofHours(8).compareTo(totalTimeToday) <= 0) {
                    todayLabel.setBackground(Color.GREEN);
                }
                todayLabel.setText(String.format("  today: %s, (£%.2f)   ", formatDuration(totalTimeToday), asCash(totalTimeToday)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            totalTimeThisHour = workClock.getRunningTimeThisHour();
            cashThisHour = Optional.of(asCash(totalTimeThisHour));
            lastHourlyUpdate = Optional.of(Instant.now());
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(updateEverySecond, 1, 1, TimeUnit.SECONDS);

        Runnable updateEvery10Millis = () -> {
            if (cashThisHour.isPresent()) {
                Duration sinceLastHourlyUpdate = Duration.between(lastHourlyUpdate.get(), Instant.now());
                Duration total = totalTimeThisHour;
                double cash = cashThisHour.get();
                if (workClock.isClockedIn()) {
                    total = total.plus(sinceLastHourlyUpdate);
                    cash = cash + sinceLastHourlyUpdate.toMillis() * netPerMillisecond;
                }
                hourLabel.setText(String.format("   hour: %s, (£%.4f)   ", formatHourlyDuration(total), cash));
            } else {
                hourLabel.setText("   hour: 0s, (£0.0000)   ");
            }
        };
        executor.scheduleAtFixedRate(updateEvery10Millis, 1, 1, TimeUnit.MILLISECONDS);

        frame.setVisible(true);

        while (true) {
        }
    }

    private static double grossPerDay = 750;
    private static double conversion = 0.5412;
    private static double netPerDay = grossPerDay * conversion;
    private static double netPerHour = netPerDay / 8.0;
    private static double netPerSecond = netPerHour / 3600.0;
    private static double netPerMillisecond = netPerSecond / 1000;

    private static double asCash(Duration duration) {
        return duration.toSeconds() * netPerSecond;
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

    private static String formatHourlyDuration(Duration duration) {
        String asString = "";
        if (duration.compareTo(Duration.ofMinutes(1).minus(Duration.ofSeconds(1))) > 0) {
            asString = asString + String.format(" %sm", duration.toMinutesPart());
        }
        asString = asString + String.format(" %ss %03dms",
                duration.toSecondsPart(), duration.toMillisPart());

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
