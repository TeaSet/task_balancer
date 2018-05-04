package com.jetbrains.intership.task.decentralized;

import java.util.concurrent.atomic.AtomicInteger;

public class VersionMonitor {
    private AtomicInteger version = new AtomicInteger(0);


    public int getVersion() {
        return version.get();
    }


    public synchronized void inc() {
        version.incrementAndGet();
        try {
            notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void await(int version) throws InterruptedException {
        while (this.version.get() == version) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;

            }
        }
    }
}

