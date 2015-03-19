package com.hp.scheduler.socket.event;

import java.util.EventObject;

/**
 *
 * @author Omar
 */
public class SocketEvent extends EventObject {

    String stream;

    public SocketEvent(Object source, String stream) {
        super(source);
        this.stream = stream;
    }

    public String getStream() {
        return stream;
    }

}
