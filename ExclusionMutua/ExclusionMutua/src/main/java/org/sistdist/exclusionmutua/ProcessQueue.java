package org.sistdist.exclusionmutua;

import java.util.ArrayDeque;

/**
 * Don't know the reason of this class, but it's a wrapper for a QUEUE with only 3 methods;
 *
 * @author orozco
 */
public class ProcessQueue {

    ArrayDeque<QueueElement> queue = null;

    public ProcessQueue() {
        this.queue = new ArrayDeque<>();
    }

    public void push(QueueElement element) {
        queue.push(element);

    }

    public QueueElement peek() {
        return queue.peek();
    }

    public QueueElement pop() {
        return queue.pop();
    }

}
