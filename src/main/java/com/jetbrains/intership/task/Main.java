package com.jetbrains.intership.task;


public class Main {

    public static void main(String[] args) {

        Delegator delegator = new Delegator(20);
        WorkerPool pool = new WorkerPool(6);

        delegator.addWorkers(pool);
        new Thread(delegator).start();
        pool.start();
    }

}
