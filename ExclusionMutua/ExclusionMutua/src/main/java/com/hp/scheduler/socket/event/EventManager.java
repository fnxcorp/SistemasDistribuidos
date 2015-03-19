package com.hp.scheduler.socket.event;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Omar
 */
public class EventManager {

    private final List<SocketEventListener> listeners = new ArrayList<>();

    public synchronized void addEventListener(SocketEventListener l) {
        listeners.add(l);
    }

    public synchronized void removeEventListener(SocketEventListener l) {
        listeners.remove(l);
    }

    public synchronized void eventReceived(String stream) {
        fireReceiveEvent(stream);
    }

    public synchronized void sendEvent(String stream) {
        fireSendEvent(stream);
    }

    private synchronized void fireReceiveEvent(String stream) {
        SocketEvent event = new SocketEvent(this, stream);
        for (SocketEventListener listener : listeners) {
            listener.eventReceived(event);
        }
    }

    private synchronized void fireSendEvent(String stream) {
        SocketEvent event = new SocketEvent(this, stream);
        for (SocketEventListener listener : listeners) {
            listener.sendEvent(event);
        }
    }
}
