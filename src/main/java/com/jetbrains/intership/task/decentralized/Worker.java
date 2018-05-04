package com.jetbrains.intership.task.decentralized;

import java.util.concurrent.LinkedBlockingDeque;

public class Worker implements Runnable {

    private final StealingPool pool;
    private final int id;

    public StealingPool getPool() {
        return pool;
    }

    public Worker(int id, StealingPool pool) {
        this.id = id;
        this.pool = pool;
    }

    @Override
    public void run() {
        LinkedBlockingDeque myQueue = pool.getWorkersQueue()[id];
        while (!Thread.currentThread().isInterrupted()){
            while (!myQueue.isEmpty()){
                Task<?> taskToHandle  = (Task<?>) myQueue.pollFirst();
                if (taskToHandle != null){
                    taskToHandle.handle(this);
                }
                else break;
            }
            int version = pool.getVersionMonitor().getVersion();
            if (!pool.steal(id)) {
                if(myQueue.isEmpty()){
                    try {
                        pool.getVersionMonitor().await(version);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    protected void submitToProcessor(Task taskToAdd) {
        pool.getWorkersQueue()[id].addFirst(taskToAdd);
    }

}
