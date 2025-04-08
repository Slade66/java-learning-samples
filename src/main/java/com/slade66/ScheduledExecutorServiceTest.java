package com.slade66;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorServiceTest {

    class MyThread implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
            }
            System.out.println("=".repeat(10));
        }
    }

    public void basicUsage1() throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            private int i = 1;

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("MyThread-" + i++);
                return t;
            }
        });

        scheduledExecutorService.scheduleWithFixedDelay(new MyThread(), 0, 5, TimeUnit.SECONDS);

        Thread.sleep(10 * 60 * 1000);
    }

}
