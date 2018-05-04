package com.jetbrains.intership.task.simple;

import java.util.concurrent.LinkedBlockingQueue;

public class Worker implements Runnable {

    private final WorkerPool pool;
    private final int id;
    private Delegator delegator;

    public Worker(int id, WorkerPool pool, Delegator delegator) {
        this.id = id;
        this.pool = pool;
        this.delegator = delegator;
    }

    public WorkerPool getPool() {
        return pool;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        LinkedBlockingQueue currentQueue = pool.getWorkQueues()[id];
        while (!Thread.currentThread().isInterrupted()) {
            while (!currentQueue.isEmpty()) {
                System.out.println(Thread.currentThread().getName());

                int task = (int) currentQueue.peek();
                System.out.println("Worker " + id + " takes a task " + task + "         " + pool.getWorkQueues()[id].size());
                try {
                    Thread.sleep(task);
                    currentQueue.take();
                } catch (InterruptedException e) {
                    System.out.println("Worker " + id + " finished with ERROR");
                    Thread.currentThread().interrupt();
                }
            }
            if (currentQueue.isEmpty()) {
                break;
            }
        }
        System.out.println("Executor " + id + " finished SUCCESSFULLY");
    }
}
