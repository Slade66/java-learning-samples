package com.slade66;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
public class MultiThreadDownloader {

    private final String downloadPath;

    public ContentLengthAndAcceptRanges fetchContentLengthAndAcceptRanges() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(downloadPath)).method("HEAD", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<Void> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
        HttpHeaders responseHeaders = response.headers();
        String contentLength = responseHeaders.firstValue("Content-Length").orElse("未知");
        String acceptRanges = response.headers().firstValue("Accept-Ranges").orElse("不支持");
        return new ContentLengthAndAcceptRanges(contentLength, acceptRanges);
    }

    @RequiredArgsConstructor
    @Getter
    private static class ContentLengthAndAcceptRanges {
        private final String contentLength;
        private final String acceptRanges;
    }

    private void run() {
        try {
            ContentLengthAndAcceptRanges contentLengthAndAcceptRanges = fetchContentLengthAndAcceptRanges();
            System.out.println("文件大小（字节）：" + contentLengthAndAcceptRanges.getContentLength());
            System.out.println("是否支持分块下载：" + contentLengthAndAcceptRanges.getAcceptRanges());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MultiThreadDownloader multiThreadDownloader = new MultiThreadDownloader("https://assets.mubu.com/client/Mubu-5.0.0.exe");
        multiThreadDownloader.run();
    }

}
