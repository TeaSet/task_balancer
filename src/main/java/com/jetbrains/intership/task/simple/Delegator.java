package com.jetbrains.intership.task.simple;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Delegator implements Runnable {

    private LinkedBlockingQueue mainQueue;
    private WorkerPool pool;

    public Delegator(int queueSize) {
        this.mainQueue = new LinkedBlockingQueue(queueSize);
    }

    public void addWorkers(WorkerPool pool) {
        this.pool = pool;
    }


    @Override
    public void run() {
        try {
            if (mainQueue.isEmpty()) {
                generateNumbers();
            }
            while (!mainQueue.isEmpty()) {
                Integer task = (Integer) mainQueue.peek();
                LinkedBlockingQueue workerQueue = pool.getWorkQueues()[0];
                for (LinkedBlockingQueue temp : pool.getWorkQueues()) {
                    if (temp.size() < workerQueue.size()) {
                        workerQueue = temp;
                    }
                }
                if (task != null) {
                    try {
                        workerQueue.put(task);
                        mainQueue.take();
                    } catch (InterruptedException e) {
                        System.out.println("Delegator finished with ERROR");
                        Thread.currentThread().interrupt();
                    }

                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Delegator has finished his work");
    }

    private void generateNumbers() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            mainQueue.put(ThreadLocalRandom.current().nextInt(100));
        }
    }

}