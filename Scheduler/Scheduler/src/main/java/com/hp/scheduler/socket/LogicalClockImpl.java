package com.hp.scheduler.socket;

/**
 *
 * @author Omar
 */
public class LogicalClockImpl implements LogicalClock {

    int c;

    public LogicalClockImpl() {
        c = 0;
    }

    @Override
    public int getValue() {
        return c;
    }

    @Override
    public void tick() {
        c = c + 1;
    }

    @Override
    public void sendAction() {
        tick();
    }

    @Override
    public void receiveAction(int src) {
        c = Math.max(c, src);
        tick();
    }

}
