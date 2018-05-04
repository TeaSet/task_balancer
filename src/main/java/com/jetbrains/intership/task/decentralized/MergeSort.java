package com.jetbrains.intership.task.decentralized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;


public class MergeSort extends Task<int[]> {

    private final int[] array;


    public MergeSort(int[] array) {
        this.array = array;
    }


    @Override
    protected void start() {
        List<Task<int[]>> tasks = new ArrayList<>();
        int size = array.length;
        if (array != null && size >= 2) {

            int mid = size / 2;
            int[] arr1 = new int[mid];
            int[] arr2 = new int[size - mid];
            for (int i = 0; i < mid; i++) {
                arr1[i] = array[i];
            }
            for (int i = 0; i < size-mid; i++) {
                arr2[i] = array[i + mid];
            }
            MergeSort task1 = new MergeSort(arr1);
            MergeSort task2 = new MergeSort(arr2);
            this.spawn(task1, task2);
            tasks.add(task1);
            tasks.add(task2);


            this.whenResolved(tasks, () -> {

                int[] aSorted;
                int[] bSorted;
                int[] answer;
                aSorted = task1.getResult().get();
                bSorted = task2.getResult().get();
                answer = merge(aSorted, bSorted);

                complete(answer);

            });
        } else{
            this.complete(array);
        }

    }

    public static int[] merge(int[] a, int[] b) {

        int i = 0;
        int j = 0;
        int[] res = new int[a.length + b.length];
        for (int k = 0; k < res.length; k++) {
            if ((j == b.length) || (i < a.length && a[i] <= b[j])) {
                res[k] = a[i];
                i++;
            } else {
                res[k] = b[j];
                j++;
            }
        }
        return res;
    }

    public static void main(String[] args) throws InterruptedException {

        LinkedBlockingDeque<Task<?>> sharedQueue = new LinkedBlockingDeque<>();
        StealingPool pool = new StealingPool(4);
        int n = 10;

        int[] array = new Random().ints(n).toArray();

        MergeSort task = new MergeSort(array);

        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            System.out.println(Arrays.toString(task.getResult().get()));
            l.countDown();
        });

        l.await();
        pool.shutdown();

    }

}

