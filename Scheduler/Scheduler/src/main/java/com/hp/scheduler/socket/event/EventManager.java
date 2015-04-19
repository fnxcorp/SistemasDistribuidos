package com.hp.scheduler.socket.event;

import java.util.List;
import java.util.ArrayList;

/**
 * This class is the main event manager, there should be one per process, this class is used to register services, which
 * once registered will be listening for incoming events or send its own events to other processes.
 *
 * @author Omar
 */
public class EventManager {

    private final List<SocketEventListener> listeners = new ArrayList<>();

    /**
     * Adds a service to the listening list.
     *
     * @param l the service that will want to listen and send events.
     */
    public synchronized void addEventListener(SocketEventListener l) {
        listeners.add(l);
    }

    /**
     * Removes an service from the listening list, so it won't be able to send and receive more events.
     *
     * @param l the service that will be removed.
     */
    public synchronized void removeEventListener(SocketEventListener l) {
        listeners.remove(l);
    }

    /**
     * This method is fired each time an event is received.
     *
     * @param stream the text data for the event
     */
    public synchronized void eventReceived(String stream) {
        fireReceiveEvent(stream);
    }

    /**
     * Used to send an event to another process.
     *
     * @param stream the text data associated to the event
     */
    public synchronized void sendEvent(String stream) {
        fireSendEvent(stream);
    }

    /**
     * For every event that is received the List with listeners is traversed so it can send the event to each of the
     * registered services.
     *
     * @param stream the text data for the event
     */
    private synchronized void fireReceiveEvent(String stream) {
        SocketEvent event = new SocketEvent(this, stream);
        for (SocketEventListener listener : listeners) {
            listener.eventReceived(event);
        }
    }

    /**
     * Once a service sends an event, this event is dispatched to every registered service, in case they want to do
     * something else with that data.
     *
     * @param stream the text data associated to the event
     */
    private synchronized void fireSendEvent(String stream) {
        SocketEvent event = new SocketEvent(this, stream);
        for (SocketEventListener listener : listeners) {
            listener.sendEvent(event);
        }
    }
}
