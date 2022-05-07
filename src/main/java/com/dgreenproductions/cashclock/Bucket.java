package com.dgreenproductions.cashclock;

public class Bucket {
    private String name;
    private double capacity;
    private double contents;

    public Bucket(String name, double capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getContents() {
        return contents;
    }

    public void setContents(double contents) {
        this.contents = contents;
    }

    public String asText() {
        return String.format("%s (£%.2f)", name, contents);
    }

    public boolean isFull() {
        return contents == capacity;
    }

    public boolean isEmpty() {
        return contents == 0;
    }
}
