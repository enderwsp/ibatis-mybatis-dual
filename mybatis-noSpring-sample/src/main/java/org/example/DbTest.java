package org.example;

import org.h2.tools.Server;

import java.sql.SQLException;

public class DbTest {
    public static String DbDriver="org.h2.Driver";
    public static String DbUrl="jdbc:h2:~/test";
    public static String DbUserName="sa";
    public static String DbPassword="";
    public static String DbDriver2="org.h2.Driver";
    public static String DbUrl2="jdbc:h2:~/test";
    public static String DbUserName2="sa";
    public static String DbPassword2="";

    /***
     * CREATE TABLE USERTEST (ID INT PRIMARY KEY, USERNAME VARCHAR(12), PASSWORD VARCHAR(24), EMAIL VARCHAR(35));
     * @param args
     */

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
