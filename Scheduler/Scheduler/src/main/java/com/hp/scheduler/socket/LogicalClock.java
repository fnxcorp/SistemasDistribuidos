package com.hp.scheduler.socket;

/**
 *
 * @author Omar
 */
public interface LogicalClock {

    public int getValue();

    public void tick();

    public void sendAction();

    public void receiveAction(int src);

}
