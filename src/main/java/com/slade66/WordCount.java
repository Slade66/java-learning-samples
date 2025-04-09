package com.slade66;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 统计文本文件中某个单词的词频。
 */
public class WordCount {

    private static final String sampleText = """
            Java is a popular programming language.
            Python is also widely used in data science.
            I love Java and Spring Boot framework.
            Go is known for its simplicity and concurrency.
            Java is platform-independent.
            Java Java Java!
            Node.js is great for I/O intensive apps.
            The Java Virtual Machine is a powerful abstraction.
            C++ is closer to hardware.
            Java is verbose but powerful.
            The Java community is huge.
            JavaScript is not the same as Java.
            Kotlin runs on the JVM just like Java.
            Java developers often use IntelliJ IDEA.
            I once wrote a backend service using Java.
            Java makes it easier to build scalable systems.
            Python has simpler syntax than Java.
            Rust is gaining popularity too.
            Java excels in enterprise systems.
            Functional programming is possible in Java 8+.
            Java streams are elegant and powerful.
            Some companies still run legacy Java 6 systems.
            Lambda expressions were added in Java 8.
            Java can be verbose, but IDEs help a lot.
            Java is widely used in Android development.
            You can build microservices with Java and Spring Cloud.
            Java has strong typing and static analysis tools.
            Log4j is a popular logging library in Java.
            Java is everywhere.
            Keep calm and code in Java.
            """;

    private static final Path filePath = Paths.get("sample_text.txt");

    private static void writeFile() throws IOException {
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.writeString(filePath, sampleText, StandardOpenOption.CREATE);
    }

    @RequiredArgsConstructor
    private static class WordCountTask extends RecursiveTask<Integer> {

        private static final int THRESHOLD = 10;

        private final List<String> lines;
        private final int start;
        private final int end;

        @Override
        protected Integer compute() {
            if (end - start <= THRESHOLD) {
                // 小任务：直接统计
                int count = 0;
                for (int i = start; i < end; i++) {
                    count += countKeyword(lines.get(i), "Java");
                }
                return count;
            } else {
                // 拆分任务
                int mid = (start + end) / 2;
                WordCountTask left = new WordCountTask(lines, start, mid);
                WordCountTask right = new WordCountTask(lines, mid, end);
                left.fork(); // 异步执行左边
                int rightResult = right.compute(); // 当前线程执行右边
                int leftResult = left.join(); // 等左边执行结果

                return leftResult + rightResult;
            }
        }

        private int countKeyword(String line, String keyword) {
            int count = 0;
            int index = 0;
            while ((index = line.indexOf(keyword, index)) != -1) {
                count++;
                index += keyword.length();
            }
            return count;
        }

    }

    @Test
    public void test() {
        try {
            writeFile();
            List<String> lines = Files.readAllLines(filePath);
            WordCountTask task = new WordCountTask(lines, 0, lines.size());
            ForkJoinPool pool = new ForkJoinPool();
            int totalCount = pool.invoke(task);
            System.out.println("Total 'Java' count = " + totalCount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
