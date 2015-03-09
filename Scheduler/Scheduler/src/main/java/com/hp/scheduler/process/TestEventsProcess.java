package com.hp.scheduler.process;

import com.hp.scheduler.socket.event.EventManager;
import com.hp.scheduler.socket.event.SocketEvent;
import com.hp.scheduler.socket.event.SocketEventListener;

/**
 *
 * @author Omar
 */
public class TestEventsProcess implements SocketEventListener {

    public TestEventsProcess() {
    }

    @Override
    public void eventReceived(SocketEvent evt) {
        System.out.println("Event Received:");
    }

    @Override
    public void sendEvent(SocketEvent evt) {
        System.out.println("Event Send: ");
    }

    public static void main(String args[]) {
        EventManager eventManager = new EventManager();
        TestEventsProcess tep = new TestEventsProcess();
        eventManager.addEventListener(tep);
        eventManager.sendEvent(">> Evento Enviado");
        eventManager.eventReceived(">> Evento Recibido");

    }

}
