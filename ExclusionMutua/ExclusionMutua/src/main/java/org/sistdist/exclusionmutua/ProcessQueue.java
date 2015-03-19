/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sistdist.exclusionmutua;

import java.util.ArrayDeque;

/**
 *
 * @author orozco
 */
public class ProcessQueue {
    ArrayDeque<QueueElement> queue=null;

    public ProcessQueue() {
        this.queue=new ArrayDeque<>();
    }
    
    public void push(QueueElement element){
        queue.push(element);
        
    }
    
    public QueueElement peek(){
        return queue.peek();
    }
    
    public QueueElement pop(){
        return queue.pop();
    }
   
    
    
    
    
}
