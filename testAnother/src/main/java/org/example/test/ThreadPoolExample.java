package org.example.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ExecutorService;


//ThreadFactory

public class ThreadPoolExample {
    public static void main(String[] args) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("CustomThread-" + count++);
                return thread;
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool(threadFactory);

        executor.execute(() -> {
            System.out.println("线程名称: " + Thread.currentThread().getName());
        });

        executor.shutdown();
    }
}

