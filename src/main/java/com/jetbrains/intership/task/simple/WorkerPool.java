package com.jetbrains.intership.task.simple;

import java.util.concurrent.LinkedBlockingQueue;

public class WorkerPool<T> {

    private final LinkedBlockingQueue<T>[] workQueues;
    private final Thread[] threads;
    private Delegator delegator;

    public WorkerPool(int numThreads) {
        workQueues = new LinkedBlockingQueue[numThreads];
        threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            workQueues[i] = new LinkedBlockingQueue<>();
        }
    }

    public LinkedBlockingQueue<T>[] getWorkQueues() {
        return workQueues;
    }

    public void shutdown() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException("The thread has already interrupted");
        }
        for (Thread thread : threads) {
            thread.interrupt();
        }

    }

    public void start() {
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Worker(i, this, delegator), "Thread's id: " + i);
            threads[i].start();
        }
    }
}
