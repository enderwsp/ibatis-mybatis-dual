package org.example;

import com.ibatis.common.jdbc.SimpleDataSource;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.transaction.TransactionConfig;
import com.ibatis.sqlmap.engine.transaction.TransactionManager;
import com.ibatis.sqlmap.engine.transaction.jdbc.JdbcTransactionConfig;
import org.ibatis.mybatis.utils.DBEnv;
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
        ps.put("JDBC.Driver", DBEnv.Master.Driver);
        ps.put("JDBC.ConnectionURL", DBEnv.Master.Url);
        ps.put("JDBC.Username", DBEnv.Master.UserName);
        ps.put("JDBC.Password", DBEnv.Master.Password);
        SimpleDataSource dataSource = new SimpleDataSource(ps);
        SqlMapClientImpl sqlMapClient;
        try {
            sqlMapClient = (SqlMapClientImpl) SqlMapClientBuilder.buildSqlMapClient(Resources.getResourceAsReader(resource));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TransactionConfig txc = new JdbcTransactionConfig();
        txc.setDataSource(dataSource);
        TransactionManager txm = new TransactionManager(txc);
        sqlMapClient.getDelegate().setTxManager(txm);
        exeSql(sqlMapClient, true);
    }

    private static void exeSql(SqlMapClient sqlMapClient, boolean useTx) {
        SqlMapSession session = null;
        Connection con = null;
        try {
            session = sqlMapClient.openSession();
            if (useTx) {
                session.startTransaction();
            }
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
            if (useTx) {
                session.commitTransaction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (useTx) {
                try {
                    session.endTransaction();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
