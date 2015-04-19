package com.hp.scheduler.socket.event;

/**
 * This interface should be implemented by classes which want to be services and be able to get and send events.
 *
 * @author Omar
 */
public interface SocketEventListener {

    public void eventReceived(SocketEvent evt);

    public void sendEvent(SocketEvent evt);

}
