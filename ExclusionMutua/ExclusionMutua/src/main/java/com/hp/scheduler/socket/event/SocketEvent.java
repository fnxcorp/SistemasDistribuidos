package com.hp.scheduler.socket.event;

import java.util.EventObject;

/**
 * This class is used to save an event object, at this point the object consist only of a text stream, which is
 * delimited by pipes.
 *
 * @author Omar
 */
public class SocketEvent extends EventObject {

    String stream;

    /**
     * Creates a Socket event, indicating the source of the event and the text with the corresponding event data.
     *
     * @param source the source of the event
     * @param stream the text data associated to the event
     */
    public SocketEvent(Object source, String stream) {
        super(source);
        this.stream = stream;
    }

    /**
     * Get the text data of the event.
     *
     * @return the text data that the event contains
     */
    public String getStream() {
        return stream;
    }

}
