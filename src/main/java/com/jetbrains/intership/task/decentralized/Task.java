package com.jetbrains.intership.task.decentralized;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Task<E> {

    private Worker currentHandler = null;
    private Deferred result = new Deferred();


    protected abstract void start();

    public final void handle(Worker handler) {
        if(currentHandler == null){
            currentHandler = handler;
            this.start();
        }
        else currentHandler = handler;
    }

    protected final void spawn(Task<?>... task) throws NullPointerException {

        for (Task<?> taskToAdd : task) {
            if (taskToAdd == null) {
                throw new NullPointerException("spawn function got null task");
            } else {
                currentHandler.submitToProcessor(taskToAdd);
                currentHandler.getPool().getVersionMonitor().inc();
            }
        }
    }

    protected final void whenResolved(Collection<? extends Task<?>> tasks, Runnable callback) {
        AtomicInteger count = new AtomicInteger(tasks.size());
        for (Task<?> task : tasks) {
            task.result.whenResolved(()->{
                count.decrementAndGet();
                if(count.get() == 0){
                    callback.run();
                }
            });
        }
    }

    protected final void complete(E result) {
        this.result.resolve(result);

    }

    public final Deferred<E> getResult() {
        return result;
    }

    public Worker getCurrentHandler() {
        return currentHandler;
    }
}

