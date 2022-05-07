package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.fest.assertions.Assertions.assertThat;

public class TestBuckets {

    private Buckets buckets;

    @BeforeEach
    void setUp() {
        buckets = new Buckets();
    }

    @Test
    public void testSingleBucketZeroCash() {
        buckets.add("Rent", 950);
        buckets.setTotalCash(0);
        assertThat(buckets.get("Rent")).isEqualTo(0.0);
    }

    @Test
    public void testSingleBucketNotEmptyNotFull() {
        buckets.add("Rent", 950);
        buckets.setTotalCash(403.52);
        assertThat(buckets.get("Rent")).isEqualTo(403.52);
    }

    @Test
    public void testSingleBucketFull() {
        buckets.add("Rent", 950);
        buckets.setTotalCash(960.33);
        assertThat(buckets.get("Rent")).isEqualTo(950);
    }

    @Test
    public void twoBucketsFirstHalfFull() {
        buckets.add("Rent", 950);
        buckets.add("Pension", 1500);
        buckets.setTotalCash(499);
        assertThat(buckets.get("Rent")).isEqualTo(499);
        assertThat(buckets.get("Pension")).isEqualTo(0);
    }

    @Test
    public void twoBucketsFirstIsFullSecondIsHalfFull() {
        buckets.add("Rent", 950);
        buckets.add("Pension", 1500);
        buckets.setTotalCash(1035);
        assertThat(buckets.get("Rent")).isEqualTo(950);
        assertThat(buckets.get("Pension")).isEqualTo(85);
    }
}
