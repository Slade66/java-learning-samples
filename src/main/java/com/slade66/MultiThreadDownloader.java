package com.slade66;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MultiThreadDownloader {

    private final String downloadPath;
    private final long chunkSize = 5 * 1024 * 1024;

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
            printContentLengthAndAcceptRanges(contentLengthAndAcceptRanges);
            Map<Integer, List<Long>> tasks = calculateByteRangeForThreads(contentLength);
            printTasks(tasks);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
