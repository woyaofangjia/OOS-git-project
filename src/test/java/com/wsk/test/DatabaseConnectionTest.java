package com.wsk.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Properties props = new Properties();
        Connection conn = null;
        
        try {
            // 读取配置文件
            String propsPath = "src/main/resources/jdbc.properties";
            props.load(new FileInputStream(propsPath));
            
            // 打印配置信息
            System.out.println("=== 配置文件信息 ===");
            System.out.println("username from jdbc.properties: " + props.getProperty("username"));
            System.out.println("password from jdbc.properties: " + props.getProperty("password"));
            
            // 检查所有属性键
            System.out.println("\n=== 配置文件所有键 ===");
            for (String key : props.stringPropertyNames()) {
                System.out.println(key + ": " + props.getProperty(key));
            }
            
            // 获取系统属性
            System.out.println("\n=== 系统属性信息 ===");
            System.out.println("System property username: " + System.getProperty("username"));
            System.out.println("System property user.name: " + System.getProperty("user.name"));
            System.out.println("System property java.io.tmpdir: " + System.getProperty("java.io.tmpdir"));
            
            // 强制使用MySQL 8.0驱动
        System.out.println("\n=== 加载驱动 ===");
        System.out.println("使用MySQL 8.0驱动: com.mysql.cj.jdbc.Driver");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("成功加载MySQL驱动!");
        } catch (ClassNotFoundException e) {
            System.out.println("加载MySQL驱动失败! 请检查classpath中是否包含mysql-connector-java-8.0.33.jar");
            System.out.println("错误详情: " + e.getMessage());
        }
            
            // 尝试连接数据库
            System.out.println("\n=== 尝试数据库连接 ===");
            // 从配置中查找正确的URL键名
            String url = null;
            for (String key : props.stringPropertyNames()) {
                if (key.contains("url")) {
                    url = props.getProperty(key);
                    System.out.println("找到URL配置: " + key + " = " + url);
                    break;
                }
            }
            String user = props.getProperty("username");
            String password = props.getProperty("password");
            
            System.out.println("\n连接参数：");
            System.out.println("URL: " + url);
            System.out.println("用户名: " + user);
            System.out.println("密码: " + (password == null ? "null" : "******"));
            
            // 分析可能的问题
            System.out.println("\n=== 问题分析 ===");
            if (System.getProperty("user.name").equals("liuyixin")) {
                System.out.println("重要发现: 系统用户名(user.name)是'liuyixin'，这可能是连接池默认使用的用户名！");
                System.out.println("这解释了为什么配置为root，但实际尝试使用liuyixin用户连接。");
            }
            
            if (url != null && user != null && password != null) {
                System.out.println("\n正在尝试连接数据库...");
                try {
                    conn = DriverManager.getConnection(url, user, password);
                    System.out.println("数据库连接成功！");
                } catch (SQLException e) {
                    System.err.println("数据库连接失败: " + e.getMessage());
                    if (e.getMessage().contains("liuyixin")) {
                        System.err.println("确认: 错误信息包含'liuyixin'用户名！");
                    }
                }
            } else {
                System.out.println("配置不完整，无法尝试连接。");
            }
            
        } catch (IOException e) {
            System.err.println("读取配置文件失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("\n数据库连接已关闭");
                } catch (SQLException e) {
                    System.err.println("关闭连接失败: " + e.getMessage());
                }
            }
        }
    }
}