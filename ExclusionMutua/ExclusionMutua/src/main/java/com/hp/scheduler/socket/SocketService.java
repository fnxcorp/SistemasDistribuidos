package com.hp.scheduler.socket;

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
 * This class creates 2 sockets: 1 to listen for incoming messages, and one to send messages between processes.
 *
 * @author Omar Aguilar
 */
public class SocketService extends Thread {

    String id = null;
    String hostname;
    int port;
    Listener listener = null;
    EventManager evtManager;

    /**
     * Initializes the socket service by using a host name and port, also includes an event manager in order to dispatch
     * messages.
     *
     * @param hostname the host name where the socket will reside
     * @param port the port where the process will be listening
     * @param evtManager the event manager
     */
    public SocketService(String hostname, int port, EventManager evtManager) {
        id = getName();
        this.hostname = hostname;
        this.port = port;
        listener = new Listener(port, this);

        this.evtManager = evtManager;
    }

    /**
     * Method used to send create a message, that later will be send to a different process.
     *
     * @param srcProcessID the id of the process that sends the message.
     * @param processLc the logical clock value of the process that sends the message.
     * @param destProcess the id of the destination process
     * @param destHostname is the host name of the remote process
     * @param destPort the port of the remote process
     * @param type the type of event that was generated
     * @param meta extra data in the message
     */
    public void sendEvent(String srcProcessID, int processLc, String destProcess,
            String destHostname, int destPort, EventType type, String meta) {
        String message = String.format("%s|%s|%s|%s|%s|%s|%s", srcProcessID, processLc, destProcess, type, meta, this.port, this.hostname);
        send(destHostname, destPort, message);
        evtManager.sendEvent(message);
//        System.out.println("SEND>" + id);
    }

    /**
     * Sends the actual message to another process, the remote process is found using the hostname and port parameters.
     *
     * @param hostanme the host name of the remote process
     * @param port the port of the remote process
     */
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

    /**
     * When receiving an event, this is redirected to the event manager.
     *
     * @param local the id of the local process (i guess)
     * @param from the id of the remote process
     * @param fromLc the value of the logical clock of the remote process
     * @param meta extra data on the message
     * @param fromPort the port from the remote process
     * @param fromHostName the host name of the remote process
     */
    public void receiveEvent(String local, String from, int fromLc, EventType type, String meta, String fromPort, String fromHostName) {
        String message = String.format("%s|%s|%s|%s|%s|%s|%s", from, fromLc, local, type, meta, fromPort, fromHostName);
        //TODO add event receive processing
        evtManager.eventReceived(message);
//        System.out.println("RECEIVE>" + id);
    }

    /**
     * Starts the listener thread.
     */
    @Override
    public void run() {
        listener.start();
    }

    public void close() {
        listener.close();
    }

    /**
     * This class is active listening for incoming messages, but using a thread so is non-blocking.
     */
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
                        String event = (new String(returnpacket.getData(), 0, returnpacket.getLength(), "UTF-8"));
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
                        String meta = parts[4];
                        String fromPort = parts[5];
                        String fromHost = parts[6];
                        ssRef.receiveEvent(local, from, lcFrom, type, meta, fromPort, fromHost);
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
