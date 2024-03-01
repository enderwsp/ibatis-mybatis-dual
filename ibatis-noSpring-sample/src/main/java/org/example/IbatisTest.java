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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IbatisTest {
    public static Logger log = LoggerFactory.getLogger(IbatisTest.class);
    public static String resource = "ibatis/SqlMapConfig.xml";


    public static void main(String[] args) {
        SqlMapClientImpl sqlMapClient = init(resource, DBEnv.Master.Driver, DBEnv.Master.Url, DBEnv.Master.UserName, DBEnv.Master.Password);

        exeSql(sqlMapClient, true);
    }

    public static SqlMapClientImpl init(String resource, String drver, String url, String name, String pw) {
        Map ps = new HashMap();
        ps.put("JDBC.Driver", drver);
        ps.put("JDBC.ConnectionURL", url);
        ps.put("JDBC.Username", name);
        ps.put("JDBC.Password", pw);
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
        return sqlMapClient;
    }

    private static void exeSql(SqlMapClient sqlMapClient, boolean useTx) {
        try {

            if (useTx) {
                sqlMapClient.startTransaction();
            }
            Map dd = new HashMap();
            dd.put("id", 1236);
            dd.put("username", "TestIbatis");
            dd.put("usernamelike", "Test%");
            dd.put("password", System.currentTimeMillis() + "-PW");
            dd.put("email", System.currentTimeMillis() + "@123.com");
            sqlMapClient.insert("testibatis.insert", dd);
            int udx = sqlMapClient.update("testibatis.update", dd);
            log.info("----------update:udx:" + udx);
            List<Map> rs = sqlMapClient.queryForList("testibatis.select", dd);
            log.info("----------select:rs:" + rs);
            int dudx = sqlMapClient.delete("testibatis.delete", dd);
            log.info("----------delete:dudx:" + dudx);
            if (useTx) {
                sqlMapClient.commitTransaction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (useTx) {
                try {
                    sqlMapClient.endTransaction();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
