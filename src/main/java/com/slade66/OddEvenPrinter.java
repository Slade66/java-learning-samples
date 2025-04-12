package com.slade66;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>两个线程交替打印奇数和偶数</h1>
 *
 * <p><b>题目描述：</b></p>
 * <ul>
 *   <li>启动两个线程：</li>
 *   <li>线程A打印奇数：1, 3, 5, ...</li>
 *   <li>线程B打印偶数：2, 4, 6, ...</li>
 *   <li>要求两个线程按照顺序交替输出 1 到 10</li>
 * </ul>
 *
 * <p><b>输出示例：</b></p>
 * <pre>
 * 线程A: 1
 * 线程B: 2
 * 线程A: 3
 * 线程B: 4
 * ...
 * 线程B: 10
 * </pre>
 *
 * <p><b>要求：</b></p>
 * <ul>
 *   <li>两个线程必须协作，确保输出顺序正确，不能乱序</li>
 *   <li>禁止使用 Thread.sleep() 等方式进行线程控制</li>
 *   <li>可以使用 Java 并发工具，例如：synchronized、wait/notify、Lock/Condition、Semaphore 等</li>
 * </ul>
 */
public class OddEvenPrinter {

    @Test
    public void solution1() throws InterruptedException {
        AtomicInteger number = new AtomicInteger(1);
        final Object Lock = new Object();

        Thread threadA = new Thread(() -> {
            while (number.get() < 100) {
                synchronized (Lock) {
                    try {
                        while ((number.get() & 1) == 0) {
                            Lock.wait();
                        }
                        System.out.println(Thread.currentThread().getName() + ":" + number.getAndIncrement());
                        Lock.notify();
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }, "线程A");

        Thread threadB = new Thread(() -> {
            while (number.get() <= 100) {
                synchronized (Lock) {
                    try {
                        while ((number.get() & 1) != 0) {
                            Lock.wait();
                        }
                        System.out.println(Thread.currentThread().getName() + ":" + number.getAndIncrement());
                        Lock.notify();
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }, "线程B");

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();
    }

}
