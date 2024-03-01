package org.ibatis.mybatis.utils;

public class DBEnv {
    public static class Master{
        public static String Driver="org.h2.Driver";
        public static String Url="jdbc:h2:~/test";
        public static String UserName="sa";
        public static String Password="";
    }
    public static class SlavesOne{
        public static String Driver="";
        public static String Url="";
        public static String UserName="";
        public static String Password="";
    }
    public static class SlavesTwo{
        public static String Driver="";
        public static String Url="";
        public static String UserName="";
        public static String Password="";
    }
}