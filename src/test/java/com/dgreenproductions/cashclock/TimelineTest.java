package com.dgreenproductions.cashclock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.fest.assertions.Assertions.assertThat;

public class TimelineTest {

    @Test
    public void canCancelFutureActions() {
        Instant startTime = Instant.now();
        Timeline timeline = new Timeline(startTime);
        TestAction action = new TestAction(startTime.plus(Duration.ofSeconds(10)));
        timeline.schedule(action);
        timeline.cancelFutureActions();
        timeline.advanceBySeconds(20);
        assertThat(action.wasPerformed()).isFalse();
    }

    @Test
    public void canBeAdvancedBySeconds() {
        Instant now = Instant.now();
        Timeline timeline = new Timeline(now);
        timeline.advanceBySeconds(1);
        assertThat(timeline.getCurrentTime()).isEqualTo(now.plusSeconds(1));
    }

    @Test
    public void hasInitialTime() {
        Instant now = Instant.now();
        Timeline timeline = new Timeline(now);
        assertThat(timeline.getCurrentTime()).isEqualTo(now);
    }

    @Test
    public void canSetCurrentTime() {
        Instant startTime = Instant.now();
        Timeline timeline = new Timeline(startTime);
        Instant time = startTime.plusSeconds(10);
        timeline.advanceTimeTo(time);
        assertThat(timeline.getCurrentTime()).isEqualTo(time);
    }

    @Test
    public void cannotAdvanceTimeEarlierThanCurrentTime() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Instant startTime = Instant.now();
            Timeline timeline = new Timeline(startTime);
            Instant time = startTime.minusSeconds(1);
            timeline.advanceTimeTo(time);
        });
    }

    @Test
    public void actionCannotBeScheduledInThePast() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Instant startTime = Instant.now();
            Timeline timeline = new Timeline(startTime);
            timeline.schedule(new TestAction(startTime.minusSeconds(1)));
        });
    }

    @Test
    public void actionCannotBeScheduledAtCurrentTime() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Instant startTime = Instant.now();
            Timeline timeline = new Timeline(startTime);
            timeline.schedule(new TestAction(startTime.minusSeconds(1)));
        });
    }

    @Test
    public void actionDueAtCurrentTimeIsPerformedImmediately() {
        Instant startTime = Instant.now();
        Timeline timeline = new Timeline(startTime);
        Instant actionTime = startTime;
        TestAction action = new TestAction(actionTime);
        timeline.schedule(action);
        assertThat(action.wasPerformed()).isTrue();
    }

    @Test
    public void aDueActionIsExecuted() {
        Instant startTime = Instant.now();
        Timeline timeline = new Timeline(startTime);

        Instant actionTime = startTime.plusSeconds(10);
        TestAction action = new TestAction(actionTime);

        timeline.schedule(action);

        assertThat(action.wasPerformed()).isFalse();
        timeline.advanceTimeTo(actionTime);
        assertThat(action.wasPerformed()).isTrue();
    }

    @Test
    public void aDueActionIsNotExecutedMoreThanOnce() {
        Instant startTime = Instant.now();
        Timeline timeline = new Timeline(startTime);

        Instant actionTime = startTime.plusSeconds(10);
        TestAction action = new TestAction(actionTime);

        timeline.schedule(action);

        timeline.advanceTimeTo(actionTime);
        timeline.advanceTimeTo(actionTime.plusSeconds(1));
        assertThat(action.getPerformedCount()).isEqualTo(1);
    }

    @Test
    public void multipleDueActionsAreExecuted() {
        Instant startTime = Instant.now();
        Timeline timeline = new Timeline(startTime);

        TestAction action1 = new TestAction(startTime.plusSeconds(10));
        TestAction action2 = new TestAction(startTime.plusSeconds(11));

        timeline.schedule(action1);
        timeline.schedule(action2);

        timeline.advanceTimeTo(action2.getWhen());
        assertThat(action1.wasPerformed()).isTrue();
        assertThat(action2.wasPerformed()).isTrue();
    }


    private class TestAction extends Action {
        private int performedCount = 0;

        public TestAction(Instant when) {
            super(when);
        }

        @Override
        public void perform() {
            performedCount = performedCount + 1;
        }

        public boolean wasPerformed() {
            return performedCount > 0;
        }

        public int getPerformedCount() {
            return performedCount;
        }
    }
}