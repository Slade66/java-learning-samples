package com.slade66;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 题目描述：在我们的业务场景中，每次调用对方的 RPC 接口都会阻塞 50 毫秒，而实际业务可能需要批量调用，例如 1,000 次调用。
 * 如果采用顺序调用，总耗时将达到 1,000 × 50 毫秒，远无法满足性能要求。
 * 考虑到我们作为客户端调用方无法修改对方的代码，只能优化自己的调用逻辑，因此请设计一种并发优化方案，实现对 RPC 接口的并发调用，
 * 从而大幅缩短整体调用时间，并确保返回结果严格按照请求顺序排列。实现方式不限，你可以自由选择合适的技术和方法。
 * 请提供完整代码实现，并添加必要注释，同时输出前 10 个调用结果以验证顺序正确。
 */
public class BatchCallBlockRpc {

    /**
     * 模拟 RPC 调用接口，阻塞 50ms 后返回结果。
     *
     * @return 开始调用此接口的时间
     */
    public String callRpc() {
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return formatted;
    }

    /**
     * 直接循环调用
     */
    @Test
    public void testBatchCallBlockRpc0() {
        for (int i = 1; i <= 1000; i++) {
            System.out.println(callRpc() + " " + i);
        }
    }

    /**
     * 使用 CompletableFuture 异步调用
     */
    @Test
    public void testBatchCallBlockRpc1() {
        int totalCalls = 1000;
        ExecutorService pool = Executors.newFixedThreadPool(100);
        List<CompletableFuture<String>> results = new ArrayList<>(totalCalls);
        for (int i = 0; i < totalCalls; i++) {
            final int taskNumber = i + 1;
            CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> String.format("%s %s", callRpc(), taskNumber), pool);
            results.add(result);
        }
        CompletableFuture.allOf(results.toArray(new CompletableFuture[totalCalls])).join();
        results.stream().forEach(cf -> System.out.println(cf.join()));
    }

}
