package com.jetbrains.intership.task.simple;


public class Main {

    public static void main(String[] args) {

        Delegator delegator = new Delegator(100);
        WorkerPool pool = new WorkerPool(4);

        delegator.addWorkers(pool);
        new Thread(delegator).start();
        pool.start();
    }

}
