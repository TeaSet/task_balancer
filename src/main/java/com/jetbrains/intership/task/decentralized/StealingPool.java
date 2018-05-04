package com.jetbrains.intership.task.decentralized;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;

public class StealingPool {

    private LinkedBlockingDeque<Task<?>>[] workersQueue;
    private Thread[] threads;
    private VersionMonitor versionMonitor;

    public StealingPool(int nthreads) {
        workersQueue = new LinkedBlockingDeque[nthreads];
        threads = new Thread[nthreads];
        versionMonitor = new VersionMonitor();
        for (int i = 0; i < nthreads; i++) {
            workersQueue[i] = new LinkedBlockingDeque<>();
        }
    }

    public LinkedBlockingDeque<Task<?>>[] getWorkersQueue() {
        return workersQueue;
    }

    public VersionMonitor getVersionMonitor() {
        return versionMonitor;
    }

    public void submit(Task<?> task) {
        workersQueue[new Random().nextInt(workersQueue.length)].addFirst(task);
        versionMonitor.inc();
    }

    public void shutdown() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException("The thread has been already interrupted");
        }
        for (Thread thread : threads) {
            thread.interrupt();
        }

    }

    public void start() {
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Worker(i, this), "Thread's id: " + i);
            threads[i].start();
        }
    }

    public boolean steal(int workerId) {

        int len = workersQueue.length;
        for (int i = 1; i < len; i++) { //except current queue
            int index = (i + workerId) % len;
            if (workersQueue[index].size()>1){
                int size = workersQueue[index].size()/2;
                for (int n = 0; n < size; n++){
                    Task<?> taskToSteal = workersQueue[index].pollLast();
                    if(taskToSteal!=null){
                        workersQueue[workerId].addFirst(taskToSteal);
                    }
                    else{
                        break;
                    }
                }
                return true;
            }
        }

        return false;
    }
}
