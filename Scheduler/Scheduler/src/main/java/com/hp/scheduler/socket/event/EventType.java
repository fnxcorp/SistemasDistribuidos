package com.hp.scheduler.socket.event;

/**
 * This enum is used to listen the different types of available Events, the idea of this class is to help services to
 * discern which events they can handle and which not.
 *
 * @author Omar
 */
public enum EventType {

    CONNECT,
    DISCONNECT,
    USE_LICENSE,
    RELEASE,
    ALLOWED,
    UNKNOWN,
    INITIALIZE,
    REFUSE
}
