package com.hp.scheduler.socket.event;

/**
 *
 * @author Omar
 */
public interface SocketEventListener {

    public void eventReceived(SocketEvent evt);

    public void sendEvent(SocketEvent evt);

}
