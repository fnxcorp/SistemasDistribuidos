package com.hp.scheduler.process;

import com.hp.scheduler.socket.LogicalClockImpl;
import com.hp.scheduler.socket.SocketService;
import com.hp.scheduler.socket.event.EventManager;
import com.hp.scheduler.socket.event.EventType;
import com.hp.scheduler.socket.event.SocketEvent;
import com.hp.scheduler.socket.event.SocketEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTextArea;

/**
 * This service is in charge of do log of events.
 *
 * @author Omar
 */
public class EventLogImpl implements SocketEventListener, EventLog {

    //date EVENT [src_process] (src_lc_value) [dest_proc] - event_id :: metadata
    private final String LOG_LINE = "%s EVENT [%s (%d)] [%s] - %s :: %s :: Request port: %d%n";
    private final String LOG_LINE_SEND = "%s EVENT [%s (%d)] [%s] - %s :: %s :: Sending port: %d%n";
    final SimpleDateFormat sdf;
    JTextArea logComponent = null;
    String log = null;
    LogicalClockImpl lc = null;

    /**
     * Creates an event logger.
     */
    public EventLogImpl() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.log = "";
    }

    /**
     * Creates an event logger, and also allows to specify a GUI component where to send the log data, and includes the
     * logical clock of the local process.
     *
     * @param logComponent a text area where the logs will be displayed
     * @param lc the internal logical clock
     */
    public EventLogImpl(JTextArea logComponent, LogicalClockImpl lc) {
        this.logComponent = logComponent;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.log = "";
        this.lc = lc;
    }

    /**
     *
     * @param src the id of the process that sends the message.
     * @param lcSrc the logical clock value of the process that sends the message.
     * @param dest the id of the destination process
     * @param type the type of event that was generated
     * @param meta extra data in the message
     * @param fromPort the port of the local process
     * @param host is the host name of local process
     */
    @Override
    public void logSendEvent(String src, int lcSrc, String dest, EventType type, String meta, Integer fromPort, String host) {
        String tmp = String.format(LOG_LINE_SEND, sdf.format(Calendar.getInstance().getTime()), src, lcSrc, dest, type, meta, fromPort);
        System.out.print(tmp);
        log = log.concat(tmp);
        if (logComponent != null) {
            logComponent.setText(log);
        }
    }

    /**
     * Logs a received event.
     *
     * @param local the id of the local process
     * @param from the id of the remote process
     * @param lcFrom the value of the logical clock of the remote process
     * @param type the type of event that was generated
     * @param meta extra data on the message
     * @param fromPort the port from the remote process
     * @param host the host name of the remote process
     */
    @Override
    public void logReceiveEvent(String local, String from, int lcFrom, EventType type, String meta, Integer fromPort, String host) {
        String tmp = String.format(LOG_LINE, sdf.format(Calendar.getInstance().getTime()), from, lcFrom, local, type, meta, fromPort);
        System.out.print(tmp);
        log = log.concat(tmp);
        if (logComponent != null) {
            logComponent.setText(log);
        }
    }

    /**
     * This method is in charge of parsing the event, incrementing the logical clock and log the event accordingly.
     *
     * @param evt the received event.
     */
    @Override
    public void eventReceived(SocketEvent evt) {
        String stream = evt.getStream();
        String[] parts = stream.split("\\|");
        String from = parts[0];
        int lcFrom = Integer.parseInt(parts[1]);
        String local = parts[2];
        EventType type;
        lc.receiveAction(lcFrom);
        try {
            type = EventType.valueOf(parts[3]);
        } catch (IllegalArgumentException iae) {
            type = EventType.UNKNOWN;
        }
        String meta = parts[4];
        Integer fromPort = Integer.parseInt(parts[5]);
        String fromHost = parts[6];
        logReceiveEvent(local, from, lcFrom, type, meta, fromPort, fromHost);
    }

    /**
     * This method is in charge of parsing a local event, incrementing the logical clock and log the event accordingly.
     *
     * @param evt the received event.
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
        String meta = parts[4];
        Integer fromPort = Integer.parseInt(parts[5]);
        String fromHost = parts[6];
        logSendEvent(src, lcSrc, dest, type, meta, fromPort, fromHost);
    }

    /**
     * This was used to do some tests only.
     *
     * @para args arguments that are not needed
     */
    public static void main(String arg[]) {
        EventManager evtManager = new EventManager();
        EventLogImpl el = new EventLogImpl();
        evtManager.addEventListener(el);
        SocketService ss8085 = new SocketService("localhost", 8085, evtManager);
        SocketService ss8086 = new SocketService("localhost", 8086, evtManager);
        ss8085.start();
        ss8086.start();
//        ss8085.sendEvent("testClient", 1, "testServer", "localhost", 8086, EventType.CONNECT, null);
//        ss8086.receiveEvent("testServer", "testClient", 1, EventType.ALLOWED, null);
//        ss8085.sendEvent("testClient", 2, "testServer", "localhost", 8086, EventType.USE_LICENSE, null);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
    }

}
