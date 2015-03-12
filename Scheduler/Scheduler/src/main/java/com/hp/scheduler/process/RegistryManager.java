/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hp.scheduler.process;

import com.hp.scheduler.socket.LogicalClockImpl;
import com.hp.scheduler.socket.SocketService;
import com.hp.scheduler.socket.event.EventType;
import com.hp.scheduler.socket.event.SocketEvent;
import com.hp.scheduler.socket.event.SocketEventListener;
import java.util.HashMap;

/**
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

    public RegistryManager(SocketService service, LogicalClockImpl lc) {
        this.clientStatus = new HashMap<>();
        this.service = service;
        this.lc = lc;
    }

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
