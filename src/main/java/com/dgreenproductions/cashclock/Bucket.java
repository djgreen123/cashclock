package com.dgreenproductions.cashclock;

public class Bucket {
    private String name;
    private double capacity;
    private boolean highlight;
    private double contents;

    public Bucket(String name, double capacity, boolean highlight) {
        this.name = name;
        this.capacity = capacity;
        this.highlight = highlight;
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
        return String.format("%s (£%.2f)", name, capacity - contents);
    }

    public boolean isFull() {
        return contents == capacity;
    }

    public boolean isEmpty() {
        return contents == 0;
    }

    public boolean isHighlight() {
        return highlight;
    }
}
