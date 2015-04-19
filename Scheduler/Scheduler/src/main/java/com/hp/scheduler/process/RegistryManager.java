package com.hp.scheduler.process;

import com.hp.scheduler.socket.LogicalClockImpl;
import com.hp.scheduler.socket.SocketService;
import com.hp.scheduler.socket.event.EventType;
import com.hp.scheduler.socket.event.SocketEvent;
import com.hp.scheduler.socket.event.SocketEventListener;
import java.util.HashMap;

/**
 * This service will be used manage clients.
 *
 * @author orozco
 */
public class RegistryManager implements SocketEventListener {

    /**
     * *
     * Contains the clientId and its status
     */
    private HashMap<String, Boolean> clientStatus = null;
    SocketService service = null;
    LogicalClockImpl lc = null;

    /**
     * Creates a RegistryManager specifying a SocketService and the LogicalClock of the process.
     *
     * @param service the pair of sockets to listen and send events
     * @param lc the logical clock of the local process
     */
    public RegistryManager(SocketService service, LogicalClockImpl lc) {
        this.clientStatus = new HashMap<>();
        this.service = service;
        this.lc = lc;
    }

    /**
     * This event will be fired each time an event is received, in this particular case is used to detect CONNECT
     * events.
     *
     * @param evt the event that was received.
     */
    @Override
    public void eventReceived(SocketEvent evt) {
        String stream = evt.getStream();
        String[] parts = stream.split("\\|");
        String from = parts[0];
        int lcFrom = Integer.parseInt(parts[1]);
        String local = parts[2];
        EventType type;
        Integer fromPort = Integer.parseInt(parts[5]);
        String fromHost = parts[6];
        try {
            type = EventType.valueOf(parts[3]);
        } catch (IllegalArgumentException iae) {
            type = EventType.UNKNOWN;
        }
        if (type == EventType.CONNECT) {
            if (!clientStatus.containsKey(from)) {
                clientStatus.put(from, true);
                lc.tick();
                service.sendEvent("Manager", lc.getValue(), from, fromHost, fromPort, EventType.ALLOWED, "Conection Allowed");
            } else {
                //send message of refused
                lc.tick();
                service.sendEvent("Manager", lc.getValue(), from, fromHost, fromPort, EventType.REFUSE, "Conection Refused, Id already registered");
            }
        }
        //String meta = "";
        // logReceiveEvent(local, from, lcFrom, type, meta);
    }

    /**
     * This method is called when a service within the process sends an event.
     */
    @Override
    public void sendEvent(SocketEvent evt) {
        String stream = evt.getStream();
        String[] parts = stream.split("\\|");
        String src = parts[0];
        int lcSrc = Integer.parseInt(parts[1]);
        String dest = parts[2];
        EventType type;
        try {
            type = EventType.valueOf(parts[3]);
        } catch (IllegalArgumentException iae) {
            type = EventType.UNKNOWN;
        }
        String meta = "";
        // logSendEvent(src, lcSrc, dest, type, meta);
    }

}
