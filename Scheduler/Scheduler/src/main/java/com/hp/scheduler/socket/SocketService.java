package com.hp.scheduler.socket;

import com.hp.scheduler.process.EventLog;
import com.hp.scheduler.process.EventLogImpl;
import com.hp.scheduler.socket.event.EventManager;
import com.hp.scheduler.socket.event.EventType;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Omar
 */
public class SocketService extends Thread {

    String id = null;
    String hostname;
    int port;
    Listener listener = null;
    EventManager evtManager;

    public SocketService(String hostname, int port, EventManager evtManager) {
        id = getName();
        this.hostname = hostname;
        this.port = port;
        listener = new Listener(port, this);
        this.evtManager = evtManager;
    }

    public void sendEvent(String srcProcess, int processLc, String destProcess,
            String destHostname, int destPort, EventType type, String meta) {
        String message = String.format("%s|%s|%s|%s|%s", srcProcess, processLc, destProcess, type, meta);
        send(destHostname, destPort, message);
        evtManager.sendEvent(message);
        System.out.println("SEND>" + id);
    }

    private void send(String hostname, int toPort, String msg) {
        DatagramPacket sPacket;
        try {
            InetAddress ia = InetAddress.getByName(hostname);
            DatagramSocket datasocket = new DatagramSocket();
            try {
                byte[] buffer = msg.getBytes();
                sPacket = new DatagramPacket(buffer, buffer.length, ia, toPort);
                datasocket.send(sPacket);
                datasocket.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace(System.err);
        }
    }

    public void receiveEvent(String local, String from, int fromLc, EventType type, String meta) {
        String message = String.format("%s|%s|%s|%s|%s", from, fromLc, local, type, meta);
        //TODO add event receive processing
        evtManager.eventReceived(message);
        System.out.println("RECEIVE>" + id);
    }

    @Override
    public void run() {
        listener.start();
        //give some time to allow the listener to start on all processes :)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
        listener.close();
    }

    private class Listener extends Thread {

        int port;
        int len = 1024;
        AtomicBoolean keepGoing = new AtomicBoolean(true);
        DatagramSocket datasocket;
        SocketService ssRef;

        /**
         * Creates the listener for the process
         *
         * @param port the port where the listener will start
         */
        public Listener(int port, SocketService ssRef) {
            this.port = port;
            this.ssRef = ssRef;
        }

        /**
         * Used to close the socket once the "processing" has finished.
         */
        public void close() {
            keepGoing.set(false);
            datasocket.close();
        }

        @Override
        /**
         * Handles the reception of messages for a process.
         */
        public void run() {
            DatagramPacket datapacket, returnpacket;
            try {
                datasocket = new DatagramSocket(port);
                byte[] buf = new byte[len];
                while (keepGoing.get()) {
                    try {
                        datapacket = new DatagramPacket(buf, buf.length);
                        datasocket.receive(datapacket);
                        returnpacket = new DatagramPacket(datapacket.getData(), datapacket.getLength(), datapacket.getAddress(), datapacket.getPort());
                        String event = (new String(returnpacket.getData(), "UTF-8"));
                        String[] parts = event.split("\\|");
                        String from = parts[0];
                        int lcFrom = Integer.parseInt(parts[1]);
                        String local = parts[2];
                        EventType type;
                        try {
                            type = EventType.valueOf(parts[3]);
                        } catch (IllegalArgumentException iae) {
                            type = EventType.UNKNOWN;
                        }
                        String meta = "";
                        ssRef.receiveEvent(local, from, lcFrom, type, meta);
                    } catch (IOException e) {
                        if (keepGoing.get()) {
                            e.printStackTrace(System.err);
                        }
                    }
                }
            } catch (SocketException se) {
                if (keepGoing.get()) {
                    se.printStackTrace(System.err);
                }
            }
        }
    }

}
