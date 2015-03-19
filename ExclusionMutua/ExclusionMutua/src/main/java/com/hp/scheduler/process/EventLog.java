package com.hp.scheduler.process;

import com.hp.scheduler.socket.event.EventType;

/**
 *
 * @author Omar
 */
public interface EventLog {

    public void logSendEvent(String src, int lcSrc, String dest, EventType type, String meta, Integer fromPort, String fromHost);

    public void logReceiveEvent(String src, String from, int lcFrom, EventType type, String meta, Integer fromPort, String fromHost);
}
