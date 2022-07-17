package com.dgreenproductions.cashclock;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dgreenproductions.cashclock.Week.dayOfWeek;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.Color.BLUE;

public class ClockInAndOutMain {
    private static final int LARGE_TEXT_SIZE = 26;
    private static final int MEDIUM_TEXT_SIZE = 18;
    private static final int SMALL_TEXT_SIZE = 10;

    private static final String CLOCK_IN_LABEL = " Clock IN ";
    private static final String CLOCK_OUT_LABEL = " Clock OUT ";
    public static final Color DARK_GREEN = new Color(4, 120, 35);
    private static Duration totalTimeThisHour;
    private static Optional<Instant> lastHourlyUpdate = Optional.empty();
    private static Optional<Double> cashThisHour = Optional.empty();


    public static void main(String[] args) throws InterruptedException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Path path = Path.of("/Users/duncangreen/WarehouseDocuments/By Company/Sky/timelog.txt");

        System.out.println("Cash Clock running...");

        WorkSessionLog workSessionLog = new WorkSessionLog();
        if (Files.exists(path)) {
            LogFileReader reader = new LogFileReader(path);
            reader.readAll((from, to) -> workSessionLog.log(from, to));
        }

        Clock clock = new RealClock();
        WorkClock workClock = new WorkClock(clock, workSessionLog);

        LogFileWriter writer = new LogFileWriter(path);
        SessionListener listener = (start, end) -> writer.appendEntry(start, end);
        workClock.addListener(listener);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame("Cash Clock - DG, Sky");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500,280);

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
        checkInOutButton.setFont(new Font("Serif", Font.PLAIN, LARGE_TEXT_SIZE));
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
        hourLabel.setFont(new Font("Serif", Font.PLAIN, LARGE_TEXT_SIZE));
        topPanel.add(hourLabel);

        JLabel todayLabel = new JLabel("Today");
        todayLabel.setFont(new Font("Serif", Font.PLAIN, LARGE_TEXT_SIZE));
        todayLabel.setOpaque(true);
        topPanel.add(todayLabel);
        Color todayLabelColor = todayLabel.getBackground();

        JLabel monthLabel = new JLabel("This Month");
        monthLabel.setFont(new Font("Serif", Font.PLAIN, LARGE_TEXT_SIZE));
        topPanel.add(monthLabel);

        JPanel bucketSummaryPanel1 = new JPanel();
        bucketSummaryPanel1.setFont(new Font("Serif", Font.PLAIN, SMALL_TEXT_SIZE));
        bucketSummaryPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        c.gridy = 2;
        outer.add(bucketSummaryPanel1, c);

        JPanel bucketSummaryPanel2 = new JPanel();
        bucketSummaryPanel2.setFont(new Font("Serif", Font.PLAIN, SMALL_TEXT_SIZE));
        bucketSummaryPanel2.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        c.gridy = 3;
        outer.add(bucketSummaryPanel2, c);

        JPanel bucketSummaryPanel3 = new JPanel();
        bucketSummaryPanel3.setFont(new Font("Serif", Font.PLAIN, SMALL_TEXT_SIZE));
        bucketSummaryPanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        c.gridy = 4;
        outer.add(bucketSummaryPanel3, c);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        frame.add(bottomPanel, SOUTH);

        c.gridy = 1;
        JPanel dailySummaryPanel = new JPanel();
        bottomPanel.add(dailySummaryPanel, c);

        JLabel mondayLabel = new JLabel("Mon: ");
        mondayLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        mondayLabel.setOpaque(true);
        dailySummaryPanel.add(mondayLabel);

        JLabel tuesdayLabel = new JLabel("Tue: ");
        tuesdayLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        tuesdayLabel.setOpaque(true);
        dailySummaryPanel.add(tuesdayLabel);

        JLabel wednesdayLabel = new JLabel("Wed: ");
        wednesdayLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        wednesdayLabel.setOpaque(true);
        dailySummaryPanel.add(wednesdayLabel);

        JLabel thursdayLabel = new JLabel("Thu: ");
        thursdayLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        thursdayLabel.setOpaque(true);
        dailySummaryPanel.add(thursdayLabel);

        JLabel fridayLabel = new JLabel("Fri: ");
        fridayLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        fridayLabel.setOpaque(true);
        dailySummaryPanel.add(fridayLabel);

        JLabel saturdayLabel = new JLabel("Sat: ");
        saturdayLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        saturdayLabel.setOpaque(true);
        dailySummaryPanel.add(saturdayLabel);

        JLabel sundayLabel = new JLabel("Sun: ");
        sundayLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        sundayLabel.setOpaque(true);
        dailySummaryPanel.add(sundayLabel);

        c.gridy = 2;
        JPanel otherSummaryPanel = new JPanel();
        bottomPanel.add(otherSummaryPanel, c);

        JLabel previousMonthLabel = new JLabel("Last Month");
        previousMonthLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        previousMonthLabel.setOpaque(true);
        otherSummaryPanel.add(previousMonthLabel);

        JLabel contractLabel = new JLabel("Contract");
        contractLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        contractLabel.setOpaque(true);
        otherSummaryPanel.add(contractLabel);

        JLabel perMonthLabel = new JLabel("Per Month");
        perMonthLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        perMonthLabel.setOpaque(true);
        otherSummaryPanel.add(perMonthLabel);
        perMonthLabel.setText(String.format("£/m £%,.2f  ", netPerMonth));

        JLabel perWeekLabel = new JLabel("Per Week");
        perWeekLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        perWeekLabel.setOpaque(true);
        otherSummaryPanel.add(perWeekLabel);
        perWeekLabel.setText(String.format("£/wk £%,.2f  ", netPerWeek));

        JLabel perDayLabel = new JLabel("Per Day");
        perDayLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        perDayLabel.setOpaque(true);
        otherSummaryPanel.add(perDayLabel);
        perDayLabel.setText(String.format("£/d £%,.2f  ", netPerDay));

        JLabel perHourLabel = new JLabel("Per Hour");
        perHourLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        perHourLabel.setOpaque(true);
        otherSummaryPanel.add(perHourLabel);
        perHourLabel.setText(String.format("£/hr £%,.2f  ", netPerHour));

        JLabel perMinuteLabel = new JLabel("Per Minute");
        perMinuteLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        perMinuteLabel.setOpaque(true);
        otherSummaryPanel.add(perMinuteLabel);
        perMinuteLabel.setText(String.format("£/m £%,.3f ", netPerMinute));

        JLabel perSecondLabel = new JLabel("Per Second");
        perSecondLabel.setFont(new Font("Serif", Font.PLAIN, MEDIUM_TEXT_SIZE));
        perSecondLabel.setOpaque(true);
        otherSummaryPanel.add(perSecondLabel);
        perSecondLabel.setText(String.format("£/s £%,.4f", netPerSecond));

        Buckets buckets = new Buckets();
        buckets.add("Rent", 925);
        buckets.addHighlight("Carol Anne Dark Chocolate Brazil Nuts", 8.99);
        buckets.add("Food", 250);
        buckets.add("Gas & Electric", 60);
        buckets.add("Water", 20);
        buckets.add("Council Tax", 116);
        buckets.add("Car tax", 200 / 12.0);
        buckets.add("Storage", 112.20);
        buckets.add("Pension", 1500);
        buckets.addHighlight("Horizon Forbidden West", 69.99);
        buckets.add("Holiday", 200);
        buckets.add("Car wash", 17);
        buckets.add("Car servicing", 500 / 12.0);
        buckets.add("Netflix", 9.99);
        buckets.add("Savings", 4000);
        buckets.addHighlight("Kampa Kielder 4 Air Tent", 599.99);
        buckets.add("Remainder", 10000);

        Map<String, JLabel> bucketLabelMap = new HashMap<>();
        List<Bucket> bucketList = buckets.getBuckets();
        int bucketCount = 0;
        int groupSize = bucketList.size() / 3;

        for (Bucket bucket : bucketList) {
            JLabel bucketLabel = new JLabel(bucket.getName());
            bucketLabel.setOpaque(true);
            bucketLabel.setFont(new Font("Default", Font.PLAIN, SMALL_TEXT_SIZE));
            if (bucketCount < groupSize) {
                bucketSummaryPanel1.add(bucketLabel);
            } else {
                if (bucketCount >= groupSize && bucketCount < 2 * groupSize ) {
                    bucketSummaryPanel2.add(bucketLabel);
                } else {
                    bucketSummaryPanel3.add(bucketLabel);
                }
            }
            bucketCount++;
            bucketLabelMap.put(bucket.getName(), bucketLabel);
        }

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable updateEveryMinute = () -> {
            List<String> lines = new ArrayList<>();
            for (Bucket bucket : bucketList) {
                lines.add(String.format("%s, £%,.2f, £%,.2f", bucket.getName(), bucket.getContents(), bucket.getCapacity()));
            }

            // write buckets to file
            LocalDateTime localNow = LocalDateTime.ofInstant(clock.getCurrentTime(), ZoneOffset.UTC);
            Month month = localNow.getMonth();
            Integer year = localNow.getYear();
            Path bucketPath = Path.of(String.format("/Users/duncangreen/WarehouseDocuments/By Company/Sky/%s-%d.txt", month.getDisplayName(TextStyle.SHORT, Locale.UK), year));
            try {
                FileUtils.writeLines(bucketPath.toFile(), "UTF8", lines, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        executor.scheduleAtFixedRate(updateEveryMinute, 1, 1, TimeUnit.MINUTES);

        Runnable updateEverySecond = () -> {
            Duration totalTime = workClock.getRunningTotalTime();
            contractLabel.setText(String.format("  contract: %s, () ", formatTotalDuration(totalTime), asCash(totalTime)));

            Duration totalTimeThisMonth = workClock.getRunningTotalTimeThisMonth();
            double totalThisMonthCash = asCash(totalTimeThisMonth);
            monthLabel.setText(String.format("  month: %s, (£%,.2f)", formatTotalDuration(totalTimeThisMonth), totalThisMonthCash));

            setDaySummary(workClock, clock, DayOfWeek.MONDAY, mondayLabel, "mon");
            setDaySummary(workClock, clock, DayOfWeek.TUESDAY, tuesdayLabel, "tue");
            setDaySummary(workClock, clock, DayOfWeek.WEDNESDAY, wednesdayLabel, "wed");
            setDaySummary(workClock, clock, DayOfWeek.THURSDAY, thursdayLabel, "thu");
            setDaySummary(workClock, clock, DayOfWeek.FRIDAY, fridayLabel, "fri");
            setDaySummary(workClock, clock, DayOfWeek.SATURDAY, saturdayLabel, "sat");
            setDaySummary(workClock, clock, DayOfWeek.SUNDAY, sundayLabel, "sun");


            buckets.setTotalCash(totalThisMonthCash);
            List<Bucket> listOfBuckets = buckets.getBuckets();
            for (Bucket bucket : listOfBuckets) {
                JLabel label = bucketLabelMap.get(bucket.getName());
                label.setText(bucket.asText());
                if (bucket.isHighlight()) {
                    label.setFont(new Font("Default", Font.BOLD, SMALL_TEXT_SIZE+2));
                }
                if (bucket.isFull()) {
                    label.setForeground(DARK_GREEN);
                }
                if (bucket.isEmpty()) {
                    label.setForeground(Color.GRAY);
                }
                if (!bucket.isFull() && !bucket.isEmpty()) {
                    label.setForeground(BLUE);
                }
            }

            Duration totalTimePreviousMonth = workClock.getTotalTimePreviousMonth();
            previousMonthLabel.setText(String.format("last month: %s, (£%,.2f) ", formatTotalDuration(totalTimePreviousMonth), asCash(totalTimePreviousMonth)));

            try {
                Duration totalTimeToday = workClock.getRunningTotalToday();
                if (Duration.ofHours(8).compareTo(totalTimeToday) <= 0) {
                    todayLabel.setBackground(Color.GREEN);
                } else {
                    todayLabel.setBackground(todayLabelColor);
                }
                double percentTodayWorked = (double)totalTimeToday.toMillis() / Duration.ofHours(8).toMillis() * 100.0;
                todayLabel.setText(String.format("  today: %s (%.1f%%), (£%,.2f)", formatDuration(totalTimeToday), percentTodayWorked, asCash(totalTimeToday)));

                if (percentTodayWorked >= 80) {
                    outer.setBackground(Color.PINK);
                } else {
                    outer.setBackground(todayLabelColor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            totalTimeThisHour = workClock.getRunningTimeThisHour();
            cashThisHour = Optional.of(asCash(totalTimeThisHour));
            lastHourlyUpdate = Optional.of(Instant.now());
        };

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
                hourLabel.setText(String.format("   hour: %s, (£%,.4f)", formatHourlyDuration(total), cash));
            } else {
                hourLabel.setText("   hour: 0s, (£0.0000)");
            }
        };
        executor.scheduleAtFixedRate(updateEvery10Millis, 1, 1, TimeUnit.MILLISECONDS);

        frame.setVisible(true);

        while (true) {
        }
    }

    private static void setDaySummary(WorkClock workClock, Clock clock, DayOfWeek dayOfWeek, JLabel label, String dayText) {
        Duration duration = workClock.getDayRunningTotal(dayOfWeek);
        if (dayOfWeek(clock.getCurrentTime()) == dayOfWeek) {
            label.setForeground(BLUE);
        } else {
            if (duration.compareTo(Duration.ofHours(8)) >= 0) {
                label.setForeground(DARK_GREEN);
            } else {
                label.setForeground(Color.GRAY);
            }
        }
        label.setText(String.format("%s: %s, (£%,.2f)  ", dayText, formatTotalDuration(duration), asCash(duration)));
    }

    private static double grossPerDay = 750;
    private static double conversion = 0.548295;
    private static double netPerDay = grossPerDay * conversion;
    private static double netPerWeek = netPerDay * 5;
    private static double netPerMonth = netPerWeek * 46 / 12.0;
    private static double netPerHour = netPerDay / 8.0;
    private static double netPerMinute = netPerHour / 60.0;
    private static double netPerSecond = netPerMinute / 60.0;
    private static double netPerMillisecond = netPerSecond / 1000;

    private static double asCash(Duration duration) {
        return duration.toSeconds() * netPerSecond;
    }

    private static String formatTotalDuration(Duration duration) {
        double percentTodayWorked = (double)duration.toMillis() / Duration.ofHours(8).toMillis();
        return String.format("%.2f days", percentTodayWorked);
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
}
