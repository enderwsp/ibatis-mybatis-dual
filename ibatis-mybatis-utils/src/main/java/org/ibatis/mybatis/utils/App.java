package org.ibatis.mybatis.utils;

import org.h2.tools.Server;

import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        // 启动TCP服务器并允许所有IP连接，监听默认的端口8082
        try {
            Server server = Server.createTcpServer("-tcp", "-tcpPort", "9092", "-tcpAllowOthers").start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("H2 Database TCP server started.");
    }
}
