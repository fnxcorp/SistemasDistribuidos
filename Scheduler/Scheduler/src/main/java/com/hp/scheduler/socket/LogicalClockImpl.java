package com.hp.scheduler.socket;

/**
 * Class to implement a basic logical clock.
 *
 * @author Omar
 */
public class LogicalClockImpl implements LogicalClock {

    int c;

    /**
     * Starts the logical clock with 0
     */
    public LogicalClockImpl() {
        c = 0;
    }

    /**
     * Get the clock value.
     *
     * @return the current value of the clock
     */
    @Override
    public int getValue() {
        return c;
    }

    /**
     * Increases the clock value by 1.
     */
    @Override
    public void tick() {
        c = c + 1;
    }

    /**
     * Simulates a send action, which means do a tick.
     */
    @Override
    public void sendAction() {
        tick();
    }

    /**
     * Updates the clock with the max value between the internal clock and the value that came from the process that
     * originated the message.
     *
     * @param src the value of the logical clock of the process who sent a message
     */
    @Override
    public void receiveAction(int src) {
        c = Math.max(c, src);
        tick();
    }

}
