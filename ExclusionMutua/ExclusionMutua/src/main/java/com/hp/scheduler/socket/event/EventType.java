package com.hp.scheduler.socket.event;

/**
 * This enum is used to listen the different types of available Events, the idea of this class is to help services to
 * discern which events they can handle and which not.
 *
 * @author Omar
 */
public enum EventType {

   OPEN,
   WRITE,
   READ,
   CLOSE,
   UPDATE,
   ACK,
   RELEASE,
   REQUEST,
   UNKNOWN
}
