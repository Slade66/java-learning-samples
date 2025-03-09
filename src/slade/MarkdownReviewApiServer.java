package slade;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class MarkdownReviewApiServer {

    private final int port;
    private final String folderPath;
    private final List<String> articles = Collections.synchronizedList(new ArrayList<>());

    public MarkdownReviewApiServer(int port, String folderPath) {
        this.port = port;
        this.folderPath = folderPath;
        this.loadMarkdownFilesConcurrently();
        this.startServer();
    }

    private void loadMarkdownFilesConcurrently() {
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            List<Path> markdownPaths = paths.filter(Files::isRegularFile).toList();

            CountDownLatch latch = new CountDownLatch(markdownPaths.size());

            for (Path markdownPath : markdownPaths) {
                new Thread(() -> {
                    try {
                        String content = Files.readString(markdownPath);
                        articles.add(content);
                    } catch (IOException e) {
                        System.err.println("读取文件 " + markdownPath + " 时出错: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }).start();
            }

            latch.await();
            System.out.println("所有 Markdown 文件已读取完毕，共计 " + articles.size() + " 篇。");

        } catch (Exception e) {
            System.err.println("读取文件出错：" + e.getMessage());
        }
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器已启动，监听端口：" + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            System.err.println("启动服务器出错：" + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {

        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
            ) {
                String requestLine = reader.readLine();
                if (requestLine == null || requestLine.isEmpty()) {
                    return;
                }

                if (requestLine.contains("GET /random")) {
                    String article = getRandomArticle();
                    if (article != null) {
                        writer.write("HTTP/1.1 200 OK\r\n");
                        writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
                        writer.write("\r\n");
                        writer.write(article);
                    } else {
                        writer.write("HTTP/1.1 404 Not Found\r\n");
                        writer.write("\r\n");
                        writer.write("No articles found.");
                    }
                } else {
                    writer.write("HTTP/1.1 404 Not Found\r\n");
                    writer.write("\r\n");
                    writer.write("Resource not found.");
                }
                writer.flush();

            } catch (Exception e) {
                System.err.println("处理客户端请求时出错: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("关闭客户端连接时出错：" + e.getMessage());
                }
            }
        }

    }

    private String getRandomArticle() {
        if (articles.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * articles.size());
        return articles.get(randomIndex);
    }

    public static void main(String[] args) {
        new MarkdownReviewApiServer(4000, "E:\\md");
    }

}
