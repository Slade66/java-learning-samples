package com.slade66;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模拟抢购演唱会门票的场景实践乐观锁知识
 */
public class OptimisticLockSeckill {

    private static Connection conn = null;

    private static final String PRODUCT_NAME = "小泽的演唱会门票";

    static {
        String url = "jdbc:mysql://127.0.0.1:3306/test";
        String username = "root";
        String password = "123456";
        String createTableSql = """
                    CREATE TABLE IF NOT EXISTS product (
                        id INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
                        name VARCHAR(100) NOT NULL UNIQUE COMMENT '商品名称',
                        stock INT NOT NULL COMMENT '库存数量',
                        version INT NOT NULL DEFAULT 0 COMMENT '版本号，用于乐观锁'
                    );
                """;
        String productSql = String.format("INSERT IGNORE INTO product (name, stock, version) VALUE ('%s', 10, 0);", PRODUCT_NAME);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            conn.prepareStatement(createTableSql).execute();
            conn.prepareStatement(productSql).execute();
        } catch (SQLException e) {
            System.err.println("初始化数据库失败！");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 未加锁的线程不安全示例
     */
    private static void secKill1() {
        String sql = String.format("UPDATE product SET stock = stock - 1 WHERE name = '%s' and stock > 0", PRODUCT_NAME);
        try {
            int effectRows = conn.prepareStatement(sql).executeUpdate();
            if (effectRows > 0) {
                System.out.println("购买成功");
            }
        } catch (SQLException e) {
            System.err.println("购买失败");
            e.printStackTrace();
        }
    }

    private static void secKill2() {

    }

    private static void printProductStock() {
        String sql = "select * from product";
        try {
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            System.out.println("📦 商品表内容：");
            System.out.println("ID | 名称\t\t\t| 库存 | 版本");
            while (rs.next()) {
                String name = rs.getString("name");
                int stock = rs.getInt("stock");
                int version = rs.getInt("version");

                System.out.printf("%s | %d | %d\n", name, stock, version);
            }
        } catch (SQLException e) {
            System.err.println("打印商品表！");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final int threadCount = 100;
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
        List<CompletableFuture<Void>> cfs = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            CompletableFuture<Void> cf = CompletableFuture.runAsync(OptimisticLockSeckill::secKill1, threadPool);
            cfs.add(cf);
        }
        CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0])).join();
        printProductStock();
    }

}
