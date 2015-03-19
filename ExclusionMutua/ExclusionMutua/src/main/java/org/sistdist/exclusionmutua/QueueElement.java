/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sistdist.exclusionmutua;

/**
 *
 * @author orozco
 */
public class QueueElement {
    Integer clockValue=null;
    String processId=null;
    Integer processPort=null;

    public QueueElement(Integer clockValue, String processId, Integer processPort) {
        this.clockValue=clockValue;
        this.processId= processId;
        this.processPort=processPort;
    }

    public Integer getClockValue() {
        return clockValue;
    }

    public String getProcessId() {
        return processId;
    }

    public Integer getProcessPort() {
        return processPort;
    }
    
    
    
    
    
}
