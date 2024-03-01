package org.example;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.ibatis.mybatis.utils.DBEnv;
import org.ibatis.mybatis.utils.FilesReadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisTest {
    public static Logger log = LoggerFactory.getLogger(MybatisTest.class);


    public static void main(String[] args) {
        String resource = "mybatis/mybatis-config.xml";
        String ms = "mybatis/mappers/*.xml";
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver(DBEnv.Master.Driver);
        dataSource.setUrl(DBEnv.Master.Url);
        dataSource.setUsername(DBEnv.Master.UserName);
        dataSource.setPassword(DBEnv.Master.Password);
        SqlSessionFactory f;
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
//            XMLConfigBuilder parser = new XMLConfigBuilder(Resources.getResourceAsStream(resource), null, null);
//            Configuration configuration = parser.parse();
        Configuration configuration = new Configuration();
        configuration.setEnvironment(environment);
        // 如果mapper xml与接口在同一包下，或者namespace设置正确，则无需手动添加
        // 否则需要手动添加mapper xml文件路径
        addMappers(ms, configuration);
        f = new SqlSessionFactoryBuilder().build(configuration);

        exeSql(f, true);
    }

    // 从类路径中获取所有mapper资源
    private static void addMappers(String ms, Configuration configuration) {
        // 扫描类路径下mybatis/mappers/*.xml文件并加载到Configuration中
        for (URL u : FilesReadUtils.get(ms)) {
            XMLMapperBuilder xmlMapperBuilder = null;
            try {
                xmlMapperBuilder = new XMLMapperBuilder(u.openStream(), configuration, u.toString(), configuration.getSqlFragments());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            xmlMapperBuilder.parse();
        }
    }

    private static void exeSql(SqlSessionFactory f, boolean useTx) {
        SqlSession session = null;
        try {
            session = f.openSession(!useTx);
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
            if (useTx) {
                session.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (useTx) {
                session.rollback();
            }
        }

    }
}
