package org.example;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.ibatis.mybatis.utils.DBEnv;
import org.ibatis.mybatis.utils.FilesReadUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static Logger log = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        String resource = "mybatis/mybatis-config.xml";
        String ms = "mybatis/mappers/*.xml";
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver(DBEnv.Master.Driver);
        dataSource.setUrl(DBEnv.Master.Url);
        dataSource.setUsername(DBEnv.Master.UserName);
        dataSource.setPassword(DBEnv.Master.Password);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(FilesReadUtils.getResources(ms));
        TransactionFactory tc = new SpringManagedTransactionFactory();
        factory.setTransactionFactory(tc);
        SqlSessionFactory f = null;
        try {
            f = factory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TransactionTemplate tt = new TransactionTemplate();
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource);
        tt.setTransactionManager(tm);
        SqlSessionTemplate st = new SqlSessionTemplate(f);

        exeSql(st, tt, true);
    }

    private static void exeSql(SqlSession session, TransactionTemplate tt, boolean useTx) {
        try {

            if (useTx) {
                tt.execute(status -> {
                    business(session);
                    return null;
                });
            }else {
                business(session);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (useTx) {
                session.rollback();
            }
        }

    }

    private static void business(SqlSession session) {
        Map dd = new HashMap();
        dd.put("id", 4433);
        dd.put("username", "TestIbatis");
        dd.put("usernamelike", "Test%");
        dd.put("password", System.currentTimeMillis() + "-PW");
        dd.put("email", System.currentTimeMillis() + "@123.com");
        session.insert("mybatis.insert", dd);
        int udx = session.update("mybatis.update", dd);
        log.info("----------update:udx:" + udx);
        List<Map> rs = session.selectList("mybatis.select", dd);
        log.info("----------select:rs:" + rs);
        int dudx = session.delete("mybatis.delete", dd);
        log.info("----------delete:dudx:" + dudx);
    }
}