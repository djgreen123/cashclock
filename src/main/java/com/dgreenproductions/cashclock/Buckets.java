package com.dgreenproductions.cashclock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Buckets {
    private List<Bucket> buckets = new ArrayList<>();

    public void add(String bucketName, double capacity) {
        buckets.add(new Bucket(bucketName, capacity, false));
    }

    public void addHighlight(String bucketName, double capacity) {
        buckets.add(new Bucket(bucketName, capacity, true));
    }

    public double get(String bucketName) {
        Optional<Bucket> bucket = buckets.stream().filter(b -> b.getName().equals(bucketName)).findFirst();
        return bucket.map(Bucket::getContents).orElse(0.0);
    }

    public void setTotalCash(double cash) {
        double cashRemaining = cash;
        for (Bucket bucket : buckets) {
            if (cashRemaining == 0) break;
            if (bucket.getCapacity() <= cashRemaining) {
                bucket.setContents(bucket.getCapacity());
                cashRemaining = cashRemaining - bucket.getCapacity();
            } else {
                bucket.setContents(cashRemaining);
                cashRemaining = 0;
            }
        }
    }

    public String getSummaryText() {
        StringBuilder builder = new StringBuilder();
        for (Bucket bucket : buckets) {
            builder.append(bucket.asText() + ", ");
        }
        return builder.toString();
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }
}
