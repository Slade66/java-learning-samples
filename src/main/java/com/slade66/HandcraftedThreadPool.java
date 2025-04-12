package com.slade66;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class HandcraftedThreadPool {

    private final int corePoolSize;
    private final int maxPoolSize;
    private final int keepAliveTime;
    private final TimeUnit unit;
    private final BlockingQueue<Runnable> blockingQueue;
    private final ThreadFactory threadFactory;
    private final RejectionPolicy rejectionPolicy;

    private volatile boolean isShutdown;

    private List<Thread> coreList = new CopyOnWriteArrayList<>();
    private List<Thread> supportList = new CopyOnWriteArrayList<>();

    void execute(Runnable command) {
        if (isShutdown) {
            throw new RuntimeException("Thread pool already shutdown.");
        }
        if (coreList.size() < corePoolSize) {
            Thread thread = threadFactory.newThread(new CoreTask());
            coreList.add(thread);
            thread.start();
        }
        if (blockingQueue.offer(command)) {
            return;
        }
        if (coreList.size() + supportList.size() < maxPoolSize) {
            Thread thread = threadFactory.newThread(new SupportTask());
            supportList.add(thread);
            thread.start();
        }
        if (!blockingQueue.offer(command)) {
            rejectionPolicy.reject(command, this);
        }
    }

    public void shutdown() {
        isShutdown = true;
    }

    class CoreTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (isShutdown && blockingQueue.isEmpty()) {
                    break;
                }
                try {
                    Runnable command = blockingQueue.take();
                    command.run();
                } catch (InterruptedException ignored) {

                }
            }
        }
    }

    class SupportTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (isShutdown && blockingQueue.isEmpty()) {
                    break;
                }
                try {
                    Runnable command = blockingQueue.poll(keepAliveTime, unit);
                    if (command == null) {
                        break;
                    }
                    command.run();
                } catch (InterruptedException ignored) {

                }
            }
        }
    }

    public static void main(String[] args) {
        HandcraftedThreadPool handcraftedThreadPool = new HandcraftedThreadPool(3, 6, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10), new ThreadFactory() {
            AtomicInteger number = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("HandcraftedThreadPool-" + number.getAndIncrement());
                return thread;
            }
        }, new AbortPolicy());

        for (int i = 0; i < 10; i++) {
            final int fi = i;
            handcraftedThreadPool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + ": " + fi);
            });
        }

        handcraftedThreadPool.shutdown();
    }

}

@FunctionalInterface
interface RejectionPolicy {
    void reject(Runnable task, HandcraftedThreadPool executor);
}

class AbortPolicy implements RejectionPolicy {
    @Override
    public void reject(Runnable task, HandcraftedThreadPool executor) {
        throw new RejectedExecutionException("Task rejected from " + executor);
    }
}

class DiscardPolicy implements RejectionPolicy {
    @Override
    public void reject(Runnable task, HandcraftedThreadPool executor) {
        // do nothing
    }
}

class CallerRunsPolicy implements RejectionPolicy {
    @Override
    public void reject(Runnable task, HandcraftedThreadPool executor) {
        task.run(); // 当前提交任务的线程自己执行任务
    }
}

