package com.slade66;

public class ShutdownHookTest {
    public static void main(String[] args) throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("JVM 退出前执行钩子逻辑");
        }));

        System.out.println("程序运行中...");

        Thread.sleep(100_000); // 等你去点红色按钮
    }
}
