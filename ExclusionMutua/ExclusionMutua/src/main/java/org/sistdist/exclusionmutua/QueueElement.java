/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sistdist.exclusionmutua;

import com.hp.scheduler.socket.event.EventType;

/**
 *
 * @author orozco
 */
public class QueueElement {

    private final int clockValue;
    private final String processId;
    private final int processPort;

    //this fields will be only present for the local process in its local queue
    private EventType type;
    private String data;

    public QueueElement(int processPort, String processId, int clockValue, EventType type, String data) {
        this(processPort, processId, clockValue);
        this.type = type;
        this.data = data;
    }

    public QueueElement(int processPort, String processId, int clockValue) {
        this.clockValue = clockValue;
        this.processId = processId;
        this.processPort = processPort;
    }

    public int getClockValue() {
        return clockValue;
    }

    public String getProcessId() {
        return processId;
    }

    public int getProcessPort() {
        return processPort;
    }

    public EventType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

}
