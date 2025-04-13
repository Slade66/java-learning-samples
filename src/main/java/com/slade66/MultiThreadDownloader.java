package com.slade66;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MultiThreadDownloader {

    private final String downloadPath;
    private final long chunkSize = 5 * 1024 * 1024;
    private final int maxAttempts = 3;

    public ContentLengthAndAcceptRangesDTO fetchContentLengthAndAcceptRanges() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(downloadPath)).method("HEAD", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<Void> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
        HttpHeaders responseHeaders = response.headers();
        long contentLength = Long.parseLong(responseHeaders.firstValue("Content-Length").orElse("0"));
        String acceptRanges = response.headers().firstValue("Accept-Ranges").orElse("不支持");
        return new ContentLengthAndAcceptRangesDTO(contentLength, acceptRanges);
    }

    @RequiredArgsConstructor
    @Getter
    private static class ContentLengthAndAcceptRangesDTO {
        private final long contentLength;
        private final String acceptRanges;
    }

    private void run() {
        try {
            ContentLengthAndAcceptRangesDTO contentLengthAndAcceptRanges = fetchContentLengthAndAcceptRanges();
            long contentLength = contentLengthAndAcceptRanges.getContentLength();
            String acceptRanges = contentLengthAndAcceptRanges.getAcceptRanges();
//            printContentLengthAndAcceptRanges(contentLengthAndAcceptRanges);
            Map<Integer, List<Long>> tasks = calculateByteRangeForThreads(contentLength);
//            printTasks(tasks);
            downloadTasks(tasks);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void downloadTasks(Map<Integer, List<Long>> tasks) {
        ExecutorService threadPool = Executors.newFixedThreadPool(tasks.size());
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("download_chunks");
        } catch (IOException e) {
            System.err.println("创建下载文件夹失败" + e.getMessage());
            e.printStackTrace();
        }

        Path finalTempDir = tempDir;
        tasks.forEach((taskId, byteRange) -> {
            threadPool.submit(new downloadTask(finalTempDir, taskId, byteRange));
        });
        try {
            threadPool.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {

        }
        mergeChunks(tempDir, tasks.size());
    }

    private void mergeChunks(Path tempDir, int taskSize) {
        try (var outputStream = Files.newOutputStream(Path.of("final_output_file.tmp"))) {
            for (int i = 0; i < taskSize; i++) {
                Path chunkPath = tempDir.resolve("chunk_" + i + ".tmp");
                Files.copy(chunkPath, outputStream);
            }
            System.out.println("文件合并完成！");
        } catch (IOException e) {
            System.err.println("文件合并失败：" + e.getMessage());
        }
    }

    @RequiredArgsConstructor
    class downloadTask implements Runnable {

        private final Path tempDir;
        private final int taskId;
        private final List<Long> byteRange;

        @Override
        public void run() {
            HttpClient httpClient = HttpClient.newHttpClient();
            long startByte = byteRange.get(0);
            Long endByte = byteRange.get(1);
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(downloadPath)).header("Range", String.format("bytes=%s-%s", startByte, endByte)).build();
            for (int i = 0; i < maxAttempts; i++) {
                try {
                    HttpResponse<byte[]> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
                    if (httpResponse.statusCode() != 206) {
                        System.err.printf("%s %ld-%ld 下载失败%n", taskId, startByte, endByte);
                        return;
                    }
                    Files.write(tempDir.resolve("chunk_" + taskId + ".tmp"), httpResponse.body());
                    System.out.printf("%s %ld-%ld 下载完成%n", taskId, startByte, endByte);
                    break;
                } catch (IOException | InterruptedException e) {
                    if (i == maxAttempts - 1) {
                        System.err.printf("%s %ld-%ld 下载失败%n", taskId, startByte, endByte);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void printContentLengthAndAcceptRanges(ContentLengthAndAcceptRangesDTO contentLengthAndAcceptRanges) {
        System.out.println("文件大小（字节）：" + contentLengthAndAcceptRanges.getContentLength());
        System.out.println("服务器支持的范围单位：" + contentLengthAndAcceptRanges.getAcceptRanges());
    }

    private void printTasks(Map<Integer, List<Long>> tasks) {
        tasks.forEach((taskId, byteRange) -> {
            System.out.println("线程 " + taskId + " 下载范围: " + byteRange.get(0) + " - " + byteRange.get(1));
        });
    }

    private Map<Integer, List<Long>> calculateByteRangeForThreads(long contentLength) {
        Map<Integer, List<Long>> tasks = new HashMap<>();
        int totalThreads = (int) Math.ceil((double) contentLength / chunkSize);
        long startByte = 0;
        long endByte = chunkSize - 1;
        for (int i = 0; i < totalThreads; i++) {
            List<Long> byteRange = new ArrayList<>();
            byteRange.add(startByte);
            byteRange.add(endByte);
            tasks.put(i, byteRange);

            startByte = endByte + 1;
            endByte = Math.min(startByte + chunkSize - 1, contentLength - 1);
        }
        return tasks;
    }

    public static void main(String[] args) {
        MultiThreadDownloader multiThreadDownloader = new MultiThreadDownloader("https://assets.mubu.com/client/Mubu-5.0.0.exe");
        multiThreadDownloader.run();
    }

}
