/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hp.scheduler.process;

import com.hp.scheduler.socket.LogicalClockImpl;
import com.hp.scheduler.socket.SocketService;
import com.hp.scheduler.socket.event.EventType;
import com.hp.scheduler.socket.event.SocketEvent;
import com.hp.scheduler.socket.event.SocketEventListener;
import java.util.HashMap;
import org.sistdist.exclusionmutua.FileProcess;
import org.sistdist.exclusionmutua.QueueElement;

/**
 *
 * @author orozco
 */
public class RegistryManager implements SocketEventListener {

    /**
     * *
     * Contains the clientId and its status
     */
    private HashMap<String, Boolean> clientStatus = null;
    SocketService service = null;
    LogicalClockImpl lc = null;
    FileProcess fileProcess=null;

    public RegistryManager(SocketService service, LogicalClockImpl lc, FileProcess fileProcess) {
        this.clientStatus = new HashMap<>();
        this.service = service;
        this.lc = lc;
        this.fileProcess= fileProcess;
    }

    @Override
    public void eventReceived(SocketEvent evt) {
        String stream = evt.getStream();
        String[] parts = stream.split("\\|");
        String from = parts[0];
        int lcFrom = Integer.parseInt(parts[1]);
        String local = parts[2];
        EventType type;
        Integer fromPort = Integer.parseInt(parts[5]);
        String fromHost = parts[6];
        try {
            type = EventType.valueOf(parts[3]);
        } catch (IllegalArgumentException iae) {
            type = EventType.UNKNOWN;
        }
        if (type == EventType.CRITICALSECTION) {
            //someone requested the critical section
            this.fileProcess.processQueue.push(new QueueElement(fromPort, from, lcFrom));
            if(!this.fileProcess.getInCriticalSection()){
                //if the process is not in its critical section send the ack
                //if is executin its critical section we are going to delay te ack untils it frees its critical section with a threat checking availability
                
            }
           
        }
         if (type == EventType.ACK) {
             this.fileProcess.nACK++;
             
             //Acknowdlege has been received we have to have control of how many ack we have received sice we ask for cs and if the process is on top
             if(fileProcess.nACK==fileProcess.getSendingPorts().length-1){
                 //we have received all the acknowdlegements
             }
             
         }
          if (type == EventType.RELEASE) {
             //Release received delete it from queue
              this.fileProcess.processQueue.pop();
              
         }
        //String meta = "";
        // logReceiveEvent(local, from, lcFrom, type, meta);
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
        String meta = "";
        // logSendEvent(src, lcSrc, dest, type, meta);
    }

}
