package org.example.aspect;

import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.IbatisTest;
import org.example.aspect.dual.common.DBOptDualContext;
import org.ibatis.mybatis.utils.DBEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
public class IbatisAspect {
    public static Logger log = LoggerFactory.getLogger(IbatisAspect.class);
    public static Logger log1 = LoggerFactory.getLogger(Enhancer.class);
    public static Logger log12 = LoggerFactory.getLogger(InvocationHandler.class);
    private static SqlMapClientImpl sqlMapClientSlave = IbatisTest.init(IbatisTest.resource, DBEnv.SlavesOne.Driver, DBEnv.SlavesOne.Url, DBEnv.SlavesOne.UserName, DBEnv.SlavesOne.Password);
    private static Map<String, Method> RelectedMethodWithArgTsCache = new ConcurrentHashMap();


    // 定义一个切点，匹配所有实现了InterfaceA接口方法的类
//    @Pointcut("execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.insert(..))"
//            + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.update(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.delete(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryForObject(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryForList(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryWithRowHandler(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryForPaginatedList(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryForMap(..))"+ "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.startBatch(..))"+ "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.executeBatch(..))"+ "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.executeBatchDetailed(..))"
//    )
    @Pointcut("execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.insert(..))"
            + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.update(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.delete(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.queryForObject(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.queryForList(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.queryWithRowHandler(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.queryForMap(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.startBatch(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.executeBatch(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.executeBatchDetailed(..))"
    )
    public void dbOpts() {

    }

    // 在此切点上应用通知，比如一个前置通知
//    @Around("dbOpts()")
//    public Object AroundMethodExecution(ProceedingJoinPoint pjp) throws Throwable {
//        if (sqlMapClientSlave != pjp.getTarget()) {
//            String m = pjp.getSignature().getName();
//            String as = "#";
//            for (Object arg : pjp.getArgs()) {
//                as += arg.getClass() + "&";
//            }
//            Method currentSlave = IbatisAspectUtil.cacheMethod(RelectedMethodWithArgTsCache, pjp, m + as);
//            log.info("before " + m);
//            Object r = pjp.proceed(pjp.getArgs());
//            log.info("after " + m);
//            log.info("before Slave " + m);
//            try {
//                Object sr = currentSlave.invoke(sqlMapClientSlave, pjp.getArgs());
//                log.info("before Slave " + m + "====" + sr);
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//            log.info("after Slave" + m);
//            return r;
//        } else {
//            return pjp.proceed(pjp.getArgs());
//        }
//    }

    @Around("dbOpts()")
    public Object AroundAsyncExecution(ProceedingJoinPoint pjp) throws Throwable {
        if (sqlMapClientSlave != pjp.getTarget()) {
            String m = pjp.getSignature().getName();
            String as = "#";
            for (Object arg : pjp.getArgs()) {
                as += arg.getClass() + "&";
            }
            Method currentSlave = IbatisAspectUtil.cacheMethod(RelectedMethodWithArgTsCache, pjp, m + as);
            log.info("before " + m);
            Object r = pjp.proceed(pjp.getArgs());
            log.info("after " + m);
            log.info("before Slave " + m);
            try {
                Object sr = currentSlave.invoke(sqlMapClientSlave, pjp.getArgs().c);
                log.info("before Slave " + m + "====" + sr);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            log.info("after Slave" + m);
            return r;
        } else {
            return pjp.proceed(pjp.getArgs());
        }
    }

    @Pointcut("execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.startTransaction(..))"
            + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.commitTransaction(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.endTransaction(..))"
    )
    public void dbTxOpts() {

    }

    @Around("dbTxOpts()")
    public Object AfterMethodExecution(ProceedingJoinPoint pjp) throws Throwable {
        if (sqlMapClientSlave != pjp.getTarget()) {
            Object r = pjp.proceed(pjp.getArgs());
            String m = pjp.getSignature().getName();
            switch (m) {
                case "startTransaction": {
                    creatDualContext();
                    break;
                }
                case "commitTransaction": {
                    exeAndCommitDualContext();
                    break;
                }
                case "endTransaction": {
                    cleanDualContext();
                    break;
                }
            }
            return r;
        } else {
            return pjp.proceed(pjp.getArgs());
        }
    }


}