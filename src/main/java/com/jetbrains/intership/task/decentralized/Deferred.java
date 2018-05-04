package com.jetbrains.intership.task.decentralized;

import java.util.ArrayList;

public class Deferred<T> {


    protected ArrayList<Runnable> callBacks;
    private  T result;
    private boolean isResolved =  false;


    public Deferred() {
        callBacks = new ArrayList<Runnable>();
    }


    public T get() {
        if (isResolved())
            return result;
        else
            throw new IllegalStateException("The object has not resolved yet");

    }

    public boolean isResolved() {
        return isResolved;
    }


    public synchronized void resolve(T value) {
        if (isResolved())
            throw new IllegalStateException("The object has been already resolved");
        else{
            result = value;
            isResolved =  true;
            for(int i = 0; i < callBacks.size();i++)
                callBacks.get(i).run();
            callBacks.clear();
        }
    }


    public synchronized void whenResolved(Runnable callback) {
        if (isResolved())
            callback.run();
        else callBacks.add(callback);
    }
}


