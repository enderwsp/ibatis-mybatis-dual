package org.example;

import com.ibatis.common.jdbc.SimpleDataSource;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.client.SqlMapSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IbatisTest {
    public static Logger log = LoggerFactory.getLogger(IbatisTest.class);

    public static void main(String[] args) {
        log = LoggerFactory.getLogger(IbatisTest.class);
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        log.atLevel(Level.INFO);
        String resource = "ibatis/SqlMapConfig.xml";
        Map ps = new HashMap();
        ps.put("JDBC.Driver", DbTest.DbDriver);
        ps.put("JDBC.ConnectionURL", DbTest.DbUrl);
        ps.put("JDBC.Username", DbTest.DbUserName);
        ps.put("JDBC.Password", DbTest.DbPassword);
        SimpleDataSource dataSource = new SimpleDataSource(ps);
        SqlMapClient sqlMapClient;
        try {
            sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(Resources.getResourceAsReader(resource));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        exeSql(sqlMapClient, dataSource, false);
    }

    private static void exeSql(SqlMapClient sqlMapClient, SimpleDataSource dataSource, boolean b) {
        SqlMapSession session = null;
        Connection con = null;
        try {
            session = sqlMapClient.openSession();
            con = session.getCurrentConnection();
            if (con == null) {
                session.setUserConnection(dataSource.getConnection());
                con = session.getCurrentConnection();
            }
            con.setAutoCommit(!b);
            Map dd = new HashMap();
            dd.put("id", 1234);
            dd.put("username", "TestIbatis");
            dd.put("usernamelike", "Test%");
            dd.put("password", System.currentTimeMillis() + "-PW");
            dd.put("email", System.currentTimeMillis() + "@123.com");
            session.insert("testibatis.insert", dd);
            int udx = session.update("testibatis.update", dd);
            log.info("----------update:udx:" + udx);
            List<Map> rs = session.queryForList("testibatis.select", dd);
            log.info("----------select:rs:" + rs);
            int dudx = session.delete("testibatis.delete", dd);
            log.info("----------delete:dudx:" + dudx);
            if (b) {
                con.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (b) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
