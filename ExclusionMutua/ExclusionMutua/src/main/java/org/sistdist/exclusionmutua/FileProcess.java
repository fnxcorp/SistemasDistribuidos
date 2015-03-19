/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sistdist.exclusionmutua;

import com.hp.scheduler.process.EventLogImpl;
import com.hp.scheduler.process.RegistryManager;
import com.hp.scheduler.socket.LogicalClockImpl;
import com.hp.scheduler.socket.SocketService;
import com.hp.scheduler.socket.event.EventManager;
import com.hp.scheduler.socket.event.EventType;
import java.util.Objects;

/**
 *
 * @author orozco
 */
public class FileProcess {

    String processName;

    RegistryManager registryManager = null;
    SocketService service = null;
    EventManager eventManager = null;
    String thisIP = "";
    EventLogImpl eventLog = null;
    LogicalClockImpl lc = new LogicalClockImpl();
    Integer listeningPort = null;
    Integer[] sendingPorts = null;
   
    Boolean inCriticalSection=false;
    
    public Integer nACK=null;
    public ProcessQueue processQueue = null;

    public FileProcess(String processName, Integer listeningPort, Integer[] sendingPorts) {
        this.processName = processName;
        this.listeningPort = listeningPort;
        this.sendingPorts = sendingPorts;
        this.processQueue = new ProcessQueue();

        lc = new LogicalClockImpl();
        eventLog = new EventLogImpl(lc);
        eventManager = new EventManager();
        eventManager.addEventListener(eventLog);
        lc.tick();
        service = new SocketService(thisIP, listeningPort, eventManager);
        service.start();

        registryManager = new RegistryManager(service, lc, this);
        eventManager.addEventListener(registryManager);
    }

    private void executeAction(EventType type) {
        switch (type) {
            case OPEN:
                break;

            case READ:
                break;

            case UPDATE:
                //this should ask for critical section access

                requestCS();
                break;

            case WRITE:
                //this should ask for critical section access
                break;
            case CLOSE:
                break;
        }
    }

    public Boolean getInCriticalSection() {
        return inCriticalSection;
    }

    public void setInCriticalSection(Boolean inCriticalSection) {
        this.inCriticalSection = inCriticalSection;
    }

    public Integer[] getSendingPorts() {
        return sendingPorts;
    }
    
    
    
    
    private void requestCS() {
        nACK=0;
        this.processQueue.push(new QueueElement(this.listeningPort, processName, this.lc.getValue()));
        for (Integer sendingPort : sendingPorts) {
            if (!Objects.equals(sendingPort, this.listeningPort)) {
                service.sendEvent(this.processName, lc.getValue(), "", "localhost", sendingPort, EventType.CRITICALSECTION, "Requesting critical section");
            }
        }
    }
    
    private void releaseCS(){
        //remove from own queue
        this.processQueue.pop();
        //send release message for all
        
        for (Integer sendingPort : sendingPorts) {
            if (!Objects.equals(sendingPort, this.listeningPort)) {
                service.sendEvent(this.processName, lc.getValue(), "", "localhost", sendingPort, EventType.RELEASE, "Requesting critical section");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("hell world");
        Integer[] processPorts = new Integer[]{9999, 9998, 9997};
        //we a re going to create 3 process in difefrent ports
        FileProcess p1 = new FileProcess("Process 1", processPorts[0], processPorts);
        FileProcess p2 = new FileProcess("Process 2", processPorts[1], processPorts);
        FileProcess p3 = new FileProcess("Process 3", processPorts[2], processPorts);

    }

}
