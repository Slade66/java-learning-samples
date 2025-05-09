package com.slade66;

import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <h1>交替打印数字和字母</h1>
 *
 * <p>有两个线程分别打印数字和字母：</p>
 * <ul>
 *   <li>线程 A：每次打印两个递增的数字，从 1 开始（例如：1、2、3、4、...、52）；</li>
 *   <li>线程 B：每次打印一个递增的大写英文字母，从 A 开始（例如：A、B、C、...、Z）。</li>
 * </ul>
 *
 * <p>期望的输出格式为 {@code 12A34B56C...5152Z}，即两个数字 + 一个字母 交替输出。</p>
 *
 * <p>实现要求：<p>
 * <ol>
 *   <li>必须使用线程协作机制（如 {@code synchronized + wait/notify}、{@code Lock + Condition}、{@code Semaphore} 等）；</li>
 *   <li>必须保证输出顺序严格一致，不能出现乱序。</li>
 * </ol>
 */
public class PrintNumbersAndLettersAlternately {

    @Test
    public void solution1() {
        Object turn = new Object();

        new Thread(() -> {
            int num = 1;
            synchronized (turn) {
                while (true) {
                    for (int num2 = num + 1; num <= num2; num++) {
                        System.out.print(num);
                    }
                    try {
                        turn.notify();
                        if (num > 52) {
                            break;
                        }
                        turn.wait();
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }, "线程 A").start();

        new Thread(() -> {
            char c = 'A';
            synchronized (turn) {
                while (true) {
                    System.out.print(c++);
                    if (c > 'Z') {
                        break;
                    }
                    try {
                        turn.notify();
                        turn.wait();
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }, "线程 B").start();
    }

    @Test
    public void solution2() throws InterruptedException {
        AtomicBoolean isNumber = new AtomicBoolean(true);
        Object lock = new Object();

        Thread threadA = new Thread(() -> {
            int num = 1;
            while (num <= 52) {
                synchronized (lock) {
                    while (!isNumber.get()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ignored) {

                        }
                    }
                    System.out.print(num++);
                    System.out.print(num++);
                    isNumber.set(false);
                    lock.notify();
                }
            }
        }, "线程 A");

        Thread threadB = new Thread(() -> {
            char c = 'A';
            while (c <= 'Z') {
                synchronized (lock) {
                    while (isNumber.get()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ignored) {

                        }
                    }
                    System.out.print(c++);
                    isNumber.set(true);
                    lock.notify();
                }
            }
        }, "线程 B");

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();
    }

    @Test
    public void solution3() throws InterruptedException {
        Lock lock = new ReentrantLock();
        AtomicBoolean isNumber = new AtomicBoolean(true);
        Condition numberCondition = lock.newCondition();
        Condition alphabetCondition = lock.newCondition();

        Thread threadA = new Thread(() -> {
            int num = 1;
            while (num <= 52) {
                try {
                    lock.lock();
                    while (!isNumber.get()) {
                        numberCondition.await();
                    }
                    System.out.print(num++);
                    System.out.print(num++);
                    isNumber.set(false);
                    alphabetCondition.signal();
                } catch (InterruptedException ignored) {

                } finally {
                    lock.unlock();
                }
            }
        });

        Thread threadB = new Thread(() -> {
            char c = 'A';
            while (c <= 'Z') {
                try {
                    lock.lock();
                    while (isNumber.get()) {
                        alphabetCondition.await();
                    }
                    System.out.print(c++);
                    isNumber.set(true);
                    numberCondition.signal();
                } catch (InterruptedException ignored) {

                } finally {
                    lock.unlock();
                }
            }
        });

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();
    }

    @Test
    public void solution4() throws InterruptedException {
        Semaphore numberPermit = new Semaphore(1);
        Semaphore alphabetPermit = new Semaphore(0);

        Thread threadA = new Thread(() -> {
            int number = 1;
            while (number <= 52) {
                try {
                    numberPermit.acquire();
                    System.out.print(number++);
                    System.out.print(number++);
                    alphabetPermit.release();
                } catch (InterruptedException ignored) {

                }
            }
        });

        Thread threadB = new Thread(() -> {
            char c = 'A';
            while (c <= 'Z') {
                try {
                    alphabetPermit.acquire();
                    System.out.print(c++);
                    numberPermit.release();
                } catch (InterruptedException ignored) {

                }
            }
        });

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();
    }

    @Test
    public void solution5() throws InterruptedException {
        SynchronousQueue<Integer> callThreadA = new SynchronousQueue<>();
        SynchronousQueue<Integer> callThreadB = new SynchronousQueue<>();

        Thread threadA = new Thread(() -> {
            int num = 1;
            while (num <= 52) {
                try {
                    callThreadA.take();
                    System.out.print(num++);
                    System.out.print(num++);
                    callThreadB.put(1);
                } catch (InterruptedException ignored) {

                }
            }
        });

        Thread threadB = new Thread(() -> {
            char c = 'A';
            while (c <= 'Z') {
                try {
                    callThreadB.take();
                    System.out.print(c++);
                    if (c <= 'Z') {
                        callThreadA.put(1);
                    }
                } catch (InterruptedException ignored) {

                }
            }
        });

        threadA.start();
        threadB.start();

        callThreadA.put(1);

        threadA.join();
        threadB.join();
    }

}
