package org.ibatis.mybatis.utils;

public class DBEnv {
    public static class Master{
        public static String Driver=org.h2.Driver.class.getName();
//        public static String Driver="org.h2.Driver";
        public static String Url="jdbc:h2:~/test";
        public static String UserName="sa";
        public static String Password="";
    }
    public static class SlavesOne{
        public static String Driver=com.mysql.cj.jdbc.Driver.class.getName();
//        public static String Driver="com.mysql.cj.jdbc.Driver";
        public static String Url="jdbc:mysql://127.0.0.1:3306/hzj?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai";
        public static String UserName="root";
        public static String Password="root";
    }
    public static class SlavesTwo{
        public static String Driver="";
        public static String Url="";
        public static String UserName="";
        public static String Password="";
    }
}