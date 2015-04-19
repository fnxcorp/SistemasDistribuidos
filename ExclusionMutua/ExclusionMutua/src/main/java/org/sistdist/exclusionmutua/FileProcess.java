package org.sistdist.exclusionmutua;

import com.hp.scheduler.process.EventLogImpl;
import com.hp.scheduler.socket.LogicalClockImpl;
import com.hp.scheduler.socket.SocketService;
import com.hp.scheduler.socket.event.EventManager;
import com.hp.scheduler.socket.event.EventType;
import com.hp.scheduler.socket.event.SocketEvent;
import com.hp.scheduler.socket.event.SocketEventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Scanner;

/**
 * This class simulates a process where it owns a local file (which is supposed to be a local copy of a global file), it
 * provides some methods OPEN, CLOSE, READ, WRITE and UPDATE, the last 2 methods modify the file for that reason those
 * need to access a CS, and this is done using Lamport's algorithm.
 *
 * @author Fernando Orozco
 * @author Omar Aguilar
 */
public class FileProcess implements SocketEventListener {

    String processName;
    int listeningPort;
    int[] sendingPorts;
    public ProcessQueue processQueue;
    File localFile;
    String localFileName;
    ArrayDeque<Integer> pendingACKS;

    SocketService service = null;
    EventManager eventManager = null;
    EventLogImpl eventLog = null;
    LogicalClockImpl lc;

    boolean inCriticalSection;
    public int nACK;

    /**
     * Creates a file process.
     *
     * @param processName the name of the process
     * @param listeningPort the port where the process will listen for incoming messages
     * @param sendingPorts an array of the ports of the other processes (includes its own port)
     */
    public FileProcess(String processName, int listeningPort, int[] sendingPorts) {
        System.out.println("Starting " + processName);
        this.processName = processName;
        this.listeningPort = listeningPort;
        this.sendingPorts = sendingPorts;
        this.processQueue = new ProcessQueue();

        inCriticalSection = false;
        nACK = 0;

        lc = new LogicalClockImpl();
        pendingACKS = new ArrayDeque<>();
        localFileName = processName.replaceAll("\\s", "_").toLowerCase() + ".txt";
        eventLog = new EventLogImpl(lc);
        eventManager = new EventManager();
        service = new SocketService("localhost", listeningPort, eventManager);
        start();
    }

    /**
     * Add the event listeners and start the socket listener.
     */
    private void start() {
        eventManager.addEventListener(eventLog);
        eventManager.addEventListener(this);
        lc.tick();
        service.start();
    }

    /**
     * Executes the desired action.
     *
     * @param type the type of the action (OPEN, READ, CLOSE)
     */
    public void executeAction(EventType type) {
        executeAction(type, null, false);
    }

    /**
     * Executes the desired action, plus includes some data to the action.
     *
     * @param type the type of the action (WRITE, UPDATE)
     * @param data data associated to the action
     */
    public void executeAction(EventType type, String data) {
        executeAction(type, data, false);
    }

    /**
     * This is the method in charge of executing the action, is private because the CS access is allowed only when we
     * have all ACK from other processes.
     *
     * @param type the type of the action (OPEN, CLOSE, READ, WRITE, UPDATE)
     * @param data data associated to the action
     * @param hasCSAccess if this is true we now that the process can access its CS
     */
    private void executeAction(EventType type, String data, boolean hasCSAccess) {
        lc.tick();
        switch (type) {
            case OPEN: //opens a file if it doesn't exists it will be createad
                if (localFile == null) {
                    localFile = new File(localFileName);
                    if (!localFile.exists()) {
                        try {
                            localFile.createNewFile();
                        } catch (IOException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                    System.out.printf("%s has opened its file%n", processName);
                } else {
                    System.out.printf("%s has already open its file%n", processName);
                }
                break;
            case CLOSE: //to close we only remove the file reference
                localFile = null;
                break;
            case READ: //prints the content of the local file
                if (localFile == null) {
                    System.out.printf("%s hasn't opened its file :(%n", processName);
                } else {
                    StringBuilder content = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile)))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            content.append(line).append(System.lineSeparator());
                        }
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                    System.out.printf("%s contents:%n%s", localFileName, content.toString());
                }
                break;
            case UPDATE: //overwrites the content of the local file
                //this should ask for critical section access
                if (localFile == null) {
                    System.out.printf("%s hasn't opened its file :(%n", processName);
                } else if (!hasCSAccess) {
                    requestCS(type, data);
                } else {
                    try (FileWriter f = new FileWriter(localFile)) {
                        f.append(data);
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    } finally {
                        releaseCS();
                    }
                }
                break;
            case WRITE: //appends data to the local file
                if (localFile == null) {
                    System.out.printf("%s hasn't opened its file :(%n", processName);
                } else if (!hasCSAccess) {
                    requestCS(type, data);
                } else {
                    //this should ask for critical section access
                    try (FileWriter f = new FileWriter(localFile, true)) {
                        f.append(data).append(System.lineSeparator());
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    } finally {
                        releaseCS();
                    }
                }
                break;
        }
    }

    /**
     * Tells if the process is executing its CS.
     *
     * @return a flag that indicates if the process is using its CS
     */
    public boolean isInCriticalSection() {
        return inCriticalSection;
    }

    /**
     * Sets the critical section value.
     *
     * @param inCriticalSection a flag indicating whether the process is in or out of its CS
     */
    public void setInCriticalSection(boolean inCriticalSection) {
        this.inCriticalSection = inCriticalSection;
    }

    /**
     * Gest the ports of the available processes.
     *
     * @return the array of the ports of the available processes
     */
    public int[] getSendingPorts() {
        return sendingPorts;
    }

    /**
     * This method is in charge of request access to the CRITICAL SECTION, sends a REQUEST to other processes.
     *
     * @param type the type of the action (WRITE, UPDATE)
     * @param data data associated to the action
     */
    private void requestCS(EventType type, String data) {
        nACK = 0;
        processQueue.push(new QueueElement(listeningPort, processName, lc.getValue(), type, data));
        for (int sendingPort : sendingPorts) {
            if (sendingPort != listeningPort) {
                lc.tick();
                service.sendEvent(this.processName, lc.getValue(), "" + sendingPort, "localhost", sendingPort, EventType.REQUEST, "Requesting critical section");
            }
        }
    }

    /**
     * Once the local process finishes using its CS, will send a RELEASE to other processes and also sends pending ACKS.
     */
    private void releaseCS() {
        //Reset ACK counter
        nACK = 0;
        //remove from own queue
        processQueue.pop();
        //send release message to all other processes
        for (int sendingPort : sendingPorts) {
            if (sendingPort != listeningPort) {
                lc.tick();
                service.sendEvent(processName, lc.getValue(), "" + sendingPort, "localhost", sendingPort, EventType.RELEASE, "Releasing critical section");
            }
        }

        //send pending acks
        while (!pendingACKS.isEmpty()) {
            int fromPort = pendingACKS.pop();
            lc.tick();
            service.sendEvent(processName, lc.getValue(), fromPort + "", "localhost", fromPort, EventType.ACK, "ACK reponse");
        }
    }

    /**
     * Allow a clean shutdown of the process.
     */
    public void close() {
        service.close();
    }

    //Event Handling
    @Override
    public void eventReceived(SocketEvent evt) {
        String stream = evt.getStream();
        String[] parts = stream.split("\\|");
        String from = parts[0];
        int lcFrom = Integer.parseInt(parts[1]);
//        String local = parts[2];
        EventType type;
        Integer fromPort = Integer.parseInt(parts[5]);
//        String fromHost = parts[6];
        try {
            type = EventType.valueOf(parts[3]);
        } catch (IllegalArgumentException iae) {
            type = EventType.UNKNOWN;
        }
        switch (type) {
            case REQUEST:
                //someone requested access the critical section
                processQueue.push(new QueueElement(fromPort, from, lcFrom));
                if (!isInCriticalSection()) {
                    //if the process is not in its critical section send the ack
                    lc.tick();
                    service.sendEvent(processName, lc.getValue(), fromPort + "", "localhost", fromPort, EventType.ACK, "ACK reponse");
                } else {
                    pendingACKS.add(fromPort);
                }
                //if is executing its critical section we are going to delay te ack untils it frees its critical section with a thread checking availability
                break;
            case ACK:
                nACK++;
                //Acknowdlege has been received we have to have control of how many ack we have received sice we ask for cs and if the process is on top
                if (nACK == getSendingPorts().length - 1) {
                    //we have received all the acknowdlegements
                    QueueElement top = processQueue.peek();
                    if (processName.equals(top.getProcessId())) {
                        executeAction(top.getType(), top.getData(), true);
                    }
                }
                break;
            case RELEASE:
                //Release received delete it from queue
                processQueue.pop();
                break;
        }
    }

    @Override
    public void sendEvent(SocketEvent evt) {
        //In this case we don't have to do anything special about outgoing events
    }

    public static void main(String[] args) {
//        System.out.println("starting processes");
        int[] processPorts = new int[]{9999, 9998, 9997};
        //we a re going to create 3 process in difefrent ports
        FileProcess p1 = new FileProcess("Process 1", processPorts[0], processPorts);
        FileProcess p2 = new FileProcess("Process 2", processPorts[1], processPorts);
        FileProcess p3 = new FileProcess("Process 3", processPorts[2], processPorts);
//        p1.start();
//        p2.start();
//        p3.start();

        Scanner sc = new Scanner(System.in);
        boolean quit = false;
        while (!quit) {
            System.out.print("Enter command: ");
            String text = sc.next();
            text = text == null ? "" : text.trim();
            if ("q".equalsIgnoreCase(text) || "quit".equalsIgnoreCase(text)) {
                quit = true;
                p1.close();
                p2.close();
                p3.close();
                continue;
            }
            String[] command = text.split("\\|");
            if (command.length >= 2) {
                int processId = Integer.parseInt(command[0]);
                FileProcess ref = processId == 1 ? p1 : processId == 2 ? p2 : p3;
                String action = command[1].trim();
                if ("open".equalsIgnoreCase(action)) {
                    ref.executeAction(EventType.OPEN);
                } else if ("close".equalsIgnoreCase(action)) {
                    ref.executeAction(EventType.CLOSE);
                } else if ("read".equalsIgnoreCase(action)) {
                    ref.executeAction(EventType.READ);
                } else if ("write".equalsIgnoreCase(action)) {
                    ref.executeAction(EventType.WRITE, command.length >= 3 ? command[2].trim() : "dummy");
                } else if ("update".equalsIgnoreCase(action)) {
                    ref.executeAction(EventType.UPDATE, command.length >= 3 ? command[2].trim() : "dummy");
                } else {
                    System.out.println("unknown action");
                }
            }
        }
    }

}
