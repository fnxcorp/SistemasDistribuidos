package com.hp.scheduler.process;

import com.hp.scheduler.socket.SocketService;
import com.hp.scheduler.socket.event.EventManager;
import com.hp.scheduler.socket.event.EventType;
import com.hp.scheduler.socket.event.SocketEvent;
import com.hp.scheduler.socket.event.SocketEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
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

    public EventLogImpl() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.log = "";
    }

    public EventLogImpl(JTextArea logComponent) {
        this.logComponent = logComponent;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.log = "";
    }

    @Override
    public void logSendEvent(String src, int lcSrc, String dest, EventType type, String meta, Integer fromPort, String host) {
        String tmp = String.format(LOG_LINE_SEND, sdf.format(Calendar.getInstance().getTime()), src, lcSrc, dest, type, meta, fromPort);
        System.out.print(tmp);
        log = log.concat(tmp);
        if (logComponent != null) {
            logComponent.setText(log);
        }
    }

    @Override
    public void logReceiveEvent(String local, String from, int lcFrom, EventType type, String meta, Integer fromPort, String host) {
        String tmp = String.format(LOG_LINE, sdf.format(Calendar.getInstance().getTime()), from, lcFrom, local, type, meta, fromPort);
        System.out.print(tmp);
        log = log.concat(tmp);
        if (logComponent != null) {
            logComponent.setText(log);
        }
    }

    @Override
    public void eventReceived(SocketEvent evt) {
        String stream = evt.getStream();
        String[] parts = stream.split("\\|");
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
        Integer fromPort = Integer.parseInt(parts[5]);
        String fromHost = parts[6];
        logReceiveEvent(local, from, lcFrom, type, meta, fromPort, fromHost);
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
        String meta = parts[4];
        Integer fromPort = Integer.parseInt(parts[5]);
        String fromHost = parts[6];
        logSendEvent(src, lcSrc, dest, type, meta, fromPort, fromHost);
    }

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
